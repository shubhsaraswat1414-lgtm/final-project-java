public class Application {
    String appId;
    Candidate candidate;
    Job job;
    String status;

    public Application(String appId, Candidate candidate, Job job) {
        this.appId = appId;
        this.candidate = candidate;
        this.job = job;
        this.status = "Under Review";
    }

    @Override
    public String toString() {
        return appId + " | " + candidate.name + " -> " + job.title + " [" + status + "]";
    }
}
