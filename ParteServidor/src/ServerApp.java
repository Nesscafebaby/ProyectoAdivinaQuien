import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerApp {
    private static final int PORT = 5000;
    private static final Queue<ClientHandler> esperando = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciado en el puerto " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler cliente = new ClientHandler(socket);
                new Thread(cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<String> obtenerPersonajesDesdeBD() {
        List<String> personajes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/personajes", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nombre, imagen FROM personajes_g")) {
            while (rs.next()) {
                personajes.add(rs.getString("nombre") + "|" + rs.getString("imagen"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personajes;
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nombreJugador;
        private ClientHandler pareja;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar lista de personajes al cliente
                List<String> personajes = ServerApp.obtenerPersonajesDesdeBD();
                out.println("PERSONAJES:" + String.join(",", personajes));

                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("[" + socket.getInetAddress() + "] " + msg);

                    if (msg.startsWith("NOMBRE_JUGADOR:")) {
                        nombreJugador = msg.substring("NOMBRE_JUGADOR:".length());
                        emparejar();

                    } else if (msg.equals("PEDIR_PARTIDAS")) {
                        List<Partida> partidas = BaseDeDatos.obtenerPartidas();
                        StringBuilder respuesta = new StringBuilder("PARTIDAS:");

                        for (Partida p : partidas) {
                            respuesta.append(p.getNombreJugador()).append("|")
                                    .append(p.getPersonajeGanador()).append("|")
                                    .append(p.getFecha()).append(",");
                        }

                        if (!partidas.isEmpty()) {
                            respuesta.deleteCharAt(respuesta.length() - 1);
                        }

                        out.println(respuesta.toString());
                    }
                    else if (msg.startsWith("GANADOR:")) {
                        String[] partes = msg.substring("GANADOR:".length()).split("\\|", 2);
                        if (partes.length == 2) {
                            BaseDeDatos.registrarPartida(partes[0], partes[1]);
                            System.out.println("Partida registrada: " + partes[0] + " ganó con " + partes[1]);
                            if (pareja != null && pareja.out != null) {
                                pareja.out.println("PERDISTE");
                            }
                        }

                    } else {
                        // Reenviar mensaje al oponente
                        if (pareja != null && pareja.out != null) {
                            pareja.out.println(msg);
                        } else {
                            out.println("Esperando oponente...");
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + socket.getInetAddress());
            } finally {
                desconectar();
            }
        }


        private void emparejar() {
            for (ClientHandler otro : esperando) {
                if (otro != this) { // permite misma IP, pero no a sí mismo
                    esperando.remove(otro);
                    this.pareja = otro;
                    otro.pareja = this;

                    this.out.println("Emparejado con: " + (otro.nombreJugador != null ? otro.nombreJugador : "otro jugador"));
                    otro.out.println("Emparejado con: " + (this.nombreJugador != null ? this.nombreJugador : "otro jugador"));

                    // 👉 Agregamos log para verificar en consola quién se emparejó con quién
                    System.out.println("Emparejados: " +
                            (this.nombreJugador != null ? this.nombreJugador : "JugadorX") + " <--> " +
                            (otro.nombreJugador != null ? otro.nombreJugador : "JugadorY"));

                    return;
                }
            }

            esperando.offer(this);
            out.println("Esperando oponente...");
        }


        private void desconectar() {
            esperando.remove(this);
            try { socket.close(); } catch (IOException ignored) {}

            if (pareja != null && pareja.out != null) {
                pareja.out.println("Tu oponente se ha desconectado.");
                pareja.pareja = null;
            }
        }
    }
}
