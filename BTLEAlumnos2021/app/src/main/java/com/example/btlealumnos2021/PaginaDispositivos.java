package com.example.btlealumnos2021;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PaginaDispositivos extends Fragment implements TextChangeListener {
    TextView distancia_texto;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_popup, container, false);
        distancia_texto = v.findViewById(R.id.singlastrength);

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
}