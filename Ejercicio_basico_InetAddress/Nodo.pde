//clase Nodo que extiende de Thread
public class Nodo extends Thread {
  private InetAddress inet; //objeto InetAddress
  private boolean conecAux; //booleano que indica el estado de coneccion previo, para evaluar si se desconecto
  private PVector posicion; // posiciones x y y del nodo
  private PVector vel; // velocidad en x y y  del nodo
  private PVector ace; // aceleracion en x y y del nodo
  private int estado; //estado del nodo que define la imagen a pintar
  private int indice; //indice que recorre la animacion de la explosion o desconeccion
  private PVector destino; //lugar al cual se dirige el nodo
  private boolean coneccion; //booleano que indica si el nodo esta conectado o desconectado
  private boolean seleccion; //booleano para saber si se tiene seleccionado el objeto con el mouse o no
  private boolean colapso; //booleano que desata la animacion de un objeto que se desconecto
  private boolean vive;// booleano para el while
  private String miIP; // guarda la IP
  private String hostName; // nombre de dicho host
  private  PImage[] secDesconect; //almacena las imagenes de desconeccion
  private PImage conect, desconect; // imagenes de los estados conectado o desconectado


  //metodo constructor de Nodo, resive la ip y el arreglo de imagenes por constructor
  public Nodo(String ipA, PImage[] imgs) {

    //----imagenes----//
    conect= loadImage("burbuja.png");
    desconect= loadImage("burbujaDesconectada.png");
    secDesconect= imgs; 

    //---Posiciones y variables de movimiento---//
    posicion=new PVector(random(width), random(height)); // se crea en un lugar random dentro del lienzo 
    vel= new PVector(0, 0);
    ace= new PVector(0, 0);
    destino= new PVector(0, 0);

    //----datos----//
    indice=0;
    miIP=ipA;
    vive=true;
    estado=0; // estado inicial es desconectado
    seleccion=false; // inicializar la seleccion con el mouse en falso
    conecAux=false; // inicializa el estado auxiliar de coneccion como falso
    start();
  }

  //retorno void parametro PImage
  //metodo pintar de nodo, que pintara las imagenes que le entran por parametro
  public void pintar() {
    switch(estado) {
    case 0:
      /** si el objeto se desconecto (colapso==true), entonces se desata la animacion de destruccion, cuando el indice llega a 10 vuelve a ser 0 y colapso se vuelve 
       false para permitir que se pinte la imagen normal de coneccion*/
      if (colapso==false) {
        image(desconect, posicion.x, posicion.y);
      } 
      else if (colapso==true) {
        image(secDesconect[indice], posicion.x, posicion.y);
        indice++;
        if (indice>9) {
          indice=0;
          colapso=false;
        }
      }
      break;

    case 1:
      image(conect, posicion.x, posicion.y);
      break;
    }
  }


  /** reescribir metodo run de la clase Thread, en el
   se llama a los metodos que analizan la coneccion */
  public void run() {
    /*se le asigna a conecAux el valor de coneccion antes de que se analice su coneccion
     de este modo luego si conecAux es true pero coneccion es false, se detecta que el objeto
     se desconecto y desata la animacion seteando el valor de colapso como true. 
     Tambien se instancia el objeto InetAddress y se evalua si esta conectado o no.*/
    while (vive==true) {
      try {
        conecAux=coneccion;
        inet = InetAddress.getByName(miIP);
        coneccion=inet.isReachable(3000);
        if (coneccion==true) {
          estado=1;
        }
        else if (coneccion==false) {
          estado=0;
        }
        if (conecAux==true&&coneccion==false) {
          colapso=true;
        }
      }
      catch (IOException uhe) {
        println("Host desconcido");
      }
      hostName=inet.getHostName();
      try {
        sleep((int)random(20, 30));
      }
      catch(InterruptedException exSleep) {
      }
    }
  }


  /** metodo que define un movimiento sea arrastrarse o flotar*/
  public void mover (float x, float y) {
    if (seleccion==false) {
      if (frameCount%100==0) { // solo por ahora
        destino= new PVector(random(width), random(height));
      }
      PVector dir = PVector.sub(destino, posicion);
      // normalizar mi direccion
      dir.normalize();
      // escalar mi direccion
      dir.mult(0.2);
      //setear mi aceleracion
      ace = dir;
      //vel se actualiza por acceleracion y mi location por vel
      vel.add(ace);
      vel.limit(2); // limite de mi velocidad
      posicion.add(vel); // setea mi poscicion,
    }
    else {
      posicion.set(x, y);
    }
  }
  public float getPosX() {
    return posicion.x;
  }
  public float getPosY() {
    return posicion.y;
  }
  public void setSel(boolean nS) {
    seleccion=nS;
  }
  public boolean getSel() {
    return seleccion;
  }

  //crea una variable de tamaño que dependiendo de si esta conectado o no retorna el valor del ancho de la imagen correspondiente
  public int getTam() {
    int tamano=0;
    if (estado==1) {
      tamano=(conect.width/2);
    }
    else if (estado==0) {
      tamano= (desconect.width/2);
    }
    return tamano;
  }

  //metodo que imprime texto en pantalla con los valores del IP, el nombre del Host y si esta o no conectado
  public void mostrar(int x, int y) {
    text("IP: "+miIP, x, y);
    text("Host: "+hostName, x+10, y+15);
    if (estado==1) {
      text("Estado: Conectado", x+10, y+30);
    }
    else if (estado==0) {
      text("Estado: Desconectado", x+10, y+30);
    }

    //---solo para verificar en consola---//
    println(hostName);
    println(coneccion);
    println("/////////");
  }
}

