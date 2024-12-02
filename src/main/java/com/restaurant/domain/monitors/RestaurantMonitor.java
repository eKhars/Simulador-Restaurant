package com.restaurant.domain.monitors;

public class RestaurantMonitor {
    public static final int TOTAL_TABLES = 10;
    private final boolean[] tables;
    private final Object monitor = new Object();

    public RestaurantMonitor() {
        tables = new boolean[TOTAL_TABLES];
    }

    public int findAvailableTable() {
        synchronized (monitor) {
            for (int i = 0; i < tables.length; i++) {
                if (!tables[i]) {
                    return i;
                }
            }
            return -1;
        }
    }

    public void occupyTable(int tableNumber) {
        synchronized (monitor) {
            tables[tableNumber] = true;
        }
    }

    public void releaseTable(int tableNumber) {
        synchronized (monitor) {
            tables[tableNumber] = false;
            monitor.notifyAll();
        }
    }

    public void waitForAvailableTable() throws InterruptedException {
        synchronized (monitor) {
            while (findAvailableTable() == -1) {
                monitor.wait();
            }
        }
    }
}