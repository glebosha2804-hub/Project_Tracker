package tasktracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

/**
 * TaskTrackerGUI is the main window of the program.
 * It displays:
 *   - A button to create a new task
 *   - A list of all tasks
 *   - Filters (All / Completed / Pending)
 *   - Buttons to complete, edit, or delete a task
 *   - Stats and a progress bar
 *
 * This class handles ALL visual user interaction.
 */
public class TaskTrackerGUI extends JFrame {

    /**
     * Filter enum determines what type of tasks to show.
     */
    private enum Filter {
        ALL, COMPLETED, PENDING
    }

    private final TaskManager taskManager;    // Stores and manages tasks

    // Swing components
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JLabel statsLabel;
    private JProgressBar progressBar;

    // Current active filter (default = ALL)
    private Filter currentFilter = Filter.ALL;

    /**
     * Constructor — sets up the window.
     */
    public TaskTrackerGUI(TaskManager taskManager) {
        this.taskManager = taskManager;

        setTitle("Task Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);              // Larger window for comfort
        setLocationRelativeTo(null);    // Centers window on screen

        initComponents();               // Build all UI components
        setVisible(true);
    }

    /**
     * Sets up all panels, buttons, lists, filters, and actions.
     */
    private void initComponents() {

        // Use BorderLayout to place panels (North, Center, South)
        setLayout(new BorderLayout(10, 10));

        // ───────────────────────────────────────────────
        // TOP PANEL — "New Task" button
        // ───────────────────────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newTaskButton = new JButton("New Task");
        newTaskButton.addActionListener(this::handleNewTask);
        topPanel.add(newTaskButton);
        add(topPanel, BorderLayout.NORTH);

        // ───────────────────────────────────────────────
        // CENTER — Task list area
        // ───────────────────────────────────────────────
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer to change each task's color + tooltip
        taskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Task) {
                    Task task = (Task) value;

                    // Text displayed in the list
                    setText(task.toString());

                    // Hover popup text
                    setToolTipText(task.getTooltipText());

                    if (!isSelected) { // Keep highlight color if selected
                        LocalDate today = LocalDate.now();

                        // ✔ Completed = green
                        if (task.isCompleted()) {
                            setForeground(new Color(0, 128, 0)); 
                        }
                        // ❗ Overdue = dark red
                        else if (task.getDueDate() != null && task.getDueDate().isBefore(today)) {
                            setForeground(new Color(150, 0, 0));
                        }
                        // ❗ Pending but not overdue = bright red
                        else {
                            setForeground(Color.RED);
                        }
                    }
                }

                return c;
            }
        });

        // Double-click = edit task
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedTask();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);

        // ───────────────────────────────────────────────
        // BOTTOM — Filters, action buttons, stats
        // ───────────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // ───── FILTER BUTTONS (All / Completed / Pending)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));

        JRadioButton allButton = new JRadioButton("All", true);
        JRadioButton completedButton = new JRadioButton("Completed");
        JRadioButton pendingButton = new JRadioButton("Pending");

        // Group the buttons so only ONE can be selected
        ButtonGroup group = new ButtonGroup();
        group.add(allButton);
        group.add(completedButton);
        group.add(pendingButton);

        // Add filter behaviors
        allButton.addActionListener(e -> {
            currentFilter = Filter.ALL;
            refreshView();
        });
        completedButton.addActionListener(e -> {
            currentFilter = Filter.COMPLETED;
            refreshView();
        });
        pendingButton.addActionListener(e -> {
            currentFilter = Filter.PENDING;
            refreshView();
        });

        filterPanel.add(allButton);
        filterPanel.add(completedButton);
        filterPanel.add(pendingButton);

        bottomPanel.add(filterPanel, BorderLayout.NORTH);

        // ───── ACTION BUTTONS (Complete, Edit, Delete)
        JPanel buttonPanel = new JPanel();
        JButton completeButton = new JButton("Mark Complete");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");

        completeButton.addActionListener(this::handleMarkComplete);
        editButton.addActionListener(this::handleEditTask);
        deleteButton.addActionListener(this::handleDeleteTask);

        buttonPanel.add(completeButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        // ───── STATS + PROGRESS BAR (Bottom)
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));

        statsLabel = new JLabel("No tasks yet.");  // Shows counts
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);         // Show % inside bar

        statusPanel.add(statsLabel, BorderLayout.NORTH);
        statusPanel.add(progressBar, BorderLayout.SOUTH);

        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Refresh display on startup
        refreshView();
    }

    // ───────────────────────────────────────────────
    // NEW TASK
    // ───────────────────────────────────────────────
    private void handleNewTask(ActionEvent e) {
        TaskDialog dialog = new TaskDialog(this);  // Open popup
        dialog.setVisible(true);

        Task created = dialog.getCreatedTask();    // Retrieve task
        if (created != null) {
            taskManager.addTask(created);
            refreshView();
        }
    }

    // ───────────────────────────────────────────────
    // MARK AS COMPLETE
    // ───────────────────────────────────────────────
    private void handleMarkComplete(ActionEvent e) {
        Task selected = taskList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to mark complete.",
                    "No Task Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        taskManager.markComplete(selected);
        refreshView();
    }

    // ───────────────────────────────────────────────
    // EDIT TASK (button or double-click)
    // ───────────────────────────────────────────────
    private void handleEditTask(ActionEvent e) {
        editSelectedTask();
    }

    private void editSelectedTask() {
        Task selected = taskList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to edit.",
                    "No Task Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        TaskDialog dialog = new TaskDialog(this, selected);
        dialog.setVisible(true);

        refreshView(); // Task was edited — refresh list
    }

    // ───────────────────────────────────────────────
    // DELETE TASK
    // ───────────────────────────────────────────────
    private void handleDeleteTask(ActionEvent e) {
        Task selected = taskList.getSelectedValue();

        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a task to delete.",
                    "No Task Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete: \"" + selected.getTitle() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            taskManager.removeTask(selected);
            refreshView();
        }
    }

    // ───────────────────────────────────────────────
    // FILTER LOGIC
    // ───────────────────────────────────────────────
    private boolean shouldShow(Task t) {
        switch (currentFilter) {
            case ALL:
                return true;
            case COMPLETED:
                return t.isCompleted();
            case PENDING:
                return !t.isCompleted();
            default:
                return true;
        }
    }

    // ───────────────────────────────────────────────
    // REFRESH LIST + STATS + PROGRESS BAR
    // ───────────────────────────────────────────────
    private void refreshView() {
        // Clear list, then re-add based on filter
        taskListModel.clear();
        for (Task t : taskManager.getTasks()) {
            if (shouldShow(t)) {
                taskListModel.addElement(t);
            }
        }

        // Update stats
        int total = taskManager.getTotalCount();
        int completed = taskManager.getCompletedCount();
        int remaining = total - completed;
        double percent = taskManager.getCompletionPercent();

        statsLabel.setText(
                "Total: " + total +
                " | Completed: " + completed +
                " | Remaining: " + remaining
        );

        progressBar.setValue((int) percent);
        progressBar.setString(String.format("%.1f%%", percent));
    }
}

