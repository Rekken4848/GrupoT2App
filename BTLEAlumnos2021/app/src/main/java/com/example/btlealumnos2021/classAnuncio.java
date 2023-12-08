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
                            //cuerpo contiene el/los anuncios obtenidos
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

    int idNuevoAnuncio;
    String DNIAdmin;
    public void crearYPublicarAnuncio(String nombreDispositivo, POJOAnuncio anuncioAPublicar){
        PeticionarioREST elPeticionario = new PeticionarioREST();
        //obtenemos todos los anuncios para saber la id del anuncio que crearemos ahora y poder crear Dispositivo_Anuncio
        elPeticionario.hacerPeticionREST("GET",  "http://192.168.21.58:8080/todosAnuncios", null,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d( "PeticionarioAnuncioLength", "codigo = " + codigo + "\n" + cuerpo);

                        try {
                            Type listType = new TypeToken<ArrayList<POJOAnuncio>>(){}.getType();
                            Gson gson = new Gson();
                            ArrayList<POJOAnuncio> listaTodos = gson.fromJson(cuerpo, listType);
                            idNuevoAnuncio = listaTodos.get(listaTodos.size()-1).getAnuncio_id() + 1;

                            PeticionarioREST elPeticionario2 = new PeticionarioREST();
                            //obtenemos todos los anuncios para saber la id del anuncio que crearemos ahora y poder crear Dispositivo_Anuncio
                            elPeticionario2.hacerPeticionREST("GET",  "http://192.168.21.58:8080/adminDispositivo" + "/" + nombreDispositivo, null,
                                    new PeticionarioREST.RespuestaREST () {
                                        @Override
                                        public void callback(int codigo, String cuerpo) {
                                            Log.d( "PeticionarioAdmin", "codigo = " + codigo + "\n" + cuerpo);

                                            try {
                                                Type listType = new TypeToken<ArrayList<POJOAdmin>>(){}.getType();
                                                Gson gson = new Gson();
                                                ArrayList<POJOAdmin> listaTodosAdmin = gson.fromJson(cuerpo, listType);
                                                POJOAdmin admin = listaTodosAdmin.get(0);
                                                DNIAdmin = admin.getDni_admin();

                                                //creamos el anuncio
                                                PeticionarioREST elPeticionario3 = new PeticionarioREST();
                                                elPeticionario3.hacerPeticionREST("POST",  "http://192.168.21.58:8080/anuncio",
                                                        "{ \"contenido\": " + anuncioAPublicar.getContenido() + ", \"titulo\": " + anuncioAPublicar.getTitulo() + ", \"problemas\": \"" + anuncioAPublicar.getProblemas() + "\", \"estado\": \"" + anuncioAPublicar.getEstado() + "\"}",
                                                        new PeticionarioREST.RespuestaREST () {
                                                            @Override
                                                            public void callback(int codigo, String cuerpo) {
                                                                Log.d( "Post Anuncio", "codigo = " + codigo + "\n" + cuerpo);

                                                                try {
                                                                    //creamos el dispoditivo_anuncio
                                                                    PeticionarioREST elPeticionario4 = new PeticionarioREST();
                                                                    elPeticionario4.hacerPeticionREST("POST",  "http://192.168.21.58:8080/dispositivo_anuncio",
                                                                            "{ \"dispositivo_id\": " + nombreDispositivo + ", \"anuncio_id\": " + idNuevoAnuncio + "\"}",
                                                                            new PeticionarioREST.RespuestaREST () {
                                                                                @Override
                                                                                public void callback(int codigo, String cuerpo) {
                                                                                    Log.d( "Post AnuncioDispositivo", "codigo = " + codigo + "\n" + cuerpo);

                                                                                    try {

                                                                                        //creamos el admin_anuncio
                                                                                        PeticionarioREST elPeticionario5 = new PeticionarioREST();
                                                                                        elPeticionario5.hacerPeticionREST("POST",  "http://192.168.21.58:8080/admin_anuncio",
                                                                                                "{ \"dni_admin\": " + DNIAdmin + ", \"anuncio_id\": " + idNuevoAnuncio + "\"}",
                                                                                                new PeticionarioREST.RespuestaREST () {
                                                                                                    @Override
                                                                                                    public void callback(int codigo, String cuerpo) {
                                                                                                        Log.d( "Post AnuncioAdmin", "codigo = " + codigo + "\n" + cuerpo);

                                                                                                        try {


                                                                                                        }catch (Exception err1){
                                                                                                            Log.e("Post AnuncioAdmin", "error al hacer post y crear anuncio: " + err1);
                                                                                                        }

                                                                                                    }
                                                                                                }
                                                                                        );

                                                                                    }catch (Exception err1){
                                                                                        Log.e("Post AnuncioDispositivo", "error al hacer post y crear anuncio: " + err1);
                                                                                    }

                                                                                }
                                                                            }
                                                                    );

                                                                }catch (Exception err1){
                                                                    Log.e("Post Anuncio", "error al hacer post y crear anuncio: " + err1);
                                                                }

                                                            }
                                                        }
                                                );


                                            }catch (Exception err1){
                                                Log.e("PeticionarioAdmin", "error al obtener el admin de este dispositivo: " + err1);
                                            }

                                        }
                                    }
                            );


                        }catch (Exception err1){
                            Log.e("PeticionarioAnuncioLength", "error al obtener todos los anuncios: " + err1);
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
