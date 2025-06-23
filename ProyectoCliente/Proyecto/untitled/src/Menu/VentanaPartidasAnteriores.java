package Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class VentanaPartidasAnteriores extends JFrame {

    private List<Partida> partidasOriginales;
    private DefaultTableModel modelo;

    public VentanaPartidasAnteriores(List<Partida> partidas) {
        this.partidasOriginales = partidas;

        setTitle("Partidas Anteriores");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con campo de búsqueda y botón
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        JTextField campoBusqueda = new JTextField(20);
        JButton botonBuscar = new JButton("Buscar por nombre");

        panelBusqueda.add(new JLabel("Nombre del jugador:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(botonBuscar);
        add(panelBusqueda, BorderLayout.NORTH);

        // Tabla y modelo
        String[] columnas = {"Jugador", "Personaje Ganador", "Fecha"};
        modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Llenar la tabla inicialmente
        llenarTabla(partidas);

        // Acción del botón de búsqueda
        botonBuscar.addActionListener(e -> {
            String nombre = campoBusqueda.getText().trim().toLowerCase();
            if (nombre.isEmpty()) {
                llenarTabla(partidasOriginales); // mostrar todas si campo vacío
            } else {
                List<Partida> filtradas = partidasOriginales.stream()
                        .filter(p -> p.getNombreJugador().toLowerCase().contains(nombre))
                        .collect(Collectors.toList());
                llenarTabla(filtradas);
            }
        });

        setVisible(true);
    }

    private void llenarTabla(List<Partida> partidas) {
        modelo.setRowCount(0); // limpiar
        for (Partida p : partidas) {
            modelo.addRow(new Object[]{p.getNombreJugador(), p.getPersonajeGanador(), p.getFecha()});
        }
    }
}
