CPUSCHED - CPU Scheduling Simulator (Java)

Commands are run from parent folder src
Compile:
del CPUSCHED\*.class
javac CPUSCHED\Process.java CPUSCHED\CPUScheduler.java CPUSCHED\GenerateData.java

Run (FIFO):
java CPUSCHED.CPUScheduler CPUSCHED\Datafile1-txt.txt FIFO

Run (SJF non-preemptive):
java CPUSCHED.CPUScheduler CPUSCHED\Datafile1-txt.txt SJF

Sample input: datafile.txt (500 processes, format: arrival burst per line)
Sample output: printed directly to console (copy to sample_output_FIFO.txt / sample_output_SJF.txt after running)

Generator for datafile.txt (run once):
See GenerateData.java below if needed.

Statistics printed exactly as required in spec.