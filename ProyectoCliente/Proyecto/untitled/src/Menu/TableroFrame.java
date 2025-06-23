package Menu;

import Comunicacion.SocketListener;
import Comunicacion.SocketObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

public class TableroFrame extends JFrame implements SocketObserver {
    private final Socket socket;
    private final SocketListener listener;
    private final String nombreJugador;
    private final Personaje personajeJugador;
    private Personaje personajeEnemigo;
    private List<Personaje> personajesVisibles;
    private final Map<JLabel, String> mapaImagenesOriginales = new HashMap<>();
    private PrintWriter out;
    private JTextArea areaMensajes;
    private JLabel etiquetaFechaHora;

    public TableroFrame(Personaje personaje, List<Personaje> personajes, Socket socket, String nombreJugador, SocketListener listener) {
        this.socket = socket;
        this.listener = listener;
        this.personajeJugador = personaje;
        this.nombreJugador = nombreJugador;
        this.personajesVisibles = new ArrayList<>(personajes);

        Collections.shuffle(this.personajesVisibles);
        this.personajesVisibles = this.personajesVisibles.stream().limit(24).collect(Collectors.toList());

        listener.agregarObserver(this);

        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Tablero de juego");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        PanelFondo fondo = new PanelFondo("/Imagenes/FondoEsc.png");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);
        ControlDeSonido sonido = new ControlDeSonido("src/Imagenes/Cancion.wav");
        JLabel lblBocina = sonido.getBocinaLabel();
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setOpaque(false);
        panelSur.add(lblBocina);
        fondo.add(panelSur, BorderLayout.SOUTH);

        areaMensajes = new JTextArea(10, 20);
        areaMensajes.setEditable(false);

        JPanel gridPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Personaje p : personajesVisibles) {
            JPanel personajePanel = new JPanel();
            personajePanel.setLayout(new BoxLayout(personajePanel, BoxLayout.Y_AXIS));
            personajePanel.setOpaque(false);

            String ruta = "/Personajes/" + p.getImagen();
            ImageIcon icon = new ImageIcon(getClass().getResource(ruta));
            Image img = icon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(img));

            mapaImagenesOriginales.put(imgLabel, ruta);

            JButton botonAccion = new JButton("¿Es este?");
            botonAccion.setFont(new Font("Arial", Font.PLAIN, 10));
            botonAccion.setAlignmentX(Component.CENTER_ALIGNMENT);
            botonAccion.setFocusable(false);

            botonAccion.addActionListener(e -> {
                enviarMensaje("SUPOSICION:" + p.getNombre());
                areaMensajes.append("Yo: ¿Tu personaje es " + p.getNombre() + "?\n");

                if (personajeEnemigo != null && p.getNombre().equalsIgnoreCase(personajeEnemigo.getNombre())) {
                    enviarMensaje("GANADOR:" + nombreJugador + "|" + personajeJugador.getNombre());

                    enviarMensaje("GANADOR:" + nombreJugador + "|" + personajeJugador.getNombre());

                }
            });

            imgLabel.addMouseListener(new MouseAdapter() {
                private boolean oculto = false;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!oculto) {
                        imgLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/imagenes/X.png"))
                                .getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH)));
                        oculto = true;
                    } else {
                        imgLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(mapaImagenesOriginales.get(imgLabel)))
                                .getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH)));
                        oculto = false;
                    }
                }
            });

            personajePanel.add(imgLabel);
            personajePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            personajePanel.add(botonAccion);
            gridPanel.add(personajePanel);
        }

        fondo.add(gridPanel, BorderLayout.CENTER);
        fondo.add(crearPanelDerecho(areaMensajes), BorderLayout.EAST);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperior.setOpaque(false);
        JLabel labelPersonajeJugador = new JLabel();
        ImageIcon iconJugador = new ImageIcon(getClass().getResource("/Personajes/" + personajeJugador.getImagen()));
        Image imgJugador = iconJugador.getImage().getScaledInstance(100, 120, Image.SCALE_SMOOTH);
        labelPersonajeJugador.setIcon(new ImageIcon(imgJugador));
        labelPersonajeJugador.setBorder(BorderFactory.createTitledBorder("Tu personaje: " + personajeJugador.getNombre()));
        panelSuperior.add(labelPersonajeJugador);
        fondo.add(panelSuperior, BorderLayout.NORTH);

        setVisible(true);
    }

    private void enviarMensaje(String msg) {
        if (out != null) {
            out.println(msg);
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
    @Override
    public void onMensajeRecibido(String mensaje) {
        if (mensaje.startsWith("PERSONAJE_ELEGIDO:")) {
            String[] partes = mensaje.substring("PERSONAJE_ELEGIDO:".length()).split("\\|");
            if (partes.length == 2) {
                personajeEnemigo = new Personaje(partes[0], partes[1]);
                areaMensajes.append("Personaje enemigo recibido.\n");
                SwingUtilities.invokeLater(this::prepararTablero);
            }
        } else if (mensaje.startsWith("SUPOSICION:")) {
            String intento = mensaje.substring("SUPOSICION:".length());
            if (intento.equalsIgnoreCase(personajeJugador.getNombre())) {
                enviarMensaje("GANASTE:true");
                areaMensajes.append("¡El oponente ha adivinado tu personaje!\n");

                SwingUtilities.invokeLater(() -> {
                    // Imagen del personaje del jugador
                    ImageIcon iconPersonaje = new ImageIcon(getClass().getResource("/Personajes/" + personajeJugador.getImagen()));
                    Image imgPersonaje = iconPersonaje.getImage().getScaledInstance(150, 180, Image.SCALE_SMOOTH);
                    JLabel labelPersonaje = new JLabel(new ImageIcon(imgPersonaje));
                    labelPersonaje.setBorder(BorderFactory.createTitledBorder("Tu personaje era: " + personajeJugador.getNombre()));

                    // Imagen de derrota
                    ImageIcon iconPerdiste = new ImageIcon(getClass().getResource("/Imagenes/Perdiste.png"));
                    Image imgPerdiste = iconPerdiste.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
                    JLabel labelDerrota = new JLabel(new ImageIcon(imgPerdiste));
                    labelDerrota.setAlignmentX(Component.CENTER_ALIGNMENT);

                    // Panel combinado
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.add(labelPersonaje);
                    panel.add(Box.createRigidArea(new Dimension(0, 10)));
                    panel.add(labelDerrota);

                    JOptionPane.showMessageDialog(this, panel, "¡Has perdido!", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });

            } else {
                areaMensajes.append("El oponente intentó con: " + intento + " y falló.\n");
            }
        } else if (mensaje.startsWith("GANASTE:")) {
            if (mensaje.substring("GANASTE:".length()).equalsIgnoreCase("true")) {
                areaMensajes.append("¡Felicidades! Adivinaste el personaje del oponente. 🎉\n");
                enviarMensaje("GANADOR:" + nombreJugador + "|" + personajeJugador.getNombre());
                areaMensajes.append("El personaje enemigo era: " + personajeEnemigo.getNombre());
                int opcion = JOptionPane.showOptionDialog(
                        this,
                        "¿Quieres volver a jugar?",
                        "Partida terminada",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Sí", "No"},
                        "Sí"
                );

                dispose(); // Cierra este frame

                if (opcion == JOptionPane.YES_OPTION) {
                    new MenuPrincipal(); // Regresa al menú principal
                }

                SwingUtilities.invokeLater(() -> {
                    // Mostrar personaje enemigo
                    ImageIcon iconPersonaje = new ImageIcon(getClass().getResource("/Personajes/" + personajeEnemigo.getImagen()));
                    Image imgPersonaje = iconPersonaje.getImage().getScaledInstance(150, 180, Image.SCALE_SMOOTH);
                    JLabel labelPersonaje = new JLabel(new ImageIcon(imgPersonaje));
                    labelPersonaje.setBorder(BorderFactory.createTitledBorder("El personaje enemigo era: " + personajeEnemigo.getNombre()));

                    // Imagen de "Ganaste"
                    ImageIcon iconGanador = new ImageIcon(getClass().getResource("/Imagenes/Ganador.png"));
                    Image imgGanador = iconGanador.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
                    JLabel labelGanador = new JLabel(new ImageIcon(imgGanador));
                    labelGanador.setAlignmentX(Component.CENTER_ALIGNMENT);

                    // Panel combinado
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.add(labelPersonaje);
                    panel.add(Box.createRigidArea(new Dimension(0, 10)));
                    panel.add(labelGanador);

                    JOptionPane.showMessageDialog(this, panel, "¡Ganaste!", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });
            }
        } else if (mensaje.startsWith("Emparejado con:")) {
            String oponente = mensaje.substring("Emparejado con:".length()).trim();
            areaMensajes.append("Te has emparejado con: " + oponente + "\n");
        } else {
            areaMensajes.append("Otro: " + mensaje + "\n");
        }
    }




    private JPanel crearPanelDerecho(JTextArea areaMensajes) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Preguntas predeterminadas
        String[] preguntas = {
                "¿Tu personaje tiene sombrero?",
                "¿Es hombre?",
                "¿Tiene lentes?",
                "¿Tiene el cabello rubio?",
                "¿Está sonriendo?",
                "¿Tiene barba o bigote?",
                "¿Usa accesorios?",
                "¿Tiene el cabello largo?",
                "¿Está usando gorra?",
                "¿Tiene ojos grandes?"
        };

        JComboBox<String> comboPreguntas = new JComboBox<>(preguntas);
        comboPreguntas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Botón para enviar pregunta seleccionada
        BotonRedondeado btnHacerPregunta = new BotonRedondeado("Hacer pregunta");
        btnHacerPregunta.addActionListener(e -> {
            String pregunta = (String) comboPreguntas.getSelectedItem();
            if (pregunta != null && !pregunta.isEmpty()) {
                areaMensajes.append("Yo pregunto: " + pregunta + "\n");
                enviarMensaje("PREGUNTA:" + pregunta);
            }
        });

        // Campo para escribir mensajes personalizados
        JTextField campoEntrada = new JTextField();
        campoEntrada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        BotonRedondeado botonEnviar = new BotonRedondeado("Enviar");
        botonEnviar.addActionListener(e -> {
            String msg = campoEntrada.getText().trim();
            if (!msg.isEmpty()) {
                out.println("Mensaje: " + msg);
                areaMensajes.append("Yo: " + msg + "\n");
                campoEntrada.setText("");
            }
        });

        // Botones de sí y no
        BotonRedondeado botonSi = new BotonRedondeado("Sí");
        botonSi.addActionListener(e -> {
            out.println("Respuesta: Sí");
            areaMensajes.append("Yo: Sí\n");
        });

        BotonRedondeado botonNo = new BotonRedondeado("No");
        botonNo.addActionListener(e -> {
            out.println("Respuesta: No");
            areaMensajes.append("Yo: No\n");
        });

        // Agregado al panel
        panel.add(Box.createVerticalGlue());
        panel.add(new JScrollPane(areaMensajes));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboPreguntas);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(btnHacerPregunta);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(campoEntrada);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(botonEnviar);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(botonSi);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(botonNo);

        return panel;
    }

    private void prepararTablero() {
        if (personajeEnemigo == null) return;

        if (!personajesVisibles.contains(personajeEnemigo)) {
            personajesVisibles.add(personajeEnemigo);
        }

        Collections.shuffle(personajesVisibles);
        personajesVisibles = personajesVisibles.stream().limit(24).collect(Collectors.toList());

        if (!personajesVisibles.contains(personajeEnemigo)) {
            personajesVisibles.set(0, personajeEnemigo);
        }

        JPanel gridPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapaImagenesOriginales.clear();

        for (Personaje p : personajesVisibles) {
            JPanel personajePanel = new JPanel();
            personajePanel.setLayout(new BoxLayout(personajePanel, BoxLayout.Y_AXIS));
            personajePanel.setOpaque(false);

            String ruta = "/Personajes/" + p.getImagen();
            ImageIcon icon = new ImageIcon(getClass().getResource(ruta));
            Image img = icon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            mapaImagenesOriginales.put(imgLabel, ruta);

            JButton botonAccion = new JButton("¿Es este?");
            botonAccion.setFont(new Font("Arial", Font.PLAIN, 10));
            botonAccion.setAlignmentX(Component.CENTER_ALIGNMENT);
            botonAccion.setFocusable(false);

            botonAccion.addActionListener(e -> {
                enviarMensaje("SUPOSICION:" + p.getNombre());
                areaMensajes.append("Yo: ¿Tu personaje es " + p.getNombre() + "?\n");

                if (p.getNombre().equalsIgnoreCase(personajeEnemigo.getNombre())) {
                    System.out.println("Enviando mensaje de ganador: GANADOR:" + nombreJugador + "|" + personajeJugador.getNombre());

                    enviarMensaje("GANADOR:" + nombreJugador + "|" + personajeJugador.getNombre());

                }
            });

            imgLabel.addMouseListener(new MouseAdapter() {
                private boolean oculto = false;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!oculto) {
                        imgLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/imagenes/X.png"))
                                .getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH)));
                        oculto = true;
                    } else {
                        imgLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(mapaImagenesOriginales.get(imgLabel)))
                                .getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH)));
                        oculto = false;
                    }
                }
            });

            personajePanel.add(imgLabel);
            personajePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            personajePanel.add(botonAccion);
            gridPanel.add(personajePanel);
        }

        getContentPane().add(gridPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void dispose() {
        listener.eliminarObserver(this);
        super.dispose();
    }
}

