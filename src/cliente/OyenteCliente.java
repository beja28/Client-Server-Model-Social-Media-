package cliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class OyenteCliente extends Thread {
    private Socket socket;

    public OyenteCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                // Escuchar mensajes del servidor
                String mensaje = dataInputStream.readUTF();
                System.out.println("Mensaje del servidor: " + mensaje);

                // Aquí puedes agregar lógica para procesar el mensaje recibido del servidor
                // por ejemplo, actualizar la interfaz de usuario, mostrar notificaciones, etc.
            }
        } catch (IOException e) {
            System.out.println("Error al escuchar mensajes del servidor: " + e.getMessage());
        }
    }
}
