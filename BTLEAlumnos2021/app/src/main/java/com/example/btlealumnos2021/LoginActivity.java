package com.example.btlealumnos2021;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    IPUnificada ipunificada = new IPUnificada();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Check if the user is already logged in
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);

        if (isLoggedIn) {
            // If user is already logged in, redirect to MainActivity or another screen
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } else {
            // User is not logged in, display the login screen
            setContentView(R.layout.login);

            EditText editTextDNI = findViewById(R.id.dnilogin);
            EditText editTextCorreo = findViewById(R.id.correologin);
            EditText editTextCP = findViewById(R.id.cplogin);
            Button loginButton = findViewById(R.id.iniciarsesion);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = editTextDNI.getText().toString().trim();
                    String email = editTextCorreo.getText().toString().trim();
                    String postalcode = editTextCP.getText().toString().trim();

                    if (!id.isEmpty() && !email.isEmpty() && !postalcode.isEmpty()) {
                        // Assuming login successful, set isLoggedIn to true in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.apply();

                        checkCredentialsAndLogin(id, email, postalcode);
                    } else {
                        // Display fields are empty
                        Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkCredentialsAndLogin(String id, String email, String postalcode) { // 192.168.43.252
        String urlPersona = ipunificada.getIpServidor() + "/persona/" + id;
        String urlDireccion = ipunificada.getIpServidor() + "/direccion/" + id;

        PeticionarioREST elPeticionarioPersona = new PeticionarioREST();
        elPeticionarioPersona.hacerPeticionREST("GET", urlPersona, null, new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigoPersona, String cuerpoPersona) {
                if (codigoPersona == 200) {
                    PeticionarioREST elPeticionarioDireccion = new PeticionarioREST();
                    elPeticionarioDireccion.hacerPeticionREST("GET", urlDireccion, null, new PeticionarioREST.RespuestaREST() {
                        @Override
                        public void callback(int codigoDireccion, String cuerpoDireccion) {
                            if (codigoDireccion == 200) {
                                try {
                                    JSONObject jsonObjectPersona = new JSONObject(cuerpoPersona);
                                    JSONObject jsonObjectDireccion = new JSONObject(cuerpoDireccion);

                                    String dni = jsonObjectPersona.getString("dni");
                                    String correo = jsonObjectPersona.getString("correo");
                                    String cp = jsonObjectDireccion.getString("codigo_postal");

                                    if (dni.equals(id) && email.equals(correo) && postalcode.equals(cp)) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(KEY_LOGGED_IN, true);
                                        editor.apply();

                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                        // Credentials are incorrect, prompt user to re-enter credentials
                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                            }
                                        });*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                            intent.putExtra("dni", id);
                            intent.putExtra("correo", email);
                            intent.putExtra("codigo_postal", postalcode);

                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

}


