public class Interview {
    String interviewId;
    Application application;
    String date;

    public Interview(String interviewId, Application application, String date) {
        this.interviewId = interviewId;
        this.application = application;
        this.date = date;
    }

    @Override
    public String toString() {
        return interviewId + " | Date: " + date + " | " + application.candidate.name + " (" + application.job.title + ")";
    }
}

