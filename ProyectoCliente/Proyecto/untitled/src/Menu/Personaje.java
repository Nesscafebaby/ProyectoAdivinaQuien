package Menu;

import java.util.Objects;

public class Personaje {
    private int id;
    private String nombre;
    private String imagen;

    // Constructor completo (para BD o DAO)
    public Personaje(int id, String nombre, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    // Constructor usado por el cliente (sin ID)
    public Personaje(String nombre, String imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    // Equals y hashCode: necesarios para buscar personajes por nombre o para Map
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Personaje)) return false;
        Personaje that = (Personaje) o;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    // Para mostrar el nombre en listas si es necesario
    @Override
    public String toString() {
        return nombre;
    }
}
