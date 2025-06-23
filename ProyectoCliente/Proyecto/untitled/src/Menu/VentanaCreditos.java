package Menu;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class VentanaCreditos extends JFrame {
    private JLabel etiquetaFechaHora;
    public VentanaCreditos() {
        setTitle("Créditos");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        // Panel con fondo
        PanelConFondo fondo = new PanelConFondo("/imagenes/FondoSt.png");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        // Panel central vertical
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // Logo escalado
        ImageIcon logoOriginal = new ImageIcon(getClass().getResource("/imagenes/UAALogo.png"));
        Image logoEscalado = logoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(logoEscalado));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(logo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel para texto con fondo suave
        JPanel panelTexto = new JPanel();
        panelTexto.setOpaque(true);
        panelTexto.setBackground(new Color(0, 0, 0, 100)); // negro transparente
        panelTexto.setLayout(new BorderLayout());
        panelTexto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea areaCreditos = new JTextArea();
        areaCreditos.setEditable(false);
        areaCreditos.setOpaque(false);
        areaCreditos.setFont(new Font("Serif", Font.BOLD, 18));
        areaCreditos.setForeground(Color.WHITE);
        areaCreditos.setText("""
                                 Adivina Quién
                       Universidad Autónoma de Aguascalientes
                       Departamento de Sistemas Electrónicos
                        Materia: Programación III (JAVA SE)
                                   Semestre: 4A

                                   Integrantes:
                              - Romain Elioenai Diaz De Leon
                              - Cesar Yael Ortiz
                              - Eduardo Pedroza López

                        Profesora: Dra. Georgina Salazar Partida
                                      Fecha: Junio 2025
                """);
        areaCreditos.setLineWrap(true);
        areaCreditos.setWrapStyleWord(true);
        areaCreditos.setHighlighter(null);
        areaCreditos.setBorder(null);

        panelTexto.add(areaCreditos, BorderLayout.CENTER);
        panelTexto.setMaximumSize(new Dimension(480, 250));

        panelCentral.add(panelTexto);
        panelCentral.setAlignmentX(Component.CENTER_ALIGNMENT);

        fondo.add(panelCentral, BorderLayout.CENTER);
        setVisible(true);
    }

    private void actualizarFechaHora() {
        Timer timer = new Timer();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                etiquetaFechaHora.setText(formato.format(new Date()));
            }
        }, 0, 1000);
    }
}
