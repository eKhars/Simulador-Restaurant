package com.restaurant.config;

public class GameConfig {
    // Configuración del restaurante
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 520;
    public static final int TOTAL_TABLES = 10;
    public static final int TOTAL_WAITERS = 1;
    public static final int TOTAL_COOKS = 1;

    // Posiciones fijas
    public static final double ENTRANCE_X = 900;
    public static final double ENTRANCE_Y = 120;
    public static final double KITCHEN_X = 170;
    public static final double KITCHEN_Y = 200;

    public static final double WAITER_X = 205;
    public static final double WAITER_Y = 290;

    public static final double RECEPTIONIST_X = 895;
    public static final double RECEPTIONIST_Y = 100;

    // Velocidades de movimiento (pixels por segundo)
    public static final double CUSTOMER_SPEED = 100.0;
    public static final double WAITER_SPEED = 150.0;

    // Tiempos (en milisegundos)
    public static final int MIN_EATING_TIME = 5000;
    public static final int MAX_EATING_TIME = 10000;

    // Dimensiones de los sprites
    public static final int SPRITE_SIZE = 32;
}