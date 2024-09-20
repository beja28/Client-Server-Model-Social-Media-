package servidor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerEstadoConsola {
    
	//Se usa para mostrar el estado del servidor en ciertos momentos
    public static void log(String level, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        System.out.println("[SERVER " + level + "] - " + timestamp + " - " + message);
    }
}
