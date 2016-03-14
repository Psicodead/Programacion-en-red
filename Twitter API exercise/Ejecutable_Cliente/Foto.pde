//clase que permite mostrar el tweet en el tablero para ser leido
public class Foto {
  private int posX; //posicion x de la foto
  private int posY; //posicion y de la foto 
  private String texto; //guarda el tweet para mostrarlo
  private String tweetero; //nombre de quien hizo el tweet
  private PImage instantanea; //imagen de la metafora para mostrar el tweet, foto polaroid
  private PFont fuente;

  //constructor de la foto que resive el mensaje tweeteado por parametro
  public Foto(String ms, String usua) {
    texto=ms;
    tweetero=usua;
    fuente=loadFont("HelveticaLTStd-Light-80.vlw");
    texto=ms;
    posX=0;
    posY=0;
    instantanea=loadImage("instantanea.png");
  }

  //metodo para pintar la foto en el tablero segun su posicion en arraylist
  public void pintar(int x, int y) {
    posX=x;
    posY=y;
    image(instantanea, posX, posY);
    fill(#1C99EA);
    textFont(fuente, 24);
    text(tweetero, posX-120, posY+80);
    fill(10);
    textFont(fuente, 18);
    text(texto, posX-120, posY+100,265,60);
    noStroke();
    fill(#1A1A55);
    ellipse(posX, posY-165, 30, 30);
  }
}

