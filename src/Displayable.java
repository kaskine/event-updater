import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class Displayable {

    private JFrame frame;
    private File fileExcel = null, fileHTML = null;
    private boolean hasChangedFileSelection = false;

    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    private static final String INPUT_FILE_TYPE = ".xlsx";
    private static final String OUTPUT_FILE_TYPE = ".html";

    private static final String ICON_FILE_PATH = "automation-icon.png";

    /**
     * Constructs a new Displayable
     */
    Displayable() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setup();
    }

    /**
     * Calls all setup methods
     */
    private void setup() {
        setupFrame();
        setupButtons();
        frame.setVisible(true);
    }

    /**
     * Sets up the frame
     */
    private void setupFrame() {

        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(ICON_FILE_PATH));

        frame = new JFrame("Event Updater");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(3, 1));
        frame.setIconImage(icon.getImage());

        Application.getApplication().setDockIconImage(icon.getImage());
    }

    /**
     * Sets up the buttons
     */
    private void setupButtons() {

        JButton buttonUpdate = new JButton("Update Events");
        buttonUpdate.addActionListener(e -> {
            if (hasCompletedFileSelection() && hasChangedFileSelection) {
                new Converter(fileExcel, fileHTML);
                buttonUpdate.setText("Updated");
                hasChangedFileSelection = false;
            }
        });

        JButton buttonExcel = new JButton("Excel File");
        buttonExcel.addActionListener(e -> {
            File file = getFile();
            if (file != null) {
                fileExcel = file;
                buttonExcel.setText("Excel File: " + file.getName());
                hasChangedFileSelection = true;
                buttonUpdate.setText("Update Events");
            }
        });

        JButton buttonHTML = new JButton("HTML File");
        buttonHTML.addActionListener(e -> {
            File file = getFile();
            if (file != null) {
                fileHTML = file;
                buttonHTML.setText("HTML File: " + file.getName());
                hasChangedFileSelection = true;
                buttonUpdate.setText("Update Events");
            }
        });

        frame.add(buttonExcel);
        frame.add(buttonHTML);
        frame.add(buttonUpdate);
    }

    /**
     * Verifies that both files have been selected and that the files chosen are the appropriate file types.
     * @return Returns true if both files have been selected and are the appropriate file types
     */
    private boolean hasCompletedFileSelection() {
        return fileExcel != null && fileHTML != null && (fileExcel.toString().contains(INPUT_FILE_TYPE)
                                                         && fileHTML.toString().contains(OUTPUT_FILE_TYPE));
    }

    /**
     * Calls a JFileChooser for the user to select their files
     * @return Returns a file the user has selected with a JFileChooser
     */
    private File getFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(frame);

        File file = null;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        return file;
    }
}
