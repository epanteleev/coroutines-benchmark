package org.bench;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskPool {
    private final Worker[] workers;

    public TaskPool(int tasks) {
        Dispatcher.setup(tasks);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(tasks);
        this.workers = new Worker[tasks];

        for (int i = 0; i < tasks; i++) {
            this.workers[i] = new Worker();
        }
        for (Worker th: this.workers) {
            executor.execute(th);
        }
    }

    public Worker dispatch() {
        return this.workers[Dispatcher.apply()];
    }

    private static class Dispatcher {
        private static int worked = 0;
        private static int tasks = 0;

        private static void setup(int tasks) {
            Dispatcher.tasks = tasks;
        }

        private static int apply() {
            int w = worked;
            worked++;
            return w % tasks;
        }
    }
}