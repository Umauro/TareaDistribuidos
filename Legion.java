import java.io.*;
import java.net.*;
import java.util.*;

public class Legion{
    private String nombreDistrito;
    private InetAddress ipSoldado;

    public Legion(String nombre, InetAddress ip){
        nombreDistrito = nombre;
        ipSoldado = ip;
    }

    public int comprobarSoldado(InetAddress ip){
        if(ip == ipSoldado){
            return 1;
        }
        return 0;
    }

    public void cambiarDistrito(String nombre){
        nombreDistrito = nombre;
    }
}
