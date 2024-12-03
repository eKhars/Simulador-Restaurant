package com.restaurant.domain.monitors;

import com.restaurant.domain.models.Order;
import com.restaurant.domain.models.OrderStatus;
import java.util.LinkedList;
import java.util.Queue;

public class OrderQueueMonitor {
    private final Queue<Order> pendingOrders;
    private final Queue<Order> inProcessOrders;
    private final Queue<Order> readyOrders;
    private final Object monitor = new Object();

    public OrderQueueMonitor() {
        pendingOrders = new LinkedList<>();
        inProcessOrders = new LinkedList<>();
        readyOrders = new LinkedList<>();
    }

    public void addOrder(Order order) {
        synchronized (monitor) {
            pendingOrders.add(order);
            monitor.notify();
        }
    }

    public Order getNextOrder() throws InterruptedException {
        synchronized (monitor) {
            while (pendingOrders.isEmpty()) {
                monitor.wait();
            }
            Order order = pendingOrders.poll();
            order.setStatus(OrderStatus.IN_PROCESS);
            inProcessOrders.add(order);
            return order;
        }
    }

    public void markOrderAsReady(Order order) {
        synchronized (monitor) {
            order.setStatus(OrderStatus.READY);
            inProcessOrders.remove(order);
            readyOrders.add(order);
            monitor.notifyAll();
        }
    }

    public Order checkReadyOrder(int tableNumber) throws InterruptedException {
        synchronized (monitor) {
            for (Order order : readyOrders) {
                if (order.getTableNumber() == tableNumber) {
                    readyOrders.remove(order);
                    order.setStatus(OrderStatus.DELIVERED);
                    return order;
                }
            }
            return null;
        }
    }
}