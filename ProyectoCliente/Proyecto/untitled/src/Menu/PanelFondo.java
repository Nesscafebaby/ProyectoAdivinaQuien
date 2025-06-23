package Menu;

import javax.swing.*;
import java.awt.*;

public class PanelFondo extends JPanel {
    private Image imagenFondo;

    public PanelFondo(String rutaRelativa) {
        imagenFondo = new ImageIcon(getClass().getResource(rutaRelativa)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
