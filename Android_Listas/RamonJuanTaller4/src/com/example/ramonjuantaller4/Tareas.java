package com.example.ramonjuantaller4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.example.ramonjuantaller4.MainActivity.EnviarTarea;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.test.suitebuilder.annotation.LargeTest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Tareas extends ActionBarActivity {
	ListView lista;
	MiAdaptador adaptador;
	ArrayList<TareaLista> datos;
	ArrayList<String> listaConte;
	ArrayList<String> listaAutor;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) // para abilitar los metodos de este api.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tareas);
		Bundle b = new Bundle(); //objeto para obtener los extras
		b = getIntent().getExtras();
		listaConte=b.getStringArrayList("listaConte"); //arraylist con el contenido de las listas para actualizar
		listaAutor=b.getStringArrayList("listaAutor"); //arraylist con el autor de las listas para actualizar
		datos = new ArrayList<TareaLista>(); //arralist de los objetos taras, que se agregan a la lista
		for(int i=0;i<listaConte.size();i++){
			addTarea(listaAutor.get(i),listaConte.get(i)); //agrego las tareas que ya existian en el servidor al arraylist de tareas
		}
		
		final String user = b.getString("usuario"); //usuario que se logeo
		String ocupacion = b.getString("ocupacion"); //ocupacion del usuario logeado
		
		//---textview que muestra al usuario logeado y su ocupacion---//
		TextView infoUser = (TextView) findViewById(R.id.InfoUsuario); 
		infoUser.setTextColor(Color.LTGRAY);
		infoUser.setText(ocupacion + ":  " + user);
		//-------------------------------------------------//
		
		Button bAddTarea = (Button) findViewById(R.id.addTarea);
		
		//--Adaptabilidad de la aplicacion, el fondo cambia dependiendo de la ocupacion del usuario--//
		RelativeLayout layout =(RelativeLayout)findViewById(R.id.tareas);
		if(ocupacion.equals("Fotografo")){
			System.out.println("holiiiii");
			layout.setBackground(getResources().getDrawable(R.drawable.registro));
			System.out.println("holiiiii--segui");
		}else if(ocupacion.equals("Tecnico Luces")){
			layout.setBackground(getResources().getDrawable(R.drawable.tarea_luces));
		}else if(ocupacion.equals("Tecnico Revelado")){
			layout.setBackground(getResources().getDrawable(R.drawable.tarea_revelado));
		}
		//---------------------------------------//
		
		
		lista = (ListView) findViewById(R.id.listaTareas); //targeteo el listview
		adaptador = new MiAdaptador(this, datos); //inicializo el adaptador personalizado
		lista.setAdapter(adaptador); //aplico el adaptador a mi listview
		
		//--boton para agregar tareas--//
		bAddTarea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText nTarea = (EditText) findViewById(R.id.contenidoTarea);
				if (nTarea.getText().toString() != "") {
					addTarea(user, nTarea.getText().toString()); //agrego un objeto tarea con el usuario y el contenido del edittext
					listaConte.add(nTarea.getText().toString()); //actualizo el arraylist de contenido que sera enviada al servidor
					listaAutor.add(user); //actualizo el arraylist de autor que sera enviada al servidor
					EnviarLista envLista = (EnviarLista) new EnviarLista().execute(listaConte,listaAutor); //envio la lista actualizada para almacenarla en el servidor
					nTarea.setText(""); //limpio el campo del edittext
					adaptador.notifyDataSetChanged(); //notifico al adaptador que hubo un cambio.
				}
			}
		});
		
		//--metodo para eliminar elementos al mantener precionado el click 
		lista.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				datos.remove(position); //remuevo tareas de la lista
				listaConte.remove(position); //remuevo los datos del arraylist contenido que se envia al server
				listaAutor.remove(position); //remuevo los datos del arraylist autor que se envia al server
				EnviarLista envLista = (EnviarLista) new EnviarLista().execute(listaConte,listaAutor); //envio los arraylist actualizados
				adaptador.notifyDataSetChanged(); //notifico al adaptador que hubo un cambio
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tareas, menu);
		return true;
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

	//metodo para agregar objetos tareas al arraylist
	public void addTarea(String autor, String tarea) {
		datos.add(new TareaLista(autor, tarea, false));
	}
	
	
	/**
	 * clase aynctask que maneja la comunicacion en la interfaz de tarea
	 * su principal objetivo es enviar un mensaje aviso "actualizarLista" para
	 * indicarle al servidor que hubo un cambio en la lista de tareas y le 
	 * enviara la lista actualizada. Primero se envia el contenido y luego el autor
	 * de la tarea.
	 * @author JUAN CAMILO
	 */
	public class EnviarLista extends AsyncTask<ArrayList<String>, Void, Void> {
		@Override
		protected Void doInBackground(ArrayList<String>... params) {
			try {
				Socket s = new Socket(InetAddress.getByName("10.0.2.2"), 5000); //172.30.187.17
				DataOutputStream salidaDatos = new DataOutputStream(s.getOutputStream());
				salidaDatos.writeUTF("actualizarLista"); //envio el aviso 
				salidaDatos.flush();
				ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream());	
				salida.writeObject(params[0]); //envio primero contenido
				salida.writeObject(params[1]); //luego envio autor
				salida.flush();
				salida.close();
				System.out.println("envio");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
