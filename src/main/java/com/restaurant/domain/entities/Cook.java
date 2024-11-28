package com.restaurant.domain.entities;

import com.restaurant.domain.models.Order;
import com.restaurant.domain.monitors.OrderQueueMonitor;

public class Cook implements Runnable {
    private final OrderQueueMonitor orderQueueMonitor;
    private volatile boolean isResting;
    private Order currentOrder;

    public Cook(int id, OrderQueueMonitor orderQueueMonitor) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.isResting = true;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                currentOrder = orderQueueMonitor.getNextOrder();
                isResting = false;

                Thread.sleep(currentOrder.getPreparationTime());

                orderQueueMonitor.markOrderAsReady(currentOrder);
                currentOrder = null;
                isResting = true;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}