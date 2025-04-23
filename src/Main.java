import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PNC Tilesheet Editor");
        frame.setSize(1120, 720);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("src/PNCTSEIcon.png").getImage());

        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Tilesheet to Tiles", new TileSheetToTiles());
        jTabbedPane.addTab("Tiles to Tilesheet", new TilesToTileSheet());
        jTabbedPane.addTab("Image Splitter", new ImageSplitter());
        frame.add(jTabbedPane);
        frame.setVisible(true);
    }
}
