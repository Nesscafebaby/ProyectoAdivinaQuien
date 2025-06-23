package Comunicacion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketListener extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final List<SocketObserver> observers = new CopyOnWriteArrayList<>();

    public SocketListener(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void agregarObserver(SocketObserver observer) {
        observers.add(observer);
    }

    public void eliminarObserver(SocketObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void run() {
        try {
            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                for (SocketObserver obs : observers) {

                    obs.onMensajeRecibido(mensaje);
                }
            }
        } catch (IOException e) {
            System.err.println("Conexión cerrada o error en SocketListener: " + e.getMessage());
        }
    }
}
