package cliente;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.swing.SwingUtilities;

import cliente.vista.InicioSesionView;
import modelo.conexiones.ConexionRuntimeException;
import modelo.LockTicket;
import modelo.RedSocial;
import modelo.User;
import modelo.mensajes.ContenidoRedSocialRequest;
import modelo.mensajes.DescargarFotoRequest;
import modelo.mensajes.EliminarFoto;
import modelo.mensajes.InicioSesionRequest;
import modelo.mensajes.PostearFoto;
import servidor.Servidor;
import utils.Foto;
import utils.OyenteServidor;

import java.util.HashMap;
import java.util.Map;

public class Cliente {
	//A cada cliente se le suma un valor para que no tengan el mismo puerto
	public static final int PUERTO = 35000;
	public final static boolean DEBUG = false;	//Para decidir si se quieren mostrar los mensajes de debug o no

    private ConexionClienteConServidor servidor;
    private OyenteServidor oyenteServer;

    private User user;
    private RedSocial redSocial;
       
    //Conexiones con otros clientes de la red, para intercambiar fotos
    private final Map<Integer, ConexionClienteCliente> conexionesConClientes = new HashMap<>();
    private final LockTicket lockConexiones = new LockTicket();
    
    private PropertyChangeListener vistaGeneral;
    
    public static void main(String[] args) {
    	
    	DebugClienteConsola.iniCLiente(DEBUG);
    	
        SwingUtilities.invokeLater(() -> new InicioSesionView(new Cliente()));
    }

    
    public void setVista(PropertyChangeListener vista) {
        this.vistaGeneral = vista;
    }

    public void iniciarSesion(String usuario) {
        if (this.user != null) {
            throw new IllegalStateException("Ya se ha iniciado sesion con este usuario");
        }
        if (this.servidor == null || !this.servidor.estaAbierta()) {
            this.servidor = new ConexionClienteConServidor(Servidor.PUERTO, this);
        }

        this.servidor.escribir(new InicioSesionRequest(usuario));
    }

    public void cerrarSesion() {
    	DebugClienteConsola.debugInfo(this.user.getNombre(), "Cerrando sesion", DEBUG);
        this.servidor.desconectar();
    }
    
    
    public void actualizarRedSocial() {
        this.servidor.escribir(new ContenidoRedSocialRequest());
    }

    public void registrarFoto(String nombre, String imagePath) {
    	DebugClienteConsola.debugInfo(this.user.getNombre(), "Registrando foto", DEBUG);
        this.servidor.escribir(new PostearFoto(nombre, imagePath));
    }

    public void eliminarFoto(Foto foto) {
    	DebugClienteConsola.debugInfo(this.user.getNombre(), "Eliminando foto", DEBUG);
        this.servidor.escribir(new EliminarFoto(foto.getId()));
    }
	
    
    public boolean descargarFoto(Foto foto, String imagePath) {
        ConexionClienteCliente cliente;
        
        //Importante se adquiere el lock
        this.lockConexiones.lock();

        try {
            //Conexion P2P, conectamos el cliente que pide descargar la foto con el propietario
            cliente = this.conexionesConClientes.get(foto.getIdPropietario());
            
            //El cliente no puede ser nulo, el user tmp y el user tmb tiene que estar conectado
            if (cliente == null) {
                User user = this.redSocial.getUsuarios().get(foto.getIdPropietario());
                if (user == null) {
                    throw new IllegalArgumentException("User con id: " + foto.getIdPropietario() + "No se encuentra");
                }
                if (user.getIp() == null) {
                    return false;
                }
                
                //Si se cumplen todas las condiciones se crea la conexion
                cliente = new ConexionClienteCliente(user.getIp(), Cliente.PUERTO + user.getId(), this, user);
            }
        } finally {
            this.lockConexiones.unlock();	//Se suelta lock
        }
        
        DebugClienteConsola.debugInfo(this.user.getNombre(), "Realizando peticion para descargar foto", DEBUG);
        cliente.escribir(new DescargarFotoRequest(foto.getId(), imagePath));
        return true;
    }
	

    
    void inicioSesion(User user) {
        User prev = this.user;
        this.user = user;
        this.vistaGeneral.propertyChange(new PropertyChangeEvent(this, "user", prev, user));
        this.abrirOyenteServidor();
    }
    

    private void abrirOyenteServidor() {
    	DebugClienteConsola.debugInfo(this.user.getNombre(), "Abriendo OyenteServidor", DEBUG);
    	
        if (this.oyenteServer != null) {
            this.oyenteServer.close();
        }        
        
        this.oyenteServer = new OyenteServidor(Cliente.PUERTO + this.user.getId(), (socket) -> {
    		
    		/*
    		 * Se implementa la funcion que va a recibir el consumer, en este caso cada vez que se realiza una nueva
    		 * conexion se crea una nueva conexion de tipo cliente - cliente    		
    		*/
    		    		    		
        	DebugClienteConsola.debugInfo(this.user.getNombre(), "Conexion aceptada desde: " + socket.getInetAddress().getHostAddress(), DEBUG);
    		
        	
    		if (socket.isConnected()) {
    	        new ConexionClienteCliente(socket, this);
    	    } else {
    	    	DebugClienteConsola.debugError(this.user.getNombre(), "El socket no se ha podido conectar", DEBUG);
    	    }
    	});
    	        
        
        Thread thread = new Thread(this.oyenteServer);
        thread.setName("Conexion User" + this.user.getId() + " - Sever");
        thread.start();
    }

    
    void nuevaRedSocial(RedSocial RedSocial) {
    	RedSocial prev = this.redSocial;
        this.redSocial = RedSocial;
        this.vistaGeneral.propertyChange(new PropertyChangeEvent(this, "redSocial", prev, RedSocial));
    }    

    
    void servidorDesconectado() {
    	    	
        User prev = this.user;
        this.user = null;
        
        //Importante cerrar el OyenteServidor si esta creado
        if (this.oyenteServer != null) {
            this.oyenteServer.close();
            this.oyenteServer = null;
        }
        this.vistaGeneral.propertyChange(new PropertyChangeEvent(this, "user", prev, null));
        
        DebugClienteConsola.debugInfo(prev.getNombre(), "El cliente se ha desconectado del servidor", DEBUG);
    }

    
    protected Foto getFoto(int id) {
        return this.redSocial.getFotos().get(id);
    }

    
    byte[] cargarFoto(Foto foto) {
    	DebugClienteConsola.debugInfo(this.user.getNombre(), "Cargando foto: " + foto.getNombre(), DEBUG);
    	
        if (foto.getIdPropietario() == this.user.getId()) {
        	
            File file = new File(foto.getRuta());
            if (file.isFile()) {
                try {
                    return Files.readAllBytes(file.toPath());
                } catch (IOException ex) {
                    throw new ConexionRuntimeException("No se ha podido cargar la foto", ex);
                }
            }
        }
        
        return null;
    }    

    
    void clienteDesconectado(ConexionClienteCliente conexion) {
    	    	
        User user = conexion.getUser();
        
        //Si hay un usuario con esta conexion...
        if (user != null) {
            this.lockConexiones.lock();
            try {
                this.conexionesConClientes.remove(conexion.getUser().getId());
            } finally {
                this.lockConexiones.unlock();
            }
        }
        
        DebugClienteConsola.debugInfo(this.user.getNombre(), "Cliente desconectado del servidor", DEBUG);
    }

    
    public void finalizarDescarga(String nombreFoto, String imagePath, byte[] foto) {
    	
        if (foto != null) {
            File file = new File(imagePath);
            try {
            	/*
            	 * Se escriben los bytes de la foto en el nuevo "file" que se ha creado
            	 * StandardOpenOption.CREATE es para que se cree el fichero si no se ha creado
            	 * porque a veces da error
            	 */
                Files.write(file.toPath(), foto, StandardOpenOption.CREATE);
            } catch (IOException ex) {
                this.vistaGeneral.propertyChange(new PropertyChangeEvent(this, "foto_descargada", null, null));
                //Runtime Exeption
                throw new ConexionRuntimeException("No se ha podido completar la descarga", ex);
            }

            //Una vez que se ha descargado, se registra en el servidor
            this.servidor.escribir(new PostearFoto(nombreFoto, imagePath));
            this.vistaGeneral.propertyChange(new PropertyChangeEvent(this, "foto_descargada", null, file.getName()));
        } else {
            this.vistaGeneral.propertyChange(new PropertyChangeEvent(this, "foto_descargada", null, null));
        }
        
        DebugClienteConsola.debugInfo(this.user.getNombre(), "Ha completado la descarga de la foto con nombre: " + nombreFoto, DEBUG);
    }
    
}

