# Recruit Pro - Job Recruitment Management System

Recruit Pro is a Java Swing desktop application for managing a simple
recruitment workflow. It provides one workspace for maintaining candidates,
job vacancies, applications, interviews, candidate shortlisting, and
recruitment statistics.

## Features

- Candidate management
  - Add candidates with contact information, qualification, skills,
    experience, and expected salary
  - Edit and delete candidates
  - Validate required fields, duplicate IDs, email format, experience, and
    salary values
- Job management
  - Add job vacancies with required skills and salary
  - Edit and delete jobs
  - Sort jobs by supported fields
  - Validate required fields, duplicate IDs, and salary values
- Application management
  - Apply an existing candidate to an existing job
  - Prevent duplicate candidate-job applications
  - Require at least one year of candidate experience
  - Update application status:
    `Under Review`, `Shortlisted`, `Interview Scheduled`, `Accepted`, or
    `Rejected`
- Interview scheduling
  - Schedule interviews for submitted applications
  - Validate dates using `YYYY-MM-DD` format
  - Automatically update the application status to `Interview Scheduled`
- Candidate shortlisting
  - Select a job and find candidates whose skills match the job requirements
  - Sort matching candidates by the number of matching skills
- Search
  - Search candidates, jobs, applications, and interviews from the workspace
- Recruitment report
  - Show totals for candidates, jobs, applications, and interviews
  - Show application status counts
  - Show average candidate experience and expected salary
- Console log
  - Display important actions and validation-related messages inside the GUI

## Requirements

- Java Development Kit (JDK) 8 or later
- A desktop environment that supports Java Swing

The application does not require an external database or third-party
dependency. All data is stored in memory while the application is running.

## Project structure

```text
FINAL PROJECT JAVA/
├── .gitignore
├── .vscode/
│   └── settings.json
├── README.md
└── src/
    ├── Application.java
    ├── Candidate.java
    ├── Interview.java
    ├── InvalidApplicationException.java
    ├── InvalidInterviewException.java
    ├── Job.java
    ├── Main.java
    ├── RecruitmentData.java
    └── RecruitmentGUI.java
```

Generated `.class` files, JAR files, and operating-system metadata are
excluded from version control by [`.gitignore`](.gitignore).

## Class overview

### `Main`

The application entry point. It starts `RecruitmentGUI` on Swing's Event
Dispatch Thread using `SwingUtilities.invokeLater`.

### `RecruitmentGUI`

The main `JFrame` and user interface. It builds the navigation bar, dashboard
cards, data table, action buttons, search field, and console log. It also
contains the event handlers for adding, editing, deleting, sorting, searching,
shortlisting, reporting, and status updates.

### `RecruitmentData`

Holds the in-memory collections used by the GUI:

- `ArrayList<Candidate>` for candidates
- `TreeMap<String, Job>` for jobs
- `HashMap<String, Application>` for applications
- `LinkedList<Interview>` for interviews

### `Candidate`

Represents a candidate with an ID, name, email, qualification, skills,
experience, and expected salary.

### `Job`

Represents a vacancy with an ID, title, required skills, and salary.

### `Application`

Connects one candidate to one job and stores the application's current
status.

### `Interview`

Connects an interview ID and date to an application.

### Exception classes

- `InvalidApplicationException` reports invalid application operations.
- `InvalidInterviewException` reports invalid interview operations.

## Compile and run

Open a terminal in the project directory:

```bash
cd "/Users/shubhsaraswat/Documents/FINAL PROJECT JAVA"
```

Compile all source files into a separate `bin` directory:

```bash
mkdir -p bin
javac -d bin src/*.java
```

Start the application:

```bash
java -cp bin Main
```

On Windows, the equivalent commands are:

```bat
mkdir bin
javac -d bin src\*.java
java -cp bin Main
```

## Initial sample data

When the application starts, it loads sample data containing:

- Four candidates
- Three job vacancies
- One application
- One scheduled interview

The sample data is created by `RecruitmentGUI.loadSampleData()`. Since the
current version uses in-memory collections, changes are cleared when the
application closes or restarts.

## Typical workflow

1. Open the **Candidates** view and add or edit candidate records.
2. Open **Jobs** and add available vacancies.
3. Open **Applications** and apply a candidate to a job.
4. Update the application status as the recruitment process progresses.
5. Use **Shortlisting** to compare candidate skills with a job's requirements.
6. Open **Interviews** to schedule an interview for an application.
7. Use **Report** to review recruitment totals and summary statistics.
8. Use the search box to find matching records across the current data set.

## Validation rules

- Candidate and job IDs must not be empty or duplicated.
- Candidate names, qualifications, job titles, and skill lists are required.
- Candidate emails must contain `@`.
- Experience cannot be negative.
- Salaries must be finite, non-negative numbers.
- A candidate must have at least one year of experience to apply.
- A candidate cannot apply to the same job more than once.
- An interview can only be scheduled for an existing application.
- Interview dates must use the `YYYY-MM-DD` format and be valid calendar dates.

## Data and persistence

This project currently has no database or file-based persistence layer.
`RecruitmentData` stores all records in memory, which keeps the project
lightweight and easy to run but means data is not retained between sessions.

Possible future improvements include:

- Saving and loading records from JSON, CSV, or a relational database
- Adding user authentication and recruiter roles
- Adding confirmation dialogs before destructive operations
- Exporting reports
- Adding automated tests for validation and recruitment workflows

## Version control

The project is hosted on GitHub:

<https://github.com/shubhsaraswat1414-lgtm/final-project-java>

To publish future changes:

```bash
git add .
git commit -m "Describe your change"
git push
```

## License

No license has been specified for this project yet.
