package PRODUCER_CONSUMER;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


// This class implements the producer-consumer problem using a bounded buffer. It simulates multiple producer threads that generate sales records and multiple consumer threads that process those records. The producers create SalesRecord objects with random data and put them into a shared BoundedBuffer, while the consumers take SalesRecord objects from the buffer, aggregate sales data, and print local statistics. After all producers have finished producing, the main thread sends poison pills to signal the consumers to stop consuming. Finally, it prints global statistics summarizing the total sales for each store, each month, and the overall aggregate sales, along with the total time taken for the simulation.
public class ProducerConsumer {
    private static final int TOTAL_ITEMS = 1000;
    private static BoundedBuffer buffer;
    private static final AtomicInteger producedCount = new AtomicInteger(0);
    private static double globalAggregate = 0.0;
    private static final double[] globalStoreSales = new double[11]; // 1-10
    private static final double[] globalMonthSales = new double[12];
    private static long simStart;

    public static void main(String[] args) {
        int[] ps = {2, 5, 10};
        int[] cs = {2, 5, 10};
        int b = 10;

        for (int p : ps) {
            for (int c : cs) {
                System.out.println("\n=== RUN p=" + p + " c=" + c + " b=" + b + " ===");
                runOneSimulation(p, c, b);
            }
        }
    }
    // This method runs a single simulation of the producer-consumer problem with the specified number of producers (p), consumers (c), and buffer capacity (b). It initializes the shared buffer, starts the producer and consumer threads, and waits for them to finish. After all threads have completed their work, it prints the global statistics, including total sales for each store, total sales for each month, aggregate sales, and total time taken for the simulation.
    private static void runOneSimulation(int p, int c, int b) {
        simStart = System.currentTimeMillis();
        buffer = new BoundedBuffer(b);
        producedCount.set(0);
        Arrays.fill(globalStoreSales, 0);
        Arrays.fill(globalMonthSales, 0);
        globalAggregate = 0;

        // Start producers (1 thread per store)
        Thread[] producers = new Thread[p];
        for (int i = 0; i < p; i++) {
            int storeId = i + 1;
            producers[i] = new Thread(() -> producerTask(storeId));
            producers[i].start();
        }

        // Start consumers (1 thread per consumer)
        Thread[] consumers = new Thread[c];
        for (int i = 0; i < c; i++) {
            int consId = i + 1;
            consumers[i] = new Thread(() -> consumerTask(consId, p));
            consumers[i].start();
        }

        // Wait for all producers to finish
        for (Thread t : producers) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }

        // Designated thread (main) sets flag + poison pills to signal consumers to stop
        for (int i = 0; i < c; i++) {
            try {
                buffer.put(new SalesRecord()); // poison
            } catch (InterruptedException ignored) {}
        }

        // Wait for consumers to finish
        for (Thread t : consumers) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }

        double elapsedSec = (System.currentTimeMillis() - simStart) / 1000.0;
        printGlobalStats(p, elapsedSec);
    }
    // This method represents the task that each producer thread will execute. It continuously generates SalesRecord items with random data until it has produced a total of 1000 items across all producers. Each SalesRecord includes a date, store ID, month, register number, and sales amount. The producer then puts each generated SalesRecord into the shared BoundedBuffer and sleeps for a random short duration to simulate time taken to produce an item. The use of an AtomicInteger ensures that the total count of produced items is accurately tracked across multiple producer threads
    private static void producerTask(int storeId) { // storeId is 1-based
        Random rand = new Random();
        while (true) {
            if (producedCount.get() >= TOTAL_ITEMS) break;
            int curr = producedCount.get();
            if (producedCount.compareAndSet(curr, curr + 1)) {
                int dd = 1 + rand.nextInt(30); // day 1-30
                int mm = 1 + rand.nextInt(12); // month 1-12
                int reg = 1 + rand.nextInt(6);  // register 1-6
                double amt = 0.50 + rand.nextDouble() * 999.49;
                SalesRecord rec = new SalesRecord(dd, mm, storeId, reg, amt);
                try {
                    buffer.put(rec);
                    Thread.sleep(5 + rand.nextInt(36));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
    // This method represents the task that each consumer thread will execute. It continuously takes SalesRecord items from the buffer until it encounters a poison pill, which signals it to stop consuming. The consumer maintains local aggregates for total sales, month-wise sales, and store-wise sales. After finishing consumption, it updates the global aggregates in a synchronized block to ensure thread safety. Finally, it prints its local statistics, which reflect the total sales amount processed by this consumer, the breakdown of sales by month, and the breakdown of sales by store.
    private static void consumerTask(int consId, int maxP) {
        Random rand = new Random(); // not used but kept for possible extension
        double locAggregate = 0.0;
        double[] locMonth = new double[12]; // month-wise local totals
        double[] locStore = new double[maxP + 1]; // store-wise local totals (1-based index for convenience)

        while (true) {
            SalesRecord rec;
            try {
                rec = buffer.take();
            } catch (InterruptedException e) {
                break;
            }
            if (rec.isPoison) break;

            locAggregate += rec.amount;
            locMonth[rec.month - 1] += rec.amount;  // month from date parse not needed since we store it directly
            locStore[rec.storeId] += rec.amount;
        }

        // Add local to global (synchronized)
        synchronized (ProducerConsumer.class) {
            globalAggregate += locAggregate;
            for (int m = 0; m < 12; m++) globalMonthSales[m] += locMonth[m];
            for (int s = 1; s <= maxP; s++) globalStoreSales[s] += locStore[s];
        }

        // Print local stats
        System.out.println("Consumer " + consId + " local statistics:"); // This is printed after the consumer finishes consuming all its items, which includes the poison pill. So it reflects the total local stats for that consumer.
        System.out.println("  Total sales: $" + String.format("%.2f", locAggregate)); //  This is the total sales amount that this consumer processed. It is calculated by summing up the amounts of all SalesRecord items that this consumer took from the buffer and processed before it encountered the poison pill.
        System.out.print("  Month-wise: ");
        for (int m = 0; m < 12; m++) if (locMonth[m] > 0) System.out.print("M" + (m+1) + ":$" + String.format("%.2f ", locMonth[m]));
        System.out.println();
        System.out.print("  Store-wide local: "); // This is the total sales amount that this consumer processed for each store. It is calculated by summing up the amounts of all SalesRecord items that this consumer took from the buffer and processed, grouped by storeId, before it encountered the poison pill.
        for (int s = 1; s <= maxP; s++) if (locStore[s] > 0) System.out.print("S" + s + ":$" + String.format("%.2f ", locStore[s])); // This prints the local store-wise totals for this consumer, showing how much sales amount this consumer processed for each store. It iterates through the locStore array, which holds the local totals for each store, and prints the store number and the corresponding total if it is greater than 0.
        System.out.println("\n");
    }
    // This method prints the global statistics after all producers and consumers have finished their work. It shows the total sales for each store, the total sales for each month across all stores, the aggregate sales amount, and the total time taken for the simulation. The statistics are formatted to two decimal places for better readability.
    private static void printGlobalStats(int p, double elapsedSec) {
        System.out.println("=== GLOBAL STATISTICS ===");
        System.out.println("Store-wide total sales:");
        for (int s = 1; s <= p; s++) {
            System.out.println("  Store " + s + ": $" + String.format("%.2f", globalStoreSales[s]));
        }
        System.out.println("Month-wise total sales (all stores):");
        for (int m = 1; m <= 12; m++) {
            System.out.println("  Month " + m + ": $" + String.format("%.2f", globalMonthSales[m-1]));
        }
        System.out.println("Aggregate sales (all sales together): $" + String.format("%.2f", globalAggregate));
        System.out.println("Total time for simulation: " + String.format("%.2f", elapsedSec) + " seconds");
    }
}