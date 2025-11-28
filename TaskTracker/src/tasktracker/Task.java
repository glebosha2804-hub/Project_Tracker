package tasktracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The Task class represents a single task in the system.
 * It stores all information about a task, such as:
 *  - title
 *  - who it is for (assignee)
 *  - due date
 *  - type/category
 *  - priority (LOW, MEDIUM, HIGH)
 *  - completed or not
 *
 * This is the "data model" for one task.
 * It does NOT know anything about the GUI — just data.
 */
public class Task {

    /**
     * Enum representing priority levels.
     * Enums are great for fixed sets of values.
     */
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    // ==== Task fields (data) ====
    private String title;           // Task title or name
    private String assignee;        // Who the task is for
    private LocalDate dueDate;      // When the task is due
    private String type;            // Category (school, work, etc.)
    private Priority priority;      // Priority of the task
    private boolean completed;      // Has the task been completed?

    /**
     * Constructor when only a title is given.
     * Other fields get default values.
     */
    public Task(String title) {
        // Calls the full constructor below
        this(title, "", null, "", Priority.MEDIUM);
    }

    /**
     * Full constructor that sets all fields.
     * This allows creating tasks with detailed info.
     */
    public Task(String title, String assignee, LocalDate dueDate,
                String type, Priority priority) {

        this.title = title;

        // If any optional field is null, give it a safe default
        this.assignee = assignee != null ? assignee : "";
        this.dueDate = dueDate;  // can be null if user selects "no due date"
        this.type = type != null ? type : "";
        this.priority = (priority != null) ? priority : Priority.MEDIUM;

        this.completed = false; // newly created tasks start as NOT completed
    }

    // ==== GETTERS ====
    // These return information about the task.
    public String getTitle() { return title; }
    public String getAssignee() { return assignee; }
    public LocalDate getDueDate() { return dueDate; }
    public String getType() { return type; }
    public Priority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    // ==== SETTERS ====
    // These update the task (used when editing).
    public void setTitle(String title) { this.title = title; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setType(String type) { this.type = type; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    /**
     * Utility method:
     * Converts the due date to a "yyyy-MM-dd" string.
     * If the task has no due date, return empty string.
     */
    public String getDueDateAsString() {
        if (dueDate == null) return "";
        return dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Tooltip text displayed when hovering over a task in the list.
     * Uses HTML so that we can use line breaks (<br>) and formatting (<b>).
     */
    public String getTooltipText() {
        StringBuilder sb = new StringBuilder("<html>");

        sb.append("<b>").append(title).append("</b><br>");
        sb.append("Priority: ").append(priority).append("<br>");

        if (!assignee.isBlank()) {
            sb.append("For: ").append(assignee).append("<br>");
        }

        if (dueDate != null) {
            sb.append("Due: ").append(getDueDateAsString()).append("<br>");
        }

        if (!type.isBlank()) {
            sb.append("Type: ").append(type).append("<br>");
        }

        sb.append("Status: ")
          .append(completed ? "Completed" : "Pending");

        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Text shown in the main task list.
     *
     * Example:
     *   ✔ [HIGH] Finish Project (Due: 2025-02-14) - For: John [School]
     *
     * This string is only for display purposes.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Completed = ✔  | Not completed = ✘
        sb.append(completed ? "✔ " : "✘ ");

        // Show priority
        sb.append("[").append(priority).append("] ");

        // Title always shown
        sb.append(title);

        // Optional fields shown only if present
        if (dueDate != null) {
            sb.append(" (Due: ").append(getDueDateAsString()).append(")");
        }

        if (!assignee.isBlank()) {
            sb.append(" - For: ").append(assignee);
        }

        if (!type.isBlank()) {
            sb.append(" [").append(type).append("]");
        }

        return sb.toString();
    }
}
