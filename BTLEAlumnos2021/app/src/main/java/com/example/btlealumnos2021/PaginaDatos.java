package com.example.btlealumnos2021;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;

public class PaginaDatos extends Fragment {
    IPUnificada ipUnificada = new IPUnificada();
    TextView valorOzono;
    TextView valorContaminacion;
    TextView valorTemperatura;
    private SharedPreferences shrdPrefs;
    private static final String ETIQUETA_LOG = ">>>>";
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        valorOzono=v.findViewById(R.id.valorOzono);
        valorContaminacion=v.findViewById(R.id.valorContaminacion);
        valorTemperatura=v.findViewById(R.id.valorTemperatura);

        webView = v.findViewById(R.id.adaptadorMapa);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        ViewPager2 viewPager2 = ((MainActivity) getActivity()).viewPager;
        viewPager2.setUserInputEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Permitir zoom
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Cargar la página web con el mapa Leaflet
        webView.loadUrl("file:///android_asset/mapa.html");
        webView.setWebChromeClient(new WebChromeClient());

        Timer timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PeticionarioREST elPeticionario = new PeticionarioREST();

                Log.d("Shared preferences", "voy a recoger el shared");
                try {
                    shrdPrefs = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE);
                    String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "");

                    Log.d("Shared preferences", "he recogido correctamente el shared");

                    long fechaActualMilis = System.currentTimeMillis();
                    long fechaDesdeMilis = fechaActualMilis-86400000;

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

                    String fechaActualString = df.format(new Date(fechaActualMilis));
                    String fechaDesdeString = df.format(new Date(fechaDesdeMilis));
                    Log.d( "PeticionarioEstimacion", fechaActualString);
                    Log.d( "PeticionarioEstimacion", fechaDesdeString);

                    //IP movil http://192.168.43.252:8080/
                    //IP casa http://192.168.1.21:8080/
                    //movil diego en wifi residencia 192.168.87.206
                    //pc diego en wifi residencia 192.168.85.210
                    //url de prueba = "http://192.168.85.210:8080/medicionEntreFechasYDispositivo" + "/" + "2023-10-15 01:00:00" + "/" + "2023-10-15 23:59:59"  + "/" + "FFFFFFFFFF"
                    elPeticionario.hacerPeticionREST("GET",  ipUnificada.getIpServidor() + "/medicionEntreFechasYDispositivo" + "/" + fechaDesdeString + "/" + fechaActualString  + "/" + nombreDispositivo, null,
                            new PeticionarioREST.RespuestaREST () {
                                @Override
                                public void callback(int codigo, String cuerpo) {
                                    Log.d( "PeticionarioEstimacion", "codigo = " + codigo + "\n" + cuerpo);

                                    try {
                                        //cuerpo contiene la/las mediciones obtenidas
                                        Type listType = new TypeToken<List<POJOMedicion>>(){}.getType();
                                        Gson gson = new Gson();
                                        List<POJOMedicion> listaMediciones = gson.fromJson(cuerpo, listType);
                                        float valorFinalTemp=0;
                                        float valorFinalOzono=0;
                                        int contadorTemp=0;
                                        int contadorOzono=0;

                                        for (POJOMedicion MedicionObjeto : listaMediciones) {
                                            System.out.println("ID: " + MedicionObjeto.getId());
                                            System.out.println("Valor: " + MedicionObjeto.getValor());
                                            System.out.println("Tipo_Valor: " + MedicionObjeto.getTipo_valor_id());
                                            System.out.println("Fecha: " + MedicionObjeto.getFecha());
                                            System.out.println("Lugar: " + MedicionObjeto.getLugar());
                                            System.out.println("------");

                                            float esteValor = MedicionObjeto.getValor();

                                            switch (MedicionObjeto.getTipo_valor_id()){
                                                case 0: //caso temperatura
                                                    valorFinalTemp=valorFinalTemp+esteValor;
                                                    contadorTemp++;
                                                    break;

                                                case 1: //caso ozono
                                                    valorFinalOzono=valorFinalOzono+esteValor;
                                                    contadorOzono++;
                                                    break;
                                            }
                                        }

                                        if (contadorTemp!=0){
                                            float valorMedioTemp = valorFinalTemp/contadorTemp;
                                            System.out.println("Temperatura" + valorMedioTemp);
                                            compararConMedidasOficiales(0, valorMedioTemp);
                                        } else{
                                            compararConMedidasOficiales(0, -999); //ponemos un valor imposible para decirle a la funcion que de este valor no tenemos mediciones
                                        }

                                        if (contadorOzono!=0){
                                            float valorMedioOzono = valorFinalOzono/contadorOzono;
                                            System.out.println("Ozono" + valorMedioOzono);
                                            compararConMedidasOficiales(1, valorMedioOzono);
                                        } else{
                                            compararConMedidasOficiales(1, -999);
                                        }

                                    }catch (Exception err2){
                                        compararConMedidasOficiales(0, -999);
                                        compararConMedidasOficiales(1, -999);
                                        Log.e("PeticionarioEstimacion", "error al mostrar los datos");
                                    }

                                }
                            }
                    );

                }catch (Exception err){
                    Log.e("TimerEstimacionDatos", err.toString());
                }
            }
        }, 0,120000);
        return v;
    }

    // --------------------------------------------------------------
    // int tipoValor, float Valor --> compararConMedidasOficiales()
    // --------------------------------------------------------------
    public void compararConMedidasOficiales(int tipoValor, float valor){

        // el segundo int de la funcion mostrarValorEnHome indica la gravedad del dato{ 0=verde/ningun peligro 1=amarillo/ligeramente contaminado 2=rojo/alerta de contaminacion
        switch (tipoValor){
            case 0: //caso temperatura
                mostrarValorEnHome(tipoValor, valor, 0);
                break;

            case 1: //caso ozono
                if (valor < 180){
                    mostrarValorEnHome(tipoValor, valor, 0);
                } else if (valor < 240) {
                    mostrarValorEnHome(tipoValor, valor, 1);
                } else{
                    mostrarValorEnHome(tipoValor, valor, 2);
                }
                break;

        }

    } // ()

    // --------------------------------------------------------------
    // int tipoValor, float Valor --> compararConMedidasOficiales()
    // --------------------------------------------------------------
    public void mostrarValorEnHome(int tipoValor, float valor, int gravedad){

        //gravedad 0 = no hay contaminacion / contaminacion leve (verde)
        //gravedad 1 = hay contaminacion modedara (amarillo)
        //gravedad 2 = contaminacion alta o excesiva / alerta (rojo)

        switch (tipoValor){
            case 0: //caso temperatura
                if (valor == -999 || valor == -999.0){
                    valorTemperatura.setText("No info");
                } else{
                    DecimalFormat df = new DecimalFormat("###.#");
                    valorTemperatura.setText(df.format(valor) + " ºC");
                }

                break;

            case 1: //caso ozono
                if (valor == -999 || valor == -999.0){ //no tenemos datos de ozono
                    valorOzono.setText("No info");
                } else if (gravedad == 0){ // cuando hay poca contaminacion
                    valorOzono.setText(valor + " µg/m3");
                    valorOzono.setTextColor(Color.GREEN);

                    valorContaminacion.setText("Baja");
                    valorContaminacion.setTextColor(Color.GREEN);
                } else if (gravedad == 1) { //contaminacion moderada
                    valorOzono.setText(valor + " µg/m3");
                    valorOzono.setTextColor(Color.YELLOW);

                    valorContaminacion.setText("Media");
                    valorContaminacion.setTextColor(Color.YELLOW);
                } else { //hay mucha contaminacion
                    valorOzono.setText(valor + " µg/m3");
                    valorOzono.setTextColor(Color.RED);

                    valorContaminacion.setText("Alta");
                    valorContaminacion.setTextColor(Color.RED);
                }
                break;

        }

    } // ()

}