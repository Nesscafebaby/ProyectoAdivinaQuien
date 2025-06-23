package Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonajeDAO {

    public List<Personaje> obtenerTodos() {
        List<Personaje> personajes = new ArrayList<>();
        String sql = "SELECT id, nombre, imagen FROM personajes_g";

        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String imagen = rs.getString("imagen");
                personajes.add(new Personaje(id, nombre, imagen));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener personajes: " + e.getMessage());
        }

        return personajes;
    }
}
