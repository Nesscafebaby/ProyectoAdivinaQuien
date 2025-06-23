package Menu;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class VentanaInstrucciones extends JFrame {
    private JLabel etiquetaFechaHora;
    public VentanaInstrucciones() {
        setTitle("Instrucciones");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Fondo con imagen
        PanelConFondo fondo = new PanelConFondo("/imagenes/FondoSt.png");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        // Panel vertical central
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // Título opcional
        JLabel titulo = new JLabel("Instrucciones del Juego");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(titulo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel de texto con fondo semitransparente
        JPanel panelTexto = new JPanel();
        panelTexto.setOpaque(true);
        panelTexto.setBackground(new Color(0, 0, 0, 100));
        panelTexto.setLayout(new BorderLayout());
        panelTexto.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setOpaque(false);
        areaTexto.setFont(new Font("Serif", Font.PLAIN, 16));
        areaTexto.setForeground(Color.WHITE);
        areaTexto.setText("""
                Objetivo del juego
                El objetivo es adivinar, antes que el oponente, cuál es el personaje secreto que ha elegido. Para ello, se deben hacer preguntas estratégicas que permitan ir descartando personajes hasta identificar al correcto.
                
                Preparación del juego
                Cada jugador selecciona un personaje secreto de una lista de 34 personajes disponibles en total.
                
                Ambos jugadores comparten el mismo conjunto general de personajes.
                
                En el tablero principal, se muestran únicamente 24 personajes seleccionados aleatoriamente de esos 34.
                
                La selección del personaje debe mantenerse en secreto hasta que uno de los jugadores haga una suposición directa.
                
                Mecánica del juego
                En cada turno, el jugador puede hacer una pregunta que se responda únicamente con "sí" o "no".
                
                Las preguntas deben referirse a características visibles del personaje, como:
                
                Color o estilo de cabello
                
                Uso de gafas, sombrero u otros accesorios
                
                Rasgos faciales como barba o bigote
                
                Color de piel
                
                Sexo del personaje
                
                Según la respuesta, el jugador descarta personajes que no coincidan con la característica.
                
                Para realizar una suposición directa sobre el personaje enemigo, el jugador debe pulsar el botón “¿Es este?” ubicado debajo de cada personaje en el tablero.
                
                Reglas generales
                Las preguntas deben formularse de manera que puedan responderse con "sí" o "no".
                
                Solo se puede hacer una suposición directa por turno.
                
                El botón “¿Es este?” sirve para intentar adivinar el personaje del oponente.
                
                Si el jugador acierta, gana inmediatamente la partida.
                
                Si el jugador falla al hacer la suposición, pierde automáticamente la partida.
                
                El resultado se notifica de inmediato a ambos jugadores.
                
                Condiciones de victoria y derrota
                Un jugador gana cuando adivina correctamente el personaje secreto del oponente.
                
                Un jugador pierde si su personaje ha sido adivinado por el oponente o si realiza una suposición incorrecta.
                
                Al final de la partida, se mostrará en pantalla el personaje secreto del oponente, acompañado de una imagen de victoria o derrota, según el resultado.
                """);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setHighlighter(null);
        areaTexto.setBorder(null);

        panelTexto.add(areaTexto, BorderLayout.CENTER);
        panelTexto.setMaximumSize(new Dimension(500, 300));

        panelCentral.add(panelTexto);
        fondo.add(panelCentral, BorderLayout.CENTER);

        setVisible(true);
    }
    private void actualizarFechaHora() {
        java.util.Timer timer = new Timer();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                etiquetaFechaHora.setText(formato.format(new Date()));
            }
        }, 0, 1000);
    }
}
