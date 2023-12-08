package com.example.btlealumnos2021;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class PaginaDispositivos extends Fragment implements TextChangeListener {
    TextView distancia_texto;
    ImageView recargarecycler;
    ImageView ImagenEscanerQR;
    private Context context;
    private SharedPreferences shrdPrefs;
    private classAnuncio classanuncio;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        //distancia_texto = v.findViewById(R.id.singlastrength);

        context = this.getContext();

        shrdPrefs = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE);
        String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "GTI-3A");

        RecyclerView recyclerAnuncio = v.findViewById(R.id.recyclerView);
        recyclerAnuncio.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        classanuncio = new classAnuncio();
        classanuncio.recogerAnunciosDeServidorYMostrarRecycler(nombreDispositivo, recyclerAnuncio, context);

        recargarecycler = v.findViewById(R.id.recargarRecyclerImage);
        recargarecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classanuncio.recogerAnunciosDeServidorYMostrarRecycler(nombreDispositivo, recyclerAnuncio, context);
            }
        });

        ImagenEscanerQR = v.findViewById(R.id.imageViewEscanerQR);
        ImagenEscanerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirEscaneoQr();
            }
        });

        // Registra el receptor de difusión
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("actualizarTexto".equals(intent.getAction())) {
                    String nuevoTexto = intent.getStringExtra("nuevoTexto");
                    onTextChange(nuevoTexto);
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("actualizarTexto"));
        return v;
    }

    private BroadcastReceiver broadcastReceiver;
    // Método de la interfaz TextChangeListener para actualizar el texto en la actividad
    @Override
    public void onTextChange(String newText) {
        distancia_texto.setText(newText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Desregistra el receptor de difusión al destruir la actividad
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void abrirEscaneoQr(){
        Log.d("<<<>>>", " boton vincular sensor con qr Pulsado");
        Log.d("<<<>>>", " Empezamos escaneo de qr con camara");

        //abrimos la camara con la libreria de escaneo de qr zxing
        new IntentIntegrator(getActivity()).initiateScan();
        //la respuesta del escaneo se obtiene en onActivityResult

    } // ()

}