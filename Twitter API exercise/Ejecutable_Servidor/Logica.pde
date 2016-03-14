public class Logica {
  private Servidor server; //Variable server encargado de la comunicacion
  /**Constructor de Logica donde se inicializa y se corre el 
  hilo server.*/
  
  public Logica() {
    server=new Servidor();
  }
}

