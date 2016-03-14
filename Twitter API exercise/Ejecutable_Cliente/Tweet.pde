public class Tweet extends Thread {
  private PVector posicion; // variable PVector que guarda posiciones x y y del tweet
  private PVector vel; // variable PVector que guarda velX y velY
  private PVector ace; // variable PVector que guarda aceleracion
  private PVector destino;  // variable PVector que guarda las posiciones X y Y del destino
  private boolean vive; // booleano que indica si el hilo esta vivo
  private String tweet; //string que guarda el tweet
  private String usuario; //usuario que realizo el tweet
  private PImage[] secDer;// secuencia de imagenes para la animacion mirando a la derecha
  private PImage[] secIzq; // secuencia de imagenes para la animacion mirando a la izquierda
  private int estadoFoto; //indica si el tweet ya fue fotografiado o no: 1 sin foto, 2 fotografiado.
  private int indFoto;//indica la imagen a pitnar
  private int cont;//contador que permite modifiar despues de cierto tiempo (cont) modificar indFoto.

  /*contructor para el Tweet, resive el tweet por constructor
   ademas inicializa las variables de movimiento e imagenes.*/
  public Tweet(String ms, String usua) {
    cont=0;
    vive=true;
    posicion= new PVector(690, 400);
    vel= new PVector(0, 0);
    ace= new PVector(0, 0);
    destino= new PVector(400, 300);
    indFoto=0;
    estadoFoto=1;

    //--Imagenes--//
    secDer=new PImage[4];
    secIzq=new PImage[4];
    //--DER--//
    //sin fotografiar;
    secDer[0]= loadImage("pajaroUno1.png");
    secDer[1]= loadImage("pajaroUno2.png");
    //fotografiadas;
    secDer[2]= loadImage("pajaroDos1.png");
    secDer[3]= loadImage("pajaroDos2.png");
    //--IZQ--//
    //sin fotografiar;
    secIzq[0]= loadImage("pajaroUno1Izq.png");
    secIzq[1]= loadImage("pajaroUno2Izq.png");
    //fotografiadas;
    secIzq[2]= loadImage("pajaroDos1Izq.png");
    secIzq[3]= loadImage("pajaroDos2Izq.png");
    /////////////////

    //--Strings--//
    tweet=ms;
    usuario=usua;
    start();
  }

  //metodo run encargado de la animacion del hilo y llama al metodo mover
  public void run() {
    while (vive) {
      mover();
      cont++;
      if (cont>15) {
        cont=1;
      }
      if (cont%15==0) {
        indFoto++;
      }

      switch(estadoFoto) {
      case 1:
        if (indFoto>1) {
          indFoto=0;
        }
        break;
      case 2:
        if (indFoto>3) {
          indFoto=2;
        }
        break;
      }
      try {
        sleep(15);
      }
      catch(InterruptedException exSleep) {
      }
    }
  }

  //pinta las imagenes del tweet
  public void pintar() {
    if (vel.x>=0) {
      image(secDer[indFoto], posicion.x, posicion.y);
    }
    else {
      image(secIzq[indFoto], posicion.x, posicion.y);
    }
  }

  //metodo para el moviemiento, que simula el vuelo del pajaro
  public void mover () {
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
    vel.limit(3); // limite de mi velocidad
    posicion.add(vel); // setea mi poscicion,
  }

  //al dar click sobre el pajaro este cambia su estado para siempre, indicando que ya fue fotografiado
  public void setEstado(int x, int y) {
    if (dist(x, y, posicion.x, posicion.y)<40) {
      //estadoFoto=2;

      //Prueba de que funciona la animacion--//
      if (estadoFoto==1) {
        estadoFoto=2;
      }
    }
    ////////////////////////////
  }
  public float getPosX() {
    return posicion.x;
  }
  public float getPosY() {
    return posicion.y;
  }
  public String getTweet() {
    return tweet;
  }
    public String getUsua() {
    return usuario;
  }
  public int getEstadoFoto() {
    return estadoFoto;
  }
}

