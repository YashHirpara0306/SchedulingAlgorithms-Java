import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchedulingAlgorithms {

    // ---------- Utilities ----------
    private static void printHeaderLine(String s) {
        System.out.print("ProcessID  ");
        if (s != null) {
            System.out.println(s);
        } else {
            System.out.println();
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    private static void sortIntArray(int[] arr) {
        // simple selection / bubble style small-n sort (like C code)
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arr[i] > arr[j]) {
                    int tmp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = tmp;
                }
            }
        }
    }

    private static double averageWaitingTime(int[] finishTime, int[] arrivalTime, int[] burstTime) {
        double sum = 0.0;
        int n = finishTime.length;
        for (int i = 0; i < n; i++) {
            sum += (finishTime[i] - arrivalTime[i] - burstTime[i]);
        }
        return sum / n;
    }

    private static double averageTurnaroundTime(int[] finishTime, int[] arrivalTime) {
        double sum = 0.0;
        int n = finishTime.length;
        for (int i = 0; i < n; i++) {
            sum += (finishTime[i] - arrivalTime[i]);
        }
        return sum / n;
    }

    private static void displayProcessTable(int[] pid, int[] at, int[] bt, int[] pr, int[] ft) {
        System.out.println("\n\ntable of process\n");
        System.out.println("|PID |   | AT |  | BT |   | PR |    | FT |   | TAT |   | VT |  ");
        int c = pid.length;
        for (int i = 0; i < c; i++) {
            int tat = ft[i] - at[i];
            int wt = tat - bt[i];
            System.out.printf("  %d        %d       %d       %d        %d        %d       %d   %n",
                    pid[i], at[i], bt[i], pr[i], ft[i], tat, wt);
        }
    }

    // ---------- FCFS ----------
    private static void firstComeFirstServed(int[] at, int[] bt, int[] pr, int[] pid) {
        int c = pid.length;

        // Sort by arrival time (simple stable selection)
        for (int i = 0; i < c; i++) {
            for (int j = i + 1; j < c; j++) {
                if (at[i] > at[j]) {
                    swap(at, i, j);
                    swap(bt, i, j);
                    swap(pid, i, j);
                    swap(pr, i, j);
                }
            }
        }

        int[] ft = new int[c];
        Arrays.fill(ft, 0);

        int current = 0;

        System.out.print(" PROCESS EXECUTION CHART: ");
        System.out.print("START: ");
        for (int i = 0; i < c; i++) {
            System.out.print("p" + pid[i] + " --> ");
        }
        System.out.println(" (END)");

        System.out.print("\n GANT CHART : ");
        System.out.print("0 ");

        for (int j = 0; j < c; j++) {
            if (at[j] <= current) {
                current += bt[j];
                System.out.print("|p" + pid[j] + "| " + current + " ");
                ft[j] = current;
            } else {
                System.out.print(" " + at[j] + " ");
                current = at[j];
                System.out.print(" |p" + pid[j] + "| " + (current + bt[j]) + " ");
                current += bt[j];
                ft[j] = current;
            }
        }

        System.out.printf("%n Average Waiting Time is: %f  %n", averageWaitingTime(ft, at, bt));
        System.out.printf("%n%n Average Turnaround Time %f %n", averageTurnaroundTime(ft, at));

        displayProcessTable(pid, at, bt, pr, ft);
    }

    // ---------- SJF (non-preemptive) ----------
    private static class ExecNode {
        int id;
        int start;
        int end;
        ExecNode next;

        ExecNode(int id, int start, int end) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.next = null;
        }
    }

    private static void shortestJobFirst(int[] at, int[] bt, int[] pr, int[] pid) {
        int c = pid.length;

        // Sort by burst time (like original)
        for (int i = 0; i < c; i++) {
            for (int j = i + 1; j < c; j++) {
                if (bt[i] > bt[j]) {
                    swap(at, i, j);
                    swap(bt, i, j);
                    swap(pid, i, j);
                    swap(pr, i, j);
                }
            }
        }

        ExecNode head = null, tail = null;
        int[] ft = new int[c];
        int[] cm = new int[c];
        Arrays.fill(ft, 0);
        Arrays.fill(cm, 0);

        int time = at[0];
        int current = 0;
        int complete = 0;

        while (complete != c) {
            boolean progressed = false;
            for (int i = 0; i < c; i++) {
                if (at[i] <= current && cm[i] == 0) {
                    complete++;
                    ft[i] = current + bt[i];
                    current = current + bt[i];
                    cm[i] = 1;

                    ExecNode node = new ExecNode(pid[i], current - bt[i], current);
                    if (head == null) {
                        head = tail = node;
                    } else {
                        tail.next = node;
                        tail = node;
                    }
                    progressed = true;
                    break;
                } else {
                    if (time >= at[i] && cm[i] == 0) {
                        time = at[i];
                    }
                }
            }
            if (!progressed) {
                if (current < time) {
                    current = time;
                } else {
                    // if nothing available and no upcoming time update, advance to smallest arrival
                    int nextArr = Integer.MAX_VALUE;
                    for (int i = 0; i < c; i++) {
                        if (cm[i] == 0 && at[i] > current && at[i] < nextArr) nextArr = at[i];
                    }
                    if (nextArr != Integer.MAX_VALUE) current = nextArr;
                }
            }
        }

        // print process execution chart
        System.out.print(" PROCESS EXECUTION CHART: ");
        System.out.print("(START) ");
        ExecNode ptr = head;
        while (ptr != null) {
            System.out.print("p" + ptr.id + " --> ");
            ptr = ptr.next;
        }
        System.out.println(" (END)");

        // GANTT
        System.out.print("\n GANT CHART:");
        ExecNode pt2 = head;
        if (pt2 != null) {
            if (pt2.start != 0) {
                System.out.print("0 " + pt2.start);
            } else {
                System.out.print("0");
            }
        } else {
            System.out.print("0");
        }

        while (pt2 != null) {
            System.out.print(" |p" + pt2.id + "| ");
            if (pt2.next == null || pt2.end == pt2.next.start) {
                System.out.print(pt2.end);
            } else {
                System.out.print(pt2.end + " " + pt2.next.end);
            }
            pt2 = pt2.next;
        }

        System.out.printf("%n Average Waiting Time is: %f  %n", averageWaitingTime(ft, at, bt));
        System.out.printf(" Average Turnaround %f %n", averageTurnaroundTime(ft, at));

        displayProcessTable(pid, at, bt, pr, ft);
    }

    // ---------- SRTF (Shortest Remaining Time First) ----------
    private static void shortestRemainingTimeFirst(int[] atIn, int[] btIn, int[] prIn, int[] pidIn) {
        // Work on copies, because we mutate burst times
        int c = pidIn.length;
        int[] at = Arrays.copyOf(atIn, c);
        int[] bt = Arrays.copyOf(btIn, c);
        int[] pr = Arrays.copyOf(prIn, c);
        int[] pid = Arrays.copyOf(pidIn, c);
        int[] cbrt = Arrays.copyOf(bt, c);

        // initial sort by burst time (like C code)
        for (int i = 0; i < c; i++) {
            for (int j = i + 1; j < c; j++) {
                if (bt[i] > bt[j]) {
                    swap(at, i, j);
                    swap(bt, i, j);
                    swap(pid, i, j);
                    swap(pr, i, j);
                    swap(cbrt, i, j);
                }
            }
        }

        // build unique arrival times list (like the linked list approach in C)
        Set<Integer> set = new LinkedHashSet<>();
        for (int i = 0; i < c; i++) set.add(at[i]);
        int n = set.size();
        int[] arr = new int[n];
        int idx = 0;
        for (Integer v : set) arr[idx++] = v;
        sortIntArray(arr);

        int[] ft = new int[c];
        int[] cm = new int[c];
        Arrays.fill(ft, 0);
        Arrays.fill(cm, 0);

        int time = 0;
        System.out.print("\n Gantt Chart: ");
        if (n > 0 && arr[0] > 0) {
            System.out.print("0 |W| " + arr[0]);
            time = arr[0];
        } else {
            System.out.print("0 ");
        }

        int complete = 0;

        for (int i = 0; i < n - 1; i++) {
            int fmin = arr[i];
            int smin = arr[i + 1];
            boolean restart;
            do {
                restart = false;
                for (int j = 0; j < c; j++) {
                    if (cm[j] == 0 && at[j] <= time) {
                        if (bt[j] < smin - fmin) {
                            time += bt[j];
                            System.out.print("|p" + pid[j] + "| " + time);
                            ft[j] = time;
                            cm[j] = 1;
                            complete++;
                            fmin = time;
                            restart = true; // goto jump in C
                            break;
                        } else if (bt[j] == smin - fmin) {
                            time += bt[j];
                            System.out.print("|p" + pid[j] + "| " + time);
                            ft[j] = time;
                            cm[j] = 1;
                            complete++;
                            break;
                        } else {
                            bt[j] -= (smin - fmin);
                            time = smin;
                            System.out.print("|p" + pid[j] + "| " + time);
                            break;
                        }
                    }
                }
            } while (restart);

            // sort remaining by burst time after each interval (similar to C)
            for (int k = 0; k < c; k++) {
                for (int j = k + 1; j < c; j++) {
                    if (bt[k] > bt[j]) {
                        swap(at, k, j);
                        swap(bt, k, j);
                        swap(pid, k, j);
                        swap(pr, k, j);
                        swap(ft, k, j);
                        swap(cm, k, j);
                        swap(cbrt, k, j);
                    }
                }
            }
        }

        if (complete != c) {
            for (int i = 0; i < c; i++) {
                if (cm[i] == 0) {
                    time += bt[i];
                    System.out.print("|p" + pid[i] + "| " + time);
                    complete++;
                    cm[i] = 1;
                    ft[i] = time;
                }
            }
        }

        System.out.printf("%n Average Waiting Time is: %f  %n", averageWaitingTime(ft, at, cbrt));
        System.out.printf(" Average Turnaround %f %n", averageTurnaroundTime(ft, at));
        displayProcessTable(pid, at, cbrt, pr, ft);
    }

    // ---------- NPPS (Non-Preemptive Priority Scheduling) ----------
    private static void nonPreemptivePriorityScheduling(int[] at, int[] bt, int[] pr, int[] pid) {
        int c = pid.length;

        // sort by priority (lower value = higher priority in original C code)
        for (int i = 0; i < c; i++) {
            for (int j = i + 1; j < c; j++) {
                if (pr[i] > pr[j]) {
                    swap(at, i, j);
                    swap(bt, i, j);
                    swap(pid, i, j);
                    swap(pr, i, j);
                }
            }
        }

        ExecNode head = null, tail = null;
        int[] ft = new int[c];
        int[] cm = new int[c];
        Arrays.fill(ft, 0);
        Arrays.fill(cm, 0);

        int time = at[0];
        int current = 0;
        int complete = 0;

        while (complete != c) {
            boolean progressed = false;
            for (int i = 0; i < c; i++) {
                if (at[i] <= current && cm[i] == 0) {
                    complete++;
                    ft[i] = current + bt[i];
                    current = current + bt[i];
                    cm[i] = 1;

                    ExecNode node = new ExecNode(pid[i], current - bt[i], current);
                    if (head == null) {
                        head = tail = node;
                    } else {
                        tail.next = node;
                        tail = node;
                    }
                    progressed = true;
                    break;
                } else {
                    if (time >= at[i] && cm[i] == 0) {
                        time = at[i];
                    }
                }
            }
            if (!progressed) {
                if (current < time) current = time;
                else {
                    int nextArr = Integer.MAX_VALUE;
                    for (int i = 0; i < c; i++) if (cm[i] == 0 && at[i] > current && at[i] < nextArr) nextArr = at[i];
                    if (nextArr != Integer.MAX_VALUE) current = nextArr;
                }
            }
        }

        System.out.print(" PROCESS EXECUTION CHART: ");
        System.out.print("(START) ");
        ExecNode pt = head;
        while (pt != null) {
            System.out.print("p" + pt.id + " --> ");
            pt = pt.next;
        }
        System.out.println(" (END)");

        System.out.print("\n GANT CHART:");
        ExecNode pt2 = head;
        if (pt2 != null) {
            if (pt2.start != 0) {
                System.out.print("0 " + pt2.start);
            } else {
                System.out.print("0");
            }
        } else {
            System.out.print("0");
        }
        while (pt2 != null) {
            System.out.print(" |p" + pt2.id + "| ");
            if (pt2.next == null || pt2.end == pt2.next.start) {
                System.out.print(pt2.end);
            } else {
                System.out.print(pt2.end + " " + pt2.next.end);
            }
            pt2 = pt2.next;
        }

        System.out.printf("%n Average Waiting Time is: %f  %n", averageWaitingTime(ft, at, bt));
        System.out.printf(" Average Turnaround %f %n", averageTurnaroundTime(ft, at));
        displayProcessTable(pid, at, bt, pr, ft);
    }

    // ---------- PPS (Preemptive Priority Scheduling) ----------
    private static void preemptivePriorityScheduling(int[] atIn, int[] btIn, int[] prIn, int[] pidIn) {
        // similar to SRTF but using priority as comparator during sorts (lower pr -> higher priority)
        int c = pidIn.length;
        int[] at = Arrays.copyOf(atIn, c);
        int[] bt = Arrays.copyOf(btIn, c);
        int[] pr = Arrays.copyOf(prIn, c);
        int[] pid = Arrays.copyOf(pidIn, c);
        int[] cbrt = Arrays.copyOf(bt, c);

        // initial sort by priority
        for (int i = 0; i < c; i++) {
            for (int j = i + 1; j < c; j++) {
                if (pr[i] > pr[j]) {
                    swap(at, i, j);
                    swap(bt, i, j);
                    swap(pid, i, j);
                    swap(pr, i, j);
                    swap(cbrt, i, j);
                }
            }
        }

        Set<Integer> set = new LinkedHashSet<>();
        for (int i = 0; i < c; i++) set.add(at[i]);
        int n = set.size();
        int[] arr = new int[n];
        int idx = 0;
        for (Integer v : set) arr[idx++] = v;
        sortIntArray(arr);

        int[] ft = new int[c];
        int[] cm = new int[c];
        Arrays.fill(ft, 0);
        Arrays.fill(cm, 0);

        int time = 0;
        System.out.print("\n Gantt Chart: ");
        if (n > 0 && arr[0] > 0) {
            System.out.print("0 |W| " + arr[0]);
            time = arr[0];
        } else {
            System.out.print("0 ");
        }

        int complete = 0;

        for (int i = 0; i < n - 1; i++) {
            int fmin = arr[i];
            int smin = arr[i + 1];
            boolean restart;
            do {
                restart = false;
                for (int j = 0; j < c; j++) {
                    if (cm[j] == 0 && at[j] <= time) {
                        if (bt[j] < smin - fmin) {
                            time += bt[j];
                            System.out.print("|p" + pid[j] + "| " + time);
                            ft[j] = time;
                            cm[j] = 1;
                            complete++;
                            fmin = time;
                            restart = true;
                            break;
                        } else if (bt[j] == smin - fmin) {
                            time += bt[j];
                            System.out.print("|p" + pid[j] + "| " + time);
                            ft[j] = time;
                            cm[j] = 1;
                            complete++;
                            break;
                        } else {
                            bt[j] -= (smin - fmin);
                            time = smin;
                            System.out.print("|p" + pid[j] + "| " + time);
                            break;
                        }
                    }
                }
            } while (restart);

            // sort remaining by priority now (like C's pr-based sort)
            for (int k = 0; k < c; k++) {
                for (int j = k + 1; j < c; j++) {
                    if (pr[k] > pr[j]) {
                        swap(at, k, j);
                        swap(bt, k, j);
                        swap(pid, k, j);
                        swap(pr, k, j);
                        swap(ft, k, j);
                        swap(cm, k, j);
                        swap(cbrt, k, j);
                    }
                }
            }
        }

        if (complete != c) {
            for (int i = 0; i < c; i++) {
                if (cm[i] == 0) {
                    time += bt[i];
                    System.out.print("|p" + pid[i] + "| " + time);
                    complete++;
                    cm[i] = 1;
                    ft[i] = time;
                }
            }
        }

        System.out.printf("%n Average Waiting Time is: %f  %n", averageWaitingTime(ft, at, cbrt));
        System.out.printf(" Average Turnaround %f %n", averageTurnaroundTime(ft, at));
        displayProcessTable(pid, at, cbrt, pr, ft);
    }

    // ---------- Round Robin ----------
    private static void roundRobin(int[] at, int[] bt, int[] pr, int[] pid, int contextSwitch) {
        // contextSwitch parameter is present to match the C signature, but originalC doesn't use it inside RR logic
        int count2 = pid.length;
        Scanner in = new Scanner(System.in);
        System.out.print(" ENTER  THE VALUE OF QUANTUM TIME: ");
        int qt = in.nextInt();

        System.out.println();
        System.out.print(" Gantt Chart: ");
        int com = 0;
        int ct = 0;
        int check;

        int[] ft = new int[count2];
        int[] wt = new int[count2];
        int[] tat = new int[count2];
        int[] rt = new int[count2];

        for (int i = 0; i < count2; i++) rt[i] = bt[i];

        while (com != count2) {
            check = 0;
            for (int i = 0; i < count2; i++) {
                if (at[i] <= ct && rt[i] > 0) {
                    if (rt[i] <= qt) {
                        com++;
                        ct += rt[i];
                        ft[i] = ct;
                        tat[i] = ft[i] - at[i];
                        wt[i] = tat[i] - bt[i];
                        for (int j = 0; j < rt[i]; j++) {
                            System.out.print(" P" + (i + 1));
                        }
                        rt[i] = 0;
                    } else {
                        ct += qt;
                        for (int j = 0; j < qt; j++) {
                            System.out.print(" P" + (i + 1));
                        }
                        rt[i] -= qt;
                    }
                    check = 1;
                }
            }
            if (check == 0) {
                ct++;
                System.out.print(" e");
            }
        }

        for (int i = 0; i < count2; i++) {
            tat[i] = ft[i] - at[i];
        }

        System.out.println("\n\n");
        System.out.println(" Priority\tArraival Time\tBurst Time\tFinish Time\tTurn Around\tWaiting time");

        for (int i = 0; i < count2; i++) {
            System.out.printf("  %d\t\t %d\t\t %d\t\t %d\t\t %d\t\t %d%n",
                    pr[i], at[i], bt[i], ft[i], tat[i], wt[i]);
        }
        float sum1 = 0, sum2 = 0;
        for (int i = 0; i < count2; i++) {
            sum1 += tat[i];
            sum2 += wt[i];
        }
        System.out.printf(" Average Turn Around Time is: %.2f%n", sum1 / count2);
        System.out.printf(" Average Waiting Time is: %.2f%n", sum2 / count2);
    }

    // ---------- Main and input parsing ----------
    public static void main(String[] args) {
        String filename = "inputfile.txt";
        String headerLine = null;
        String rest = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            headerLine = br.readLine();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            rest = sb.toString();
        } catch (FileNotFoundException fnfe) {
            System.err.println("inputfile.txt not found. Put the file in the same folder and run again.");
            return;
        } catch (IOException ioe) {
            System.err.println("Error reading inputfile.txt: " + ioe.getMessage());
            return;
        }

        printHeaderLine(headerLine);

        // Extract all integers from the rest of the file.
        // We'll consider the last integer as contextSwitch, rest grouped as triples (arrival, burst, priority).
        List<Integer> ints = new ArrayList<>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(rest);
        while (m.find()) {
            ints.add(Integer.parseInt(m.group()));
        }

        if (ints.size() == 0) {
            System.err.println("No integer data found in inputfile.txt after header.");
            return;
        }

        int contextSwitch = 0;
        if (ints.size() >= 1) {
            contextSwitch = ints.get(ints.size() - 1);
        }

        // processes are everything except the last integer
        int procIntCount = Math.max(0, ints.size() - 1);
        if (procIntCount % 3 != 0) {
            // If it's not a multiple of 3, try to be tolerant: if there are extra tokens at end, we still attempt to use complete triples
            System.out.println(" Warning: process data count is not multiple of 3. Using complete triples only.");
            procIntCount = (procIntCount / 3) * 3;
        }

        int procCount = procIntCount / 3;
        if (procCount == 0) {
            System.err.println("No process triples found in inputfile.txt.");
            return;
        }

        int[] at = new int[procCount];
        int[] bt = new int[procCount];
        int[] pr = new int[procCount];
        int[] pid = new int[procCount];

        for (int i = 0; i < procCount; i++) {
            at[i] = ints.get(i * 3);
            bt[i] = ints.get(i * 3 + 1);
            pr[i] = ints.get(i * 3 + 2);
            pid[i] = i + 1;
        }

        // Print processes (similar to C output)
        for (int j = 0; j < procCount; j++) {
            System.out.printf("P%d %d %d %d%n", pid[j], at[j], bt[j], pr[j]);
        }

        System.out.println();
        System.out.println("Context Switch (read from file): " + contextSwitch);
        System.out.println("\n\n=*=*=*=*=*=*=*=*=*=!!!  WELCOME TO YOU !!!=*=*=*=*=*=*=*=*=*=\n\n");

        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println(" Enter 1: First Come First Served (FCFS)");
            System.out.println(" Enter 2: Shortest Job First (SJF)");
            System.out.println(" Enter 3: Shortest Remaining Time First (SRTF)");
            System.out.println(" Enter 4: Non Preemptive Priority Scheduling(NPPS)");
            System.out.println(" Enter 5: Preemptive Priority Scheduling(PPS)");
            System.out.println(" Enter 6: Round Robin (RR)");
            System.out.print("\nENTER: ");

            int operation;
            try {
                operation = Integer.parseInt(input.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            // Make copies of arrays to avoid in-place modification changing the original data across runs
            int[] atCopy = Arrays.copyOf(at, at.length);
            int[] btCopy = Arrays.copyOf(bt, bt.length);
            int[] prCopy = Arrays.copyOf(pr, pr.length);
            int[] pidCopy = Arrays.copyOf(pid, pid.length);

            switch (operation) {
                case 1:
                    firstComeFirstServed(atCopy, btCopy, prCopy, pidCopy);
                    break;
                case 2:
                    shortestJobFirst(atCopy, btCopy, prCopy, pidCopy);
                    break;
                case 3:
                    shortestRemainingTimeFirst(atCopy, btCopy, prCopy, pidCopy);
                    break;
                case 4:
                    nonPreemptivePriorityScheduling(atCopy, btCopy, prCopy, pidCopy);
                    break;
                case 5:
                    preemptivePriorityScheduling(atCopy, btCopy, prCopy, pidCopy);
                    break;
                case 6:
                    roundRobin(atCopy, btCopy, prCopy, pidCopy, contextSwitch);
                    break;
                default:
                    System.out.println(" !!! ERROR , please enter the valid operation\n");
                    continue;
            }

            System.out.println("\n\nDo you want to run another algorithm? (y/n): ");
            String resp = input.nextLine().trim().toLowerCase();
            if (!resp.equals("y") && !resp.equals("yes")) {
                break;
            }
            System.out.println();
        }

        input.close();
        System.out.println("Program finished.");
    }
}
