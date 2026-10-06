public class Job {
    String id;
    String title;
    String[] requiredSkills;
    double salary;

    public Job(String id, String title, String[] requiredSkills, double salary) {
        this.id = id;
        this.title = title;
        this.requiredSkills = requiredSkills;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return id + ": " + title + " (₹" + salary + ")";
    }
}
