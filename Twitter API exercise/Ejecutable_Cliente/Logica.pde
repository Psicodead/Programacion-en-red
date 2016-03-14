public class Logica {
  private PImage fondo, inicio, miCursor, miCursorSel, tableFondo; //imagenes del fondo y de la pantalla inicial
  private int pantalla; //indicador de la pantalla actual, 0=inicio, 1=ejecutando
  private boolean selCursor; //indica si se esta haciendo hover sobre un tweet
  private String mensajeTemp; //variable para verificar si el mensaje es nuevo
  private ArrayList<Tweet> tweets; //ArrayList que guardara los tweets
  private LinkedList<Foto> fotos; //linkedList que guardara mis fotos, se utiliza para tener mas facilidad de agregar y quitar elementos en lugares especificos.
  private Cliente cliente; //variable encargada de la comunicacion
  private boolean tablero; //variable para activar y desactivar el tablero

  /**constructor donde se realizara todo lo grafico, y se llamara a
   los objetos respondables de la comunicacion.*/
  public Logica() {
    tablero=false;
    miCursor=loadImage("visor1.png");
    miCursorSel=loadImage("visor2.png");
    //paj=new Tweet("sss");

    //---inicializar imagenes---//
    inicio=loadImage("inicio.png");
    fondo=loadImage("fondo.png");
    tableFondo=loadImage("tablero.png");
    imageMode(CENTER);
    pantalla=0;

    //---Inicializar objetos---//
    tweets=new ArrayList<Tweet>();
    fotos=new LinkedList<Foto>();

    //--Cliente--//
    cliente=new Cliente();
    for (int i=0;i<3;i++) {
      // fotos.add(new Foto("holitas mi tweet es"));
    }
  }


  public void ejecutar() {
    switch(pantalla) {

    case 0: //inicio
      image(inicio, width/2, height/2);
      break;

    case 1: //aplicacion

      image(fondo, width/2, height/2);

      //si el tablero esta desactivado se aplican estas dos operaciones que son solo esteticas del cursor
      if (tablero==false) {
        noCursor();
        selCursor=false;
      }

      /**si al cliente ya le llego algo, entonces guardo el tweet en un auxiliar, al mismo modo que el
       nombre del usuario, y veo que si eso que llego es nuevo(no es el mismo que ya habia recibido, entonces
       agrega un nuevo objeto Tweet al arraylist, al cual se le entregan las dos variables String, 
       en este orden: Tweet, nombre. */
      if (cliente.getTweet()!=null) {
        String mensaje= cliente.getTweet();
        String usua=cliente.getUsuario();
        if (mensaje.equals(mensajeTemp)==false) {
          tweets.add(new Tweet(mensaje, usua));
        }
        mensajeTemp = mensaje;
      }

      /**Mediante este for pinto todos los pajaros que estan dentro del arraylist, asi mismo si el cursor esta sobre uno de ellos
       se activara el estado de seleccion del cursor que permitira dar un feedback.*/
      for (int i=0;i<tweets.size();i++) {
        tweets.get(i).pintar();
        if (dist(mouseX, mouseY, tweets.get(i).getPosX(), tweets.get(i).getPosY())<40) {
          selCursor=true;
        }
      }

      //--visuales del tablero--//
      /**Si el tablero no esta activado se pintara un visor de camara en lugar del puntero, ademas este cambiara
       si se posa sobre un tweet para indicar que se puede fotografiar el tweet. Si el click esta sobre el tablerito
       de la esquina izquierda se mostrara el cursor tipo arrow. Finalmente si el tablero se encuentra activado
       entonces se pintara el cursor tipo arrow. */
      if (tablero==false) {
        if (dist(mouseX, mouseY, width-35, height-35)>90) {
          if (!selCursor) {
            image(miCursor, mouseX, mouseY);
          }
          if (selCursor) {
            image(miCursorSel, mouseX, mouseY);
          }
        }
        else {
          cursor(ARROW);
        }
      }
      if (tablero==true) {
        cursor(ARROW);
        println(fotos.size());
        image(tableFondo, width/2, height/2);
        for (int i=0;i<fotos.size();i++) {

          fotos.get(i).pintar(290+(i*310), height/2);
        }
      }
      break;

    case 2: //posible tablero o un estado;
      break;
    }
  }

  //metodo encargado de las relaciones del mouse.
  public void mouse(int x, int y) {
    //--llamar metodo cambio pantalla--//
    cambPant();

    /**activacion y desactivacion del tablero donde se ven las fotos y se peude leer el tweet
     se valida que se vuelva true si se da click sobre el tablerito de la esquina inferior, si esta falso;
     y falso si esta verdadero y se click en el icono de salir del tablero.*/
    if (tablero==false) {
      if (dist(x, y, width-35, height-35)<90) {
        tablero=true;
      }
    }
    if (tablero==true) {
      if (dist(x, y, 1070, 570)<20) {
        tablero=false;
      }
    }

    /**Si se da click sobre uno de los pajaritos que no ha sido fotografiado (clickeado), se vuelve azul oscuro
     indicando que ya fue fotografiado(setEstadoFoto) y se agrega una foto al tablero correspondiente a ese tweet.
     Si en el tablero hay mas de 3 fotos, se elimina la primera que se agrego y se agrega una nueva, asi manteniendo
     siempre un maximo de 3 tweets visibles*/
    for (int i=0;i<tweets.size();i++) {
      if (dist(x, y, tweets.get(i).getPosX(), tweets.get(i).getPosY())<40) {
        if (tweets.get(i).getEstadoFoto()==1) {
          tweets.get(i).setEstado(x, y);
          if (fotos.size()<3) {
            fotos.add(new Foto( tweets.get(i).getTweet(), tweets.get(i).getUsua()));
          }
          else {
            fotos.removeFirst();
            fotos.addLast(new Foto( tweets.get(i).getTweet(), tweets.get(i).getUsua()));
          }
        }
      }
    }
  }
  public void cambPant() {
    if (pantalla==0) {
      pantalla=1;
    }
  }
}

