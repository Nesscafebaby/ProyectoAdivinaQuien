package Comunicacion;

import java.net.Socket;

public class AppController {
    private static AppController instancia;

    private Socket socket;
    private SocketListener listener;
    private String nombreJugador;

    private AppController() {}

    public static AppController getInstancia() {
        if (instancia == null) {
            instancia = new AppController();
        }
        return instancia;
    }

    public void inicializarConexion(String ip, int puerto, String nombre) throws Exception {
        if (socket == null || socket.isClosed()) {
            this.socket = new Socket(ip, puerto);
            this.listener = new SocketListener(socket);
            this.listener.start();
        }
        this.nombreJugador = nombre;
    }

    public Socket getSocket() {
        return socket;
    }

    public SocketListener getListener() {
        return listener;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void cerrarTodo() {
        try {
            if (listener != null) listener.interrupt();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
