package servidor;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;


import modelo.RedSocial;

/*
	1.- Esta clase es esencial ya que crea el socket del servidor, y espera a que los clientes se conecten
	2.- Cuando se acepta una conexion, se crea una instancia de socket que representa la conexion entre el cliente y el servidor
	3.- Entonces se crea una instancia de ConexionCliente pasandole el socket. Esta clase se encarga de la comunicaci�n con el cliente

 */

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

public class OyenteServidor implements Runnable {

	private int puerto;	//Puerto en el que se va a estar escuchando constantemente
	private ServerSocket serverSocket;	//Socket del servidor
	private final Consumer<Socket> conectionAction;


	/*
	 *	--> EXPLICACION DE PORQUE HEMOS DECIDIDO USAR LA INTERFAZ "Consumer<Socket>"
	 * 
	 * Esta interfaz esta dise�ada para realizar una operacion que acepta un solo argumento, en concreto el "socket"
	 * 
	 * Cuando se llama a la funcion .accept(socket) realiza la operaci�n que se define en la funcion lambda
	 * cuando se crea esta clase OyenteServidor
	 * En este caso se crea una instancia de "ConexionServidorConCliente" usando el socket y se le pasa uan instacia
	 * del "Server"
	 * 
	 * */
	
	
	
	public OyenteServidor(int puerto, Consumer<Socket> conectionAction) {
		this.puerto = puerto;
		this.conectionAction = conectionAction;
	}


	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(this.puerto);
			ServerEstadoConsola.log("INFO", "Escuchando en el puerto: " + this.puerto);

			
			//El hilo siempre permanece abierto esperando una nueva conexion
			while (!this.serverSocket.isClosed()) {

				Socket socketCliente;
				try {
					socketCliente = this.serverSocket.accept();	//Bloquea

				} catch (IOException e) {	                    	                    
					ServerEstadoConsola.log("ERROR", "al abrir una nueva conexion");
					throw new RuntimeException(e);
				}


				ServerEstadoConsola.log("INFO", "Nueva conexion con " + socketCliente.getInetAddress());


				//Se llama al consumer
				this.conectionAction.accept(socketCliente);
			}
		} catch (BindException e) {
			ServerEstadoConsola.log("ERROR", "El puerto " + puerto + " ya esta en uso por otra aplicacion");
		    throw new RuntimeException(e);
		    
		} catch (IOException e) {
			ServerEstadoConsola.log("ERROR", "al abrir el ServerSocket");
			throw new RuntimeException(e);
		}
	}
}

