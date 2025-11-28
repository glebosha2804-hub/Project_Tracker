package tasktracker;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskManager is responsible for storing and managing ALL tasks in the app.
 *
 * It acts as the "backend" of your task system.
 * The GUI (TaskTrackerGUI) depends on this class to:
 *   - Add tasks
 *   - Remove tasks
 *   - Mark tasks complete
 *   - Retrieve the current list of tasks
 *   - Get stats (completed %, counts, etc.)
 *
 * This class contains NO GUI code — only task data logic.
 */
public class TaskManager {

    /**
     * The internal list that holds all Task objects.
     * It is private so only TaskManager can modify it directly.
     */
    private final List<Task> tasks = new ArrayList<>();


    /**
     * Adds a full Task object to the list.
     *
     * Used when:
     *  - The user creates a task in TaskDialog
     *  - The GUI passes the completed Task object here
     */
    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    /**
     * Convenience method: add a task using ONLY a title.
     *
     * Not used by the GUI now (since the GUI uses TaskDialog),
     * but still helpful for quick creation or testing.
     */
    public void addTask(String title) {
        if (title == null || title.trim().isEmpty()) {
            return; // ignore empty task names
        }
        tasks.add(new Task(title.trim()));
    }

    /**
     * Removes a task from the list.
     *
     * Called when the user presses "Delete Task".
     */
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Marks a task as completed.
     *
     * The task isn't removed — only its state changes.
     * GUI uses this to update color + progress bar.
     */
    public void markComplete(Task task) {
        if (task != null) {
            task.setCompleted(true);
        }
    }

    /**
     * Returns a COPY of the task list.
     *
     * Why a copy?
     *  - Prevents the GUI from directly modifying the internal list
     *  - Safer — enforces proper encapsulation
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns the total number of tasks.
     *
     * Used by the GUI for displaying stats.
     */
    public int getTotalCount() {
        return tasks.size();
    }

    /**
     * Counts how many tasks are completed.
     *
     * Used to calculate percentage + dashboard text.
     */
    public int getCompletedCount() {
        int count = 0;
        for (Task t : tasks) {
            if (t.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns progress as a percentage (0–100).
     *
     * Used by the GUI to update the progress bar.
     */
    public double getCompletionPercent() {
        int total = getTotalCount();

        if (total == 0)
            return 0.0;  // avoid division by zero

        return (getCompletedCount() * 100.0) / total;
    }
}
