package cliente;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugClienteConsola {
	/*
	 * Se usa para debuguear las acciones que esta realizando un cliente
	 * En cada cliente se tiene una variable "boolean DEBUG" si se quiere debuguear se pone a true y saldran todos los mensajes
	 */
	
    public static void debugInfo(String nombreCliente, String message, boolean DEBUG) {
    	
    	if(DEBUG) {    		
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            System.out.println("[CLIENTE INFO] - [" + nombreCliente + "] - " + timestamp + " - " + message);
    	}
    }
    
    
    public static void debugError(String nombreCliente, String message, boolean DEBUG) {
    	
    	if(DEBUG) {    		
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            System.out.println("[CLIENTE ERROR] - [" + nombreCliente + "] - " + timestamp + " - " + message);
    	}
    }    
    
    
    public static void iniCLiente(boolean DEBUG) {
    	
    	if(DEBUG) {
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
    		long idHilo = Thread.currentThread().getId();	//Obtiene el id del Hilo que esta ejecutando esta funcion
            System.out.println("[CLIENTE INFO] - " + timestamp + " - Cliente iniciado su ejecucion en el hilo: " + idHilo);
    	}
    }
	
}
