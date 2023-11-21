package com.example.btlealumnos2021;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// -----------------------------------------------------------------------------------
// @author: Hugo Martin Escrihuela
// -----------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity implements TextChangeListener {

    private BroadcastReceiver broadcastReceiver;
    // Método de la interfaz TextChangeListener para actualizar el texto en la actividad
    @Override
    public void onTextChange(String newText) {
        distancia_texto.setText(newText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistra el receptor de difusión al destruir la actividad
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    Button boton;
    Button botonEscaner;
    TextView textoNombre;

    TextView distancia_texto;

    public static class BluetoothLeScannerWrapper {
        private static BluetoothLeScanner elEscannerEstatico;

        public BluetoothLeScannerWrapper(BluetoothLeScanner scanner) {
            //elEscanner = scanner;
        }

        public static void setBluetoothLeScanner(BluetoothLeScanner scanner) {
            elEscannerEstatico = scanner;
        }

        public static BluetoothLeScanner getBluetoothLeScanner() {
            return elEscannerEstatico;
        }
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void botonBuscarSensor(View v) {
        Log.d(ETIQUETA_LOG, " boton buscar Sensor Pulsado");
        Log.d("Pasar dato", " Enviar el servicio: " + elEscanner.hashCode());
        /*Gson gson = new Gson();
        String json = gson.toJson(elEscanner);*/
        //BluetoothLeScannerWrapper wrapper = new BluetoothLeScannerWrapper(elEscanner);
        BluetoothLeScannerWrapper.setBluetoothLeScanner(elEscanner);
        //Activamos el servicio
        Intent i = new Intent(this, ServicioReceptorBeacons.class);
        //i.putExtra("escaner", wrapper);

        // Iniciar el servicio con el Intent que contiene datos
        startService(i);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("actualizarTexto"));
    } // ()

    // --------------------------------------------------------------
    // inicializarBlueTooth()
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, " Primer chequeo " );
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        bta.enable();

        Log.d(ETIQUETA_LOG, " Algo " );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        }
        else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        }
    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_popup);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        /*boton = findViewById(R.id.botonBuscarSensor);
        botonEscaner =findViewById(R.id.vincularqr);
        textoNombre=findViewById(R.id.textView);*/
        distancia_texto=findViewById(R.id.singlastrength);

        /*boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonBuscarSensor();
            }
        });*/

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");

        //para este sprint mostramos el dato guardado  en el textview para comprobar que funciona
        SharedPreferences shrdPrefs = getPreferences(MODE_PRIVATE);
        String valorAMostrar = shrdPrefs.getString("NombreDispositivo", "GTI-3A");
        //textoNombre.setText(valorAMostrar);
        //botonBuscarSensor();
    } // onCreate()

    // --------------------------------------------------------------
    // N, Lista<Texto>, Lista<N> --> onRequestPermissionsResult()
    // --------------------------------------------------------------
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void abrirEscaneoQr(View view){
        Log.d(ETIQUETA_LOG, " boton vincular sensor con qr Pulsado");
        Log.d(ETIQUETA_LOG, " Empezamos escaneo de qr con camara");

        //abrimos la camara con la libreria de escaneo de qr zxing
        new IntentIntegrator(this).initiateScan();
        //la respuesta del escaneo se obtiene en onActivityResult

    } // ()

    // --------------------------------------------------------------
    // N, Lista<Texto>, Lista<N> --> onRequestPermissionsResult()
    // --------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.d(ETIQUETA_LOG, "requestCode: " + String.valueOf(requestCode));
        //obtenemos en un string el resultado del escaneo de qr
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        String nombreObtenido = result.getContents();

        Log.d(ETIQUETA_LOG, "Datos de QR obtenidos:" + nombreObtenido);

        //vamos a guardar este valor obtenido en la cache de la app
        guardarEnCache(nombreObtenido);

    } // ()

    // --------------------------------------------------------------
    // String --> guardarEnCache()
    // --------------------------------------------------------------
    public void guardarEnCache(String valor){

        SharedPreferences shrdPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrefs.edit();
        //crea un archivo xml donde almacena el dato en la ubicacion:
        //data/com.example.btlealumnos2021/shared_prefs
        editor.putString("NombreDispositivo", valor);
        editor.commit();

        //para este sprint mostramos el dato guardado  en el textview para comprobar que funciona
        String valorAMostrar = shrdPrefs.getString("NombreDispositivo", "GTI-3A");
        //textoNombre.setText(valorAMostrar);

    } // ()


} // class
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------