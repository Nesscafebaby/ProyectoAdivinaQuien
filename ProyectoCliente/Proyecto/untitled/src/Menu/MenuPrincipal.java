package Menu;

import Comunicacion.SocketListener;
import Comunicacion.SocketObserver;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends JFrame implements SocketObserver {

    private JLabel etiquetaFechaHora;
    private Socket socket;
    private PrintWriter out;
    private SocketListener listener;

    public MenuPrincipal() {
        setTitle("Adivina Quién - Menú Principal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        PanelConFondo fondo = new PanelConFondo("/imagenes/FondoMenu.png");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        etiquetaFechaHora = new JLabel();
        etiquetaFechaHora.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaFechaHora.setFont(new Font("Arial", Font.BOLD, 14));
        fondo.add(etiquetaFechaHora, BorderLayout.NORTH);
        actualizarFechaHora();

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);

        BotonRedondeado btnIniciarJuego = crearBoton("Iniciar juego");
        BotonRedondeado btnInstrucciones = crearBoton("Ver instrucciones");
        BotonRedondeado btnJuegosAnteriores = crearBoton("Ver juegos anteriores");
        BotonRedondeado btnCreditos = crearBoton("Ver créditos");

        btnIniciarJuego.addActionListener(e -> {
            String ip = JOptionPane.showInputDialog(this, "Ingresa la IP del servidor:", "127.0.0.1");
            if (ip != null && !ip.trim().isEmpty()) {
                String nombreJugador = JOptionPane.showInputDialog(this, "Ingresa tu nombre:");
                if (nombreJugador != null && !nombreJugador.trim().isEmpty()) {
                    iniciarConexion(ip.trim(), nombreJugador.trim());
                }
            }
        });

        btnJuegosAnteriores.addActionListener(e -> {
            if (socket == null || socket.isClosed() || out == null) {
                String ip = JOptionPane.showInputDialog(this, "Ingresa la IP del servidor:", "127.0.0.1");
                if (ip == null || ip.trim().isEmpty()) return;

                try {
                    socket = new Socket(ip.trim(), 5000);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    listener = new SocketListener(socket);
                    listener.agregarObserver(this);
                    listener.start();

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo conectar al servidor.",
                            "Error de conexión",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return;
                }
            }

            out.println("PEDIR_PARTIDAS");
        });

        btnCreditos.addActionListener(e -> new VentanaCreditos());
        btnInstrucciones.addActionListener(e -> new VentanaInstrucciones());

        panelBotones.add(crearEspaciador(10));
        panelBotones.add(btnIniciarJuego);
        panelBotones.add(crearEspaciador(10));
        panelBotones.add(btnInstrucciones);
        panelBotones.add(crearEspaciador(10));
        panelBotones.add(btnJuegosAnteriores);
        panelBotones.add(crearEspaciador(10));
        panelBotones.add(btnCreditos);

        JPanel contenedorCentral = new JPanel(new GridBagLayout());
        contenedorCentral.setOpaque(false);
        contenedorCentral.add(panelBotones);
        fondo.add(contenedorCentral, BorderLayout.CENTER);

        ControlDeSonido sonido = new ControlDeSonido("src/Imagenes/Cancion.wav");
        JLabel lblBocina = sonido.getBocinaLabel();

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setOpaque(false);
        panelSur.add(lblBocina);
        fondo.add(panelSur, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void iniciarConexion(String ip, String nombreJugador) {
        try {
            socket = new Socket(ip, 5000);
            listener = new SocketListener(socket);
            listener.agregarObserver(this);
            listener.start();

            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("NOMBRE_JUGADOR:" + nombreJugador);

            new SeleccionarPersonajeFrame(socket, listener, nombreJugador);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar al servidor en " + ip,
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    private Component crearEspaciador(int altura) {
        return Box.createRigidArea(new Dimension(0, altura));
    }

    private BotonRedondeado crearBoton(String texto) {
        BotonRedondeado boton = new BotonRedondeado(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setPreferredSize(new Dimension(300, 50));
        return boton;
    }

    @Override
    public void onMensajeRecibido(String mensaje) {
        if (mensaje.startsWith("PARTIDAS:")) {
            String data = mensaje.substring("PARTIDAS:".length());
            List<Partida> partidas = new ArrayList<>();

            for (String registro : data.split(",")) {
                String[] partes = registro.split("\\|");
                if (partes.length == 3) {
                    partidas.add(new Partida(partes[0], partes[1], partes[2]));
                }
            }

            SwingUtilities.invokeLater(() -> new VentanaPartidasAnteriores(partidas));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuPrincipal::new);
    }
}
