import java.io.*;
import java.net.*;
import java.util.*;

//Envio de mensajes para ingreso o expulsion de titanes en una lista, ademas se utiliza para enviar la lista completa de titanes.
public class UnicastRequest implements Serializable{
  private int id;
  private String accion;
  private List<Titanes> lista;
  public UnicastRequest(int ID, String toDo){
      id = ID;
      accion = toDo;
  }
  public int getId(){
    return id;
  }
  public String getAccion(){
    return accion;
  }
  public void setAccion(String toDo){
    accion = toDo;
  }

  public void asignarLista(List<Titanes> nuevos){
      lista = nuevos;
  }

  public List<Titanes> getLista(){
      return lista;
  }
}
