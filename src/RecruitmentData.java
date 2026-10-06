import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class RecruitmentData {
    final ArrayList<Candidate> candidates = new ArrayList<>();
    final TreeMap<String, Job> jobs = new TreeMap<>();
    final HashMap<String, Application> applications = new HashMap<>();
    final LinkedList<Interview> interviews = new LinkedList<>();
}
