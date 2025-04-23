import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ImageSplitter extends JPanel {
    JLabel inputLabel, inputImage, outputLabel, fileName, dimensions, folder, loaded, rows, cols;
    JButton fileSelector, outputSelector, tilingStart, printTiles;
    JComboBox<Integer> rowComboBox, colComboBox;
    ArrayList<BufferedImage> images = new ArrayList<>();
    BufferedImage image;
    File directory;
    String format;
    public ImageSplitter() {
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

        gbcLeft.gridx = 0; gbcLeft.gridy = 2;
        inputImage = new JLabel();
        inputImage.setBackground(Color.GRAY);
        inputImage.setOpaque(true);
        inputImage.setHorizontalAlignment(SwingConstants.CENTER);
        inputImage.setVerticalAlignment(SwingConstants.CENTER);
        inputImage.setPreferredSize(new Dimension(500,300));
        leftPanel.add(inputImage, gbcLeft);

        gbcLeft.gridx = 0; gbcLeft.gridy = 3;
        dimensions = new JLabel("Awaiting Dimensions...");
        dimensions.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        dimensions.setForeground(Color.WHITE);
        leftPanel.add(dimensions, gbcLeft);

        gbcLeft.gridx = 0; gbcLeft.gridy = 4;  gbcLeft.gridwidth = 1;
        rows = new JLabel("Rows", SwingConstants.CENTER);
        rows.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        rows.setPreferredSize(new Dimension(80, 20));
        rows.setForeground(Color.WHITE);
        rows.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        leftPanel.add(rows, gbcLeft);

        gbcLeft.gridx = 1;  gbcLeft.gridy = 4;
        cols = new JLabel("Columns", SwingConstants.CENTER);
        cols.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        cols.setPreferredSize(new Dimension(80, 20));
        cols.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        cols.setForeground(Color.WHITE);
        leftPanel.add(cols, gbcLeft);

        gbcLeft.gridx = 2;  gbcLeft.gridy = 4;
        fileSelector = new JButton("Select Image");
        fileSelector.setBackground(Color.WHITE);
        fileSelector.setFocusable(false);
        fileSelector.addActionListener(e -> selectInputFile());
        leftPanel.add(fileSelector, gbcLeft);

        gbcLeft.gridx = 0; gbcLeft.gridy = 5;
        rowComboBox = new JComboBox<>();
        rowComboBox.setMaximumRowCount(3);
        for (int i = 2; i <= 100; i++) rowComboBox.addItem(i);
        leftPanel.add(rowComboBox, gbcLeft);

        gbcLeft.gridx = 1; gbcLeft.gridy = 5;
        colComboBox = new JComboBox<>();
        colComboBox.setMaximumRowCount(3);
        for (int i = 2; i <= 100; i++) colComboBox.addItem(i);
        leftPanel.add(colComboBox, gbcLeft);

        gbcLeft.gridx = 2; gbcLeft.gridy = 5;
        tilingStart = new JButton("Start Splitting!");
        tilingStart.setBackground(Color.WHITE);
        tilingStart.setFocusable(false);
        tilingStart.addActionListener(e -> startSplitting());
        leftPanel.add(tilingStart, gbcLeft);

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
        outputSelector = new JButton("Select Output Destination");
        outputSelector.setBackground(Color.WHITE);
        outputSelector.setFocusable(false);
        outputSelector.addActionListener(e -> selectOutputFile());
        rightPanel.add(outputSelector, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 3;
        loaded = new JLabel("No files processed...", SwingConstants.CENTER);
        loaded.setPreferredSize(new Dimension(500, 30));
        loaded.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        loaded.setForeground(Color.WHITE);
        rightPanel.add(loaded, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 4;
        printTiles = new JButton("Print Individual Tiles!");
        printTiles.setBackground(Color.WHITE);
        printTiles.setFocusable(false);
        printTiles.addActionListener(e -> printTiles());
        rightPanel.add(printTiles, gbcRight);

        this.add(leftPanel);
        this.add(rightPanel);
    }
    private void selectOutputFile() {
        directory = TileHelper.setOutputFile();
        if (directory != null) folder.setText(Objects.requireNonNull(directory).getAbsolutePath());
    }
    private void startSplitting() {
        if(image == null) {
            JOptionPane.showMessageDialog(null, "Please select an input file first!");
            return;
        }
        int rows = Integer.parseInt(Objects.requireNonNull(rowComboBox.getSelectedItem()).toString());
        int cols = Integer.parseInt(Objects.requireNonNull(colComboBox.getSelectedItem()).toString());
        int tileWidth = image.getWidth() / cols;
        int tileHeight = image.getHeight() / rows;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * tileWidth;
                int y = i * tileHeight;
                int currentWidth = (j == cols - 1) ? image.getWidth() - x : tileWidth;
                int currentHeight = (i == rows - 1) ? image.getHeight() - y : tileHeight;
                images.add(image.getSubimage(x, y, currentWidth, currentHeight));
            }
        }
        loaded.setText(images.size() + " files loaded!");
    }
    private void selectInputFile() {
        TileHelper.ImageSelector imageSelector = TileHelper.selectImage();
        if (imageSelector != null) {
            format = imageSelector.format;
            image = imageSelector.image;
            fileName.setText(imageSelector.file.getName());
            inputImage.setIcon(TileHelper.resize(image, inputImage.getWidth(), inputImage.getHeight()));
            dimensions.setText("Width: " + image.getWidth() + " px - " + "Height: " + image.getHeight() + " px; Type: " + format);
        }
    }
    private void printTiles() {
        if (directory == null) {
            JOptionPane.showMessageDialog(null, "Please select an output directory first!");
            return;
        }
        if (images == null) {
            JOptionPane.showMessageDialog(null, "Make sure the given image is processed first!");
            return;
        }
        int count = 0;
        for (BufferedImage image : images) {
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                ImageIO.write(image, format, new File(directory + "/Tile" + count++ + timestamp + "." + format));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        JOptionPane.showMessageDialog(null, "Successfully converted image to tiles!");
    }
}
