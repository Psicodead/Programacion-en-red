// rango icesi salas 192.168.100.104
// rango casa 192.168.0.09 - 22
//rango icesi wifi 172.30.127.143
import java.net.*; //importar la libreria java.net
Logica app; 

void setup() {
  size(1200, 700);
  app=new Logica();
}
void draw() {
  background(255);
  app.ejecutar();
  app.mostrarInfo(mouseX,mouseY);
}

void mousePressed() {
  app.cambPant();
  app.coger(mouseX,mouseY);
}

void mouseReleased() {
app.soltar();
}
