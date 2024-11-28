package com.restaurant.application;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.restaurant.config.GameConfig;
import com.restaurant.domain.entities.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class GameFactory implements EntityFactory {

    @Spawns("customer")
    public Entity spawnCustomer(SpawnData data) {
        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(new Rectangle(GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, Color.BLUE))
                .with(data.<Customer>get("customerComponent"))
                .build();
    }

    @Spawns("waiter")
    public Entity spawnWaiter(SpawnData data) {
        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(new Rectangle(GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, Color.GREEN))
                .with(data.<Waiter>get("waiterComponent"))
                .build();
    }

    @Spawns("cook")
    public Entity spawnCook(SpawnData data) {
        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(new Rectangle(GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, Color.RED))
                .build();
    }

    @Spawns("table")
    public Entity spawnTable(SpawnData data) {
        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(new Rectangle(GameConfig.SPRITE_SIZE * 1.5, GameConfig.SPRITE_SIZE * 1.5, Color.BROWN))
                .with(new Table(data.get("tableNumber"), new Point2D(data.getX(), data.getY())))
                .build();
    }

    @Spawns("receptionist")
    public Entity spawnReceptionist(SpawnData data) {
        return entityBuilder()
                .at(GameConfig.RECEPTIONIST_X, GameConfig.RECEPTIONIST_Y)
                .viewWithBBox(new Rectangle(GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, Color.PURPLE))
                .with(data.<Receptionist>get("receptionistComponent"))
                .build();
    }

    @Spawns("kitchen")
    public Entity spawnKitchen(SpawnData data) {
        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(new Rectangle(GameConfig.SPRITE_SIZE * 2, GameConfig.SPRITE_SIZE * 2, Color.GRAY))
                .build();
    }
}