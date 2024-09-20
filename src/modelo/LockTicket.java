package modelo;

import java.util.concurrent.atomic.AtomicInteger;


//Hemos decidido utilizar el tipo de lock de Ticket
public class LockTicket {
	
    private final AtomicInteger num = new AtomicInteger(1);
    private int next = 1;

    public void lock() {    	
    	/*
    	 * Se incrementa el numero de turno y espera hasta que su turno coincida
    	 * con el proximo numero esperado --> se evita la inanicion de los hilos
    	 *  
    	 */
    	
        int turno = this.num.getAndIncrement();
        while (turno != this.next);
    }

    public void unlock() {
    	/*
    	 * Incrementa el numero esperado para liberar el "lock" y permitir
    	 * que otro hilo lo adquiera
    	 *  
    	 */
    	
        this.next++;
    }
}
