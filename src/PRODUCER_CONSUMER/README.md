PRODUCER-CONSUMER - Java multi-threaded solution

Compile:
javac *.java

Run one combination:
java ProducerConsumer 2 2 10

Runs ALL 9 combinations automatically when you run the main (p=2/5/10, c=2/5/10, b=10).

Sample input data: none (randomly generated inside)
Sample output: console output per run (copy to sample_output_p2c5b10.txt etc.)

Shared variables & semaphores (clearly marked in code):
- BoundedBuffer: empty, full, mutex semaphores
- AtomicInteger producedCount
- volatile allProduced flag + poison pills (special flag by main thread)
- Global stats updated with synchronized block