package Menu;


import javax.swing.*;
import java.awt.*;

public class PanelConFondo extends JPanel {
    private Image fondoMenu;

    public PanelConFondo(String rutaRelativa) {
        // Carga la imagen como recurso del proyecto
        try {
            fondoMenu = new ImageIcon(getClass().getResource(rutaRelativa)).getImage();
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + rutaRelativa);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondoMenu != null) {
            g.drawImage(fondoMenu, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
