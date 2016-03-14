import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.*;

import processing.core.PApplet;
import processing.data.XML;

/**
 * Clase Comunicacion encargada de los protocolos de la comunicacion entre el
 * cliente y el servidor, la comunicacion se realizara mediante el protocolo
 * TCP.
 */

public class Comunicacion extends Thread {

	private ServerSocket ss; //server socket de mi servidor
	private Socket s; //socket del servidor
	private XML usuarios; //xml que almacenas los usuarios registrados
	private XML tareas; //xml que almacena las tareas agregadas a la lista
	private PApplet app; // objeto PApplet
	private String userOcupacion; //ocupacion del usuario
	
	//--PARA ALMACENAMIENTO EN XML EN EL SERVIDOR--//
	private ArrayList<String> listaConte; //arraylist para guardar los contenidos de la lista
	private ArrayList<String> listaAutor; //arraylist para guardar los autores de cada tarea de la lista
    ///--------------------------------------------///
	
	public Comunicacion(PApplet app, int port) {
		this.app = app;
		tareas = app.loadXML("data/tareas.xml"); //cargo el xml de tareas
		usuarios = app.loadXML("data/usuarios.xml"); //cargo el xml de usuarios registrados
		XML[] hijos = tareas.getChildren("tarea"); //optengo los hijos de tareas para llenar los arraylist
		listaAutor = new ArrayList<String>();
		listaConte = new ArrayList<String>();
		
		/**por cada hijo del xml de tareas existente agrego una tarea, dividiendo el contenido en 
		 * dos arraylist, uno para el contenido de la tarea, y el otro para el autor de la tarea
		 */
		for (int i = 0; i < hijos.length; i++) {
			String conTarea = hijos[i].getContent();
			String usuTarea = hijos[i].getString("autor");
			listaConte.add(conTarea); 
			listaAutor.add(usuTarea);
		}
		
		if (usuarios == null) {
			System.out.println("XML could not be parsed.");
		}

		try {
			ss = new ServerSocket(port);
			s = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void run() {
		while (true) {
			if (s != null) {
				recibir();
				s = null;
			} else {
				try {
					s = ss.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * el metodo recibir se encarga de recibir avisos y segun el aviso que recibe, se dispone
	 * a recibir un objeto o dato diferente. Si llega un aviso "login" lanza el metodo validar
	 * login y si este retorna true, lanza el metodo enviarConfirmacionLogin con el parametro ocupacion 
	 * del usuario que solicito el login y luego envia las tareas.
	 * Si llega "registro" realiza las misma actividad que para login solo que con los metodos relacionados
	 * al registro.
	 * Finalmente si el aviso es "actualizarLista" se llama al metodo recibir listas.
	 */
	private void recibir() {
		try {
			DataInputStream entrada = new DataInputStream(s.getInputStream());
			String mensaje = entrada.readUTF();
			if (mensaje.startsWith("login")) {
				if (validarLogin(entrada.readUTF())) {
					enviarConfirmacionLogin(userOcupacion);
					enviarTareas();
				} else {
					enviarDenegadoLogin();
				}
			} else if (mensaje.startsWith("registro")) {
				if (registrarUsuario(entrada.readUTF(), entrada.readUTF())) {
					enviarConfirmacionRegistro();
				} else {
					enviarRegistroDenegado();
				}
			}else if (mensaje.startsWith("actualizarLista")){
				recibirListas();
				
			}
		} catch (IOException e) {
			System.out.println("usuario se ha ido");
			s = null;
		}
	}

	//metodo que envia el mensaje "usuario_no_registrado"
	private void enviarRegistroDenegado() {
		DataOutputStream salida;
		try {
			salida = new DataOutputStream(s.getOutputStream());
			salida.writeUTF("usuario_no_registrado");
			System.out.println("no registrado");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//metodo encargado de enviar la lista de tareas existentes en el xml.
	private void enviarTareas() {
		try {
			ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream());	
			salida.writeObject(listaConte);//primero se envia el contenido
			salida.writeObject(listaAutor);// y luego se envia el autor
			salida.flush();
		} catch (IOException io) {

		}
	}

	//metodo que envia el mensaje "usuario_registrado"
	private void enviarConfirmacionRegistro() {
		DataOutputStream salida;
		try {
			salida = new DataOutputStream(s.getOutputStream());
			salida.writeUTF("usuario_registrado");
			System.out.println("registrado");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * metodo encargado de registrar al usuario. Recibe por parametros el usuario que
	 * solicito el registro y su ocupacion, analiza si existe ese usuario en el xml,
	 * si existe retorna falso, si no existe lo agrega al xml, guarda el xml actualizado
	 * y retorna true.*/
	private boolean registrarUsuario(String nuevo, String ocupacion) {
		boolean registrado = false;
		if (validarLogin(nuevo) == false) {
			System.out.println(ocupacion);
			XML hijo = new XML("usuario");
			hijo.setContent(nuevo);
			hijo.setString("ocupacion", ocupacion);
			usuarios.addChild(hijo);
			app.saveXML(usuarios, "data/usuarios.xml");
			registrado = true;
		}
		return registrado;
	}

	//metodo que envia el mensaje "usuario_invalido"
	private void enviarDenegadoLogin() {
		DataOutputStream salida;
		try {
			salida = new DataOutputStream(s.getOutputStream());
			salida.writeUTF("usuario_invalido");
			System.out.println("invalido");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//metodo que envia el mensaje "usuario_valido"
	private void enviarConfirmacionLogin(String oficio) {
		DataOutputStream salida;
		try {
			salida = new DataOutputStream(s.getOutputStream());
			salida.writeUTF("usuario_valido");
			salida.writeUTF(oficio);
			System.out.println("valido");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * metodo encargado de validar el login al usuario. Recibe por parametros el usuario que
	 * solicito el login, analiza si existe ese usuario en el xml,
	 * si existe retorna true, si no existe retorna false.*/
	private boolean validarLogin(String usuario) {
		boolean encontrado = false;
		XML[] hijos = usuarios.getChildren();

		for (int i = 0; i < hijos.length; i++) {
			if (hijos[i].getContent().equals(usuario)) {
				userOcupacion = hijos[i].getString("ocupacion");
				encontrado = true;
				break;
			}
		}
		return encontrado;
	}
	
	/**
	 * metodo encargado de recibir la lista de tareas para almacenarla en el xml,
	 * primero llega el arraylist con el contenido y luego el arraylist con los autores
	 * de cada tarea. Crea un nuevo xml para remplazar el existente, pero con las
	 * tareas que le llegan desde el cliente. Finalmente guarda el xml en el data.
	 */
	private void recibirListas(){
		try {
			ObjectInputStream entradaLista= new ObjectInputStream(s.getInputStream());
			listaConte= (ArrayList<String>) entradaLista.readObject();
			listaAutor= (ArrayList<String>) entradaLista.readObject();	
			tareas = null;
			String base = "<tareas></tareas>";		
			tareas = app.parseXML(base);
			for (int i = 0; i < listaConte.size(); i++) {
				XML hijito= tareas.addChild("tarea");		
				hijito.setContent(listaConte.get(i));
				hijito.setString("autor",listaAutor.get(i));
			}
			app.saveXML(tareas,"data/tareas.xml");
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
