package modelo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import utils.Foto;


//Representa una red social con usuarios y fotos
public class RedSocial {
	private final Map<Integer, User> usersInfo = new HashMap<>();
    private final Map<Integer, Foto> fotosInfo = new HashMap<>();

    private int idFotoLast;		//El ultimo ID asignado a una foto
    private int IdUsuarioLast;	//El ultimo ID asignado a un usuario

    private final LockTicket lockUsers = new LockTicket();
    private final Semaphore semFotos = new Semaphore(1);

    
    // --- CONSTRUCTORES ---
    public RedSocial() {
    }

    public RedSocial(Map<Integer, User> users, Map<Integer, Foto> fotosInfo) {
        this.usersInfo.putAll(users);
        this.fotosInfo.putAll(fotosInfo);
        this.IdUsuarioLast = users.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
        this.idFotoLast = fotosInfo.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
    }

    public RedSocial(RedSocial redSocial) {
        this.usersInfo.putAll(redSocial.getUsuarios());
        this.fotosInfo.putAll(redSocial.getFotos());
        this.idFotoLast = redSocial.idFotoLast;
        this.IdUsuarioLast = redSocial.IdUsuarioLast;
    }

    
    // --- Funciones ---
    public Map<Integer, Foto> getFotos() {
        this.semFotos.acquireUninterruptibly();
        try {
            return new HashMap<>(this.fotosInfo);
        } finally {
            this.semFotos.release();
        }
    }
    
    
    // --- FUNCIONES ---
    public void subirFoto(String nombre, String ruta, int idPropietario) {
        this.semFotos.acquireUninterruptibly();	//Adquiere el bloqueo del semaforo
        try {
            int id = this.idFotoLast++;	//Se asigna un id para la foto
            this.fotosInfo.put(id, new Foto(id, nombre, ruta, idPropietario));
        } finally {
            this.semFotos.release();	//Libera el semaforo
        }
    }

    
    public void eliminarFoto(int id, int idPropietario) {
        this.semFotos.acquireUninterruptibly();
        try {
            Foto foto = this.fotosInfo.get(id);	//Se obtiene la foto correspondiente al id
            
            //Se verifica si la foto existe y el user coincide
            if (foto != null && foto.getIdPropietario() == idPropietario) {
                this.fotosInfo.remove(id);	//Se elimina la foto del hasmap
            }
        } finally {
            this.semFotos.release();
        }
    }

    
    //Al final si que nos hacia falta
    public Map<Integer, User> getUsuarios() {
        try {
            this.lockUsers.lock();
            //Importante retornar un nuevo hasmap, para que si por algun casual se modifica no pase nada 
            return new HashMap<>(this.usersInfo);
        } finally {
            this.lockUsers.unlock();
        }
    }

    
    //Obtiene un usuario dado su nombre
    public User getUsuario(String nombre, boolean registrar) {
        try {
            this.lockUsers.lock();
            
            //Se itera sobre todos los usuarios
            for (User user : this.usersInfo.values()) {
                if (user.getNombre().equals(nombre)) {
                    return user;
                }
            }

            
            if (registrar) {
                User user = new User(this.IdUsuarioLast++, nombre, null);
                this.usersInfo.put(user.getId(), user);
                return user;
            }

            return null;
        } finally {
            this.lockUsers.unlock();
        }
    }
}

