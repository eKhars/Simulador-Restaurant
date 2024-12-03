package com.restaurant.application;

import com.almasb.fxgl.entity.Entity;
import com.restaurant.domain.models.CustomerStats;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;


public class GameUIManager {
    private final CustomerStats customerStats;

    public GameUIManager(CustomerStats customerStats) {
        this.customerStats = customerStats;
    }

    public Entity createBackgroundEntity() {
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/fondo.png")));
        ImageView backgroundImageView = new ImageView(backgroundImage);

        backgroundImageView.setFitWidth(1050);
        backgroundImageView.setFitHeight(1000);
        backgroundImageView.setPreserveRatio(false);

        AnchorPane root = new AnchorPane();
        AnchorPane.setTopAnchor(backgroundImageView, -250.0);
        AnchorPane.setLeftAnchor(backgroundImageView, 0.0);
        root.getChildren().add(backgroundImageView);

        Entity backgroundEntity = new Entity();
        backgroundEntity.addComponent(new GameApp.BackgroundComponent(root));

        return backgroundEntity;
    }

    public void initializeUI() {
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
}
