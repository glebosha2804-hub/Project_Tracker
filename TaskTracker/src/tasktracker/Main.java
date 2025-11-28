package tasktracker;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Swing is NOT thread-safe. 
        // All GUI code must run on the "Event Dispatch Thread" (EDT) 
        // to avoid random crashes or UI glitches.
        //
        // SwingUtilities.invokeLater(...) schedules your GUI creation code
        // to run safely on that EDT.
        SwingUtilities.invokeLater(() -> {

            // Create the task manager. This stores and manages all tasks.
            // It’s passed into the GUI so both can work together.
            TaskManager manager = new TaskManager();

            // Create and show the main window.
            // The constructor of TaskTrackerGUI builds the entire interface.
            new TaskTrackerGUI(manager);
        });
    }
}
