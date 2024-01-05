package com.example.btlealumnos2021;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.btlealumnos2021.databinding.ElementoEstimacionBinding;

import java.util.ArrayList;

public class AdaptadorEstimacionCalidad extends RecyclerView.Adapter<AdaptadorEstimacionCalidad.ViewHolder> {
    ArrayList<Float> listaValoresMedios;
    ArrayList<String> listaGravedad;
    private LayoutInflater inflater;
    private Context context;

    public AdaptadorEstimacionCalidad(ArrayList<Float> listaValoresMedios,ArrayList<String> listaGravedad, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaValoresMedios = listaValoresMedios;
        this.listaGravedad =listaGravedad;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.elemento_estimacion, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.personaliza(listaValoresMedios.get(position), listaGravedad.get(position), position);
    }

    @Override
    public int getItemCount() {
        try{
            int cant = listaValoresMedios.size();
            return cant;
        } catch (Exception err){
            Log.d("AdaptadorEstimacionCalidad", "No he podido obtener el tamaño o no hay valores que mirar");
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView TituloContaminante;
        public TextView valorMedio;


        public ViewHolder(ElementoEstimacionBinding itemView) {
            super(itemView.getRoot());
            TituloContaminante = itemView.estimaciontitulo;
            valorMedio = itemView.estimacionvalor;

        }

        public ViewHolder(View itemView) {
            super(itemView);

            TituloContaminante = itemView.findViewById(R.id.estimaciontitulo);
            valorMedio = itemView.findViewById(R.id.estimacionvalor);
        }

        public void personaliza(final Float valorMedioNumero, final String gravedad, int contaminanteId) {

            switch(contaminanteId){
                case 0: //caso calidad en general
                    TituloContaminante.setText("Calidad general");
                    if (valorMedioNumero==-99F){
                        valorMedio.setText("No Info");
                    } else{
                        if (gravedad.equals("bajo")){
                            valorMedio.setTextColor(Color.GREEN);
                            valorMedio.setText("Buena");
                        } else if (gravedad.equals("medio")) {
                            valorMedio.setTextColor(Color.YELLOW);
                            valorMedio.setText("Media");
                        } else{
                            valorMedio.setTextColor(Color.RED);
                            valorMedio.setText("Mala");
                        }
                    }
                    break;

                case 1: //caso ozono
                    TituloContaminante.setText("Ozono");
                    if (valorMedioNumero==-99F){
                        valorMedio.setText("No Info");
                    } else{
                        valorMedio.setText(valorMedioNumero.toString() + " µg/m3");
                    }

                    if (gravedad.equals("bajo")){
                        valorMedio.setTextColor(Color.GREEN);
                    } else if (gravedad.equals("medio")) {
                        valorMedio.setTextColor(Color.YELLOW);
                    } else{
                        valorMedio.setTextColor(Color.RED);
                    }
                    break;

                case 2: //caso NO2
                    TituloContaminante.setText("NO2");
                    if (valorMedioNumero==-99F){
                        valorMedio.setText("No Info");
                    } else{
                        valorMedio.setText(valorMedioNumero.toString() + " µg/m3");
                    }
                    if (gravedad.equals("bajo")){
                        valorMedio.setTextColor(Color.GREEN);
                    } else if (gravedad.equals("medio")) {
                        valorMedio.setTextColor(Color.YELLOW);
                    } else{
                        valorMedio.setTextColor(Color.RED);
                    }
                    break;

                case 3: //caso SO2
                    TituloContaminante.setText("SO2");
                    if (valorMedioNumero==-99F){
                        valorMedio.setText("No Info");
                    } else{
                        valorMedio.setText(valorMedioNumero.toString() + " µg/m3");
                    }
                    if (gravedad.equals("bajo")){
                        valorMedio.setTextColor(Color.GREEN);
                    } else if (gravedad.equals("medio")) {
                        valorMedio.setTextColor(Color.YELLOW);
                    } else{
                        valorMedio.setTextColor(Color.RED);
                    }
                    break;

                case 4: //caso CO
                    TituloContaminante.setText("CO");
                    if (valorMedioNumero==-99F){
                        valorMedio.setText("No Info");
                    } else{
                        valorMedio.setText(valorMedioNumero.toString() + " mg/m3");
                    }
                    if (gravedad.equals("bajo")){
                        valorMedio.setTextColor(Color.GREEN);
                    } else if (gravedad.equals("medio")) {
                        valorMedio.setTextColor(Color.YELLOW);
                    } else{
                        valorMedio.setTextColor(Color.RED);
                    }
                    break;

                case 5: //caso benceno
                    TituloContaminante.setText("Benceno");
                    if (valorMedioNumero==-99F){
                        valorMedio.setText("No Info");
                    } else{
                        valorMedio.setText(valorMedioNumero.toString() + " µg/m3");
                    }
                    if (gravedad.equals("bajo")){
                        valorMedio.setTextColor(Color.GREEN);
                    } else if (gravedad.equals("medio")) {
                        valorMedio.setTextColor(Color.YELLOW);
                    } else{
                        valorMedio.setTextColor(Color.RED);
                    }
                    break;

                    //añadir uno para caso general (que sea el que este en posicion 0)
            }
        }
    }
}
