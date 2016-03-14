//Clase TareaLista, es el objeto que se almacenara en la lista.
//se utilizo como apoyo el tutorial http://elbauldelprogramador.com/adapter-personalizado-en-android/
package com.example.ramonjuantaller4;

import android.os.Parcel;
import android.os.Parcelable;

public class TareaLista implements Parcelable {

	private String autor; //autor que publico la tarea
	private String titulo; //contenido de la tarea
	private boolean finalizado; //ha sido o no realizado

	
	//asigno valores por constructor
	public TareaLista(String autor, String tarea, boolean checked) {
		this.autor = autor;
		this.titulo = tarea;
		this.finalizado = checked;
	}

	
	public TareaLista(Parcel in) {
		this.titulo = in.readString();
		this.autor = in.readString();
		this.finalizado = in.readInt() == 1 ? true : false;
	}

	//permite modificar el autor
	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	//permite saber el autor
	public String getAutor() {
		return autor;
	}

	//permite modificar el finalizado
	public void setChecked(boolean value) {
		this.finalizado = value;
	}

	//permite ver el finalizado
	public boolean getChecked() {
		return finalizado;
	}

	//permite ver el contenido
	public String getEquipos() {
		return titulo;
	}

	//permite modificar el contenido
	public void setEquipos(String equipos) {
		this.titulo = equipos;
	}

	
	//segun lo entendido en el tutorial las siguientes lineas de codigo son para optimizar 
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getEquipos());
		dest.writeString(getAutor());
		dest.writeInt(getChecked() ? 1 : 0);
	}

	public static final Parcelable.Creator<TareaLista> CREATOR = new Parcelable.Creator<TareaLista>() {
		public TareaLista createFromParcel(Parcel in) {
			return new TareaLista(in);
		}

		public TareaLista[] newArray(int size) {
			return new TareaLista[size];
		}
	};
}