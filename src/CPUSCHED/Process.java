package CPUSCHED;
public class Process {
    int id;
    int arrivalTime;
    int burstTime;
    int startTime = -1;
    int completionTime = -1;
    // This class represents a process in a CPU scheduling simulation. Each Process object has an identifier (id), an arrival time indicating when the process arrives in the system, a burst time representing the total CPU time required for the process to complete, a start time indicating when the process begins execution, and a completion time indicating when the process finishes execution. The startTime and completionTime are initialized to -1 to indicate that they have not been set yet. This class is used to store and manage information about each process during the scheduling simulation.
    public Process(int id, int arrival, int burst) {
        this.id = id; //  The id is a unique identifier for the process, which can be used to distinguish it from other processes in the simulation. It is typically assigned sequentially as processes are read from the input file or created in the simulation.
        this.arrivalTime = arrival; // The arrivalTime indicates the time at which the process arrives in the system and is ready to be scheduled for execution. It is used by the scheduling algorithm to determine when a process can start executing based on its arrival time relative to the current simulation time.
        this.burstTime = burst; // The burstTime represents the total amount of CPU time that the process requires to complete its execution. It is a critical parameter for scheduling algorithms, as it helps determine the order in which processes are scheduled and how long they will run on the CPU before completion.
    }
}