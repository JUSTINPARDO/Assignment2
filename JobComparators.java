import java.util.Comparator;

public class JobComparators {
    public static Comparator<Job> byProcessingTime() {
        return Comparator.comparingInt(Job::getProcessingTime);
    }

    public static Comparator<Job> byPriority() {
        return (a, b) -> {
            if (a.getPriority() != b.getPriority()) {
                return Integer.compare(a.getPriority(), b.getPriority());
            }
            return Integer.compare(a.getProcessingTime(), b.getProcessingTime());
        };
    }
}