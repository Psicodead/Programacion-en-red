package com.example.ramonjuantaller4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.os.Build;

public class Registro extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro);
		final RadioGroup grupoArea= (RadioGroup) findViewById(R.id.oficio); // el final es para que no mande error
		Button registrar = (Button) findViewById(R.id.signin);
		registrar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	
				int seleccion= grupoArea.getCheckedRadioButtonId();
				EditText usuario = (EditText) findViewById(R.id.idUsuario);
				if(seleccion!=-1){
					//
					RadioButton btn = (RadioButton) findViewById(seleccion);
	                String ocupacion = (String) btn.getText();
					
					String cheID= String.valueOf(seleccion);
					System.out.println("seleccion"+cheID);
					System.out.println(ocupacion);
					//
				EnviarTarea t = (EnviarTarea) new EnviarTarea().execute( "registro", usuario.getText().toString(),ocupacion);
				Boolean resultadoRegistro = Boolean.valueOf(false);
				try {
					resultadoRegistro = t.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				if(resultadoRegistro.booleanValue()){
					TextView info = (TextView) findViewById(R.id.textoInformacion);
					info.setTextColor(Color.GREEN);
					info.setText("El usuario se registro de manera exitosa");
					
					//volver al login
					Intent regresoLogin = new Intent(getApplicationContext(), MainActivity.class);
					//regresoLogin.putExtra("usuario", usuario.getText().toString());
					startActivity(regresoLogin);
					//
				}else{
					TextView info = (TextView) findViewById(R.id.textoInformacion);
					info.setTextColor(Color.RED);
					info.setText("El usuario no pudo ser registrado");
				}
				}else{
					TextView info = (TextView) findViewById(R.id.textoInformacion);
					info.setTextColor(Color.RED);
					info.setText("seleccione un oficio");
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registro, menu);
		return true;
	}
	public class EnviarTarea extends AsyncTask<String, Void, Boolean> {
		boolean resultado=false;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Socket s = new Socket(InetAddress.getByName("10.0.2.2"), 5000); //172.30.187.17
				DataOutputStream salidaDatos = new DataOutputStream(s.getOutputStream());
				salidaDatos.writeUTF(params[0]);
				salidaDatos.writeUTF(params[1]);
				salidaDatos.writeUTF(params[2]);
				salidaDatos.flush();
				DataInputStream entradaDatos = new DataInputStream(s.getInputStream());
				String mensaje = entradaDatos.readUTF();
				System.out.println("mensaje de respuesta: " + mensaje + " p0: " + params[0] + " ... " + mensaje.equals("usuario_valido"));
				if(params[0].equals("registro") && mensaje.equals("usuario_registrado")){
					resultado = true;
					System.out.println("resultado: registro");
				}	
				salidaDatos.close();
				entradaDatos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return resultado;
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
