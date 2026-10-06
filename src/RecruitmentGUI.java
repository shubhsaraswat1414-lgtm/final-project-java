import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class RecruitmentGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // Light Theme Palette
    private final Color COLOR_BG = new Color(246, 247, 244);         // Main background
    private final Color COLOR_SIDEBAR = new Color(24, 36, 49);       // Sidebar navy
    private final Color COLOR_CARD = new Color(255, 255, 255);        // Card white
    private final Color COLOR_CARD_INNER = new Color(248, 250, 252);  // Table background
    private final Color COLOR_PRIMARY = new Color(15, 118, 110);     // Active teal
    private final Color COLOR_TEXT = new Color(30, 41, 59);           // Dark text
    private final Color COLOR_MUTED = new Color(100, 112, 116);       // Subdued text
    private final Color COLOR_GREEN = new Color(16, 185, 129);        // Accent green
    private final Color COLOR_AMBER = new Color(217, 119, 6);         // Accent amber
    private final Color COLOR_BORDER = new Color(226, 232, 240);      // Light border

    // Recruitment data is kept separately from the Swing presentation.
    private transient final RecruitmentData data = new RecruitmentData();
    private final ArrayList<Candidate> candidates = data.candidates;
    private final TreeMap<String, Job> jobs = data.jobs;
    private final HashMap<String, Application> applications = data.applications;
    private final LinkedList<Interview> interviews = data.interviews;

    // GUI Components
    private JLabel lblCandidateCount = new JLabel("0");
    private JLabel lblJobCount = new JLabel("0");
    private JLabel lblInterviewCount = new JLabel("0");

    private JLabel lblTableTitle = new JLabel("Registered Candidates");
    private DefaultTableModel mainTableModel;
    private JTable mainTable;
    private JTextArea consoleLog = new JTextArea();
    private JTextField txtSearch = new JTextField(15);

    private String currentView = "Candidates";
    private JButton[] navButtons;
    private JButton btnAdd, btnSort, btnEdit, btnDelete, btnStatus;

    @SuppressWarnings("this-escape")
    public RecruitmentGUI() {
        setTitle("Job Recruitment Management System");
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        loadSampleData();
        buildSidebar();
        buildMainPanel();
        refreshView();
        log("System ready. " + candidates.size() + " candidates, " + jobs.size() + " jobs loaded.");
    }

    private void loadSampleData() {
        candidates.add(new Candidate("1", "Aarav Sharma", "aarav@email.com", "B.Tech CSE", new String[]{"Java", "Spring"}, 3, 85000));
        candidates.add(new Candidate("2", "Priya Verma", "priya@email.com", "MCA", new String[]{"Python", "Django"}, 4, 95000));
        candidates.add(new Candidate("3", "Rohan Mehta", "rohan@email.com", "B.Tech IT", new String[]{"React", "JavaScript"}, 2, 70000));
        candidates.add(new Candidate("4", "Ananya Singh", "ananya@email.com", "M.Tech", new String[]{"Java", "AWS"}, 5, 120000));

        jobs.put("1", new Job("1", "Senior Java Engineer", new String[]{"Java", "Spring"}, 90000));
        jobs.put("2", new Job("2", "Python Data Analyst", new String[]{"Python", "SQL"}, 75000));
        jobs.put("3", new Job("3", "Frontend Developer", new String[]{"React", "CSS"}, 80000));

        Application a = new Application("1", candidates.get(0), jobs.get("1"));
        applications.put(a.appId, a);
        interviews.add(new Interview("1", a, "2026-10-15"));
    }

    // ==================== SIDEBAR ====================
    private void buildSidebar() {
        JPanel topBar = new JPanel(new BorderLayout(24, 0));
        topBar.setBackground(COLOR_SIDEBAR);
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel logo = new JLabel("RECRUIT PRO");
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        topBar.add(logo, BorderLayout.WEST);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        nav.setOpaque(false);

        String[] labels = {"Candidates", "Jobs", "Applications", "Interviews", "Shortlisting", "Report"};
        navButtons = new JButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            navButtons[i] = createNavButton(labels[i]);
            nav.add(navButtons[i]);
        }

        highlightNav("Candidates");
        topBar.add(nav, BorderLayout.CENTER);

        JLabel role = new JLabel("RECRUITER");
        role.setFont(new Font("SansSerif", Font.BOLD, 10));
        role.setForeground(new Color(203, 213, 225));
        topBar.add(role, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    private JButton createNavButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(COLOR_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(8, 11, 8, 11));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            currentView = label;
            txtSearch.setText("");
            highlightNav(label);
            refreshView();
        });
        return btn;
    }

    private void highlightNav(String active) {
        for (JButton b : navButtons) {
            String name = b.getText().trim();
            if (name.equals(active)) {
                b.setBackground(new Color(240, 253, 250));
                b.setForeground(new Color(15, 118, 110));
                b.setFont(new Font("SansSerif", Font.BOLD, 12));
            } else {
                b.setBackground(COLOR_SIDEBAR);
                b.setForeground(new Color(203, 213, 225));
                b.setFont(new Font("SansSerif", Font.BOLD, 12));
            }
        }
    }

    // ==================== MAIN PANEL ====================
    private void buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(COLOR_BG);
        main.setBorder(new EmptyBorder(22, 24, 18, 24));

        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Recruitment workspace");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT);
        JLabel subtitle = new JLabel("Manage people, vacancies, applications and interviews in one place.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(COLOR_MUTED);
        JPanel headingText = new JPanel();
        headingText.setLayout(new BoxLayout(headingText, BoxLayout.Y_AXIS));
        headingText.setOpaque(false);
        headingText.add(title);
        headingText.add(Box.createVerticalStrut(3));
        headingText.add(subtitle);
        heading.add(headingText, BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchBar.setOpaque(false);
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchLabel.setForeground(COLOR_MUTED);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(190, 32));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(5, 8, 5, 8)));
        JButton btnSearch = makeButton("Find", COLOR_PRIMARY);
        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());
        searchBar.add(searchLabel);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        heading.add(searchBar, BorderLayout.EAST);
        topArea.add(heading);
        topArea.add(Box.createVerticalStrut(18));

        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setOpaque(false);
        cards.add(createStatCard("Candidates", lblCandidateCount, COLOR_PRIMARY));
        cards.add(createStatCard("Jobs", lblJobCount, COLOR_GREEN));
        cards.add(createStatCard("Interviews", lblInterviewCount, COLOR_AMBER));

        topRow.add(cards, BorderLayout.CENTER);
        topArea.add(topRow);
        main.add(topArea, BorderLayout.NORTH);

        // Center: table card
        JPanel tableCard = new JPanel(new BorderLayout(0, 8));
        tableCard.setBackground(COLOR_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(14, 14, 14, 14)));

        JPanel tableHeader = new JPanel(new BorderLayout(0, 10));
        tableHeader.setOpaque(false);

        lblTableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTableTitle.setForeground(COLOR_TEXT);
        tableHeader.add(lblTableTitle, BorderLayout.NORTH);

        btnAdd = makeButton("+ Add New", new Color(37, 99, 235));
        btnAdd.addActionListener(e -> handleAdd());
        btnSort = makeButton("Sort", new Color(8, 145, 178));
        btnSort.addActionListener(e -> handleSort());
        btnEdit = makeButton("Edit", new Color(234, 88, 12));
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete = makeButton("Delete", new Color(190, 38, 38));
        btnDelete.addActionListener(e -> handleDelete());
        btnStatus = makeButton("Update Status", new Color(30, 64, 85));
        btnStatus.addActionListener(e -> handleStatusUpdate());

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actionBtns.setBackground(new Color(248, 250, 249));
        actionBtns.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(8, 8, 8, 8)));
        JLabel actionLabel = new JLabel("ACTIONS");
        actionLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        actionLabel.setForeground(COLOR_MUTED);
        actionBtns.add(actionLabel);
        actionBtns.add(btnAdd);
        actionBtns.add(btnEdit);
        actionBtns.add(btnDelete);
        actionBtns.add(btnSort);
        actionBtns.add(btnStatus);
        tableHeader.add(actionBtns, BorderLayout.CENTER);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        mainTableModel = new DefaultTableModel();
        mainTable = new JTable(mainTableModel);
        mainTable.setBackground(COLOR_CARD_INNER);
        mainTable.setForeground(COLOR_TEXT);
        mainTable.setGridColor(COLOR_BORDER);
        mainTable.setRowHeight(28);
        mainTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mainTable.getTableHeader().setBackground(COLOR_CARD);
        mainTable.getTableHeader().setForeground(COLOR_MUTED);
        mainTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tableCard.add(new JScrollPane(mainTable), BorderLayout.CENTER);

        // Bottom: console
        consoleLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleLog.setEditable(false);
        consoleLog.setRows(3);
        consoleLog.setBackground(COLOR_CARD_INNER);
        consoleLog.setForeground(COLOR_MUTED);
        consoleLog.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane consoleScroll = new JScrollPane(consoleLog);
        consoleScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), "Console Log",
            0, 0, new Font("SansSerif", Font.BOLD, 11), COLOR_MUTED));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableCard, consoleScroll);
        split.setResizeWeight(0.75);
        split.setDividerSize(5);
        split.setBorder(null);
        main.add(split, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel countLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), new EmptyBorder(12, 16, 12, 16)));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.setForeground(COLOR_MUTED);

        countLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        countLabel.setForeground(accent);

        card.add(t, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton makeButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        return b;
    }

    // ==================== REFRESH VIEW ====================
    private void refreshView() {
        mainTableModel.setRowCount(0);
        mainTableModel.setColumnCount(0);

        // Show/hide context-sensitive buttons
        btnAdd.setVisible(true);
        btnSort.setVisible(currentView.equals("Candidates") || currentView.equals("Jobs"));
        btnEdit.setVisible(currentView.equals("Candidates") || currentView.equals("Jobs") || currentView.equals("Interviews"));
        btnDelete.setVisible(currentView.equals("Candidates") || currentView.equals("Jobs") || currentView.equals("Applications") || currentView.equals("Interviews"));
        btnStatus.setVisible(currentView.equals("Applications"));

        switch (currentView) {
            case "Candidates":
                btnAdd.setText("+ Add New");
                lblTableTitle.setText("Registered Candidates");
                mainTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Email", "Qualification", "Skills", "Exp", "Salary (₹)"});
                for (Candidate c : candidates)
                    mainTableModel.addRow(new Object[]{c.id, c.name, c.email, c.qualification, String.join(", ", c.skills), c.experience + " yrs", "₹" + c.expectedSalary});
                break;
            case "Jobs":
                btnAdd.setText("+ Add New");
                lblTableTitle.setText("Job Vacancies");
                mainTableModel.setColumnIdentifiers(new String[]{"ID", "Title", "Required Skills", "Salary (₹)"});
                for (Job j : jobs.values())
                    mainTableModel.addRow(new Object[]{j.id, j.title, String.join(", ", j.requiredSkills), "₹" + j.salary});
                break;
            case "Applications":
                btnAdd.setText("+ Apply");
                lblTableTitle.setText("Job Applications");
                mainTableModel.setColumnIdentifiers(new String[]{"App ID", "Candidate", "Job", "Status"});
                for (Application a : applications.values())
                    mainTableModel.addRow(new Object[]{a.appId, a.candidate.name, a.job.title, a.status});
                break;
            case "Interviews":
                btnAdd.setText("+ Schedule");
                lblTableTitle.setText("Scheduled Interviews");
                mainTableModel.setColumnIdentifiers(new String[]{"ID", "Date", "Candidate", "Position"});
                for (Interview iv : interviews)
                    mainTableModel.addRow(new Object[]{iv.interviewId, iv.date, iv.application.candidate.name, iv.application.job.title});
                break;
            case "Shortlisting":
                btnAdd.setText("Select Job");
                lblTableTitle.setText("Candidate Shortlisting — Click 'Select Job' to find matching candidates");
                mainTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Qualification", "Skills", "Experience", "Salary (₹)", "Skill Match"});
                btnSort.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
                break;
            case "Report":
                btnAdd.setVisible(false);
                btnSort.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
                lblTableTitle.setText("Recruitment Report");
                mainTableModel.setColumnIdentifiers(new String[]{"Metric", "Value"});
                generateReport();
                break;
        }
        centerTableCells();
        updateStats();
    }

    private void centerTableCells() {
        DefaultTableCellRenderer c = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < mainTable.getColumnCount(); i++)
            mainTable.getColumnModel().getColumn(i).setCellRenderer(c);
    }

    private void updateStats() {
        lblCandidateCount.setText(String.valueOf(candidates.size()));
        lblJobCount.setText(String.valueOf(jobs.size()));
        lblInterviewCount.setText(String.valueOf(interviews.size()));
    }

    // ==================== ACTIONS ====================
    private void handleAdd() {
        switch (currentView) {
            case "Candidates": addCandidate(); break;
            case "Jobs": addJob(); break;
            case "Applications": submitApplication(); break;
            case "Interviews": scheduleInterview(); break;
            case "Shortlisting": doShortlisting(); break;
            case "Report": break;
        }
    }

    private void handleSort() {
        switch (currentView) {
            case "Candidates":
                String[] opts = {"Experience (High to Low)", "Salary (Low to High)", "Name (A-Z)"};
                int choice = JOptionPane.showOptionDialog(this, "Sort by:", "Sort Candidates", 0, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
                if (choice == 0) candidates.sort((a, b) -> b.experience - a.experience);
                else if (choice == 1) candidates.sort(Comparator.comparingDouble(c -> c.expectedSalary));
                else if (choice == 2) candidates.sort(Comparator.comparing(c -> c.name));
                break;
            case "Jobs":
                String[] jobOpts = {"Job ID (A-Z)", "Salary (High to Low)", "Salary (Low to High)"};
                int jc = JOptionPane.showOptionDialog(this, "Sort by:", "Sort Jobs", 0, JOptionPane.QUESTION_MESSAGE, null, jobOpts, jobOpts[0]);
                if (jc >= 0) {
                    ArrayList<Job> sortedJobs = new ArrayList<>(jobs.values());
                    if (jc == 1) sortedJobs.sort((a, b) -> Double.compare(b.salary, a.salary));
                    else if (jc == 2) sortedJobs.sort(Comparator.comparingDouble(j -> j.salary));
                    mainTableModel.setRowCount(0);
                    for (Job j : sortedJobs)
                        mainTableModel.addRow(new Object[]{j.id, j.title, String.join(", ", j.requiredSkills), "₹" + j.salary});
                    centerTableCells();
                    log("Sorted Jobs by " + jobOpts[jc] + ".");
                    return;
                }
                break;
        }
        refreshView();
        log("Sorted " + currentView + ".");
    }

    // ==================== CANDIDATE REGISTRATION (with Validation) ====================
    private void addCandidate() {
        JTextField id = new JTextField(String.valueOf(candidates.size() + 1));
        JTextField name = new JTextField(), email = new JTextField(), qual = new JTextField();
        JTextField skills = new JTextField("Java, SQL"), exp = new JTextField("2"), sal = new JTextField("75000");

        JPanel f = new JPanel(new GridLayout(7, 2, 5, 5));
        f.add(new JLabel("ID:")); f.add(id);
        f.add(new JLabel("Name:")); f.add(name);
        f.add(new JLabel("Email:")); f.add(email);
        f.add(new JLabel("Qualification:")); f.add(qual);
        f.add(new JLabel("Skills (comma sep):")); f.add(skills);
        f.add(new JLabel("Experience (yrs):")); f.add(exp);
        f.add(new JLabel("Expected Salary (₹):")); f.add(sal);

        if (JOptionPane.showConfirmDialog(this, f, "Add Candidate", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String idVal = id.getText().trim();
                String nameVal = name.getText().trim();
                String emailVal = email.getText().trim();
                String qualVal = qual.getText().trim();

                // Validation: empty fields
                if (idVal.isEmpty()) throw new IllegalArgumentException("Candidate ID cannot be empty.");
                if (nameVal.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (emailVal.isEmpty() || !emailVal.contains("@")) throw new IllegalArgumentException("Invalid email format (must contain @).");
                if (qualVal.isEmpty()) throw new IllegalArgumentException("Qualification cannot be empty.");
                if (skills.getText().trim().isEmpty()) throw new IllegalArgumentException("Skills cannot be empty.");

                // Validation: duplicate ID
                for (Candidate c : candidates) {
                    if (c.id.equals(idVal)) throw new IllegalArgumentException("Candidate ID '" + idVal + "' already exists.");
                }

                // Validation: numeric fields
                int expVal = Integer.parseInt(exp.getText().trim());
                double salVal = Double.parseDouble(sal.getText().trim());
                if (expVal < 0) throw new IllegalArgumentException("Experience cannot be negative.");
                if (!Double.isFinite(salVal) || salVal < 0) throw new IllegalArgumentException("Salary must be a finite, non-negative number.");

                String[] sk = parseSkills(skills.getText(), "Skills");

                candidates.add(new Candidate(idVal, nameVal, emailVal, qualVal, sk, expVal, salVal));
                refreshView();
                log("Added candidate: " + nameVal);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Experience and Salary must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== JOB MANAGEMENT (with Validation) ====================
    private void addJob() {
        JTextField id = new JTextField(String.valueOf(jobs.size() + 1)), title = new JTextField();
        JTextField skills = new JTextField("Java"), sal = new JTextField("80000");

        JPanel f = new JPanel(new GridLayout(4, 2, 5, 5));
        f.add(new JLabel("ID:")); f.add(id);
        f.add(new JLabel("Title:")); f.add(title);
        f.add(new JLabel("Required Skills:")); f.add(skills);
        f.add(new JLabel("Salary (₹):")); f.add(sal);

        if (JOptionPane.showConfirmDialog(this, f, "Post Job", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String idVal = id.getText().trim();
                String titleVal = title.getText().trim();

                // Validation: empty fields
                if (idVal.isEmpty()) throw new IllegalArgumentException("Job ID cannot be empty.");
                if (titleVal.isEmpty()) throw new IllegalArgumentException("Job title cannot be empty.");
                if (skills.getText().trim().isEmpty()) throw new IllegalArgumentException("Required skills cannot be empty.");

                // Validation: duplicate ID
                if (jobs.containsKey(idVal)) throw new IllegalArgumentException("Job ID '" + idVal + "' already exists.");

                // Validation: numeric field
                double salVal = Double.parseDouble(sal.getText().trim());
                if (!Double.isFinite(salVal) || salVal < 0) throw new IllegalArgumentException("Salary must be a finite, non-negative number.");

                String[] sk = parseSkills(skills.getText(), "Required skills");

                Job j = new Job(idVal, titleVal, sk, salVal);
                jobs.put(j.id, j);
                refreshView();
                log("Posted job: " + titleVal);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== APPLICATION MANAGEMENT ====================
    private void submitApplication() {
        try {
            if (candidates.isEmpty() || jobs.isEmpty()) throw new InvalidApplicationException("Add candidates and jobs first!");
            JComboBox<Candidate> cc = new JComboBox<>(candidates.toArray(new Candidate[0]));
            JComboBox<Job> jc = new JComboBox<>(jobs.values().toArray(new Job[0]));
            JPanel f = new JPanel(new GridLayout(2, 2, 8, 8));
            f.add(new JLabel("Candidate:")); f.add(cc);
            f.add(new JLabel("Job:")); f.add(jc);
            if (JOptionPane.showConfirmDialog(this, f, "Apply for Job", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            Candidate c = (Candidate) cc.getSelectedItem();
            Job j = (Job) jc.getSelectedItem();
            if (c.experience < 1) throw new InvalidApplicationException("Candidate needs at least 1 year of experience.");

            // Validation: duplicate application
            for (Application existing : applications.values()) {
                if (existing.candidate.id.equals(c.id) && existing.job.id.equals(j.id))
                    throw new InvalidApplicationException("This candidate has already applied for this job.");
            }

            String newId = nextApplicationId();
            applications.put(newId, new Application(newId, c, j));
            refreshView();
            log("Application submitted: " + c.name + " -> " + j.title);
        } catch (InvalidApplicationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== INTERVIEW SCHEDULING ====================
    private void scheduleInterview() {
        try {
            if (applications.isEmpty()) throw new InvalidInterviewException("Submit an application first!");
            JComboBox<Application> ac = new JComboBox<>(applications.values().toArray(new Application[0]));
            JTextField date = new JTextField("2026-10-15");
            JPanel f = new JPanel(new GridLayout(2, 2, 8, 8));
            f.add(new JLabel("Application:")); f.add(ac);
            f.add(new JLabel("Date (YYYY-MM-DD):")); f.add(date);
            if (JOptionPane.showConfirmDialog(this, f, "Schedule Interview", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            Application app = (Application) ac.getSelectedItem();
            String dateVal = date.getText().trim();
            validateDate(dateVal);
            String id = nextInterviewId();
            interviews.add(new Interview(id, app, dateVal));
            app.status = "Interview Scheduled";
            refreshView();
            log("Interview scheduled: " + app.candidate.name + " on " + dateVal);
        } catch (InvalidInterviewException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== EDIT (Update) ====================
    private void handleEdit() {
        int row = mainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        switch (currentView) {
            case "Candidates": editCandidate(row); break;
            case "Jobs": editJob(row); break;
            case "Interviews": editInterview(row); break;
        }
    }

    private void editCandidate(int row) {
        Candidate c = candidateAtRow(row);
        if (c == null) return;
        JTextField name = new JTextField(c.name), email = new JTextField(c.email), qual = new JTextField(c.qualification);
        JTextField skills = new JTextField(String.join(", ", c.skills));
        JTextField exp = new JTextField(String.valueOf(c.experience)), sal = new JTextField(String.valueOf(c.expectedSalary));

        JPanel f = new JPanel(new GridLayout(6, 2, 5, 5));
        f.add(new JLabel("Name:")); f.add(name);
        f.add(new JLabel("Email:")); f.add(email);
        f.add(new JLabel("Qualification:")); f.add(qual);
        f.add(new JLabel("Skills (comma sep):")); f.add(skills);
        f.add(new JLabel("Experience (yrs):")); f.add(exp);
        f.add(new JLabel("Expected Salary (₹):")); f.add(sal);

        if (JOptionPane.showConfirmDialog(this, f, "Edit Candidate: " + c.name, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String nameVal = name.getText().trim();
                String emailVal = email.getText().trim();
                String qualVal = qual.getText().trim();
                if (nameVal.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                if (emailVal.isEmpty() || !emailVal.contains("@")) throw new IllegalArgumentException("Invalid email format.");
                if (qualVal.isEmpty()) throw new IllegalArgumentException("Qualification cannot be empty.");

                int expVal = Integer.parseInt(exp.getText().trim());
                double salVal = Double.parseDouble(sal.getText().trim());
                if (expVal < 0) throw new IllegalArgumentException("Experience cannot be negative.");
                if (!Double.isFinite(salVal) || salVal < 0) throw new IllegalArgumentException("Salary must be a finite, non-negative number.");
                String[] sk = parseSkills(skills.getText(), "Skills");

                c.name = nameVal;
                c.email = emailVal;
                c.qualification = qualVal;
                c.skills = sk;
                c.experience = expVal;
                c.expectedSalary = salVal;
                refreshView();
                log("Updated candidate: " + c.name);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Experience and Salary must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editJob(int row) {
        Job j = jobAtRow(row);
        if (j == null) return;
        JTextField title = new JTextField(j.title);
        JTextField skills = new JTextField(String.join(", ", j.requiredSkills));
        JTextField sal = new JTextField(String.valueOf(j.salary));

        JPanel f = new JPanel(new GridLayout(3, 2, 5, 5));
        f.add(new JLabel("Title:")); f.add(title);
        f.add(new JLabel("Required Skills:")); f.add(skills);
        f.add(new JLabel("Salary (₹):")); f.add(sal);

        if (JOptionPane.showConfirmDialog(this, f, "Edit Job: " + j.title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String titleVal = title.getText().trim();
                if (titleVal.isEmpty()) throw new IllegalArgumentException("Job title cannot be empty.");
                String[] sk = parseSkills(skills.getText(), "Required skills");
                double salVal = Double.parseDouble(sal.getText().trim());
                if (!Double.isFinite(salVal) || salVal < 0) throw new IllegalArgumentException("Salary must be a finite, non-negative number.");

                j.title = titleVal;
                j.requiredSkills = sk;
                j.salary = salVal;
                refreshView();
                log("Updated job: " + j.title);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editInterview(int row) {
        Interview iv = interviewAtRow(row);
        if (iv == null) return;
        JTextField date = new JTextField(iv.date);

        JPanel f = new JPanel(new GridLayout(1, 2, 5, 5));
        f.add(new JLabel("Date (YYYY-MM-DD):")); f.add(date);

        if (JOptionPane.showConfirmDialog(this, f, "Edit Interview: " + iv.application.candidate.name, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String dateVal = date.getText().trim();
            try {
                validateDate(dateVal);
            } catch (InvalidInterviewException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            iv.date = dateVal;
            refreshView();
            log("Updated interview date for " + iv.application.candidate.name + " to " + dateVal);
        }
    }

    // ==================== DELETE ====================
    private void handleDelete() {
        int row = mainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        switch (currentView) {
            case "Candidates":
                Candidate c = candidateAtRow(row);
                if (c == null) return;
                candidates.remove(c);
                applications.values().removeIf(a -> a.candidate == c);
                interviews.removeIf(iv -> iv.application.candidate == c);
                log("Deleted candidate: " + c.name);
                break;
            case "Jobs":
                Job j = jobAtRow(row);
                if (j == null) return;
                jobs.remove(j.id);
                applications.values().removeIf(a -> a.job == j);
                interviews.removeIf(iv -> iv.application.job == j);
                log("Deleted job: " + j.title);
                break;
            case "Applications":
                Application a = applicationAtRow(row);
                if (a == null) return;
                applications.remove(a.appId);
                interviews.removeIf(iv -> iv.application == a);
                log("Deleted application: " + a.appId + " (" + a.candidate.name + " -> " + a.job.title + ")");
                break;
            case "Interviews":
                Interview iv = interviews.remove(row);
                log("Deleted interview: " + iv.interviewId + " (" + iv.application.candidate.name + ")");
                break;
        }
        refreshView();
    }

    // ==================== APPLICATION STATUS UPDATE ====================
    private void handleStatusUpdate() {
        int row = mainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Application a = applicationAtRow(row);
        if (a == null) return;
        String[] statuses = {"Under Review", "Shortlisted", "Interview Scheduled", "Accepted", "Rejected"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Update status for:\n" + a.candidate.name + " → " + a.job.title,
            "Update Application Status", JOptionPane.PLAIN_MESSAGE, null, statuses, a.status);
        if (newStatus != null) {
            a.status = newStatus;
            refreshView();
            log("Application " + a.appId + " status updated to: " + newStatus);
        }
    }

    // ==================== CANDIDATE SHORTLISTING ====================
    private void doShortlisting() {
        if (jobs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No jobs available. Add jobs first.", "No Jobs", JOptionPane.INFORMATION_MESSAGE);
            log("Shortlisting failed — no jobs available.");
            return;
        }
        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No candidates available. Add candidates first.", "No Candidates", JOptionPane.INFORMATION_MESSAGE);
            log("Shortlisting failed — no candidates available.");
            return;
        }

        Job[] jobArray = jobs.values().toArray(new Job[0]);
        Job selectedJob = (Job) JOptionPane.showInputDialog(this,
            "Select a job to find matching candidates:",
            "Candidate Shortlisting", JOptionPane.PLAIN_MESSAGE, null, jobArray, jobArray[0]);
        if (selectedJob == null) return;

        mainTableModel.setRowCount(0);
        mainTableModel.setColumnCount(0);
        mainTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Qualification", "Skills", "Experience", "Salary (₹)", "Skill Match"});
        lblTableTitle.setText("Shortlisted Candidates for: " + selectedJob.title);

        // Sort candidates by match count (descending) for display
        ArrayList<Object[]> matchedRows = new ArrayList<>();
        for (Candidate c : candidates) {
            int matchCount = 0;
            for (String reqSkill : selectedJob.requiredSkills) {
                for (String cSkill : c.skills) {
                    if (cSkill.trim().equalsIgnoreCase(reqSkill.trim())) {
                        matchCount++;
                        break;
                    }
                }
            }
            if (matchCount > 0) {
                String matchStr = matchCount + "/" + selectedJob.requiredSkills.length + " skills";
                matchedRows.add(new Object[]{c.id, c.name, c.qualification, String.join(", ", c.skills), c.experience + " yrs", "₹" + c.expectedSalary, matchStr});
            }
        }

        // Sort by match count descending (parse from the matchStr)
        matchedRows.sort((a, b) -> {
            int ma = Integer.parseInt(((String) a[6]).split("/")[0]);
            int mb = Integer.parseInt(((String) b[6]).split("/")[0]);
            return mb - ma;
        });

        for (Object[] row : matchedRows) mainTableModel.addRow(row);
        centerTableCells();
        log("Shortlisting for \"" + selectedJob.title + "\" — " + mainTableModel.getRowCount() + " candidates match.");
    }

    // ==================== RECRUITMENT REPORT ====================
    private void generateReport() {
        // Count application statuses
        int underReview = 0, shortlisted = 0, interviewScheduled = 0, accepted = 0, rejected = 0;
        for (Application a : applications.values()) {
            switch (a.status) {
                case "Under Review": underReview++; break;
                case "Shortlisted": shortlisted++; break;
                case "Interview Scheduled": interviewScheduled++; break;
                case "Accepted": accepted++; break;
                case "Rejected": rejected++; break;
            }
        }

        // Compute averages
        double avgExp = 0, avgSalary = 0;
        if (!candidates.isEmpty()) {
            for (Candidate c : candidates) { avgExp += c.experience; avgSalary += c.expectedSalary; }
            avgExp /= candidates.size();
            avgSalary /= candidates.size();
        }

        // Populate report table
        mainTableModel.addRow(new Object[]{"═══ OVERVIEW ═══", ""});
        mainTableModel.addRow(new Object[]{"Total Candidates", candidates.size()});
        mainTableModel.addRow(new Object[]{"Total Job Vacancies", jobs.size()});
        mainTableModel.addRow(new Object[]{"Total Applications", applications.size()});
        mainTableModel.addRow(new Object[]{"Total Interviews Scheduled", interviews.size()});
        mainTableModel.addRow(new Object[]{"", ""});
        mainTableModel.addRow(new Object[]{"═══ APPLICATION STATUS BREAKDOWN ═══", ""});
        mainTableModel.addRow(new Object[]{"Under Review", underReview});
        mainTableModel.addRow(new Object[]{"Shortlisted", shortlisted});
        mainTableModel.addRow(new Object[]{"Interview Scheduled", interviewScheduled});
        mainTableModel.addRow(new Object[]{"Accepted", accepted});
        mainTableModel.addRow(new Object[]{"Rejected", rejected});
        mainTableModel.addRow(new Object[]{"", ""});
        mainTableModel.addRow(new Object[]{"═══ CANDIDATE STATISTICS ═══", ""});
        mainTableModel.addRow(new Object[]{"Average Experience", String.format("%.1f yrs", avgExp)});
        mainTableModel.addRow(new Object[]{"Average Expected Salary", "₹" + String.format("%.0f", avgSalary)});

        log("Recruitment report generated.");
    }

    // ==================== SEARCH ====================
    private void doSearch() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) { refreshView(); return; }

        mainTableModel.setRowCount(0);
        mainTableModel.setColumnCount(0);
        mainTableModel.setColumnIdentifiers(new String[]{"Type", "ID", "Name/Title", "Details", "Exp", "Salary (₹)"});
        lblTableTitle.setText("Search Results: \"" + txtSearch.getText().trim() + "\"");

        for (Candidate c : candidates) {
            if (c.name.toLowerCase().contains(q) || c.qualification.toLowerCase().contains(q) || String.join(" ", c.skills).toLowerCase().contains(q))
                mainTableModel.addRow(new Object[]{"Candidate", c.id, c.name, c.qualification + " | " + String.join(", ", c.skills), c.experience + " yrs", "₹" + c.expectedSalary});
        }
        for (Job j : jobs.values()) {
            if (j.title.toLowerCase().contains(q) || String.join(" ", j.requiredSkills).toLowerCase().contains(q))
                mainTableModel.addRow(new Object[]{"Job", j.id, j.title, String.join(", ", j.requiredSkills), "-", "₹" + j.salary});
        }
        centerTableCells();
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnStatus.setVisible(false);
        log("Search for \"" + q + "\" — " + mainTableModel.getRowCount() + " results.");
    }

    private Candidate candidateAtRow(int row) {
        Object id = mainTableModel.getValueAt(mainTable.convertRowIndexToModel(row), 0);
        for (Candidate candidate : candidates) if (candidate.id.equals(String.valueOf(id))) return candidate;
        return null;
    }

    private Job jobAtRow(int row) {
        Object id = mainTableModel.getValueAt(mainTable.convertRowIndexToModel(row), 0);
        return jobs.get(String.valueOf(id));
    }

    private Application applicationAtRow(int row) {
        Object id = mainTableModel.getValueAt(mainTable.convertRowIndexToModel(row), 0);
        return applications.get(String.valueOf(id));
    }

    private Interview interviewAtRow(int row) {
        Object id = mainTableModel.getValueAt(mainTable.convertRowIndexToModel(row), 0);
        for (Interview interview : interviews) if (interview.interviewId.equals(String.valueOf(id))) return interview;
        return null;
    }

    private String[] parseSkills(String value, String fieldName) {
        String[] skills = Arrays.stream(value.split(","))
            .map(String::trim)
            .filter(skill -> !skill.isEmpty())
            .toArray(String[]::new);
        if (skills.length == 0) throw new IllegalArgumentException(fieldName + " cannot be empty.");
        return skills;
    }

    private void validateDate(String value) throws InvalidInterviewException {
        if (value.isEmpty()) throw new InvalidInterviewException("Date cannot be empty.");
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new InvalidInterviewException("Date must be a valid date in YYYY-MM-DD format.");
        }
    }

    private String nextApplicationId() {
        return nextNumericId(applications.keySet());
    }

    private String nextInterviewId() {
        ArrayList<String> ids = new ArrayList<>();
        for (Interview interview : interviews) ids.add(interview.interviewId);
        return nextNumericId(ids);
    }

    private String nextNumericId(Collection<String> ids) {
        int next = 1;
        for (String id : ids) {
            try {
                next = Math.max(next, Integer.parseInt(id) + 1);
            } catch (NumberFormatException ignored) {
            }
        }
        return String.valueOf(next);
    }

    private void log(String msg) {
        consoleLog.append("> " + msg + "\n");
        consoleLog.setCaretPosition(consoleLog.getDocument().getLength());
    }
}
