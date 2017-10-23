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
    System.out.println("Ingrese nombre del Distrito");
    nombre = entrada.nextLine();

    System.out.println("Ingrese IP del grupo Multicast");
    multicastIp = entrada.nextLine();

    System.out.println("Ingrese puerto del grupo Multicast");
    multicastPort = entrada.nextInt();

    System.out.println("Ingrese IP de Peticiones");
    peticionesIp = entrada.nextLine();

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
