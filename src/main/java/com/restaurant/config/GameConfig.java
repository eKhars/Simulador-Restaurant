package com.restaurant.config;

public class GameConfig {
    // Configuración del restaurante
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final int TOTAL_TABLES = 10;
    public static final int TOTAL_WAITERS = 2;
    public static final int TOTAL_COOKS = 3;

    // Posiciones fijas
    public static final double ENTRANCE_X = 50;
    public static final double ENTRANCE_Y = 360;
    public static final double KITCHEN_X = 1100;
    public static final double KITCHEN_Y = 360;
    public static final double RECEPTIONIST_X = 150;
    public static final double RECEPTIONIST_Y = 360;

    // Velocidades de movimiento (pixels por segundo)
    public static final double CUSTOMER_SPEED = 100.0;
    public static final double WAITER_SPEED = 150.0;

    // Tiempos (en milisegundos)
    public static final int MIN_EATING_TIME = 5000;
    public static final int MAX_EATING_TIME = 10000;

    // Dimensiones de los sprites
    public static final int SPRITE_SIZE = 32;

    private GameConfig() {
        // Constructor privado para evitar instanciación
    }
}