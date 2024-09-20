package servidor;

import java.util.concurrent.atomic.AtomicInteger;


import modelo.RedSocial;
import modelo.User;
import utils.OyenteServidor;

public class Servidor {
	
	public static final int N_MAX_CLIENTES = 8;	//Numero maximo de clientes a la vez, por ejemplo 8
    public static final int PUERTO = 30001;	//Numero elevado de puerto para evitar que se este usando
    
    
    private final RedSocial redSocial;
    //Hemos decido implementar la exclusion mutua con un AtomicInteger
    private final AtomicInteger nClientesConectados;
    
    
    public Servidor() {
    	this.redSocial = new RedSocial();
    	this.nClientesConectados = new AtomicInteger();
    }
    
    
    //Se crea una instancia del servidor y incialializa  
    public static void main(String[] args) {
    	Servidor servidor = new Servidor();
        servidor.iniciar();
    }

    
    //Se crea un hilo de OyenteServidor, que crea el socket del servidor y escucha constantemente en el puerto
    public void iniciar() {
    	Thread thread = new Thread(new OyenteServidor(Servidor.PUERTO, (socket) -> {
    		
    		/*
    		 * IMPORTANTE AQUI IMPLEMENTAMOS LA FUNCION QUE TENDRA EL CONSUMER, en la clase
    		 * de OyenteServidor explicamos el porque hemos decidido usarlo    		
    		*/
    		    		    		
    		ServerEstadoConsola.log("INFO", "Conexion aceptada desde: " + socket.getInetAddress().getHostAddress());
    		
    		if (socket.isConnected()) {
    	        new ConexionServidorConCliente(this, socket);
    	    } else {
    	    	ServerEstadoConsola.log("ERROR", "Socket de cliente no conectado");
    	    }    		
    		
    	}));
    	
        thread.setName("[SERVIDOR]");
        thread.start();
    }
    
    public void eliminarFoto(int idLibro, int idPropietario) {
        this.redSocial.eliminarFoto(idLibro, idPropietario);
    }


    public void anyadirFoto(String nombre, String ruta, int idPropietario) {
        this.redSocial.subirFoto(nombre, ruta, idPropietario);
    }
  
    
    
    public User reconocerUsuario(String nombre, String ip) {
        synchronized (nClientesConectados) {
            //Se espera hasta que haya espacio para m�s clientes
            while (nClientesConectados.get() >= N_MAX_CLIENTES) {
                try {
                	nClientesConectados.wait();
                	/*Al ser nClientesConectados un recurso compartido debemos de tratarlo
                	  concurrentemente, el hilo espera mientras no sea posible registrar
                	  un nuevo usuario*/
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
                        
            nClientesConectados.incrementAndGet(); //Se incrementa el n�mero de conexiones

            User user = this.redSocial.getUsuario(nombre, true);
            
            //Si el usuario ya esta conectado --> No se puede volver a conectar
            if (user != null && user.getIp() != null ) {
            	nClientesConectados.decrementAndGet();
            	nClientesConectados.notifyAll(); //Notifica a otros hilos que pueden intentar conectarse ahora
                return null;
            }
            //Si no es nulo se asigna
            if (user != null) {
                user.setIp(ip); //Se establece la direcci�n IP del usuario
                return user;
            }
            //Si es nulo o no esta conectado se quita
            if (user == null || user.getIp() == null ) {
            	nClientesConectados.decrementAndGet();
            	nClientesConectados.notifyAll();
            }
            
            return null;
        }
    }
    

    

    public RedSocial getRedSocial() {
        return new RedSocial(this.redSocial);
    }
    
    public void desconectar(User user) {
    	//Se establece la IP del usuario en null
        user.setIp(null);

        //Se resta en 1 el numero de conexiones
        synchronized (this.nClientesConectados) {
            this.nClientesConectados.decrementAndGet();
            this.nClientesConectados.notify();
        }
    }

}