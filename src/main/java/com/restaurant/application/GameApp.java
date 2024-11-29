package com.restaurant.application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.restaurant.config.GameConfig;
import com.restaurant.domain.entities.*;
import com.restaurant.domain.models.*;
import com.restaurant.domain.monitors.*;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.restaurant.utils.PoissonDistribution;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameApp extends GameApplication {
    private RestaurantMonitor restaurantMonitor;
    private OrderQueueMonitor orderQueueMonitor;
    private CustomerQueueMonitor customerQueueMonitor;
    private CustomerStats customerStats;
    private final List<Entity> tables = new ArrayList<>();
    private ScheduledExecutorService customerSpawner;
    private int customerIdCounter = 0;
    private PoissonDistribution poissonDistribution;
    private Entity receptionistEntity;
    private List<Thread> cookThreads = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(GameConfig.WINDOW_WIDTH);
        settings.setHeight(GameConfig.WINDOW_HEIGHT);
        settings.setTitle("Restaurant Simulator");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());

        Entity background = createBackgroundEntity();
        getGameWorld().addEntity(background);

        initializeComponents();
        initializeUI();
        initializeGameElements();
        startCustomerGenerator();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (customerSpawner != null) {
                customerSpawner.shutdownNow();
            }
            for (Thread cookThread : cookThreads) {
                cookThread.interrupt();
            }
        }));
    }


    class BackgroundComponent extends Component {

        private AnchorPane root;

        public BackgroundComponent(AnchorPane root) {
            this.root = root;
        }

        @Override
        public void onAdded() {
            ViewComponent viewComponent = getEntity().getViewComponent();
            viewComponent.addChild(root);
        }
    }

    private Entity createBackgroundEntity() {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/image/fondo.png"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(1050);
        backgroundImageView.setFitHeight(1000);
        backgroundImageView.setPreserveRatio(false);

        AnchorPane root = new AnchorPane();
        AnchorPane.setTopAnchor(backgroundImageView, -250.0);
        AnchorPane.setLeftAnchor(backgroundImageView, 0.0);
        root.getChildren().add(backgroundImageView);

        // crea una entidad con el AnchorPane como componente
        Entity backgroundEntity = new Entity();
        backgroundEntity.addComponent(new BackgroundComponent(root));

        return backgroundEntity;
    }


    private void initializeComponents() {
        customerStats = new CustomerStats();
        restaurantMonitor = new RestaurantMonitor();
        orderQueueMonitor = new OrderQueueMonitor();
        customerQueueMonitor = new CustomerQueueMonitor();
        poissonDistribution = new PoissonDistribution(0.2); // Ajusta este valor para cambiar la frecuencia de llegada
    }

    private void initializeUI() {
        VBox statsBox = createStatsBox();
        getGameScene().addUINode(statsBox);
    }

    private VBox createStatsBox() {
        VBox stats = new VBox(10);
        stats.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 10; -fx-background-radius: 5;");
        stats.setTranslateX(10);
        stats.setTranslateY(10);

        String labelStyle = "-fx-font-size: 14px; -fx-font-weight: bold;";

        Label waitingTableLabel = createStatsLabel(
                customerStats.customersWaitingTableProperty(),
                "Esperando mesa: %d",
                labelStyle
        );

        Label waitingFoodLabel = createStatsLabel(
                customerStats.customersWaitingFoodProperty(),
                "Esperando comida: %d",
                labelStyle
        );

        Label eatingLabel = createStatsLabel(
                customerStats.customersEatingProperty(),
                "Comiendo: %d",
                labelStyle
        );

        Label atTablesLabel = createStatsLabel(
                customerStats.customersAtTablesProperty(),
                "En mesas: %d",
                labelStyle
        );

        stats.getChildren().addAll(
                waitingTableLabel,
                waitingFoodLabel,
                eatingLabel,
                atTablesLabel
        );

        return stats;
    }

    private Label createStatsLabel(javafx.beans.property.IntegerProperty property, String format, String style) {
        Label label = new Label();
        label.setTextFill(Color.BLACK);
        label.textProperty().bind(property.asString(format));
        label.setStyle(style);
        return label;
    }

    private void initializeGameElements() {

        initializeKitchen();
        initializeTables();
        initializeReceptionist();
        initializeWaiters();
        initializeCooks();
    }

    private void initializeKitchen() {
        getGameWorld().spawn("kitchen",
                new SpawnData(GameConfig.KITCHEN_X, GameConfig.KITCHEN_Y)
        );
    }

    private void initializeTables() {
        for (int i = 0; i < GameConfig.TOTAL_TABLES; i++) {
            int row = i / 5;
            int col = i % 5;
            double x = 200 + col * (GameConfig.SPRITE_SIZE * 2);
            double y = 3 + row * (GameConfig.SPRITE_SIZE * 2);

            SpawnData data = new SpawnData(x, y);
            data.put("tableNumber", i);
            Entity table = getGameWorld().spawn("table", data);
            tables.add(table);
        }
    }


    private void initializeReceptionist() {
        Point2D receptionistPos = new Point2D(GameConfig.RECEPTIONIST_X, GameConfig.RECEPTIONIST_Y);
        Receptionist receptionistComponent = new Receptionist(
                restaurantMonitor,
                receptionistPos,
                customerStats
        );

        SpawnData data = new SpawnData(receptionistPos.getX(), receptionistPos.getY());
        data.put("receptionistComponent", receptionistComponent);
        receptionistEntity = getGameWorld().spawn("receptionist", data);
    }

    private void initializeWaiters() {
        for (int i = 0; i < GameConfig.TOTAL_WAITERS; i++) {
            Point2D startPos = new Point2D(
                    GameConfig.WAITER_X + GameConfig.SPRITE_SIZE,
                    GameConfig.WAITER_Y - ((i + 1) * GameConfig.SPRITE_SIZE * 1.5)
            );

            Waiter waiter = new Waiter(
                    i,
                    orderQueueMonitor,
                    customerQueueMonitor,
                    startPos,
                    tables
            );

            SpawnData data = new SpawnData(startPos.getX(), startPos.getY());
            data.put("waiterComponent", waiter);
            getGameWorld().spawn("waiter", data);
        }
    }

    private void initializeCooks() {
        double kitchenY = GameConfig.KITCHEN_Y;
        for (int i = 0; i < GameConfig.TOTAL_COOKS; i++) {
            Cook cook = new Cook(i, orderQueueMonitor);
            SpawnData data = new SpawnData(
                    GameConfig.KITCHEN_X,
                    kitchenY + (i * GameConfig.SPRITE_SIZE)
            );
            data.put("cookComponent", cook);
            Entity cookEntity = getGameWorld().spawn("cook", data);

            Thread cookThread = new Thread(cook);
            cookThreads.add(cookThread);
            cookThread.start();
        }
    }

    private void startCustomerGenerator() {
        customerSpawner = Executors.newSingleThreadScheduledExecutor();
        customerSpawner.scheduleAtFixedRate(() -> {
            try {
                generateNewCustomers();
            } catch (Exception e) {
                System.err.println("Error en generador de clientes: " + e.getMessage());
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void generateNewCustomers() {
        int numCustomers = poissonDistribution.nextInt();
        for (int i = 0; i < numCustomers; i++) {
            final int currentId = customerIdCounter++;
            runOnce(() -> spawnCustomer(currentId),
                    Duration.seconds(poissonDistribution.nextArrivalTime())
            );
        }
    }

    private void spawnCustomer(int id) {
        Customer customer = new Customer(
                id,
                restaurantMonitor,
                orderQueueMonitor,
                customerQueueMonitor,
                customerStats,
                tables
        );
        SpawnData data = new SpawnData(GameConfig.ENTRANCE_X, GameConfig.ENTRANCE_Y);
        data.put("customerComponent", customer);
        getGameWorld().spawn("customer", data);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    private void stopCookThreads() {
        for (Thread cookThread : cookThreads) {
            cookThread.interrupt();
            try {
                cookThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        cookThreads.clear();
    }

    private void stopCustomerGenerator() {
        if (customerSpawner != null) {
            customerSpawner.shutdown();
            try {
                if (!customerSpawner.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    customerSpawner.shutdownNow();
                }
            } catch (InterruptedException e) {
                customerSpawner.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}