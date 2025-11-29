package tasktracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskManager class.
 * 
 * Each test checks one specific behavior:
 *  - adding tasks
 *  - removing tasks
 *  - marking as complete
 *  - copying list safely
 *  - calculating stats
 */
class TaskManagerTest {

    /**
     * Tests adding a real Task object.
     * The list should contain exactly 1 task after adding.
     */
    @Test
    void testAddTaskObject() {
        TaskManager manager = new TaskManager();
        Task task = new Task("Test");

        manager.addTask(task);

        assertEquals(1, manager.getTotalCount());
        assertEquals("Test", manager.getTasks().get(0).getTitle());
    }

    /**
     * Tests that adding a null Task does nothing (should be ignored).
     */
    @Test
    void testAddTaskObject_NullIgnored() {
        TaskManager manager = new TaskManager();

        manager.addTask((Task) null);

        assertEquals(0, manager.getTotalCount());
    }

    /**
     * Tests adding a task *by title only*.
     * TaskManager should automatically create a Task object.
     */
    @Test
    void testAddTaskByTitle() {
        TaskManager manager = new TaskManager();

        manager.addTask("Homework");

        assertEquals(1, manager.getTotalCount());
        assertEquals("Homework", manager.getTasks().get(0).getTitle());
    }

    /**
     * Tests that empty or blank titles are ignored.
     */
    @Test
    void testAddTaskByTitle_EmptyIgnored() {
        TaskManager manager = new TaskManager();

        manager.addTask("");
        manager.addTask("   "); // spaces only

        assertEquals(0, manager.getTotalCount());
    }

    /**
     * Tests removing a task from the list.
     * After removing, list size must be 0.
     */
    @Test
    void testRemoveTask() {
        TaskManager manager = new TaskManager();
        Task t = new Task("To remove");

        manager.addTask(t);
        manager.removeTask(t);

        assertEquals(0, manager.getTotalCount());
    }

    /**
     * Tests marking a task as completed.
     * The task's completed flag should become true.
     */
    @Test
    void testMarkComplete() {
        TaskManager manager = new TaskManager();
        Task t = new Task("Test");

        manager.addTask(t);
        manager.markComplete(t);

        assertTrue(t.isCompleted());
    }

    /**
     * Tests that getTasks() returns a *copy* of the list.
     * Modifying the returned list must NOT modify the original.
     */
    @Test
    void testGetTasksReturnsCopy() {
        TaskManager manager = new TaskManager();
        manager.addTask("Test");

        List<Task> copy = manager.getTasks();
        copy.clear(); // modify the copy

        assertEquals(1, manager.getTotalCount(), "Original list must not change");
    }

    /**
     * Tests counting completed tasks.
     * Only tasks manually marked as completed should be counted.
     */
    @Test
    void testGetCompletedCount() {
        TaskManager manager = new TaskManager();
        Task t1 = new Task("A");
        Task t2 = new Task("B");

        manager.addTask(t1);
        manager.addTask(t2);

        manager.markComplete(t1);

        assertEquals(1, manager.getCompletedCount());
    }

    /**
     * Tests calculation of completion percentage.
     * 2 out of 3 completed should be around 66.6%.
     */
    @Test
    void testCompletionPercent() {
        TaskManager manager = new TaskManager();
        Task t1 = new Task("A");
        Task t2 = new Task("B");
        Task t3 = new Task("C");

        manager.addTask(t1);
        manager.addTask(t2);
        manager.addTask(t3);

        manager.markComplete(t1);
        manager.markComplete(t2);

        // Allow small floating-point error = 1.0 tolerance
        assertEquals(66.666, manager.getCompletionPercent(), 1.0);
    }

    /**
     * Tests completion percent when no tasks exist.
     * Must be exactly 0.0 to avoid division by zero.
     */
    @Test
    void testCompletionPercent_NoTasks() {
        TaskManager manager = new TaskManager();
        assertEquals(0.0, manager.getCompletionPercent());
    }
}
