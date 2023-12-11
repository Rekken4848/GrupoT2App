package com.example.btlealumnos2021;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    IPUnificada ipUnificada = new IPUnificada();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_popup);

        Button denyButton = findViewById(R.id.denegarregistrar);
        Button acceptButton = findViewById(R.id.aceptarregistrar);

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Registration Accepted", Toast.LENGTH_SHORT).show();

                // Get the values of dni, correo, and codigo_postal filled in the login screen
                String dni = getIntent().getStringExtra("dni"); // Replace "dni" with the actual key used to send dni from login
                String correo = getIntent().getStringExtra("correo");
                String codigoPostal = getIntent().getStringExtra("codigo_postal");

                String requestBodyPersona = "{ \"dni\": \"" + dni + "\", \"nombre\": \"\", \"apellidos\": \"\", \"telefono\": \"\", \"correo\": \"" + correo + "\"}";



                // Perform the POST request to 'http://192.168.43.252:8080/Persona'
                PeticionarioREST elPeticionarioPersona = new PeticionarioREST();
                elPeticionarioPersona.hacerPeticionREST("POST", ipUnificada.getIpServidor() + "/Persona", requestBodyPersona,
                        new PeticionarioREST.RespuestaREST () {
                            @Override
                            public void callback(int codigoPersona, String cuerpoPersona) {
                                // Handle the response as needed
                                // For example, display a toast message based on the response code
                                if (codigoPersona == 200) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful (Persona)", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration Failed (Persona)", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                String requestBodyDireccion = "{ \"dni\": \"" + dni + "\", \"codigo_postal\": \"" + codigoPostal + "\", \"ccaa\": \"\", \"provincia\": \"\", \"calle\": \"\"}";

                // Perform the POST request to 'http://192.168.43.252:8080/Direccion'
                PeticionarioREST elPeticionarioDireccion = new PeticionarioREST();
                elPeticionarioDireccion.hacerPeticionREST("POST", ipUnificada.getIpServidor() + "/Direccion", requestBodyDireccion,
                        new PeticionarioREST.RespuestaREST () {
                            @Override
                            public void callback(int codigoDireccion, String cuerpoDireccion) {
                                // Handle the response as needed
                                // For example, display a toast message based on the response code
                                if (codigoDireccion == 200) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful (Direccion)", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration Failed (Direccion)", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

                finish();
            }
        });


    }
}
