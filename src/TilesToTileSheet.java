import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TilesToTileSheet extends JPanel {
    JLabel inputLabel, outputLabel,outputImage, fileName, folder;
    JButton fileSelector, outputSelector, joiningStart, printSheet;
    BufferedImage tileSheet;
    File directory;
    File[] files;
    String format;
    public TilesToTileSheet() {
        setLayout(new GridLayout(1, 2));
        setBackground(Color.DARK_GRAY);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.DARK_GRAY);
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.insets = new Insets(5,5,5,5);

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(5,5,5,5);

        gbcLeft.gridx = 0; gbcLeft.gridy = 0; gbcLeft.gridwidth = 3;
        inputLabel = new JLabel("Input");
        inputLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
        inputLabel.setForeground(Color.WHITE);
        leftPanel.add(inputLabel, gbcLeft);

        gbcLeft.gridx = 0; gbcLeft.gridy = 1;
        fileName = new JLabel("No file selected...", SwingConstants.CENTER);
        fileName.setPreferredSize(new Dimension(500, 30));
        fileName.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        fileName.setForeground(Color.WHITE);
        leftPanel.add(fileName, gbcLeft);

        gbcLeft.gridx = 0;  gbcLeft.gridy = 2;
        fileSelector = new JButton("Select Tiles");
        fileSelector.setBackground(Color.WHITE);
        fileSelector.setFocusable(false);
        fileSelector.addActionListener(e -> selectInputFiles());
        leftPanel.add(fileSelector, gbcLeft);

        gbcLeft.gridx = 0; gbcLeft.gridy = 3;
        joiningStart = new JButton("Start Joining!");
        joiningStart.setBackground(Color.WHITE);
        joiningStart.setFocusable(false);
        joiningStart.addActionListener(e -> startJoining());
        leftPanel.add(joiningStart, gbcLeft);

        gbcRight.gridx = 0; gbcRight.gridy = 0; gbcRight.gridwidth = 3;
        outputLabel = new JLabel("Output");
        outputLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
        outputLabel.setForeground(Color.WHITE);
        rightPanel.add(outputLabel, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 1;
        folder = new JLabel("Awaiting Destination...", SwingConstants.CENTER);
        folder.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        folder.setForeground(Color.WHITE);
        folder.setPreferredSize(new Dimension(500,30));
        rightPanel.add(folder, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 2;
        outputImage = new JLabel();
        outputImage.setBackground(Color.GRAY);
        outputImage.setOpaque(true);
        outputImage.setHorizontalAlignment(SwingConstants.CENTER);
        outputImage.setVerticalAlignment(SwingConstants.CENTER);
        outputImage.setPreferredSize(new Dimension(500,300));
        rightPanel.add(outputImage, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 3;
        outputSelector = new JButton("Select Output Destination");
        outputSelector.setBackground(Color.WHITE);
        outputSelector.setFocusable(false);
        outputSelector.addActionListener(e -> selectOutputFile());
        rightPanel.add(outputSelector, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 4;
        printSheet = new JButton("Print Tilesheet!");
        printSheet.setBackground(Color.WHITE);
        printSheet.setFocusable(false);
        printSheet.addActionListener(e -> printTileSheet());
        rightPanel.add(printSheet, gbcRight);

        this.add(leftPanel);
        this.add(rightPanel);
    }
    private void selectInputFiles() {
        TileHelper.setUIManager();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.removeChoosableFileFilter(jFileChooser.getChoosableFileFilters()[0]);
        jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images (*.jpg; *.jpeg; *.png)", "jpg", "jpeg", "png"));
        jFileChooser.setMultiSelectionEnabled(true);
        int choice = jFileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File[] file = jFileChooser.getSelectedFiles();
            format = file[0].getName().substring(file[0].getName().lastIndexOf('.') + 1).toLowerCase();
            fileName.setText(file.length + " files loaded!");
            fileSelector.requestFocus();
            files = file;
        }
    }
    private void selectOutputFile() {
        directory = TileHelper.setOutputFile();
        if (directory != null) folder.setText(Objects.requireNonNull(directory).getAbsolutePath());
    }
    private void startJoining() {
        if(files == null) {
            JOptionPane.showMessageDialog(null, "Please select an input file first!");
            return;
        }
        Queue<File> queue = new LinkedList<>(List.of(files));
        int width, height;
        try {
            width = ImageIO.read(Objects.requireNonNull(queue.peek())).getWidth();
            height = ImageIO.read(Objects.requireNonNull(queue.peek())).getHeight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Dimension dim = TileHelper.getDimensions(queue.size(), width, height);
        tileSheet = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tileSheet.createGraphics();
        for (int i = 0; i < dim.height; i += height) {
            for (int j = 0; j < dim.width; j += width) {
                if (!queue.isEmpty()) {
                    try {
                        g2d.drawImage(ImageIO.read(queue.poll()),j,i,width,height,null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        outputImage.setIcon(TileHelper.resize(tileSheet, outputImage.getWidth(), outputImage.getHeight()));
    }
    private void printTileSheet() {
        if (directory == null) {
            JOptionPane.showMessageDialog(null, "Please select an output directory first!");
            return;
        }
        if (files == null) {
            JOptionPane.showMessageDialog(null, "Make sure the given image is processed first!");
            return;
        }
        TileHelper.printSheet(tileSheet, format, directory);
    }
}
