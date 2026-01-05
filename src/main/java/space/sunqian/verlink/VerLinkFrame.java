package space.sunqian.verlink;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Main frame of verlink.
 *
 * @author sunqian
 */
public class VerLinkFrame extends JFrame {

    private static final String VERSION = "0.0.0";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final Linker linker = Linker.get();

    private final JPanel dirPanel;
    private final JTextField linkNameField = new JTextField(32);
    private static final String HISTORY_KEY = "lastPath";
    private static final Preferences prefs = Preferences.userRoot().node(".verlink/history");

    private volatile Dirs dirs;

    public VerLinkFrame() {
        setTitle("VerLink " + VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        // directory choicer
        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // browsePanel.setBorder(BorderFactory.createTitledBorder("Choose Directory"));
        // browsePanel.setBorder(BorderFactory.createCompoundBorder());
        JButton browseButton = new JButton("Choose Directory");
        browseButton.addActionListener(_ -> browseForDirectory());
        browsePanel.add(browseButton);

        // shows and selects subdirectories
        this.dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirPanel.setBorder(BorderFactory.createTitledBorder("Subdirectories: select a subdirectory to link to."));
        dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(dirPanel);
        // history
        String lastPath = prefs.get(HISTORY_KEY, "null");
        if (!lastPath.equals("null")) {
            Path path = Paths.get(lastPath);
            this.dirs = new Dirs(path);
            paintSubDirectories();
        }

        // linker
        JPanel linkPanel = createLinkPanel();

        add(browsePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(linkPanel, BorderLayout.SOUTH);
    }

    private JPanel createLinkPanel() {
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel linkNameLabel = new JLabel("Link name: ");
        linkNameField.setText("current");
        inputPanel.add(linkNameLabel);
        inputPanel.add(linkNameField);
        linkPanel.add(inputPanel);
        JPanel clickPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createLinkButton = new JButton("Create Link");
        createLinkButton.addActionListener(_ -> createLink());
        JButton deleteButton = new JButton("Delete Link");
        deleteButton.addActionListener(_ -> deleteLink());
        clickPanel.add(createLinkButton);
        clickPanel.add(deleteButton);
        linkPanel.add(clickPanel);
        return linkPanel;
    }

    private void browseForDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose Directory");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Path path = Paths.get(selectedFile.getAbsolutePath());
            this.dirs = new Dirs(path);
            prefs.put(HISTORY_KEY, path.toString());
            paintSubDirectories();
        }
    }

    private void paintSubDirectories() {
        if (dirs != null && dirs.subDirs() != null) {
            SwingUtilities.invokeLater(() -> {
                dirPanel.removeAll();
                ButtonGroup buttonGroup = new ButtonGroup();
                for (Path subDir : dirs.subDirs()) {
                    JRadioButton subRadio = new JRadioButton();
                    buttonGroup.add(subRadio);
                    JLabel subDirLabel = new JLabel(subDir.toString());
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    // rowPanel.setLayout(new BorderLayout(0, 0));
                    // rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    rowPanel.setMaximumSize(new Dimension(WIDTH - 5, 30));
                    rowPanel.add(subRadio);
                    rowPanel.add(subDirLabel);
                    dirPanel.add(rowPanel);
                }
                dirPanel.revalidate();
                dirPanel.repaint();
            });
        }
    }

    private void deleteLink() {
        Path dir = getSelectedDir();
        if (dir == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a subdirectory to delete.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        try {
            Files.deleteIfExists(dir);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this,
                "Deleting link file failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        paintSubDirectories();
    }

    private void createLink() {
        if (dirs == null) {
            return;
        }
        Path dir = getSelectedDir();
        if (dir == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a subdirectory to delete.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        try {
            Path link = dirs.parentDir().resolve(linkNameField.getText());
            linker.linkDir(link, dir, LinkType.JUNCTION);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Creating link file failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        paintSubDirectories();
    }

    private Path getSelectedDir() {
        if (dirPanel == null || dirPanel.getComponents() == null) {
            return null;
        }
        for (Component component : dirPanel.getComponents()) {
            if (component instanceof JPanel rowPanel) {
                if (rowPanel.getComponentCount() > 0) {
                    Component firstComponent = rowPanel.getComponent(0);
                    if (firstComponent instanceof JRadioButton radioButton) {
                        if (radioButton.isSelected()) {
                            JLabel subDirLabel = (JLabel) rowPanel.getComponent(1);
                            return Paths.get(subDirLabel.getText());
                        }
                    }
                }
            }
        }
        return null;
    }

    static void main() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> new VerLinkFrame().setVisible(true));
    }
}