import java.io.*;
import java.net.*;
import java.util.*;

public class main {
    public static void main(String[] args) {
        Tcliente proceso = new Tcliente();
        proceso.serverCentral();
        ConexionMulticast conexion = proceso.getConexion();
        System.out.println(conexion.getNombre());
        proceso.terminal(conexion.getPeticionesIp(), conexion.getPeticionesPort());
        proceso.infoMulti(conexion.getMulticastIp(), conexion.getMulticastPort());



    }
}
