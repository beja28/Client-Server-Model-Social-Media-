package modelo.conexiones;


//Hemos decidido crear una excepcion para cuando sucede un error en una conexion
public class ConexionRuntimeException extends RuntimeException {

    public ConexionRuntimeException(String info) {
        super(info);
    }

    public ConexionRuntimeException(String info, Exception e) {
        super(info, e);
    }
}
