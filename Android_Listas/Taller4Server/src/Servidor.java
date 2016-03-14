import java.util.ArrayList;

import processing.core.PApplet;
import processing.data.XML;

public class Servidor{

	private String[] infoUsuario;
	// private ArrayList<Tarea> tareas;
	private Comunicacion com;
	private PApplet app;
	public Servidor(PApplet nApp) {
		app=nApp;
		com = new Comunicacion(app,5000);
	}

	/**
	 * metodo encargado de la ejecucion de la clase Servidor, recibira los
	 * paquetes de datos y enviara los mensajes correspondientes.
	 */
	public void ejecutar() {

	}

	/**
	 * metodo encargado de optener la informacion
	 * 
	 * @return String
	 */
	public String[] getInfo() {
		return null; // temporal
	}
	//para modificar mi xml es un xml. add child, y en ese mismo momento despues de agregar el hijo, vuelvo a crear el XML[], para
	//que su tamaño aumente.
}
