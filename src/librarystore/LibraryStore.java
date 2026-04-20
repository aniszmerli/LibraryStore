package librarystore;

import ui.Loginframe;
import javax.swing.SwingUtilities;

public class LibraryStore {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Loginframe().setVisible(true);
        });
    }
}