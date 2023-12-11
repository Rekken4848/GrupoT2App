package com.example.btlealumnos2021;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btlealumnos2021.R;

// -----------------------------------------------------------------------------------
// @author: Hugo Martin Escrihuela
// -----------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // --------------------------------------------------------------
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    Button boton;
    Button botonEscaner;
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

    private Button[] btn = new Button[3];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn0, R.id.btn1, R.id.btn2};

    ViewPager2 viewPager;

    public class MiPagerAdapter extends FragmentStateAdapter {
        public MiPagerAdapter(FragmentActivity activity){
            super(activity);
        }
        @Override
        public int getItemCount() {
            return 3;
        }
        @Override @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new PaginaDatos();
                case 1: return new PaginaWeb();
                case 2: return new PaginaDispositivos();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        //setForcus(btn_unfocus, (Button) findViewById(v.getId()));
        //Or use switch
        switch (v.getId()){
            case R.id.btn0 :
                setFocus(btn_unfocus, btn[0]);
                viewPager.setCurrentItem(0);
                break;

            case R.id.btn1 :
                setFocus(btn_unfocus, btn[1]);
                viewPager.setCurrentItem(1);
                break;

            case R.id.btn2 :
                setFocus(btn_unfocus, btn[2]);
                viewPager.setCurrentItem(2);
                break;
        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus){
        /*btn_unfocus.setTextColor(Color.rgb(49, 50, 51));
        btn_unfocus.setBackgroundColor(Color.rgb(207, 207, 207));
        btn_focus.setTextColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(Color.rgb(3, 106, 150));*/
        btn_focus.setTypeface(null, Typeface.BOLD);
        btn_unfocus.setTypeface(null, Typeface.NORMAL);
        this.btn_unfocus = btn_focus;
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Check if the user is already logged in
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);

        if (!isLoggedIn) {
            // If user is not logged in, redirect to LoginActivity
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close this activity so that user cannot go back to it with back button
        } else {
            // User is already logged in, proceed with your main activity logic
            setContentView(R.layout.activity_main);

            Log.d(ETIQUETA_LOG, "onCreate(): empieza");


            for (int i = 0; i < btn.length; i++) {
                btn[i] = (Button) findViewById(btn_id[i]);
                btn[i].setOnClickListener(this);
            }


            btn_unfocus = btn[0];

            //Pestañas
            viewPager = findViewById(R.id.viewPagerMain);
            viewPager.setAdapter(new MiPagerAdapter(this));

        /*boton = findViewById(R.id.botonBuscarSensor);
        botonEscaner =findViewById(R.id.vincularqr);
        textoNombre=findViewById(R.id.textView);*/

        /*boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonBuscarSensor();
            }
        });*/

            inicializarBlueTooth();

            //para este sprint mostramos el dato guardado  en el textview para comprobar que funciona
            //SharedPreferences shrdPrefs = getPreferences(MODE_PRIVATE);
            //String valorAMostrar = shrdPrefs.getString("NombreDispositivo", "GTI-3A");
            //textoNombre.setText(valorAMostrar);
            SharedPreferences shrdPrefs = getPreferences(MODE_PRIVATE);
            String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "GTI-3A");
        /*SharedPreferences shrdPrefs;
                String nombreDispositivo;
                try {
                    shrdPrefs = getActivity().getPreferences(MODE_PRIVATE);
                    Log.d( "Sprint2", shrdPrefs.toString());
                    nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "GTI-3A");
                } catch(Exception error1) {
                    Log.e("Sprint2", error1.toString());
                }*/

            Log.d(ETIQUETA_LOG, " onCreate(): termina ");
        }
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
} // class
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------