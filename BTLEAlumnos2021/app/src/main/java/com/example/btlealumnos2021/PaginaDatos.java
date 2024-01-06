package com.example.btlealumnos2021;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    RecyclerView recyclerEstimaciones;

    ImageView flechaDer;
    ImageView flechaIzq;
    Context contexto;
    float razonPixeles;
    private int recyclerPos;
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

        /*
        valorOzono=v.findViewById(R.id.valorOzono);
        valorContaminacion=v.findViewById(R.id.valorContaminacion);
        valorTemperatura=v.findViewById(R.id.valorTemperatura);*/

        recyclerEstimaciones=v.findViewById(R.id.recyclerViewEstimacion);

        flechaDer=v.findViewById(R.id.flechaDer);
        flechaIzq=v.findViewById(R.id.flechaIzq);
        recyclerPos=0;

        razonPixeles=getResources().getDisplayMetrics().density;

        flechaDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recyclerPos contiene un numero entre 0 y 5 (incluidos) que representan que elemento del recycler se esta viendo mayoritariamente
                //.computeHorizontalScrollOffset() obtiene en px cuanto se ha desplazado el recycler
                //con getDisplayMetrics().density (razonPixeles) obtenemos la razon para calcular los px a dp
                //se divide entre 338 por ser la longitud en dp de cada elemento del recycler
                recyclerPos = Math.round(recyclerEstimaciones.computeHorizontalScrollOffset()/razonPixeles)/338;
                if (recyclerPos==5){
                    //si estamos en el limite nos movemos
                    recyclerEstimaciones.smoothScrollBy(Math.round(338*razonPixeles), 0);
                    return;
                }
                //nos movemos al elemento de al lado
                recyclerEstimaciones.smoothScrollToPosition(recyclerPos+1);
            }
        });

        flechaIzq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerPos = Math.round(recyclerEstimaciones.computeHorizontalScrollOffset()/razonPixeles)/338;
                if (recyclerPos==0){
                    recyclerEstimaciones.smoothScrollBy(Math.round(-338*razonPixeles), 0);
                    return;
                }
                recyclerEstimaciones.smoothScrollToPosition(recyclerPos-1);
            }
        });

        contexto=this.getContext();

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

                try {
                    shrdPrefs = getActivity().getSharedPreferences("MainActivity", MODE_PRIVATE);
                    String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "");

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
                                        float valorFinalOzono=0;
                                        float valorFinalNO2=0;
                                        float valorFinalSO2=0;
                                        float valorFinalCO=0;
                                        float valorFinalBenceno=0;
                                        int contadorOzono=0;
                                        int contadorNO2=0;
                                        int contadorSO2=0;
                                        int contadorCO=0;
                                        int contadorBenceno=0;

                                        for (POJOMedicion MedicionObjeto : listaMediciones) {
                                            System.out.println("ID: " + MedicionObjeto.getId());
                                            System.out.println("Valor: " + MedicionObjeto.getValor());
                                            System.out.println("Tipo_Valor: " + MedicionObjeto.getTipo_valor_id());
                                            System.out.println("Fecha: " + MedicionObjeto.getFecha());
                                            System.out.println("Lugar: " + MedicionObjeto.getLugar());
                                            System.out.println("------");

                                            float esteValor = MedicionObjeto.getValor();

                                            switch (MedicionObjeto.getTipo_valor_id()){

                                                case 1: //caso ozono
                                                    valorFinalOzono=valorFinalOzono+esteValor;
                                                    contadorOzono++;
                                                    break;
                                                case 2: //caso NO2
                                                    valorFinalNO2=valorFinalNO2+esteValor;
                                                    contadorNO2++;
                                                    break;

                                                case 3: //caso SO2
                                                    valorFinalSO2=valorFinalSO2+esteValor;
                                                    contadorSO2++;
                                                    break;
                                                case 4: //caso CO
                                                    valorFinalCO=valorFinalCO+esteValor;
                                                    contadorCO++;
                                                    break;

                                                case 5: //caso benceno
                                                    valorFinalBenceno=valorFinalBenceno+esteValor;
                                                    contadorBenceno++;
                                                    break;
                                            }
                                        }

                                        Float valorMedioOzono=0F;
                                        Float valorMedioNO2=0F;
                                        Float valorMedioSO2=0F;
                                        Float valorMedioCO=0F;
                                        Float valorMedioBenceno=0F;


                                        if (contadorOzono!=0){
                                            valorMedioOzono = valorFinalOzono/contadorOzono;
                                            System.out.println("Ozono " + valorMedioOzono);
                                        } else{
                                            valorMedioOzono=-99F;
                                        }

                                        if (contadorNO2!=0){
                                            valorMedioNO2 = valorFinalNO2/contadorNO2;
                                            System.out.println("NO2 " + valorMedioNO2);
                                        } else{
                                            valorMedioNO2=-99F;
                                        }

                                        if (contadorSO2!=0){
                                            valorMedioSO2 = valorFinalSO2/contadorSO2;
                                            System.out.println("SO2 " + valorMedioSO2);
                                        } else{
                                            valorMedioSO2=-99F;
                                        }

                                        if (contadorCO!=0){
                                            valorMedioCO = valorFinalCO/contadorCO;
                                            System.out.println("CO " + valorMedioCO);
                                        } else{
                                            valorMedioCO=-99F;
                                        }

                                        if (contadorBenceno!=0){
                                            valorMedioBenceno = valorFinalBenceno/contadorBenceno;
                                            System.out.println("Benceno " + valorMedioBenceno);
                                        } else{
                                            valorMedioBenceno=-99F;
                                        }

                                        ArrayList<Float> arrayMedias = new ArrayList<Float>();
                                        arrayMedias.add(0F);
                                        arrayMedias.add(valorMedioOzono);
                                        arrayMedias.add(valorMedioNO2);
                                        arrayMedias.add(valorMedioSO2);
                                        arrayMedias.add(valorMedioCO);
                                        arrayMedias.add(valorMedioBenceno);

                                        compararConMedidasOficiales(arrayMedias);

                                    }catch (Exception err2){
                                        Log.e("PeticionarioEstimacion", "error al mostrar los datos");
                                        ArrayList<Float> arrayMedias = new ArrayList<Float>();

                                        compararConMedidasOficiales(arrayMedias);
                                    }

                                }
                            }
                    );

                }catch (Exception err){
                    Log.e("TimerEstimacionDatos", err.toString());
                }
            }
        }, 0,120000);

        Context context = this.getContext();
        recyclerEstimaciones.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        return v;
    }

    // --------------------------------------------------------------
    // <R> --> compararConMedidasOficiales()
    // --------------------------------------------------------------
    public void compararConMedidasOficiales(ArrayList<Float> arrayMedias){

        ArrayList<String> arrayGravedad = new ArrayList<String>();

        if (arrayMedias.size()==0){
            arrayMedias.add(-99F);
            AdaptadorEstimacionCalidad adapter = new AdaptadorEstimacionCalidad(arrayMedias, arrayGravedad, contexto);
            recyclerEstimaciones.setAdapter(adapter);
        }

        int contadorBajos=0;
        int contadorMedios=0;
        int contadorAltos=0;

        for (int i=0;i<arrayMedias.size();i++){
            Float valor = arrayMedias.get(i);
            switch (i){
                case 0: //caso general
                    arrayGravedad.add("Reservado General");
                    break;

                case 1: //caso ozono
                    if (valor < 180){
                        arrayGravedad.add("bajo");
                        contadorBajos++;
                    } else if (valor < 240) {
                        arrayGravedad.add("medio");
                        contadorMedios++;
                    } else{
                        arrayGravedad.add("alto");
                        contadorAltos++;
                    }
                    break;

                case 2: //caso NO2
                    if (valor < 100){
                        arrayGravedad.add("bajo");
                        contadorBajos++;
                    } else if (valor < 200) {
                        arrayGravedad.add("medio");
                        contadorMedios++;
                    } else{
                        arrayGravedad.add("alto");
                        contadorAltos++;
                    }
                    break;

                case 3: //caso SO2
                    if (valor < 125){
                        arrayGravedad.add("bajo");
                        contadorBajos++;
                    } else if (valor < 350) {
                        arrayGravedad.add("medio");
                        contadorMedios++;
                    } else{
                        arrayGravedad.add("alto");
                        contadorAltos++;
                    }
                    break;

                case 4: //caso CO
                    if (valor < 5){
                        arrayGravedad.add("bajo");
                        contadorBajos++;
                    } else if (valor < 10) {
                        arrayGravedad.add("medio");
                        contadorMedios++;
                    } else{
                        arrayGravedad.add("alto");
                        contadorAltos++;
                    }
                    break;

                case 5: //caso benceno
                    if (valor < 2){
                        arrayGravedad.add("bajo");
                        contadorBajos++;
                    } else if (valor < 5) {
                        arrayGravedad.add("medio");
                        contadorMedios++;
                    } else{
                        arrayGravedad.add("alto");
                        contadorAltos++;
                    }
                    break;

            }
        }
        
        if (contadorBajos>=contadorMedios && contadorBajos>contadorAltos){
            arrayGravedad.set(0, "bajo");
        } else if (contadorMedios>contadorBajos && contadorMedios>=contadorAltos) {
            arrayGravedad.set(0, "medio");
        } else if (contadorAltos>=contadorBajos && contadorAltos>contadorMedios) {
            arrayGravedad.set(0, "alto");
        }

        AdaptadorEstimacionCalidad adapter = new AdaptadorEstimacionCalidad(arrayMedias, arrayGravedad, contexto);
        recyclerEstimaciones.setAdapter(adapter);

    } // ()

    // --------------------------------------------------------------
    // N, R, N --> mostrarValorEnHome()
    // --------------------------------------------------------------
    public void mostrarValorEnHome(int tipoValor, float valor, int gravedad){

        //gravedad 0 = no hay contaminacion / contaminacion leve (verde)
        //gravedad 1 = hay contaminacion modedara (amarillo)
        //gravedad 2 = contaminacion alta o excesiva / alerta (rojo)

        switch (tipoValor) {
            case 0: //caso temperatura
                if (valor == -999 || valor == -999.0) {
                    valorTemperatura.setText("No info");
                } else {
                    DecimalFormat df = new DecimalFormat("###.#");
                    valorTemperatura.setText(df.format(valor) + " ºC");
                }

                break;

            case 1: //caso ozono
                if (valor == -999 || valor == -999.0) { //no tenemos datos de ozono
                    valorOzono.setText("No info");
                } else if (gravedad == 0) { // cuando hay poca contaminacion
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
//------------------------------------------------------------------------------------------

        Context context = this.getContext();
        recyclerEstimaciones.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

    } // ()

}