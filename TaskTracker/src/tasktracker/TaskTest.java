package tasktracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Task class.
 * 
 * These tests verify:
 *  - that constructors correctly initialize fields
 *  - getters and setters work properly
 *  - completion flag behaves correctly
 *  - date formatting method works
 *  - toString() produces a readable string
 */
class TaskTest {

    @Test
    void testConstructorWithOnlyTitle() {
        // Creating a task with only a title
        Task task = new Task("Homework");

        // Title is set
        assertEquals("Homework", task.getTitle());

        // Optional fields get default safe values
        assertEquals("", task.getAssignee());
        assertNull(task.getDueDate());
        assertEquals("", task.getType());

        // Default priority should be MEDIUM
        assertEquals(Task.Priority.MEDIUM, task.getPriority());

        // Newly created task must not be completed
        assertFalse(task.isCompleted());
    }

    @Test
    void testFullConstructor() {
        LocalDate date = LocalDate.of(2025, 5, 10);

        Task task = new Task("Project", "John", date, "School", Task.Priority.HIGH);

        assertEquals("Project", task.getTitle());
        assertEquals("John", task.getAssignee());
        assertEquals(date, task.getDueDate());
        assertEquals("School", task.getType());
        assertEquals(Task.Priority.HIGH, task.getPriority());
    }

    @Test
    void testSetters() {
        Task task = new Task("Start");

        // Update fields
        task.setTitle("Updated");
        task.setAssignee("Mary");
        task.setType("Work");
        task.setPriority(Task.Priority.LOW);

        LocalDate newDate = LocalDate.of(2030, 1, 20);
        task.setDueDate(newDate);

        assertEquals("Updated", task.getTitle());
        assertEquals("Mary", task.getAssignee());
        assertEquals("Work", task.getType());
        assertEquals(Task.Priority.LOW, task.getPriority());
        assertEquals(newDate, task.getDueDate());
    }

    @Test
    void testMarkCompleted() {
        Task task = new Task("Test");

        // Initially not completed
        assertFalse(task.isCompleted());

        // Mark as completed
        task.setCompleted(true);

        assertTrue(task.isCompleted());
    }

    @Test
    void testDueDateFormatting() {
        Task task = new Task("Test");

        // No due date → empty string
        assertEquals("", task.getDueDateAsString());

        // Add a due date
        LocalDate date = LocalDate.of(2024, 12, 25);
        task.setDueDate(date);

        assertEquals("2024-12-25", task.getDueDateAsString());
    }

    @Test
    void testToStringOutput() {
        LocalDate date = LocalDate.of(2024, 10, 1);
        Task task = new Task("Finish Report", "Alice", date, "Work", Task.Priority.HIGH);

        String output = task.toString();

        // Very basic checks to confirm formatting
        assertTrue(output.contains("HIGH"));
        assertTrue(output.contains("Finish Report"));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("2024-10-01"));
        assertTrue(output.contains("Work"));
    }
}
