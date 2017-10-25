import java.io.*;
import java.net.*;
import java.util.*;

public class ConexionMulticast implements Serializable{
  private final String nombre;
  private final String multicastIp;
  private final int multicastPort;
  private final String peticionesIp;
  private final int peticionesPort;

  public ConexionMulticast(){
    Scanner entrada = new Scanner(System.in);
    String paraWhile;

    System.out.println("Ingrese nombre del Distrito");
    nombre = entrada.nextLine();
    //nombre = "trost";

    System.out.println("Ingrese IP del grupo Multicast");
    paraWhile = entrada.nextLine();
    //paraWhile = "230.0.0.1";
    multicastIp = paraWhile;

    System.out.println("Ingrese puerto del grupo Multicast");
    multicastPort = entrada.nextInt();
    //multicastPort = 4446;

    while(paraWhile == entrada.nextLine());
    System.out.println("Ingrese IP de Peticiones");
    paraWhile = entrada.nextLine();
    //paraWhile = "127.0.0.1";
    peticionesIp = paraWhile;

    System.out.println("Ingrese puerto de Peticiones");
    peticionesPort = entrada.nextInt();
    //peticionesPort = 4447;

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
