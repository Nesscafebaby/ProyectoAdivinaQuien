package Menu;

import Comunicacion.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class SeleccionarPersonajeFrame extends JFrame implements SocketObserver {
    private final Socket socket;
    private final SocketListener listener;
    private final String nombreJugador;
    private PrintWriter out;
    private boolean esperandoOponente = true;
    private Personaje personajeEnemigo = null;
    private JLabel etiquetaFechaHora;
    private JLabel previewLabel;
    private JList<String> listaNombres;
    private Personaje personajeSeleccionado;
    private List<Personaje> personajes = new ArrayList<>();
    private final Map<Personaje, JPanel> mapaPersonajeVisual = new HashMap<>();

    public SeleccionarPersonajeFrame(Socket socket, SocketListener listener, String nombreJugador) {
        this.socket = socket;
        this.listener = listener;
        this.nombreJugador = nombreJugador;
        listener.agregarObserver(this);
        inicializarSalida();
        construirVentana();
    }

    private void inicializarSalida() {
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void construirVentana() {
        setTitle("Seleccionar Personaje");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        PanelFondo fondo = new PanelFondo("/imagenes/FondoEsc.png");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        fondo.add(new JPanel(), BorderLayout.CENTER); // Placeholder que se reemplaza luego

        fondo.add(crearPanelDerecho(), BorderLayout.EAST);
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
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        previewLabel = new JLabel();
        previewLabel.setPreferredSize(new Dimension(120, 140));
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.add(previewLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        listaNombres = new JList<>(new DefaultListModel<>());
        listaNombres.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarDesdeLista(listaNombres.getSelectedValue());
            }
        });
        panel.add(new JScrollPane(listaNombres));

        BotonRedondeado btnAleatorio = new BotonRedondeado("Aleatorio");
        btnAleatorio.addActionListener(e -> seleccionarAleatorio());

        BotonRedondeado btnAceptar = new BotonRedondeado("Aceptar");
        btnAceptar.addActionListener(e -> confirmarSeleccion());

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnAleatorio);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnAceptar);

        return panel;
    }

    private void seleccionarDesdeLista(String nombre) {
        personajeSeleccionado = personajes.stream()
                .filter(p -> p.getNombre().equals(nombre))
                .findFirst().orElse(null);
        actualizarVistaPrevia();
        actualizarBordesSeleccion();
    }

    private void seleccionarAleatorio() {
        if (!personajes.isEmpty()) {
            personajeSeleccionado = personajes.get(new Random().nextInt(personajes.size()));
            actualizarVistaPrevia();
            listaNombres.setSelectedValue(personajeSeleccionado.getNombre(), true);
            actualizarBordesSeleccion();
        }
    }

    private void confirmarSeleccion() {
        if (personajeSeleccionado != null) {
            enviarMensaje("PERSONAJE_ELEGIDO:" + personajeSeleccionado.getNombre() + "|" + personajeSeleccionado.getImagen());
            System.out.println("Esperando al personaje del oponente...");
            esperandoOponente = true;
            // NO lanzamos aún el TableroFrame hasta recibir el personaje enemigo
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un personaje primero.");
        }
    }

    private void actualizarVistaPrevia() {
        if (personajeSeleccionado != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Personajes/" + personajeSeleccionado.getImagen()));
            Image img = icon.getImage().getScaledInstance(120, 140, Image.SCALE_SMOOTH);
            previewLabel.setIcon(new ImageIcon(img));

        }
    }

    private void actualizarBordesSeleccion() {
        for (Map.Entry<Personaje, JPanel> entry : mapaPersonajeVisual.entrySet()) {
            JPanel panel = entry.getValue();
            panel.setBorder(BorderFactory.createLineBorder(
                    entry.getKey().equals(personajeSeleccionado) ? Color.GREEN : Color.RED, 2));
        }
    }

    private void enviarMensaje(String msg) {
        if (out != null) out.println(msg);
    }

    @Override
    public void onMensajeRecibido(String mensaje) {
        if (mensaje.startsWith("PERSONAJES:")) {
            String data = mensaje.substring("PERSONAJES:".length());
            personajes = new ArrayList<>();
            for (String entry : data.split(",")) {
                String[] partes = entry.split("\\|");
                if (partes.length == 2) {
                    personajes.add(new Personaje(partes[0], partes[1]));
                }
            }
            SwingUtilities.invokeLater(this::cargarPersonajes);

        } else if (mensaje.startsWith("PERSONAJE_ELEGIDO:")) {
            String[] partes = mensaje.substring("PERSONAJE_ELEGIDO:".length()).split("\\|");
            if (partes.length == 2) {
                personajeEnemigo = new Personaje(partes[0], partes[1]);
                System.out.println("Personaje del oponente recibido: " + personajeEnemigo.getNombre());

                // Aquí decides si lanzar el TableroFrame
                if (personajeSeleccionado != null && esperandoOponente) {
                    SwingUtilities.invokeLater(this::iniciarTablero);
                }
            }
        }
    }


    private void iniciarTablero() {
        if (esperandoOponente && personajeSeleccionado != null && personajeEnemigo != null) {
            esperandoOponente = false;
            new TableroFrame(personajeSeleccionado, personajes, socket, nombreJugador, listener);
            dispose();
        }
    }

    private void cargarPersonajes() {
        mapaPersonajeVisual.clear();
        DefaultListModel<String> modeloLista = new DefaultListModel<>();

        JPanel panelGrid = new JPanel(new GridLayout(6, 6, 10, 10));
        panelGrid.setOpaque(false);
        panelGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Personaje p : personajes) {
            JLabel lbl = crearImagenPersonaje(p);
            JPanel contenedor = crearContenedor(lbl, p);

            mapaPersonajeVisual.put(p, contenedor);
            panelGrid.add(contenedor);
            modeloLista.addElement(p.getNombre());
        }

        // Rellenar con espacios vacíos si hay menos de 36
        for (int i = personajes.size(); i < 36; i++) {
            panelGrid.add(new JPanel() {{ setOpaque(false); }});
        }

        listaNombres.setModel(modeloLista);
        getContentPane().remove(0); // Quitar placeholder
        getContentPane().add(panelGrid, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JLabel crearImagenPersonaje(Personaje p) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/Personajes/" + p.getImagen()));
        Image img = icon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
        JLabel lbl = new JLabel(new ImageIcon(img));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setToolTipText(p.getNombre());
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                personajeSeleccionado = p;
                actualizarVistaPrevia();
                listaNombres.setSelectedValue(p.getNombre(), true);
                actualizarBordesSeleccion();
            }
        });
        return lbl;
    }

    private JPanel crearContenedor(JLabel lbl, Personaje p) {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setPreferredSize(new Dimension(80, 100));
        contenedor.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        contenedor.setOpaque(false);
        contenedor.add(lbl);
        return contenedor;
    }

    @Override
    public void dispose() {
        listener.eliminarObserver(this);
        super.dispose();
    }
}
