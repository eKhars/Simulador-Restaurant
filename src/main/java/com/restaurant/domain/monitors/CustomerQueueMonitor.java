package com.restaurant.domain.monitors;

import com.restaurant.domain.entities.Customer;
import java.util.LinkedList;
import java.util.Queue;

public class CustomerQueueMonitor {
    private final Queue<CustomerRequest> waitingCustomers;
    private final Object monitor = new Object();

    public static class CustomerRequest {
        public final Customer customer;
        public final int tableNumber;
        public final long arrivalTime;

        public CustomerRequest(Customer customer, int tableNumber) {
            this.customer = customer;
            this.tableNumber = tableNumber;
            this.arrivalTime = System.currentTimeMillis();
        }
    }

    public CustomerQueueMonitor() {
        waitingCustomers = new LinkedList<>();
    }

    public void addCustomer(Customer customer, int tableNumber) {
        synchronized (monitor) {
            waitingCustomers.add(new CustomerRequest(customer, tableNumber));
            monitor.notify();
        }
    }

    public CustomerRequest getNextCustomer() throws InterruptedException {
        synchronized (monitor) {
            while (waitingCustomers.isEmpty()) {
                monitor.wait();
            }
            return waitingCustomers.poll();
        }
    }

    public boolean hasWaitingCustomers() {
        synchronized (monitor) {
            return !waitingCustomers.isEmpty();
        }
    }
}