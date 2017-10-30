import java.io.*;
import java.net.*;
import java.util.*;

public class Titanes implements Serializable{
    //La gracia del atributo estado es que cuando se envie el paquete con el titan,
    //el cliente vea el valor de accion, si es 1 lo publica y agrega a la lista, sino lo borra de su lista
    private int estado = 1; //1: publicar, 2: eliminar
    private int ID;
    private String Nombre;
    private String Tipo;
    private static int contador = 0;

    //Metodo de creacion de titanes.
    public Titanes(int aidi){
        Scanner scanner = new Scanner(System.in);

        ID = aidi;

        System.out.println("Ingrese Nombre:\t");
        Nombre = scanner.nextLine();

        System.out.println("Ingrese Tipo: ");
        System.out.println("1 - Normal");
        System.out.println("2 - Excentrico");
        System.out.println("3 - Cambiante");
        int info = scanner.nextInt();

        if(info == 1) Tipo = "Normal";
        else if(info == 2) Tipo = "Excentrico";
        else Tipo = "Cambiante";
    }

    public String mostrar(){
        return ID+" "+Nombre+" "+Tipo+"\n";
    }

    public int getId(){
      return ID;
    }

    public String getTipo(){
      return Tipo;
    }

    public String getNombre(){
      return Nombre;
    }

    public int getState(){
        return estado;
    }
    public int cambiarEstado(String nuevo){
        if(nuevo.equals("asesinado")){
            estado = 2;
        }
        else if(nuevo.equals("capturado")){
            estado = 3;
        }
        return estado;
    }
}
