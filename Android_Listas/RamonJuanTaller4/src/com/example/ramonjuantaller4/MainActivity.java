package com.example.ramonjuantaller4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		Button btnLogin = (Button) findViewById(R.id.botonLogin); //identifico el boton del layout que voy a usar
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText miUsuario = (EditText) findViewById(R.id.idUsuario);  //identifico el editText del layout que voy a usar
				EnviarTarea t = (EnviarTarea) new EnviarTarea().execute("login",miUsuario.getText().toString()); //llamo a la comunicacion
				Boolean r = Boolean.valueOf(false); //inicializo un objeto booleano como false que tomara un valor de retorno de mi asynctask
				try {
					r =  t.get();
				} catch (InterruptedException e) {				
					e.printStackTrace();
				} catch (ExecutionException e) {					
					e.printStackTrace();
				}
				if(r.booleanValue()){ //si el objeto booleano es true (se logeo con exito)
					System.out.println(t.getListaConte());
					System.out.println(t.getListaAutor());
					Intent login = new Intent(getApplicationContext(), Tareas.class); //creo el intent de logear
					login.putExtra("usuario", miUsuario.getText().toString()); //paso por extra el usuario
					login.putExtra("ocupacion", t.getOficio()); //paso por extra la ocupacion
					login.putExtra("listaAutor", t.getListaAutor()); //paso el arraylist con los autores de las tareas existentes en mi servidor
					login.putExtra("listaConte", t.getListaConte()); //paso el arraylist con los contenidos de las tareas existentes en mi servidor
					startActivity(login); //lanzo la actividad login que me envia al activity Tareas
				}else{ // de lo contrario muesra un texto en rojo
					TextView info = (TextView) findViewById(R.id.infoLogin);
					info.setTextColor(Color.RED);
					info.setText("El usuario no existe");
				}
			}
		});
		
		Button btnSign = (Button) findViewById(R.id.botonRegistro); // si se da click sobre el boton de registro
		btnSign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent registro = new Intent(getApplicationContext(), Registro.class); //se crea y se lanza la actiidad registro que envia a Activity_registro
				startActivity(registro);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//asynctask encargada de la comunicacion
	public class EnviarTarea extends AsyncTask<String, Void, Boolean> {
		boolean resultado=false;
		
		//arraylists que almacenaran las tareas que lleguen por el servidor para poder pasarlas como extras
		ArrayList<String> listaConte; 
		ArrayList<String> listaAutor;
		////////////////////////////////
		
		String oficio; //oficio del usuario que se logea
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Socket s = new Socket(InetAddress.getByName("10.0.2.2"), 5000); //172.30.187.17
				DataOutputStream salidaDatos = new DataOutputStream(s.getOutputStream());
				salidaDatos.writeUTF(params[0]); //envio primero "login" que es la palabra clave para login
				salidaDatos.writeUTF(params[1]); //envio el nombre del usuario que se encuentra en el textEdit
				salidaDatos.flush();
				DataInputStream entradaDatos = new DataInputStream(s.getInputStream());
				String mensaje = entradaDatos.readUTF(); //creo un string para el mensaje que llega
				System.out.println("mensaje de respuesta: " + mensaje + " p0: " + params[0] + " ... " + mensaje.equals("usuario_valido"));
				if(params[0].equals("login") && mensaje.equals("usuario_valido")){ //si el mensaje es usuario_valido retorna true
					oficio= entradaDatos.readUTF();// y le asigna el valor que llega a oficio.
					resultado = true;
					System.out.println("resultado: login");
				}
				
				/*si el loging fue exitoso, se crea una entrada para objetos y se asigna el
				 * valor a los arraylist, el orden de llegada es, arraylist de contenido y luego 
				 * arraylist de autores.
				 */
				if(resultado==true){ 
				ObjectInputStream entradaObjeto= new ObjectInputStream(s.getInputStream());
				listaConte=(ArrayList<String>) entradaObjeto.readObject();
				listaAutor=(ArrayList<String>) entradaObjeto.readObject();
				System.out.println(listaConte);
				System.out.println(listaAutor);
				entradaObjeto.close();
				}
				
				salidaDatos.close();
				entradaDatos.close();
				
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return resultado;
		}
		
		public ArrayList<String> getListaConte(){
			return listaConte;
		}
		public ArrayList<String> getListaAutor(){
			return listaAutor;
		}
		
		public String getOficio(){
			return oficio;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
