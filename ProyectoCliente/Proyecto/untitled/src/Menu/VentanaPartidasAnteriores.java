package Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaPartidasAnteriores extends JFrame {

    public VentanaPartidasAnteriores(List<Partida> partidas) {
        setTitle("Partidas Anteriores");
        setSize(600, 400);
        setLocationRelativeTo(null);

        String[] columnas = {"Jugador", "Personaje Ganador", "Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (Partida p : partidas) {
            modelo.addRow(new Object[]{p.getNombreJugador(), p.getPersonajeGanador(), p.getFecha()});
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }
}

