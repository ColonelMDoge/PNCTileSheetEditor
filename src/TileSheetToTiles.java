import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TileSheetToTiles extends JPanel {
    JLabel inputLabel, outputLabel, fileName, width, height, dimensions, folder;
    JButton fileSelector, outputSelector, tilingStart, enablePrintSheet, enablePrintSeparate;
    JLabel inputImage, outputImage;
    JComboBox<Integer> widthComboBox, heightComboBox;
    BufferedImage image, tileSheet;
    File directory;
    ArrayList<byte[]> imageArray;
    String format;
    public TileSheetToTiles() {
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
        width = new JLabel("Tile Width", SwingConstants.CENTER);
        width.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        width.setPreferredSize(new Dimension(80, 20));
        width.setForeground(Color.WHITE);
        width.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        leftPanel.add(width, gbcLeft);

        gbcLeft.gridx = 1;  gbcLeft.gridy = 4;
        height = new JLabel("Tile Height", SwingConstants.CENTER);
        height.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        height.setPreferredSize(new Dimension(80, 20));
        height.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        height.setForeground(Color.WHITE);
        leftPanel.add(height, gbcLeft);

        gbcLeft.gridx = 2;  gbcLeft.gridy = 4;
        fileSelector = new JButton("Select Tilesheet");
        fileSelector.setBackground(Color.WHITE);
        fileSelector.setFocusable(false);
        fileSelector.addActionListener(e -> selectInputFile());
        leftPanel.add(fileSelector, gbcLeft);

        gbcLeft.gridx = 0; gbcLeft.gridy = 5;
        widthComboBox = new JComboBox<>();
        widthComboBox.setMaximumRowCount(3);
        for (int i = 1; i <= 1024; i *= 2) widthComboBox.addItem(i);
        leftPanel.add(widthComboBox, gbcLeft);

        gbcLeft.gridx = 1; gbcLeft.gridy = 5;
        heightComboBox = new JComboBox<>();
        heightComboBox.setMaximumRowCount(3);
        for (int i = 1; i <= 1024; i *= 2) heightComboBox.addItem(i);
        leftPanel.add(heightComboBox, gbcLeft);

        gbcLeft.gridx = 2; gbcLeft.gridy = 5;
        tilingStart = new JButton("Start Tiling!");
        tilingStart.setBackground(Color.WHITE);
        tilingStart.setFocusable(false);
        tilingStart.addActionListener(e -> startTiling());
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
        enablePrintSeparate = new JButton("Print Individual Tiles!");
        enablePrintSeparate.setBackground(Color.WHITE);
        enablePrintSeparate.setFocusable(false);
        enablePrintSeparate.addActionListener(e -> printTiles());
        rightPanel.add(enablePrintSeparate, gbcRight);

        gbcRight.gridx = 0; gbcRight.gridy = 5;
        enablePrintSheet = new JButton("Print Tilesheet!");
        enablePrintSheet.setBackground(Color.WHITE);
        enablePrintSheet.setFocusable(false);
        enablePrintSheet.addActionListener(e -> printTileSheet());
        rightPanel.add(enablePrintSheet, gbcRight);

        this.add(leftPanel);
        this.add(rightPanel);
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
    private void startTiling() {
        if(image == null) {
            JOptionPane.showMessageDialog(null, "Please select an input file first!");
            return;
        }
        int width = Integer.parseInt(Objects.requireNonNull(widthComboBox.getSelectedItem()).toString());
        int height = Integer.parseInt(Objects.requireNonNull(heightComboBox.getSelectedItem()).toString());
        if (image.getWidth() % width != 0 || image.getHeight() % height != 0) {
            JOptionPane.showMessageDialog(null, "Tile Sheet size is invalid for given width and height split!");
            return;
        }
        ArrayList<byte[]> array = new ArrayList<>();
        for (int i = 0; i < image.getHeight(); i += height ) {
            for (int j = 0; j < image.getWidth(); j += width) {
                BufferedImage im = image.getSubimage(j,i, width, height);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    ImageIO.write(im, fileName.getText().substring(fileName.getText().lastIndexOf(".") + 1).toLowerCase(), byteArrayOutputStream);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                byte[] bytes = byteArrayOutputStream.toByteArray();
                if (!TileHelper.containsDuplicate(array, bytes)) array.add(byteArrayOutputStream.toByteArray());
            }
        }
        Queue<byte[]> queue = new LinkedList<>(array);
        imageArray = new ArrayList<>(array);
        Dimension dim = TileHelper.getDimensions(queue.size(), width, height);
        BufferedImage tileSheet = new BufferedImage(dim.width, dim.height, image.getType());
        Graphics2D g2d = tileSheet.createGraphics();
        for (int i = 0; i < dim.height; i += height) {
            for (int j = 0; j < dim.width; j += width) {
                if (!queue.isEmpty()) {
                    try {
                        g2d.drawImage(ImageIO.read(new ByteArrayInputStream(queue.poll())),j,i,width,height,null);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        this.tileSheet = tileSheet;
        outputImage.setIcon(TileHelper.resize(tileSheet, outputImage.getWidth(), outputImage.getHeight()));
    }
    private void selectOutputFile() {
        directory = TileHelper.setOutputFile();
        if (directory != null) folder.setText(Objects.requireNonNull(directory).getAbsolutePath());
    }
    private void printTiles() {
        if (folder.getText().equals("Awaiting Destination...")) {
            JOptionPane.showMessageDialog(null, "Please select an output directory first!");
            return;
        }
        if (imageArray == null) {
            JOptionPane.showMessageDialog(null, "Make sure the given image is processed first!");
            return;
        }
        int count = 0;
        for (byte[] bytes : imageArray) {
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(bytes)), format, new File(directory + "/Tile" + count++ + timestamp + "." + format));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        JOptionPane.showMessageDialog(null, "Successfully converted image to tiles!");
    }
    private void printTileSheet() {
        if (directory == null) {
            JOptionPane.showMessageDialog(null, "Please select an output directory first!");
            return;
        }
        if (imageArray == null) {
            JOptionPane.showMessageDialog(null, "Make sure the given image is processed first!");
            return;
        }
        TileHelper.printSheet(tileSheet, format, directory);
    }
}
