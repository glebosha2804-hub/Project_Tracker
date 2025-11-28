package tasktracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * TaskDialog is the popup window used for:
 *  - Creating a new task
 *  - Editing an existing task
 *
 * It contains fields for:
 *  - Title
 *  - Assignee
 *  - Due date (Day / Month / Year or "No due date")
 *  - Type
 *  - Priority
 *
 * When the user clicks "Create" or "Save", this dialog returns
 * the new or updated Task object to the main GUI.
 */
public class TaskDialog extends JDialog {

    // --- UI components for the fields ---
    private JTextField titleField;
    private JTextField assigneeField;

    // Due date uses dropdown menus instead of typing
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;

    // User can check this to remove due date entirely
    private JCheckBox noDueDateCheck;

    private JComboBox<Task.Priority> priorityCombo;
    private JComboBox<String> typeCombo;

    // --- Return values ---
    private Task createdTask;         // Used when creating a new task
    private Task editingTask;         // Non-null when editing existing task

    /**
     * Constructor for creating a *new* task.
     */
    public TaskDialog(JFrame parent) {
        super(parent, "New Task", true);  // true = modal dialog (blocks other windows)
        initComponents();
        pack();                          // Layout components
        setLocationRelativeTo(parent);   // Center on parent window
    }

    /**
     * Constructor for editing an *existing* task.
     * This loads the current task data into the form.
     */
    public TaskDialog(JFrame parent, Task taskToEdit) {
        super(parent, "Edit Task", true);
        this.editingTask = taskToEdit;
        initComponents();
        loadFromTask(taskToEdit);        // Pre-fill the form
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Builds the UI components for the dialog.
     */
    private void initComponents() {

        // ----- Main layout -----
        setLayout(new BorderLayout(10, 10));

        // Form panel (left column: labels, right column: fields)
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Basic text fields
        titleField = new JTextField();
        assigneeField = new JTextField();

        // ----- DUE DATE PICKER: Day / Month / Year -----
        noDueDateCheck = new JCheckBox("No due date");
        dayCombo = new JComboBox<>();
        monthCombo = new JComboBox<>();
        yearCombo = new JComboBox<>();

        // Fill day combo (1 to 31)
        for (int d = 1; d <= 31; d++) {
            dayCombo.addItem(d);
        }

        // Month names for month dropdown
        String[] months = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };
        for (String m : months) {
            monthCombo.addItem(m);
        }

        // Years: from last year to 5 years ahead
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 1; y <= currentYear + 5; y++) {
            yearCombo.addItem(y);
        }

        // If user checks "No due date", disable the dropdowns
        noDueDateCheck.addActionListener(e -> updateDueDateEnabledState());

        // ----- Type dropdown -----
        typeCombo = new JComboBox<>(new String[] {
                "General", "School", "Work", "Personal", "Other"
        });
        typeCombo.setEditable(true);  // User can type custom category

        // ----- Priority dropdown -----
        priorityCombo = new JComboBox<>(Task.Priority.values());
        priorityCombo.setSelectedItem(Task.Priority.MEDIUM);

        // ----- Add components to form -----
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("For (Assignee):"));
        formPanel.add(assigneeField);

        // Date row: add day/month/year inside a sub-panel
        formPanel.add(new JLabel("Due Date:"));
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(dayCombo);
        datePanel.add(monthCombo);
        datePanel.add(yearCombo);
        formPanel.add(datePanel);

        // "No due date" checkbox
        formPanel.add(new JLabel(""));
        formPanel.add(noDueDateCheck);

        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);

        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityCombo);

        // Add form panel to window
        add(formPanel, BorderLayout.CENTER);

        // ----- Buttons (bottom) -----
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(editingTask == null ? "Create" : "Save");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(this::handleOk);

        // If cancelled → close window, return null
        cancelButton.addActionListener(e -> {
            createdTask = null;
            dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Default state: due date enabled
        noDueDateCheck.setSelected(false);
        updateDueDateEnabledState();
    }

    /**
     * Enables or disables the date dropdowns depending on the "No due date" checkbox.
     */
    private void updateDueDateEnabledState() {
        boolean enabled = !noDueDateCheck.isSelected();
        dayCombo.setEnabled(enabled);
        monthCombo.setEnabled(enabled);
        yearCombo.setEnabled(enabled);
    }

    /**
     * Loads an existing task's data into the form fields.
     */
    private void loadFromTask(Task t) {
        if (t == null) return;

        titleField.setText(t.getTitle());
        assigneeField.setText(t.getAssignee());
        typeCombo.setSelectedItem(t.getType());
        priorityCombo.setSelectedItem(t.getPriority());

        // Handle due date:
        if (t.getDueDate() == null) {
            // No due date → disable date selection
            noDueDateCheck.setSelected(true);
            updateDueDateEnabledState();
        } else {
            // Fill dropdowns with existing due date
            noDueDateCheck.setSelected(false);
            updateDueDateEnabledState();

            LocalDate d = t.getDueDate();
            dayCombo.setSelectedItem(d.getDayOfMonth());
            monthCombo.setSelectedIndex(d.getMonthValue() - 1); // months are 1–12, combo is 0–11
            yearCombo.setSelectedItem(d.getYear());
        }
    }

    /**
     * Called when user clicks "Create" or "Save".
     * Validates input and returns a new or updated Task.
     */
    private void handleOk(ActionEvent e) {

        // ---- Read the data from the form ----
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a title.",
                    "Missing Title",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assignee = assigneeField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        Task.Priority priority = (Task.Priority) priorityCombo.getSelectedItem();

        // ---- Build the due date (or null if "no due date") ----
        LocalDate dueDate = null;

        if (!noDueDateCheck.isSelected()) {
            Integer day = (Integer) dayCombo.getSelectedItem();
            Integer year = (Integer) yearCombo.getSelectedItem();
            int monthIndex = monthCombo.getSelectedIndex();  // 0–11

            if (day == null || year == null || monthIndex < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid day, month, and year.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int month = monthIndex + 1; // convert to 1–12

            try {
                dueDate = LocalDate.of(year, month, day);
            } catch (DateTimeException ex) {
                JOptionPane.showMessageDialog(this,
                        "That date is not valid.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // ---- Create or update the Task ----
        if (editingTask == null) {
            // Creating new task
            createdTask = new Task(title, assignee, dueDate, type, priority);
        } else {
            // Editing existing task
            editingTask.setTitle(title);
            editingTask.setAssignee(assignee);
            editingTask.setDueDate(dueDate);
            editingTask.setType(type);
            editingTask.setPriority(priority);

            createdTask = editingTask;
        }

        // Close the dialog and return control to the main GUI
        dispose();
    }

    /**
     * Called by the main window to retrieve the created or edited task.
     * Returns null if the user pressed "Cancel".
     */
    public Task getCreatedTask() {
        return createdTask;
    }
}
