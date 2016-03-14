public class Servidor extends Thread {
  //--Variables para Tweeter--//
  private Query query; // hashtag
  private Twitter twitter; //clase twitter para acceder a twitter

    //--Variables comunicacion--//
  private byte[] bufferSend; //buffer donde se carga lo que se manda.
  private byte[] bufferRes; // buffer donde se carga lo que se resive de tweet
  private DatagramSocket mcs; //socket que permite usar unicast para el mensaje
  private DatagramPacket paquet; //paquete para enviar datos
  private InetAddress inet;//direccion ip al cual se castea

  //--Strings--//
  private String mensaje; //mensaje en string que contiene el tweet
  private String mensajeTemp;// string que permite comparar para saber si el tweet es nuevo o no
  private String saludo; //saludo con el cliente

  //--Envio--//
  // Credenciales 
  private  ConfigurationBuilder cb;
  //booleano que activa la orden de enviar datos
  private boolean enviar;


  /**constructor de la clase servidor, en él se iniciaran
   las variables anteriores, tal como el buffer, el puerto y la 
   direccion de ip, tambien se inicializan el paquete y el socket
   */
  public Servidor() {
    //mensaje="";
    //mensajeTemp="";
    bufferRes = new byte[20];
    //---Credenciales Twitter---////
    cb= new ConfigurationBuilder();
    cb.setOAuthConsumerKey("*******************"); 
    cb.setOAuthConsumerSecret("***********************"); 
    cb.setOAuthAccessToken("************************"); 
    cb.setOAuthAccessTokenSecret("************************");
    ///////////////////////////////////

    //Objetos de Twitter
    twitter = new TwitterFactory(cb.build()).getInstance();
    query = new Query("#fotoEncuentroEn");

    try {
      // ip y socket
      inet = InetAddress.getByName("127.0.0.1");
      // socket para mandar y recibir datos.
      mcs = new DatagramSocket(5001);
      //mcs.joinGroup(inet);
    }
    catch(IOException io) {
    }
    enviar = true;
    start();
  }
  /**Sobreescritura del metodo run, que se encarga de la comunicacion
   de resivir y enviar los Tweets*/

  public void run() {
    try {
      DatagramPacket paquet= new DatagramPacket (bufferRes, bufferRes.length);
      mcs.receive(paquet);
      saludo=new String(paquet.getData());
      if (saludo.startsWith("iniciar")==true) {
        enviar=true;
      }
    }
    catch(IOException io) {
    }
    while (enviar) {
      try {
        //if (mensaje!=null) {
        mensajeTemp= mensaje;

        if (enviar) {
          query.setCount(4);
          QueryResult result = twitter.search(query);
          List <Status> tweets =  result.getTweets();

          for ( int i = 0; i<tweets.size(); i++) {
            Status tw = (Status) tweets.get(i);
            User usuario = tw.getUser();
            String nombreUsuario = usuario.getName();
            mensaje = nombreUsuario+"/"+tw.getText()+"/";
            println(mensaje);
            // mensaje= tw.getText();
          }
        }
      }
      //}
      catch  (TwitterException te) {
        println("No se puede conectar:"+te);
      }
      try {
        if (mensaje.equalsIgnoreCase(mensajeTemp)==false) {
          bufferSend=mensaje.getBytes();
          paquet=new DatagramPacket(bufferSend, bufferSend.length, inet, 5000);
          mcs.send(paquet);
        }
      }
      catch(IOException io) {
      }
      try {
        sleep(3000);
      }
      catch(InterruptedException e) {
      }
    }
  }
}

