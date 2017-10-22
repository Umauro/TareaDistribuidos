import java.io.*;
import java.net.*;
import java.util.*;

public class Titanes{
    private int ID;
    private String Nombre;
    private String Tipo;
    private static int contador = 0;
    public Titanes(){
        Scanner scanner = new Scanner(System.in);

        ID = contador;

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

        contador++;
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
}
