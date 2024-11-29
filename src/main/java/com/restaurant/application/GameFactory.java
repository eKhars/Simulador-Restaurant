package com.restaurant.application;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.restaurant.config.GameConfig;
import com.restaurant.domain.entities.*;
import com.restaurant.utils.ImageCache;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Random;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class GameFactory implements EntityFactory {

    @Spawns("customer")
    public Entity spawnCustomer(SpawnData data) {
        List<String> imagePaths = List.of(
                "image/personas/Persona.png",
                "image/personas/Persona2.png",
                "image/personas/Persona3.png",
                "image/personas/Persona4.png"
        );

        Random rand = new Random();
        String selectedPath = imagePaths.get(rand.nextInt(imagePaths.size()));
        ImageView imageView = new ImageView(ImageCache.getImage(selectedPath));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(data.<Customer>get("customerComponent"))
                .build();
    }

    @Spawns("waiter")
    public Entity spawnWaiter(SpawnData data) {
        ImageView imageView = new ImageView(ImageCache.getImage("image/personas/Mesera.png"));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(data.<Waiter>get("waiterComponent"))
                .build();
    }

    @Spawns("cook")
    public Entity spawnCook(SpawnData data) {
        List<String> imagePaths = List.of(
                "image/personas/Cocinero.png",
                "image/personas/Cocinero2.png"
        );

        Random rand = new Random();
        String selectedPath = imagePaths.get(rand.nextInt(imagePaths.size()));
        ImageView imageView = new ImageView(ImageCache.getImage(selectedPath));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .build();
    }

    @Spawns("table")
    public Entity spawnTable(SpawnData data) {
        ImageView imageView = new ImageView(ImageCache.getImage("image/objetos/Mesa1Persona.png"));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 9);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 9);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(new Table(data.get("tableNumber"), new Point2D(data.getX(), data.getY())))
                .build();
    }

    @Spawns("receptionist")
    public Entity spawnReceptionist(SpawnData data) {
        ImageView imageView = new ImageView(ImageCache.getImage("image/personas/Recepcionista.png"));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(GameConfig.RECEPTIONIST_X, GameConfig.RECEPTIONIST_Y)
                .viewWithBBox(imageView)
                .with(data.<Receptionist>get("receptionistComponent"))
                .build();
    }

    @Spawns("kitchen")
    public Entity spawnKitchen(SpawnData data) {
        ImageView imageView = new ImageView(ImageCache.getImage("image/objetos/Estufa.png"));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .view(imageView)
                .build();
    }
}
