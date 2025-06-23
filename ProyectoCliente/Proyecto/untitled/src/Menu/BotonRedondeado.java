package Menu;

import javax.swing.*;
import java.awt.*;

public class BotonRedondeado extends JButton {

    public BotonRedondeado(String texto) {
        super(texto);
        setFont(new Font("Arial", Font.BOLD, 16));
        setForeground(Color.BLACK); // texto oscuro sobre amarillo
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setContentAreaFilled(false);
        setRolloverEnabled(true); // hover
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradiente;

        if (getModel().isRollover()) {
            // Hover: amarillo más claro
            gradiente = new GradientPaint(
                    0, 0, new Color(255, 230, 80), // amarillo claro
                    0, getHeight(), new Color(255, 255, 153) // casi pastel
            );
        } else {
            // Normal: amarillo fuerte
            gradiente = new GradientPaint(
                    0, 0, new Color(255, 204, 0), // amarillo fuerte
                    0, getHeight(), new Color(255, 220, 80) // transición suave
            );
        }

        g2.setPaint(gradiente);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        g2.dispose();
    }
}
