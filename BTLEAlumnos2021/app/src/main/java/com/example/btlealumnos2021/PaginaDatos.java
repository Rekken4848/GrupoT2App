package com.example.btlealumnos2021;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;

public class PaginaDatos extends Fragment {
    TextView valorOzono;
    TextView valorCO2;
    TextView valorTemperatura;
    private static final String ETIQUETA_LOG = ">>>>";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        valorOzono=v.findViewById(R.id.valorOzono);
        valorCO2=v.findViewById(R.id.valorCO2);
        valorTemperatura=v.findViewById(R.id.valorTemperatura);

        Timer timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PeticionarioREST elPeticionario = new PeticionarioREST();

                SharedPreferences shrdPrefs = getActivity().getPreferences(MODE_PRIVATE);
                String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "GTI-3A");

                long fechaActualMilis = System.currentTimeMillis();
                long fechaDesdeMilis = fechaActualMilis-86400000;

                String fechaActualString = new Date(fechaActualMilis).toString();
                String fechaDesdeString = new Date(fechaDesdeMilis).toString();

                //movil diego en wifi residencia 192.168.87.206
                //pc diego en wifi residencia 192.168.85.210
                //url de prueba = "http://192.168.85.210:8080/medicionEntreFechasYDispositivo" + "/" + "2023-10-15 01:00:00" + "/" + "2023-10-15 23:59:59"  + "/" + "FFFFFFFFFF"
                elPeticionario.hacerPeticionREST("GET",  "http://192.168.85.210:8080/medicionEntreFechasYDispositivo" + "/" + fechaDesdeString + "/" + fechaActualString  + "/" + nombreDispositivo, null,
                        new PeticionarioREST.RespuestaREST () {
                            @Override
                            public void callback(int codigo, String cuerpo) {
                                Log.d( "pruebasPeticionario", "codigo = " + codigo + "\n" + cuerpo);

                                //cuerpo contiene la/las mediciones obtenidas
                                Type listType = new TypeToken<List<POJOMedicion>>(){}.getType();
                                Gson gson = new Gson();
                                List<POJOMedicion> listaMediciones = gson.fromJson(cuerpo, listType);
                                float valorFinalTemp=0;
                                float valorFinalOzono=0;
                                float valorFinalCO2=0;
                                int contadorTemp=0;
                                int contadorOzono=0;
                                int contadorCO2=0;
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

                                        case 6: //caso CO2
                                            valorFinalCO2=valorFinalCO2+esteValor;
                                            contadorCO2++;
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

                                if (contadorCO2!=0){
                                    float valorMedioCO2 = valorFinalCO2/contadorCO2;
                                    System.out.println("CO2" + valorMedioCO2);
                                    compararConMedidasOficiales(6, valorMedioCO2);
                                } else{
                                    compararConMedidasOficiales(6, -999);
                                }
                            }
                        }
                );
            }
        }, 120000);
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

            case 6: //caso CO2
                if (valor < 500){
                    mostrarValorEnHome(tipoValor, valor, 0);
                } else if (valor < 1200) {
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
                    valorTemperatura.setText(valor + " ºC");
                }

                break;

            case 1: //caso ozono
                if (valor == -999 || valor == -999.0){ //no tenemos datos de ozono
                    valorOzono.setText("No info");
                } else if (gravedad == 0){ // cuando hay poca contaminacion
                    valorOzono.setText(valor + " µg/m3");
                    valorOzono.setTextColor(Color.GREEN);
                } else if (gravedad == 1) { //contaminacion moderada
                    valorOzono.setText(valor + " µg/m3");
                    valorOzono.setTextColor(Color.YELLOW);
                } else { //hay mucha contaminacion
                    valorOzono.setText(valor + " µg/m3");
                    valorOzono.setTextColor(Color.RED);
                }
                break;

            case 6: //caso CO2
                if (valor == -999 || valor == -999.0){ //no tenemos datos de CO2
                    valorCO2.setText("No info");
                }else if (gravedad == 0){ // cuando hay poca contaminacion
                    valorCO2.setText(valor + " ppm");
                    valorCO2.setTextColor(Color.GREEN);
                } else if (gravedad == 1) { //contaminacion moderada
                    valorCO2.setText(valor + " ppm");
                    valorCO2.setTextColor(Color.YELLOW);
                } else { //hay mucha contaminacion
                    valorCO2.setText(valor + " ppm");
                    valorCO2.setTextColor(Color.RED);
                }
                break;
        }

    } // ()

    // --------------------------------------------------------------
    // String --> guardarEnCache()
    // --------------------------------------------------------------
    public void guardarEnCache(String valor){

        SharedPreferences shrdPrefs = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrefs.edit();
        //crea un archivo xml donde almacena el dato en la ubicacion:
        //data/com.example.btlealumnos2021/shared_prefs
        editor.putString("NombreDispositivo", valor);
        editor.commit();

        //para este sprint mostramos el dato guardado  en el textview para comprobar que funciona
        String valorAMostrar = shrdPrefs.getString("NombreDispositivo", "GTI-3A");
        //textoNombre.setText(valorAMostrar);

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void abrirEscaneoQr(View view){
        Log.d(ETIQUETA_LOG, " boton vincular sensor con qr Pulsado");
        Log.d(ETIQUETA_LOG, " Empezamos escaneo de qr con camara");

        //abrimos la camara con la libreria de escaneo de qr zxing
        new IntentIntegrator(getActivity()).initiateScan();
        //la respuesta del escaneo se obtiene en onActivityResult

    } // ()

    // --------------------------------------------------------------
    // N, Lista<Texto>, Lista<N> --> onRequestPermissionsResult()
    // --------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.d(ETIQUETA_LOG, "requestCode: " + String.valueOf(requestCode));
        //obtenemos en un string el resultado del escaneo de qr
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        String nombreObtenido = result.getContents();

        Log.d(ETIQUETA_LOG, "Datos de QR obtenidos:" + nombreObtenido);

        //vamos a guardar este valor obtenido en la cache de la app
        guardarEnCache(nombreObtenido);

    } // ()
}