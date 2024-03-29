package com.example.btlealumnos2021;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// -----------------------------------------------------------------------------------
// @author: Hugo Martin Escrihuela
// -----------------------------------------------------------------------------------
public class ServicioReceptorBeacons extends Service {
    private static final String ETIQUETA_LOG = ">>>>";

    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    private SharedPreferences shrdPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Servicio creado",
                Toast.LENGTH_SHORT).show();

        shrdPrefs = getSharedPreferences("MainActivity", MODE_PRIVATE);

        Timer timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long fechaUltimaMedicion = shrdPrefs.getLong("UltimaFechaMedicion", 0);
                long fechaActual = System.currentTimeMillis();

                if (fechaUltimaMedicion==0){
                    return;
                }
                long diferencia = fechaActual - fechaUltimaMedicion;

                // 1 hora = 3600000 milisegundos
                // 30 minutos = 1800000 milisegundos
                // 1 minuto = 60000 milisegundos
                // 2 minutos = 120000 milisegundos
                // 1 hora y media = 5400000 milisegundos
                // 1 dia (24 horas) = 86400000 milisegundos
                if (diferencia > 120000){
                    notificacionAlerta(0, 2);
                }
            }
        }, 0,120000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
        /*Toast.makeText(this,"Servicio arrancado "+ idArranque,
                Toast.LENGTH_SHORT).show();*/
        MainActivity.BluetoothLeScannerWrapper scannerWrapper = (MainActivity.BluetoothLeScannerWrapper) intent.getSerializableExtra("escaner");

        String nombreDispositivo = shrdPrefs.getString("NombreDispositivo", "");

        elEscanner = MainActivity.BluetoothLeScannerWrapper.getBluetoothLeScanner();
        Log.d("Pasar dato", " Recibe el servicio: " + elEscanner.hashCode());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServicioReceptorBeacons.this.buscarEsteDispositivoBTLE(nombreDispositivo);
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Servicio detenido",
                Toast.LENGTH_SHORT).show();
        this.detenerBusquedaDispositivosBTLE();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // --------------FUNCIONES RECEPCION IBEACONS--------------------
    // --------------------------------------------------------------
    // --------------------------------------------------------------

    // --------------------------------------------------------------
    // buscarTodosLosDispositivosBTLE()
    // --------------------------------------------------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");


                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        this.elEscanner.startScan(this.callbackDelEscaneo);

    } // ()

    // --------------------------------------------------------------
    // Escaneado --> mostrarInformacionDispositivoBTLE()
    // --------------------------------------------------------------
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ******************");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        float tipoValor = (float) Utilidades.bytesToInt(tib.getMajor());
        float valorMedicion = (float) Utilidades.bytesToInt(tib.getMinor());

        //POSTinsertarMedicion((Vgas)/100, (Vtemp)/100, bluetoothDevice.getAddress());
        //POSTinsertarMedicion(Float.parseFloat(Utilidades.bytesToHexString(tib.getMajor())), Float.parseFloat(Utilidades.bytesToHexString(tib.getMinor())));

        distanciaASonda(rssi * -1);

        compararConMedidasOficiales(tipoValor, valorMedicion);

        SharedPreferences.Editor editor = shrdPrefs.edit();
        //crea un archivo xml donde almacena el dato en la ubicacion:
        //data/com.example.btlealumnos2021/shared_prefs
        //el valor que guardamos es la fecha y hora actuales en milisegundos
        editor.putLong("UltimaFechaMedicion", System.currentTimeMillis());
        editor.commit();

    } // ()

    // --------------------------------------------------------------
    // Texto --> buscarEsteDispositivoBTLE()
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        List<ScanFilter> lsf = new ArrayList<ScanFilter>();
        lsf.add(sf);

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );
        this.elEscanner.startScan(lsf, new ScanSettings.Builder().build(), this.callbackDelEscaneo);

        //this.elEscanner.startScan(this.callbackDelEscaneo);
    } // ()

    private void cambiarTexto(String nuevoTexto) {
        // Realiza alguna lógica de cambio de texto aquí si es necesario
        notifyTextChange(nuevoTexto);
    }

    private void notifyTextChange(String newText) {
        Log.d("Distancia2", "Potencia: ");
        Intent intent = new Intent("actualizarTexto");
        intent.putExtra("nuevoTexto", newText);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void distanciaASonda(int potencia){
        Log.d("Distancia", "Potencia: " + potencia);
        if (potencia > 0 && potencia < 50) {
            cambiarTexto("Cerca");
            // Obtén el tiempo en milisegundos que deseas para la alarma
            long tiempoEnMilisegundos = 6000; // Ejemplo: 1 s

            // Configura la alarma desde el servicio
            AlarmaHelper.configurarAlarma(this, tiempoEnMilisegundos);
        } else if (potencia > 50 && potencia < 70) {
            cambiarTexto("Medio");
        } else if (potencia > 70 && potencia < 85) {
            cambiarTexto("Lejos");
        } else if (potencia > 85) {
            cambiarTexto("Muy lejos");
        }
    }

    private void compararConMedidasOficiales(float tipoValorfloat, float valorMedicionfloat){
        int tipoNotif = 1;
        int tipoValor = (int) tipoValorfloat;
        int valorMedicion = (int) valorMedicionfloat;

        switch (tipoValor){
            case 0:
                break;
            case 1: //ozono
                if(valorMedicion>240){
                    notificacionAlerta(tipoValor, tipoNotif);
                }
                break;
            case 2: //NO2
                if(valorMedicion>200){
                    notificacionAlerta(tipoValor, tipoNotif);
                }
                break;
            case 3: //SO2
                if(valorMedicion>350){
                    notificacionAlerta(tipoValor, tipoNotif);
                }
                break;
            case 4: //CO (en mg)
                if(valorMedicion>10){
                    notificacionAlerta(tipoValor, tipoNotif);
                }
                break;
            case 5: // Benceno
                if(valorMedicion>5){
                    notificacionAlerta(tipoValor, tipoNotif);
                }
                break;
        }
    }
    public void notificacionAlerta(int tipoDato, int tipoNotificacion){
        //tipo notificacion:
        // 1 = se ha recibido un valor que excede las recomendaciones oficiales
        // 2 = el timer no detecta un beacon desde hace mas de x tiempo, notificacion de alerta de posible fallo de dispositivo
        Intent i = new Intent(ServicioReceptorBeacons.this, ServicioNotifAlerta.class);
        i.putExtra("tipoNotif", tipoNotificacion);
        i.putExtra("tipoDato", tipoDato);
        startService(i);
    }
    // --------------------------------------------------------------
    // detenerBusquedaDispositivosBTLE()
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) {
            return;
        }

        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    // ----------------FUNCIONES PETICIONES REST---------------------
    // --------------------------------------------------------------
    // --------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // POSTinsertarMedicion()
    // ---------------------------------------------------------------------------------------------
    private void POSTinsertarMedicion(float Vgas, float Vtemp, String dispositivo_id) {
        // Obtiene la fecha y hora actual
        LocalDateTime fechaHoraActual = null;
        String fechaFormateada = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            fechaHoraActual = LocalDateTime.now();

            // Define el formato que deseas para la fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Formatea el LocalDateTime como una cadena en el formato deseado
            fechaFormateada = fechaHoraActual.format(formatter);
        }
        else {
            // Obtiene la fecha y hora actual del sistema
            //Date fechaHoraActual = new Date();
        }
        Log.d( "PeticionarioReal", "Pre construir");

        PeticionarioREST elPeticionario = new PeticionarioREST();

        Log.d( "PeticionarioReal", Vgas + ":" + Vtemp + ":" + fechaHoraActual + ":" + fechaFormateada);
        //IP movil http://192.168.43.252:8080/medicion
        //IP casa http://192.168.1.21:8080/medicion
        if (Vtemp != 0 && Vgas != 0) {
            // Construir la cadena de inserción y hacer la petición REST

            elPeticionario.hacerPeticionREST("POST",  "http://192.168.43.252:8080/medicion",
                    //"{ \"Vgas\": 20.00, \"Vtemp\": 30.00, \"fecha\": \"2023-10-20 21:00:00\", \"dispositivo_id\": \"Probando Java\" }",
                    "{ \"Vgas\": " + Vgas + ", \"Vtemp\": " + Vtemp + ", \"fecha\": \"" + fechaFormateada + "\", \"dispositivo_id\": \"" + dispositivo_id + "\"}",
                    new PeticionarioREST.RespuestaREST () {
                        @Override
                        public void callback(int codigo, String cuerpo) {
                            Log.d( "PeticionarioReal", "TENGO RESPUESTA:\ncodigo = " + codigo + "\ncuerpo: \n" + cuerpo);

                        }
                    }
            );
        } else {
            Log.d("PeticionarioReal", "Vtemp es nulo. No se puede hacer la inserción.");
        }

        //elPeticionario.hacerPeticionREST("POST",  "https://jsonplaceholder.typicode.com/posts",
        //elPeticionario.hacerPeticionREST("POST",  "https://reqbin.com/echo/post/json",

    } // ()

    // ---------------------------------------------------------------------------------------------
    // probarEnviarPOST()
    // ---------------------------------------------------------------------------------------------
    private void probarEnviarPOST() {
        PeticionarioREST elPeticionario = new PeticionarioREST();

        elPeticionario.hacerPeticionREST("POST",  "https://httpbin.org/post",

                "{ 'title': 'El Conde de Montecristo', 'body': 'Puros Habanos', 'userId': 1234}",
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d( "pruebasPeticionario", "TENGO RESPUESTA:\ncodigo = " + codigo + "\ncuerpo: \n" + cuerpo);

                    }
                }
        );

        //elPeticionario.hacerPeticionREST("POST",  "https://jsonplaceholder.typicode.com/posts",
        //elPeticionario.hacerPeticionREST("POST",  "https://reqbin.com/echo/post/json",

    } // ()

    // ---------------------------------------------------------------------------------------------
    // probarEnviarGET()
    // ---------------------------------------------------------------------------------------------
    private void probarEnviarGET() {
        PeticionarioREST elPeticionario = new PeticionarioREST();

        //elPeticionario.hacerPeticionREST("GET",  "https://jsonplaceholder.typicode.com/posts/101",
        elPeticionario.hacerPeticionREST("GET",  "https://jsonplaceholder.typicode.com/users/1234/posts",
                null,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d( "pruebasPeticionario", "codigo = " + codigo + "\n" + cuerpo);
                    }
                }
        );

    } // ()

    // ---------------------------------------------------------------------------------------------
    // probarEnviarGET_Otra()
    // ---------------------------------------------------------------------------------------------
    private void probarEnviarGET_Otra() {
        PeticionarioREST elPeticionario = new PeticionarioREST();

        elPeticionario.hacerPeticionREST("GET",  "http://192.168.43.252:8080/medicion", null,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d( "pruebasPeticionario", "codigo = " + codigo + "\n" + cuerpo);
                    }
                }
        );

    } // ()
}
