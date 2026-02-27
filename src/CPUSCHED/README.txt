CPUSCHED - CPU Scheduling Simulator (Java)

Compile:
javac Process.java CPUScheduler.java

Run (FIFO):
java CPUScheduler datafile.txt FIFO

Run (SJF non-preemptive):
java CPUScheduler datafile.txt SJF

Sample input: datafile.txt (500 processes, format: arrival burst per line)
Sample output: printed directly to console (copy to sample_output_FIFO.txt / sample_output_SJF.txt after running)

Generator for datafile.txt (run once):
See GenerateData.java below if needed.

Statistics printed exactly as required in spec.