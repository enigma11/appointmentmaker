package com.gdiama.app;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AppointmentRequestMakerTasksExecutor {

    private final ThreadFactory threadFactory;

    public AppointmentRequestMakerTasksExecutor(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void executeTasks(List<AppointmentMakerTask> tasks) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(Math.max((tasks.size()/2 ), 1), threadFactory);
        try {
            List<Future<Void>> futureTasks = new ArrayList<Future<Void>>();
            for (AppointmentMakerTask task : tasks) {
                futureTasks.add(executor.submit(task));
            }

            for (Future<Void> future : futureTasks) {
                future.get(80, TimeUnit.SECONDS);
            }
        } finally {
            if (!executor.isShutdown()) {
                executor.shutdown();
                executor.awaitTermination(60, TimeUnit.SECONDS);
            }
        }
    }
}
