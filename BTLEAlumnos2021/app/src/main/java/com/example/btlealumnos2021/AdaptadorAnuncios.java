package com.example.btlealumnos2021;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlealumnos2021.databinding.ElementoListaBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdaptadorAnuncios extends RecyclerView.Adapter<AdaptadorAnuncios.ViewHolder> {
    ArrayList<POJOAnuncio> listaAnuncios;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorAnuncios(ArrayList<POJOAnuncio> listaAnuncios, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaAnuncios = listaAnuncios;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.elemento_lista, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.personaliza(listaAnuncios.get(position));
    }

    @Override
    public int getItemCount() {
        return listaAnuncios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre;
        public ImageView foto;


        public ViewHolder(ElementoListaBinding itemView) {
            super(itemView.getRoot());
            nombre = itemView.contendiorequesttitulo;

        }

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.contendiorequesttitulo);
            foto = itemView.findViewById(R.id.foto);
        }

        public void personaliza(final POJOAnuncio anuncio) {
            nombre.setText("pruebaa");
        }
    }
}
