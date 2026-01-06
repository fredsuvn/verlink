package space.sunqian.verlink;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
    private static final String HISTORY_PATH_KEY = "lastPath";
    private static final String LINK_NAME_KEY = "lastLinkName";
    private static final Prefs prefs = Prefs.get();

    public VerLinkFrame() {
        setTitle("VerLink " + VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/logo.png")));
        setIconImage(icon.getImage());

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
        String lastPath = prefs.get(HISTORY_PATH_KEY);
        if (lastPath != null) {
            Path path = Paths.get(lastPath);
            paintSubDirectories(new Dirs(path));
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
        linkNameField.setText(prefs.get(LINK_NAME_KEY, "current"));
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
        // Author
        JPanel contactPanel = getContactPanel();
        linkPanel.add(contactPanel);
        return linkPanel;
    }

    private JPanel getContactPanel() {
        JPanel authorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel contactLabel = new JLabel("Contact: ");
        JButton contactContent = new JButton("https://github.com/fredsuvn/verlink");
        contactContent.addActionListener(_ -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/fredsuvn/verlink"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Browser failed to open.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        authorPanel.add(contactLabel);
        authorPanel.add(contactContent);
        return authorPanel;
    }

    private void browseForDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose Directory");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Path path = Paths.get(selectedFile.getAbsolutePath());
            prefs.set(HISTORY_PATH_KEY, path.toString());
            prefs.save();
            paintSubDirectories(new Dirs(path));
        }
    }

    private void paintSubDirectories(Dirs dirs) {
        if (dirs == null || dirs.subDirs() == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            dirPanel.removeAll();
            ButtonGroup buttonGroup = new ButtonGroup();
            for (Path subDir : dirs.subDirs()) {
                JRadioButton subRadio = new JRadioButton();
                buttonGroup.add(subRadio);
                JLabel subDirContent = new JLabel(subDir.toString());
                subDirContent.setFocusable(true);
                subDirContent.setBorder(null);
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                // rowPanel.setLayout(new BorderLayout(0, 0));
                // rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                rowPanel.setMaximumSize(new Dimension(WIDTH - 5, 30));
                rowPanel.add(subRadio);
                rowPanel.add(subDirContent);
                dirPanel.add(rowPanel);
            }
            dirPanel.revalidate();
            dirPanel.repaint();
        });
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
        paintSubDirectories(new Dirs(dir.getParent()));
    }

    private void createLink() {
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
        String linkName = linkNameField.getText();
        if (linkName == null || linkName.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please input a link name.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        try {
            Path link = dir.resolveSibling(linkName);
            linker.linkDir(link, dir, LinkType.JUNCTION);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Creating link file failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        prefs.set(LINK_NAME_KEY, linkName);
        prefs.save();
        paintSubDirectories(new Dirs(dir.getParent()));
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