package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static AtomicInteger hashCount = new AtomicInteger(0);

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        // Start the hash thread immediately
        executorService.execute(() -> {
            Random rand = new Random();
            byte[] s = new byte[32];
            while (!Thread.currentThread().isInterrupted()) {
                rand.nextBytes(s);
                // Generate salt with the specified work factor
                String salt = BCrypt.gensalt(12);

                // Generate bcrypt hash with the specified salt
                String hashedPassword = BCrypt.hashpw(new String(s), salt);

                // Print the generated hash
                System.out.println("BCrypt Hash: " + hashedPassword);
                hashCount.incrementAndGet();
            }
        });

        // Schedule the task to stop the hash thread after 1 minute
        executorService.schedule(() -> {
            executorService.shutdownNow(); // Interrupts the thread and stops the executor service
            System.out.println("Timer expired! Total hashes made: " + hashCount.get());
        }, 1, TimeUnit.MINUTES);
    }
}
