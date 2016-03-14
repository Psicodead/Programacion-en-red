
public class Logica {
  private PImage[] explosion; //secuencia de imagenes a explosion
  private PImage[] interaccion; //secuencia de imagenes al interactuar 
  private PImage fondo, inicio; // imagenes de mis interfaces
  private ArrayList<Nodo> burbujas; //arraylist que almacena los nodos
  private int pantalla; // variable que me permite cambiar entre las dos interfaces
  private String ipBuscada; // IP inicial del rango, le falta la ultima parte la cual es completada con indIP
   private int indIP; //indice que completa el rango del ip
  private boolean animac; //booleano que permite activas y desactivar la animacion de la interaccion
  private int indiceAnima; // indice que permite recorrer las diferentes imagenes del arreglo interaccion
  private int xAnim; //posicion en x donde se desata la animacion
  private int yAnim; //posicion en y donde se desata la animacion
 
 
  // constructor
  public Logica() {
    //---Carga Imagenes---//
    interaccion= new PImage[4];
    for (int i=0;i<4;i++) {
      interaccion[i]=loadImage("mover/move_0000"+i+".png");
    }
    explosion= new PImage[10];
    for (int i=0;i<10;i++) {
      explosion[i]=loadImage("explosion/explo_0000"+i+".png");
    }
    fondo= loadImage("Fondo.jpg");
    inicio=loadImage("inicio.jpg");

    //---objetos y variables---//
    burbujas= new ArrayList<Nodo>(); // inicializar el arraylist de burbujas
   
    //ipBuscada= "172.30.127."; 
    //indIP=104; 
    
    ipBuscada= "172.30.133."; 
    indIP=0;
   
    pantalla=0;
    imageMode(CENTER);
  }
  //Metodo sin retorno ejecutar
  /** en el se llenara los metodos pintar y de movimiento de 
   los objetos del arraylist*/
  public void ejecutar() {
    switch(pantalla) {
    case 0:
      image(inicio, width/2, height/2);
      break;
    case 1:
    //carga secuencialmente los objetos nodos.
      if (burbujas.size()<10) {
        if (frameCount%50==0) {
          burbujas.add(new Nodo(ipBuscada+indIP, explosion)); //agrega un objeto nodo con ip especifica y le entrega las imagenes de explosion
          indIP++; //indice que aumenta para cambiar las ips
        }
      }
      
      image(fondo, width/2, height/2); //pinta el fondo de pantalla
      
      //llamo los metodos pitar y mover de los nodos
      for (int i=0;i<burbujas.size();i++) {
        burbujas.get(i).pintar();
        burbujas.get(i).mover(mouseX, mouseY);
      }
      
      //si la animacion es true se desata la animacion indiceAnima aumenta cada 5 frames, y cuando es mayor a 3 vuelve a ser 0 y se desactiva el estado animac
      if (animac==true) {
        image(interaccion[indiceAnima], xAnim, yAnim);
        if (frameCount%5==0) {
          indiceAnima++;
        }
        if (indiceAnima>3) {
          indiceAnima=0;
          animac=false;
        }
      }
      break;
    }
  }
  //Metodo sin retorno coger parametros int int
  /** en el se desarrolla la logica de las interacciones
   con el mouse, si se da click dentro de un objeto se setua su booleano seleccion,
   se tiene en cuenta un dist de la posicion del mouse y las posiciones x y y del objeto asi como 
   el ancho de la imagen*/
  public void coger (int x, int y) {
    for (int i=0;i<burbujas.size();i++) {
      if (dist(x, y, burbujas.get(i).getPosX(), burbujas.get(i).getPosY())<burbujas.get(i).getTam()) {
        burbujas.get(i).setSel(true);
      }
    }
  }
  //Metodo sin retorno soltar
  /** en el se desarrolla la logica de las interacciones
   con el mouse respecto a soltarlo, si se tiene un objeto seleccionado, entonces el estado de este se vuelve falso
  y el estado de animacion se vuelve true, mientras se setean las posiciones de la animacion con las posiciones del objeto
  el cual se solto.   */
  public void soltar () {
    for (int i=0;i<burbujas.size();i++) {  
      if ( burbujas.get(i).getSel()==true) {
        animac=true;
        xAnim=(int) burbujas.get(i).getPosX();
        yAnim= (int) burbujas.get(i).getPosY();
      }
      burbujas.get(i).setSel(false);
    }
  }
  
  //metodo para cambiar de pantalla.
  public void cambPant() {
    if (pantalla==0) {
      pantalla++;
    }
  }
  
  //metodo que imprime en pantalla la informacion del nodo sobre el cual el mouse hace over.
  public void mostrarInfo(int x, int y) {
    for (int i=0;i<burbujas.size();i++) {
      if (dist(x, y, burbujas.get(i).getPosX(), burbujas.get(i).getPosY())<burbujas.get(i).getTam()) {
        burbujas.get(i).mostrar(x, y); //para verificar info
      }
    }
  }
}

