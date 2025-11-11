# SchedulingAlgorithms-Java

This project implements different CPU scheduling algorithms in Java. It is created to understand how process scheduling works in operating systems. The code is written in a clear and simple way so that anyone learning OS concepts can easily understand it.

## Overview
CPU Scheduling is an important part of an operating system. It decides the order in which processes will use the CPU. The main goal of scheduling is to reduce waiting time, turnaround time, and increase CPU utilization.  
In this project, different algorithms are implemented and compared to show how each one works and how the results change depending on process burst time, priority, and time quantum.

## Algorithms Implemented

### 1. First Come First Serve (FCFS)
- Processes are executed in the order they arrive.
- It is a non-preemptive algorithm.
- The process that arrives first will be executed first.
- Waiting time increases for processes that arrive later.
- Easy to implement but not efficient for systems with many short processes.

### 2. Shortest Job First (SJF)
- The process with the smallest burst time is executed first.
- It can be preemptive or non-preemptive (this project uses non-preemptive).
- Reduces average waiting time compared to FCFS.
- Disadvantage: It may cause starvation for longer processes.

### 3. Priority Scheduling
- Each process has a priority value.
- The process with the highest priority is executed first.
- If two processes have the same priority, they are scheduled based on arrival order.
- Can be preemptive or non-preemptive (this project uses non-preemptive).
- Disadvantage: Lower priority processes may starve if higher priority ones keep arriving.

### 4. Round Robin (RR)
- Each process is given a fixed time quantum.
- After that time, the process is moved to the back of the queue.
- It is a preemptive algorithm.
- Suitable for time-sharing systems.
- Reduces starvation and gives fair CPU time to all processes.

## Code Explanation

The code starts by taking process details such as:
- Process ID
- Arrival Time
- Burst Time
- Priority (if needed)

Each algorithm is implemented in a separate function or section.  
Common calculations include:
- **Waiting Time:** The total time a process waits before getting CPU.
- **Turnaround Time:** The total time from arrival to completion.
- **Completion Time:** When a process finishes execution.

After scheduling, the code displays:
- Process ID
- Arrival Time
- Burst Time
- Completion Time
- Turnaround Time
- Waiting Time

Finally, it calculates the **average waiting time** and **average turnaround time** for all processes to compare algorithm efficiency.

## How to Run
1. Clone this repository or download the project files.
2. Open the project in any Java IDE like IntelliJ IDEA, Eclipse, or VS Code.
3. Make sure Java is installed and properly configured.
4. Run the `SchedulingAlgorithms.java` file.
5. The program will execute all algorithms and print the results for each one.

## Example Output
The output shows scheduling details for each algorithm, including process times and averages.  
Sample output format:

