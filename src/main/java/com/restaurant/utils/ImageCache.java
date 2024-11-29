package com.restaurant.utils;

import com.restaurant.config.GameConfig;
import javafx.scene.image.Image;
import java.util.concurrent.ConcurrentHashMap;

public class ImageCache {
    private static final ConcurrentHashMap<String, Image> cache = new ConcurrentHashMap<>();

    public static Image getImage(String path) {
        return cache.computeIfAbsent(path, key -> {
            Image img = new Image(key,
                    GameConfig.SPRITE_SIZE * 2,  // width
                    GameConfig.SPRITE_SIZE * 2,  // height
                    true,  // preserveRatio
                    true,  // smooth
                    true); // background loading
            return img;
        });
    }

    public static void preloadImages() {
        String[] paths = {
                "image/personas/Persona.png",
                "image/personas/Persona2.png",
                "image/personas/Persona3.png",
                "image/personas/Persona4.png",
                "image/personas/Mesera.png",
                "image/personas/Cocinero.png",
                "image/personas/Cocinero2.png",
                "image/personas/Recepcionista.png",
                "image/objetos/Mesa1Persona.png",
                "image/objetos/Estufa.png"
        };

        for (String path : paths) {
            getImage(path);
        }
    }
}
