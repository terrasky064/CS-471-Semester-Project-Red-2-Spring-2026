package CPUSCHED;
import java.io.*;
import java.util.*;

public class CPUScheduler {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java CPUScheduler <datafile.txt> <FIFO|SJF>");
            return;
        }
        String fileName = args[0];
        String algo = args[1].toUpperCase();
        boolean isFIFO = algo.equals("FIFO");
        if (!isFIFO && !algo.equals("SJF")) {
            System.out.println("Invalid algorithm. Use FIFO or SJF.");
            return;
        }

        List<Process> processes = readProcesses(fileName);
        if (processes.size() != 500) {
            System.out.println("Warning: Expected 500 processes, got " + processes.size());
        }
        simulate(processes, isFIFO);
    }

    private static List<Process> readProcesses(String fileName) {
    List<Process> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line;
        int id = 1;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || first) {  // skip header
                first = false;
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                try {
                    int arrival = Integer.parseInt(parts[0]);
                    int burst = Integer.parseInt(parts[1]);
                    list.add(new Process(id++, arrival, burst));
                } catch (NumberFormatException ignored) {}
            }
        }
    } catch (Exception e) {
        System.err.println("Error reading file: " + e.getMessage());
        System.exit(1);
    }
    list.sort(Comparator.comparingInt(p -> p.arrivalTime));
    return list;
}

    private static void simulate(List<Process> processes, boolean isFIFO) {
    int n = processes.size();
    int processesToComplete = Math.min(500, n);  // Respect spec: aim for 500, but don't exceed file size

    Queue<Process> readyQueue = isFIFO ?
            new LinkedList<>() :
            new PriorityQueue<>((p1, p2) -> {
                int cmp = Integer.compare(p1.burstTime, p2.burstTime);
                return cmp != 0 ? cmp : Integer.compare(p1.arrivalTime, p2.arrivalTime);
            });

    int currentTime = 0;
    int nextIndex = 0;
    int completed = 0;
    double totalWaiting = 0, totalTurnaround = 0, totalResponse = 0;
    long totalBurst = 0;

    // Only sum bursts for the first 500 (or all if <500)
    for (int i = 0; i < processesToComplete; i++) {
        totalBurst += processes.get(i).burstTime;
    }

    while (completed < processesToComplete) {
        // Add all arrived processes up to current time
        while (nextIndex < n && processes.get(nextIndex).arrivalTime <= currentTime) {
            readyQueue.add(processes.get(nextIndex++));
        }

        if (readyQueue.isEmpty()) {
            if (nextIndex >= n) break; // No more processes
            currentTime = processes.get(nextIndex).arrivalTime; // Jump forward
            continue;
        }

        // Select next process (FIFO or shortest burst)
        Process p = isFIFO ? readyQueue.poll() : ((PriorityQueue<Process>) readyQueue).poll();
        // If the process hasn't started yet, set its start time
        if (p.startTime == -1) {
            p.startTime = currentTime;
        }
        // Simulate running the process to completion
        currentTime += p.burstTime;
        p.completionTime = currentTime;
        completed++;

        // Calculate metrics for this process
        int turnaround = p.completionTime - p.arrivalTime;
        int waiting = turnaround - p.burstTime;
        int response = p.startTime - p.arrivalTime;

        // Accumulate totals for averages
        totalTurnaround += turnaround;
        totalWaiting += waiting;
        totalResponse += response;
    }

    double elapsed = currentTime;
    double throughput = (double) totalBurst / processesToComplete;  // spec formula
    double cpuUtil = (totalBurst / elapsed) * 100;


    // Print statistics
    System.out.println("Statistics for the Run");
    System.out.println("Number of processes: " + processesToComplete);
    System.out.println("Total elapsed time (for the scheduler): " + String.format("%.0f", elapsed));
    System.out.println("Throughput (Number of processes executed in one unit of CPU burst time): " + String.format("%.2f", throughput));
    System.out.println("CPU utilization: " + String.format("%.2f", cpuUtil) + "%");
    System.out.println("Average waiting time (in CPU burst times): " + String.format("%.2f", totalWaiting / processesToComplete));
    System.out.println("Average turnaround time (in CPU burst times): " + String.format("%.2f", totalTurnaround / processesToComplete));
    System.out.println("Average response time (in CPU burst times): " + String.format("%.2f", totalResponse / processesToComplete));
}
}