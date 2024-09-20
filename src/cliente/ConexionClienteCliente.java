package cliente;

import java.net.Socket;

import modelo.mensajes.DescargarFotoReply;
import utils.Foto;
import modelo.mensajes.DescargarFotoRequest;
import modelo.User;
import modelo.conexiones.ConexionRuntimeException;
import modelo.conexiones.ConexionGeneral;


/*
 * Esta conexion se usa cuando se quiere descargar una foto entre dos usuarios
 * es decir, conecta a dos clientes entre si
 * */
public class ConexionClienteCliente extends ConexionGeneral {

	private final Cliente cliente;
    private final User user;

    
    // --- Constructores ---
    public ConexionClienteCliente(String ip, int puerto, Cliente cliente, User user) {
        super(ConexionGeneral.conectar(ip, puerto));
        this.cliente = cliente;
        this.user = user;
    }

    public ConexionClienteCliente(Socket socket, Cliente cliente) {
        super(socket);
        this.cliente = cliente;
        this.user = null;
    }

    
    // --- Funciones ---
    
    @Override
    public boolean desconectar() {
        if (super.desconectar()) {
            this.cliente.clienteDesconectado(this);
            return true;
        }
        return false;
    }
    

    //Retorna el usuario asociado a esta conexion
    public User getUser() {
        return this.user;
    }

    
    //Funcion para leer un mensaje de solicitud de descarga de foto
    @Override
    public void leer(DescargarFotoRequest mensaje) {
        Foto foto = this.cliente.getFoto(mensaje.getId());
        if (foto != null) {
            byte[] contenido = this.cliente.cargarFoto(foto);
            this.escribir(new DescargarFotoReply(foto.getNombre(), mensaje.getRuta(), contenido));
        } else {
            this.escribir(new DescargarFotoReply(null, mensaje.getRuta(), null));
        }
    }

    
    //Funcion para leer un mensaje de respuesta de descarga de foto.
    @Override
    public void leer(DescargarFotoReply mensaje) {
        if (mensaje.getFoto() == null) {
            throw new ConexionRuntimeException("La foto que se ha pedido no se encuentra");
        }
        this.cliente.finalizarDescarga(mensaje.getNombre(), mensaje.getRuta(), mensaje.getFoto());
        this.desconectar();
    }
	
}
