package Menu;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

class ControlDeSonido {

    private boolean sonidoActivo = true;
    private JLabel bocinaLabel;
    private Clip clip;
    private ImageIcon iconoEncendida, iconoApagada;

    public ControlDeSonido(String rutaCancion) {
        iconoEncendida = new ImageIcon(getClass().getResource("/imagenes/Bocina.png"));
        iconoApagada = new ImageIcon(getClass().getResource("/imagenes/BocinaRota.png"));

        // Crear label con icono inicial
        bocinaLabel = new JLabel(escalarIcono(iconoEncendida));
        bocinaLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Reproducir música
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(rutaCancion));
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Repetir música
        } catch (Exception e) {
            System.out.println("Error al cargar audio: " + e.getMessage());
        }

        bocinaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sonidoActivo = !sonidoActivo;
                actualizarEstado();
            }
        });

        actualizarEstado();
    }

    private void actualizarEstado() {
        if (sonidoActivo) {
            bocinaLabel.setIcon(escalarIcono(iconoEncendida));
            if (clip != null) clip.start();
        } else {
            bocinaLabel.setIcon(escalarIcono(iconoApagada));
            if (clip != null) clip.stop();
        }
    }

    private ImageIcon escalarIcono(ImageIcon icono) {
        Image imagen = icono.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }

    public JLabel getBocinaLabel() {
        return bocinaLabel;
    }
}
