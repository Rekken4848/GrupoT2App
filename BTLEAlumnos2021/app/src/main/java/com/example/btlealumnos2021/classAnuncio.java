package com.example.btlealumnos2021;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class classAnuncio {
    public ArrayList<POJOAnuncio> listaAnuncios;

    public classAnuncio() {
    }
    public classAnuncio(ArrayList<POJOAnuncio> listaAnuncios) {
        this.listaAnuncios=listaAnuncios;
    }

    public void recogerAnunciosDeServidorYMostrarRecycler(String nombreDispositivo, RecyclerView recycler, Context context){

        PeticionarioREST elPeticionario = new PeticionarioREST();

        //IP movil http://192.168.43.252:8080/
        //IP casa http://192.168.1.21:8080/
        //movil diego en wifi residencia 192.168.87.206
        //pc diego en wifi residencia 192.168.85.210
        //movil diego en wifi compartido 10.39.80.255
        //pc diego en wifi compartido 192.168.21.58
        //url de prueba = "http://192.168.85.210:8080/medicionEntreFechasYDispositivo" + "/" + "2023-10-15 01:00:00" + "/" + "2023-10-15 23:59:59"  + "/" + "FFFFFFFFFF"
        elPeticionario.hacerPeticionREST("GET",  "http://192.168.21.58:8080/anuncioDispositivo" + "/" + nombreDispositivo, null,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d( "PeticionarioAnuncio", "codigo = " + codigo + "\n" + cuerpo);

                        try {
                            //cuerpo contiene la/las mediciones obtenidas
                            Type listType = new TypeToken<ArrayList<POJOAnuncio>>(){}.getType();
                            Gson gson = new Gson();
                            listaAnuncios = gson.fromJson(cuerpo, listType);
                            AdaptadorAnuncios adapter = new AdaptadorAnuncios(listaAnuncios, context);
                            recycler.setAdapter(adapter);

                        }catch (Exception err2){
                            Log.e("PeticionarioEstimacion", "error al mostrar los datos");
                        }

                    }
                }
        );
    }

    public void setAnunciosList(ArrayList<POJOAnuncio> anuncioList){
        this.listaAnuncios = anuncioList;
    }
    public ArrayList<POJOAnuncio> getAnunciosList(){
        return this.listaAnuncios;
    }
}
