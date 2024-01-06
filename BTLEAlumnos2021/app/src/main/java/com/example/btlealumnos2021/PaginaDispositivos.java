package com.example.btlealumnos2021;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class PaginaDispositivos extends Fragment implements TextChangeListener {
    ConstraintLayout overlay;
    View overlay_view;
    ConstraintLayout dispositivo_hud;

    ImageView exit;

    ConstraintLayout overlay2;
    View overlay_view2;
    ImageView dispositivo_hud2;

    ImageView exit2;

    TextView distancia_texto;
    ImageView recargarecycler;
    ImageView ImagenEscanerQR;

    TextView estadoDispositivo;

    RecyclerView recyclerAnuncio;

    //para la creacion de anuncios

    ImageView butonCerrar;
    Button enviarAnuncio;

    EditText tituloAnuncio;
    EditText problemas;
    EditText contenidoAnuncio;

    POJOAnuncio anuncioACrear;

    classAnuncio utilidadesAnuncio;


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
        overlay = v.findViewById(R.id.options_overlay);
        overlay_view = v.findViewById(R.id.overlayView);
        dispositivo_hud = v.findViewById(R.id.dispositivo_hud);

        exit = v.findViewById(R.id.exit);

        overlay2 = v.findViewById(R.id.options_overlay2);
        overlay_view2 = v.findViewById(R.id.overlayView2);
        dispositivo_hud2 = v.findViewById(R.id.dispositivo_hud2);

        exit2 = v.findViewById(R.id.exit2);

        estadoDispositivo=v.findViewById(R.id.deviceState);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.GONE);
                overlay_view.setVisibility(View.GONE);
            }
        });

        exit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay2.setVisibility(View.GONE);
                overlay_view2.setVisibility(View.GONE);
            }
        });

        overlay_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No realizar ninguna acción, ya que la capa intercepta el clic
            }
        });

        dispositivo_hud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.VISIBLE);
                // Mostrar la capa transparente para deshabilitar la interacción con elementos debajo del ConstraintLayout
                overlay_view.setVisibility(View.VISIBLE);
            }
        });

        overlay_view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No realizar ninguna acción, ya que la capa intercepta el clic
            }
        });

        dispositivo_hud2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay2.setVisibility(View.VISIBLE);
                // Mostrar la capa transparente para deshabilitar la interacción con elementos debajo del ConstraintLayout
                overlay_view2.setVisibility(View.VISIBLE);
            }
        });

        distancia_texto = v.findViewById(R.id.singlastrength);

        context = this.getContext();

        shrdPrefs = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE);
        String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "");

        recyclerAnuncio = v.findViewById(R.id.recyclerView);
        recyclerAnuncio.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        classanuncio = new classAnuncio();

        if(nombreDispositivo.equals("")){
            //no hay ningun dispositivo guardado/asociado
            estadoDispositivo.setText("No hay ningun dispositivo asociado \n Puedes registrar uno con el boton arriba a la derecha");
            estadoDispositivo.setTextColor(getResources().getColor(R.color.red));
        } else {
            //hay dispositivo guardado en la app
            estadoDispositivo.setText("Estado del dispositivo");
            estadoDispositivo.setTextColor(getResources().getColor(R.color.black));

            classanuncio.recogerAnunciosDeServidorYMostrarRecycler(nombreDispositivo, recyclerAnuncio, context);
        }

        //recargar el recycler
        recargarecycler = v.findViewById(R.id.recargarRecyclerImage);
        recargarecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classanuncio.recogerAnunciosDeServidorYMostrarRecycler(nombreDispositivo, recyclerAnuncio, context);
            }
        });

        //abrir el escaner de qr
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


        //para la creacion de anuncio --------------------------------------

        utilidadesAnuncio = new classAnuncio();

        tituloAnuncio = v.findViewById(R.id.titleforrequest);
        problemas = v.findViewById(R.id.problemforrequest);
        contenidoAnuncio = v.findViewById(R.id.editTextDescripcion);


        enviarAnuncio = v.findViewById(R.id.buttonrequest);
        enviarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anuncioACrear= new POJOAnuncio(tituloAnuncio.getText().toString(), contenidoAnuncio.getText().toString(), problemas.getText().toString(), "No leido");
                utilidadesAnuncio.crearYPublicarAnuncio(nombreDispositivo, anuncioACrear, getContext());

                overlay2.setVisibility(View.GONE);
                overlay_view2.setVisibility(View.GONE);
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();

        String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "");

        if(nombreDispositivo.equals("")){
            //no hay ningun dispositivo guardado/asociado
            estadoDispositivo.setText("No hay ningun dispositivo asociado \n Puedes registrar uno con el boton arriba a la derecha");
            estadoDispositivo.setTextColor(getResources().getColor(R.color.red));
        } else {
            //hay dispositivo guardado en la app
            estadoDispositivo.setText("Estado del dispositivo");
            estadoDispositivo.setTextColor(getResources().getColor(R.color.black));

            classanuncio.recogerAnunciosDeServidorYMostrarRecycler(nombreDispositivo, recyclerAnuncio, context);
        }

    }

    // --------------------------------------------------------------
    // la respuesta a este intent se recogen en MainActivity por ser esa una actividad y esto un fragment
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // abrirEscaneoQr()
    // --------------------------------------------------------------
    public void abrirEscaneoQr(){
        Log.d("<<<>>>", " boton vincular sensor con qr Pulsado");
        Log.d("<<<>>>", " Empezamos escaneo de qr con camara");

        //abrimos la camara con la libreria de escaneo de qr zxing
        new IntentIntegrator(getActivity()).initiateScan();
        //la respuesta del escaneo se obtiene en onActivityResult

    } // ()

}