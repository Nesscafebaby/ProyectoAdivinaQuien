import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BaseDeDatos {

    private static final String URL = "jdbc:mysql://localhost/personajes";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void registrarPartida(String jugador, String personajeGanador) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String sql = "INSERT INTO partidas (jugador, personaje_ganador, fecha_hora) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, jugador);
            stmt.setString(2, personajeGanador);

            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stmt.setString(3, fechaHora);

            stmt.executeUpdate();
            stmt.close();

            System.out.println("Partida registrada correctamente en la base de datos.");

        } catch (SQLException e) {
            System.err.println("Error al registrar la partida:");
            e.printStackTrace();
        }
    }

    public static List<Partida> obtenerPartidas() {
        List<Partida> lista = new ArrayList<>();
        String sql = "SELECT jugador, personaje_ganador, fecha_hora FROM partidas ORDER BY fecha_hora DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Partida(
                        rs.getString("jugador"),
                        rs.getString("personaje_ganador"),
                        rs.getString("fecha_hora")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener partidas:");
            e.printStackTrace();
        }
        return lista;
    }
}
