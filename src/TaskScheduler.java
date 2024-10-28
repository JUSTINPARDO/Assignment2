import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class TaskScheduler {
    // Task 1
    public void scheduleJobsSPT(List<Job> jobs, BufferedWriter writer) throws IOException {
        MinPQ<Job> minHeap = new MinPQ<>(JobComparators.byProcessingTime());
        for (Job job : jobs) {
            minHeap.insert(job);
        }

        List<Job> executionOrder = new ArrayList<>();
        int currentTime = 0;
        double totalCompletionTime = 0;

        while (!minHeap.isEmpty()) {
            Job job = minHeap.delMin();
            currentTime += job.getProcessingTime();
            totalCompletionTime += currentTime;
            executionOrder.add(job);
        }

        printResults(writer, executionOrder, totalCompletionTime / jobs.size());
    }

    // Task 2
    public void scheduleJobsPriority(List<Job> jobs, BufferedWriter writer) throws IOException {
        MinPQ<Job> priorityQueue = new MinPQ<>(JobComparators.byPriority());
        for (Job job : jobs) {
            priorityQueue.insert(job);
        }

        List<Job> executionOrder = new ArrayList<>();
        int currentTime = 0;
        double totalCompletionTime = 0;

        while (!priorityQueue.isEmpty()) {
            Job job = priorityQueue.delMin();
            currentTime += job.getProcessingTime();
            totalCompletionTime += currentTime;
            executionOrder.add(job);
        }

        printResults(writer, executionOrder, totalCompletionTime / jobs.size());
    }

    // Task 3
    public void scheduleDynamicJobs(List<Job> jobs, BufferedWriter writer) throws IOException {
        jobs.sort(Comparator.comparingInt(Job::getArrivalTime));

        MinPQ<Job> readyQueue = new MinPQ<>(JobComparators.byProcessingTime());
        List<Job> executionOrder = new ArrayList<>();
        int currentTime = 0;
        int jobIndex = 0;
        double totalCompletionTime = 0;

        while (jobIndex < jobs.size() || !readyQueue.isEmpty()) {
            while (jobIndex < jobs.size() && jobs.get(jobIndex).getArrivalTime() <= currentTime) {
                readyQueue.insert(jobs.get(jobIndex));
                jobIndex++;
            }

            if (readyQueue.isEmpty() && jobIndex < jobs.size()) {
                currentTime = jobs.get(jobIndex).getArrivalTime();
                continue;
            }

            if (!readyQueue.isEmpty()) {
                Job job = readyQueue.delMin();
                currentTime += job.getProcessingTime();
                totalCompletionTime += currentTime - job.getArrivalTime();
                executionOrder.add(job);
            }
        }

        printResults(writer, executionOrder, totalCompletionTime / jobs.size());
    }

    private void printResults(BufferedWriter writer, List<Job> executionOrder, double avgCompletionTime) throws IOException {
        List<Integer> jobIds = new ArrayList<>();
        for (Job job : executionOrder) {
            jobIds.add(job.getId());
        }

        writer.write("Execution order: " + jobIds.toString() + "\n");

        writer.write(String.format("Average completion time: %.1f\n", avgCompletionTime));
    }

    // Task 1 input reading
    public List<Job> readTask1Input(String filename) throws IOException {
        List<Job> jobs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 2) {
                    jobs.add(new Job(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1])
                    ));
                }
            }
        }
        return jobs;
    }

    // Task 2 input reading
    public List<Job> readTask2Input(String filename) throws IOException {
        List<Job> jobs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    jobs.add(Job.createWithPriority(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2])
                    ));
                }
            }
        }
        return jobs;
    }

    // Task 3 input reading
    public List<Job> readTask3Input(String filename) throws IOException {
        List<Job> jobs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("#.*$", "").trim(); // Remove comments
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    jobs.add(Job.createWithArrivalTime(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2])
                    ));
                }
            }
        }
        return jobs;
    }

    public static void main(String[] args) throws IOException {
        TaskScheduler scheduler = new TaskScheduler();
        FileWriter fileWriter = new FileWriter("output.txt");


        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            // Task 1
            bufferedWriter.write("Task 1 - SPT Scheduling:\n");
            List<Job> jobs1 = scheduler.readTask1Input("src/task1-input.txt");
            scheduler.scheduleJobsSPT(jobs1, bufferedWriter);  // Pass BufferedWriter to the scheduling method
            bufferedWriter.write("\n");

            // Task 2
            bufferedWriter.write("Task 2 - Priority-based Scheduling:\n");
            List<Job> jobs2 = scheduler.readTask2Input("src/task2-input.txt");
            scheduler.scheduleJobsPriority(jobs2, bufferedWriter);  // Pass BufferedWriter to the scheduling method
            bufferedWriter.write("\n");

            // Task 3
            bufferedWriter.write("Task 3 - Dynamic Arrival Scheduling:\n");
            List<Job> jobs3 = scheduler.readTask3Input("src/task3-input.txt");
            scheduler.scheduleDynamicJobs(jobs3, bufferedWriter);  // Pass BufferedWriter to the scheduling method
        } finally {
            bufferedWriter.close();  // Always close the writer to flush the content
        }
    }
}
