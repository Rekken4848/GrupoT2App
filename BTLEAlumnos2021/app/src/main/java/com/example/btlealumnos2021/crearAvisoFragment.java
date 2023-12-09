package com.example.btlealumnos2021;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class crearAvisoFragment extends DialogFragment {

    Activity actividad;

    ImageView butonCerrar;
    Button enviarAnuncio;

    EditText tituloAnuncio;
    EditText problemas;
    EditText contenidoAnuncio;

    POJOAnuncio anuncioACrear;

    classAnuncio utilidadesAnuncio;

    SharedPreferences shrdPrefs;


    public crearAvisoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private AlertDialog crearDialogoCrearAnuncio(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.popup_request, null);
        builder.setView(v);

        utilidadesAnuncio = new classAnuncio();

        shrdPrefs = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE);
        String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "GTI-3A");

        tituloAnuncio = v.findViewById(R.id.titleforrequest);
        problemas = v.findViewById(R.id.problemforrequest);
        contenidoAnuncio = v.findViewById(R.id.editTextDescripcion);

        butonCerrar = v.findViewById(R.id.exit2);
        butonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        enviarAnuncio = v.findViewById(R.id.buttonrequest);
        enviarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anuncioACrear= new POJOAnuncio(tituloAnuncio.getText().toString(), contenidoAnuncio.getText().toString(), problemas.getText().toString(), "No leido");
                utilidadesAnuncio.crearYPublicarAnuncio(nombreDispositivo, anuncioACrear);
            }
        });

        return builder.create();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoCrearAnuncio();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.actividad = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.popup_request, container, false);
    }
}