import java.io.*;
import java.net.*;
import java.util.*;

public class ConexionMulticast implements Serializable{
  public String nombre;
  public String multicastIp;
  public int multicastPort;
  public String peticionesIp;
  public int peticionesPort;

  public ConexionMulticast(){
    Scanner entrada = new Scanner(System.in);
    String paraWhile;
    
    System.out.println("Ingrese nombre del Distrito");
    nombre = entrada.nextLine();

    System.out.println("Ingrese IP del grupo Multicast");
    paraWhile = entrada.nextLine();
    multicastIp = paraWhile;

    System.out.println("Ingrese puerto del grupo Multicast");
    multicastPort = entrada.nextInt();

    while(paraWhile == entrada.nextLine());
    System.out.println("Ingrese IP de Peticiones");
    paraWhile = entrada.nextLine();
    peticionesIp = paraWhile;

    System.out.println("Ingrese puerto de Peticiones");
    peticionesPort = entrada.nextInt();

  }

  public String getNombre(){
    return nombre;
  }

  public String getMulticastIp(){
    return multicastIp;
  }

  public int getMulticastPort(){
    return multicastPort;
  }

  public String getPeticionesIp(){
    return peticionesIp;
  }

  public int getPeticionesPort(){
    return peticionesPort;
  }

}
