package modelo.conexiones;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import modelo.mensajes.DescargarFotoReply;
import modelo.mensajes.ContenidoRedSocialRequest;
import modelo.mensajes.ContenidoRedSocialReply;
import modelo.mensajes.DescargarFotoRequest;
import modelo.mensajes.EliminarFoto;
import modelo.mensajes.PostearFoto;
import utils.OyenteCliente;
import modelo.mensajes.InicioSesionRequest;
import modelo.mensajes.InicioSesionReply;
import modelo.mensajes.Mensaje;



public class ConexionGeneral {
		
	protected final Socket socket;
    private final ObjectOutputStream stream;

    private boolean desconectado;

    public ConexionGeneral(Socket socket) {
        this.socket = socket;
        try {
            this.stream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException ex) {
            throw new UncheckedIOException("Error al abrir el stream de escritura", ex);
        }

        Thread thread = new Thread(new OyenteCliente(this.socket, this));
        thread.setName("Conexion - " + this.socket.getRemoteSocketAddress());
        thread.start();
    }

    
  
    public final void escribir(Mensaje mensaje) {
        try {
            this.stream.reset();
            this.stream.writeObject(mensaje);
        } catch (IOException ex) {
            if (this.socket.isConnected()) {
                System.err.println("Error al escribir un mensaje a " + this.socket.getRemoteSocketAddress());
                ex.printStackTrace();
            }
        }
    }
  

    public void leer(InicioSesionRequest msg) {
        throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(InicioSesionReply msg) {
    	throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(ContenidoRedSocialRequest msg) {
    	throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(ContenidoRedSocialReply msg) {
    	throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(PostearFoto msg) {
    	throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(EliminarFoto msg) {
    	throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(DescargarFotoRequest msg) {
    	throw new IllegalStateException("Operacion no permitida");
    }

    public void leer(DescargarFotoReply msg) {
        throw new IllegalStateException("Operacion no permitida");
    }

    
    public boolean desconectar() {
        if (!this.desconectado) {
            this.desconectado = true;
            try {
                this.socket.close();
            } catch (IOException ignored) {
            }

            return true;
        }

        return false;
    }

    public boolean estaAbierta() {
        return !this.socket.isClosed();
    }

    public static Socket conectar(String ip, int puerto) throws ConexionRuntimeException {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, puerto));
            return socket;
        } catch (IOException ex) {
            throw new ConexionRuntimeException("No se ha podido establecer conexion con " + ip + ":" + puerto, ex);
        }
    }
	
}
