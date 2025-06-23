import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class TestConexionBD {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/personajes", "root", "");
            System.out.println("✅ Conectado correctamente a la base de datos.");

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO partidas (jugador, personaje_ganador, fecha_hora) VALUES (?, ?, NOW())");
            stmt.setString(1, "Prueba");
            stmt.setString(2, "Leonidas");
            stmt.executeUpdate();

            stmt.close();
            conn.close();
            System.out.println("✔ Inserción realizada con éxito.");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar o insertar:");
            e.printStackTrace();
        }
    }
}
