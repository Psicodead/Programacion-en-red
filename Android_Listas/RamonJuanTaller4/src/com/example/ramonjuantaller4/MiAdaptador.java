package com.example.ramonjuantaller4;

import java.util.ArrayList;
 
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

 
//clase que permite darle forma a cada elemento de mi lista, al ser listas complejas o personalizadas.
public class MiAdaptador extends BaseAdapter
{
 
 static class ViewHolder
 {
       TextView tvAutor_pub;
       TextView tvtitulo_post;
     CheckBox cb;
    }
 
   private static final String TAG = "CustomAdapter";
  private static int convertViewCounter = 0;
 
  private ArrayList<TareaLista> data;
   private LayoutInflater inflater = null;
 
 public MiAdaptador(Context c, ArrayList<TareaLista> d)
    {
       Log.v(TAG, "Constructing CustomAdapter");
 
       this.data = d;
      inflater = LayoutInflater.from(c);
  }
 
   @Override
   public int getCount()
   {
       Log.v(TAG, "in getCount()");
        return data.size();
 }
 
   @Override
   public Object getItem(int position)
 {
       Log.v(TAG, "in getItem() for position " + position);
        return data.get(position);
  }
 
   @Override
   public long getItemId(int position)
 {
       Log.v(TAG, "in getItemId() for position " + position);
      return position;
    }
 
   @Override
   public int getViewTypeCount()
   {
       Log.v(TAG, "in getViewTypeCount()");
        return 1;
   }
 
   @Override
   public int getItemViewType(int position)
    {
       Log.v(TAG, "in getItemViewType() for position " + position);
        return 0;
   }
 
   @Override
   public void notifyDataSetChanged()
  {
       super.notifyDataSetChanged();
   }
 
   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
 
       ViewHolder holder;
 
      Log.v(TAG, "in getView for position " + position + ", convertView is "
              + ((convertView == null) ? "null" : "being recycled"));
 
     if (convertView == null)
        {
           convertView = inflater.inflate(R.layout.miadaptador, null);
 
          convertViewCounter++;
            Log.v(TAG, convertViewCounter + " convertViews have been created");
 
            holder = new ViewHolder();
 
          holder.tvAutor_pub = (TextView) convertView
                 .findViewById(R.id.autor_publicacion);
            holder.tvtitulo_post = (TextView) convertView
                   .findViewById(R.id.tvTitulo);
           holder.cb = (CheckBox) convertView.findViewById(R.id.leido);
            holder.cb.setOnClickListener(checkListener);
 
            convertView.setTag(holder);
 
     } else
          holder = (ViewHolder) convertView.getTag();
 
     // Para porde hacer click en el checkbox
        TareaLista d = (TareaLista) getItem(position);
      holder.cb.setTag(d);
        // setea todos los valores de los listview
       holder.tvAutor_pub.setText(data.get(position).getAutor());
      holder.tvtitulo_post.setText(data.get(position).getEquipos());
      holder.cb.setChecked(data.get(position).getChecked());
 
      return convertView;
 }
 
   public void setCheck(int position)
  {
       TareaLista d = data.get(position);
 
        d.setChecked(!d.getChecked());
      notifyDataSetChanged();
 }


   public boolean haveSomethingSelected()
  {
       for (int i = 0; i < data.size(); i++)
           if (data.get(i).getChecked())
               return true;
        return false;
   }
 
 private OnClickListener checkListener = new OnClickListener()
   {
       @Override
       public void onClick(View v)
     {
           TareaLista d = (TareaLista) v.getTag();
         d.setChecked(!d.getChecked());
      }
   };
}