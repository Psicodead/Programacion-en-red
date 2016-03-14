//----SUPER IMPORTANTE!!!!!---//////
/**el codigo funciona en tiempo real, es decir, no selecciona un grupo de tweets
ya existentes sino los que se hagan durante la ejecucion, twitter.com/@reimon93
el hashtag es #fotoEncuentroEn 

/**Para este codigo se conto con la ayuda de sebastian vasquez en la parte de comunicacion,
para la comunicacion con twitter se tuvo en cuenta el tutorial de la siguiente 
pagina: http://codigogenerativo.com/twitter-para-processing-2-0/, asi 
como un trabajo conjunto con Jose Bolaños y Juan Carlos Micolta*/


//Este codigo corresponde al cliente.
import java.net.*; //importar libreria de comunicacion
import java.util.*;
Logica app;

void setup(){
  size(1200,700);
  app=new Logica();
}
void draw(){
app.ejecutar();
println("mx: "+mouseX+"  my: "+mouseY);
}
void mousePressed(){
  app.mouse(mouseX,mouseY);
}
