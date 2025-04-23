import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TileHelper {
    public static class ImageSelector {
        public BufferedImage image;
        public String fileName;
        public String format;
        public int width;
        public int height;
        public File file;

        public ImageSelector(BufferedImage image, String fileName, String format, int width, int height, File file) {
            this.image = image;
            this.fileName = fileName;
            this.format = format;
            this.width = width;
            this.height = height;
            this.file = file;
        }
    }
    public static ImageSelector selectImage() {
        setUIManager();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.removeChoosableFileFilter(jFileChooser.getChoosableFileFilters()[0]);
        jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images (*.jpg; *.jpeg; *.png)", "jpg", "jpeg", "png"));
        int choice = jFileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
            try {
                BufferedImage image = ImageIO.read(file);
                return new ImageSelector(image, file.getName(), extension, image.getWidth(), image.getHeight(), file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
    public static Dimension getDimensions(int queueSize, int width, int height) {
        double sqrt = Math.sqrt(queueSize);
        int tileSheetWidth = (int) sqrt * width;
        int tileSheetHeight = sqrt > (int) sqrt + 0.5 ? (int) sqrt * height + 2 * height : sqrt == (int) sqrt ? (int) sqrt * height: (int) sqrt * height + height;
        return new Dimension(tileSheetWidth, tileSheetHeight);
    }
    public static ImageIcon resize(BufferedImage image, int targWidth, int targHeight) {
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();

        if (imageWidth > targWidth || imageHeight > targHeight) {
            if (imageWidth > targWidth) {
                imageWidth = targWidth;
                imageHeight = image.getHeight() * imageWidth / image.getWidth();
            }
            if (imageHeight > targHeight) {
                imageHeight = targHeight;
                imageWidth = image.getWidth() * imageHeight / image.getHeight();
            }
        } else if (imageHeight < targHeight) {
            imageHeight = targHeight;
            imageWidth = image.getWidth() * imageHeight / image.getHeight();
        }

        BufferedImage bi = new BufferedImage(imageWidth, imageHeight, image.getType());
        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0,0, imageWidth, imageHeight, null);
        return new ImageIcon(bi);
    }
    public static boolean containsDuplicate(ArrayList<byte[]> array, byte[] comparator) {
        for (byte[] bytes : array) {
            if (Arrays.equals(bytes, comparator)) return true;
        }
        return false;
    }
    public static void setUIManager() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static File setOutputFile() {
        setUIManager();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    public static void printSheet(BufferedImage tileSheet, String format, File directory) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            ImageIO.write(tileSheet, format, new File(directory + "/Tilesheet" + timestamp + "." + format));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JOptionPane.showMessageDialog(null, "Successfully converted image to tilesheet!");
    }
}
