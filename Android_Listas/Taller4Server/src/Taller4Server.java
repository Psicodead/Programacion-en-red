import processing.core.PApplet;

public class Taller4Server extends PApplet {
	Servidor app;
	public void setup() {
		app= new Servidor(this);
	}

	public void draw() {
		app.ejecutar();
	}

}
