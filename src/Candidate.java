public class Candidate {
    String id;
    String name;
    String email;
    String qualification;
    String[] skills;
    int experience;
    double expectedSalary;

    public Candidate(String id, String name, String email, String qualification, String[] skills, int experience, double expectedSalary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.qualification = qualification;
        this.skills = skills;
        this.experience = experience;
        this.expectedSalary = expectedSalary;
    }

    @Override
    public String toString() {
        return name + " (" + id + ") - " + qualification + ", " + experience + " yrs";
    }
}
