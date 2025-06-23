public class Partida {
    private final String nombreJugador;
    private final String personajeGanador;
    private final String fecha;

    public Partida(String nombreJugador, String personajeGanador, String fecha) {
        this.nombreJugador = nombreJugador;
        this.personajeGanador = personajeGanador;
        this.fecha = fecha;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public String getPersonajeGanador() {
        return personajeGanador;
    }

    public String getFecha() {
        return fecha;
    }
}

