//clase encargada de la comunicacion del cliente
public class Cliente extends Thread {
  private ArrayList<Tweet> tweets;//ArrayList que guarda los objetos tweets(pajaros).
  private ArrayList<Foto> fotos;//ArrayList que guarda las fotos que permiten ver el tweet.
  private byte[] bufferSend; //buffer donde se carga lo que se manda.
  private byte[] bufferRes; // buffer donde se carga lo que se resive

  private DatagramPacket paquet; //paquete para resivir.
  private InetAddress ip; //ip que identifica el local host
  private DatagramSocket mcs; //soquet para recibir datos.

  private String mensaje; //mensaje que se guarda como un String
  private String saludo;
  private String[] mensajeComp;

  //solo por ahora
  private String usuario, tweet;


  //constructor del cliente, donde se inicializan las variables
  public Cliente() {
    bufferRes= new byte[500];
    saludo="iniciar";

    try {
      //Iniciarlizo ip, y el socket
      ip = InetAddress.getByName("127.0.0.1"); 
      mcs = new DatagramSocket(5000);
    }
    catch(IOException io) {
      println(io);
    }
    start();
  }

  /**metodo run encargado de revisar la comunicacion y 
   resivir los nuevos tweets que se produscan.*/
  public void run() {
    iniciarComun();
    while (true) {
      try {
        //mensaje=" ";
        paquet= new DatagramPacket(bufferRes, bufferRes.length);
        mcs.receive(paquet);
        mensaje= new String(paquet.getData());
        if (mensaje.startsWith("iniciar")==false) {
          mensajeComp= mensaje.split("/");
          usuario = mensajeComp[0];
          tweet = mensajeComp[1];
        }
      }
      catch(IOException io) {
        println(io);
      }
      try {
        sleep(100);
      }
      catch(InterruptedException ie) {
      }
    }
  }


  public String getTweet() {
    return tweet;
  }
  public String getUsuario() {
    return usuario;
  }

  public void iniciarComun() {
    try {
      //mensaje que se envia para iniciar la comunicion.
      bufferSend= saludo.getBytes();
      DatagramPacket dp = new DatagramPacket(bufferSend, bufferSend.length, ip, 5001);
      mcs.send(dp);
    } 
    catch(IOException io) {
      println(io);
    }
  }
}

