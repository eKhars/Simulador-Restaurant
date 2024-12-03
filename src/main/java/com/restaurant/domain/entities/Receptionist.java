package com.restaurant.domain.entities;

import com.almasb.fxgl.entity.component.Component;
import com.restaurant.domain.monitors.RestaurantMonitor;
import com.restaurant.domain.models.CustomerStats;
import javafx.geometry.Point2D;
import java.util.Queue;
import java.util.LinkedList;

public class Receptionist extends Component {
    private final Point2D position;
    private final RestaurantMonitor restaurantMonitor;
    private final Queue<Customer> waitingCustomers;
    private final Object monitor = new Object();
    private boolean isBusy;
    private final CustomerStats customerStats;

    public Receptionist(RestaurantMonitor restaurantMonitor, Point2D position, CustomerStats customerStats) {
        this.restaurantMonitor = restaurantMonitor;
        this.position = position;
        this.customerStats = customerStats;
        this.waitingCustomers = new LinkedList<>();
        this.isBusy = false;

        startReceptionistBehavior();
    }

    private void startReceptionistBehavior() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processNextCustomer();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public void addCustomerToQueue(Customer customer) {
        synchronized (monitor) {
            int tableNumber = restaurantMonitor.findAvailableTable();

            if (tableNumber != -1) {
                restaurantMonitor.occupyTable(tableNumber);
                customer.assignTable(tableNumber);
            } else {
                waitingCustomers.add(customer);
                customerStats.incrementWaitingForTable();
                customer.waitForTable();
                monitor.notify();
            }
        }
    }

    private void processNextCustomer() throws InterruptedException {
        synchronized (monitor) {
            while (!waitingCustomers.isEmpty()) {
                int tableNumber = restaurantMonitor.findAvailableTable();
                if (tableNumber != -1) {
                    Customer customer = waitingCustomers.poll();
                    if (customer != null) {
                        restaurantMonitor.occupyTable(tableNumber);
                        customer.assignTable(tableNumber);
                    }
                } else {
                    break;
                }
            }
        }
    }
}