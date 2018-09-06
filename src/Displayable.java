import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

class Displayable {

    private JFrame frame;
    private Path excelPath = null, htmlPath = null;
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
                Converter.convert(excelPath, htmlPath);
                buttonUpdate.setText("Updated");
                hasChangedFileSelection = false;
            }
        });

        JButton buttonExcel = new JButton("Excel File");
        buttonExcel.addActionListener(e -> {
            Path path = getPath();
            if (path != null) {
                excelPath = path;
                buttonExcel.setText("Excel File: " + path.getName(path.getNameCount() - 1));
                hasChangedFileSelection = true;
                buttonUpdate.setText("Update Events");
            }
        });

        JButton buttonHTML = new JButton("HTML File");
        buttonHTML.addActionListener(e -> {
            Path path = getPath();
            if (path != null) {
                htmlPath = path;
                buttonHTML.setText("HTML File: " + path.getName(path.getNameCount() - 1));
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
     *
     * @return Returns true if both files have been selected and are the appropriate file types
     */
    private boolean hasCompletedFileSelection() {
        return excelPath != null && htmlPath != null && (excelPath.toString().contains(INPUT_FILE_TYPE)
                                                         && htmlPath.toString().contains(OUTPUT_FILE_TYPE));
    }

    /**
     * Calls a JFileChooser for the user to select their files
     *
     * @return Returns a Path representing the file system location of the file the user has selected with a JFileChooser
     */
    private Path getPath() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(frame);
        return (returnVal == JFileChooser.APPROVE_OPTION) ? Paths.get(fileChooser.getSelectedFile().toString()) : null;
    }
}
