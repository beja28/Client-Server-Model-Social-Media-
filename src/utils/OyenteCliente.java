package utils;


import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.SocketException;

import modelo.conexiones.ConexionGeneral;
import modelo.mensajes.Mensaje;



/**
 * Clase OyenteCliente que implementa Runnable y Closeable para manejar
 * la recepción de mensajes desde un Socket en un hilo separado.
 */
public class OyenteCliente implements Runnable, Closeable {

    // Socket a través del cual el servidor se comunica con el cliente.
    private final Socket socket;
    // Interfaz para la gestión de conexiones.
    private final ConexionGeneral conexion;

    /**
     * Constructor para crear una instancia de OyenteCliente.
     * @param socket Socket asociado con el cliente.
     * @param conexion Conexión general para manejar mensajes.
     */
    public OyenteCliente(Socket socket, ConexionGeneral conexion) {
        this.socket = socket;
        this.conexion = conexion;
    }

    /**
     * Método run que se ejecuta en un nuevo hilo para escuchar los mensajes entrantes.
     */
    @Override
    public void run() {
        try (ObjectInputStream stream = new ObjectInputStream(this.socket.getInputStream())) {
            // Bucle que se mantiene activo hasta que el socket se cierra.
            while (!this.socket.isClosed()) {
                Mensaje mensaje;
                try {
                    // Intenta leer un mensaje del stream.
                    mensaje = (Mensaje) stream.readObject();
                } catch (ClassNotFoundException ex) {
                    // Captura la excepción cuando no se reconoce la clase del mensaje.
                    System.err.println("Mensaje desconocido");
                    ex.printStackTrace();
                    continue; // Continúa al siguiente mensaje.
                }

                // Procesa el mensaje leído.
                mensaje.procesarMensaje(this.conexion);
            }
        } catch (EOFException ex) {
            // Maneja el fin de archivo para cerrar la conexión correctamente.
        } catch (SocketException ex) {
            // Captura excepciones de socket para manejar errores de red.
            if (!this.socket.isClosed()) {
                throw new UncheckedIOException("Error al leer", ex);
            }
        } catch (IOException ex) {
            // Maneja otros errores de I/O lanzando una excepción unchecked.
            throw new UncheckedIOException("Error al leer", ex);
        }  finally {
            // Desconecta la conexión al finalizar el hilo.
            this.conexion.desconectar();
        }
    }

    /**
     * Método close para cerrar el socket del cliente.
     */
    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
