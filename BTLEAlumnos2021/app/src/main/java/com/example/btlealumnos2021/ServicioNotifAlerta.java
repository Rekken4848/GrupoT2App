package com.example.btlealumnos2021;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class ServicioNotifAlerta extends Service {

    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

    @Override public void onCreate() {
        Toast.makeText(this,"Servicio creado", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {

        String TituloDato="";
        String DescDato="";

        int tipoDato = (int) intent.getSerializableExtra("tipoDato");

        switch (tipoDato){
            case 0:
                break;
            case 1: //ozono
                TituloDato="O3";
                DescDato="ozono";
                break;
            case 2: //NO2
                TituloDato="NO2";
                DescDato="dioxido de nitrogeno";
                break;
            case 3: //SO2
                TituloDato="SO2";
                DescDato="dioxido de azufre";
                break;
            case 4: //CO (en mg)
                TituloDato="CO";
                DescDato="oxido de carbono";
                break;
            case 5: // Benceno
                TituloDato="C6H6";
                DescDato="benceno";
                break;
        }
        int tipoNotif = (int) intent.getSerializableExtra("tipoNotif");

        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (tipoNotif == 1){ //es alerta por limite
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(this, CANAL_ID)
                            .setSmallIcon(android.R.drawable.ic_popup_reminder)
                            .setContentTitle("Limite de " + TituloDato + " excedido")
                            .setContentText(" Se detectaron altos niveles de " + DescDato)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Has superado el límite recomendado por la OMS de " + DescDato +". Esto podria tener efectos perjudiciales para tu salud"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            //startForeground(NOTIFICACION_ID, notificacion.build());

            notificationManager.notify(NOTIFICACION_ID, notificacion.build());

        } else if (tipoNotif == 2){ //es alerta por posible error de sensor
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(this, CANAL_ID)
                            .setSmallIcon(android.R.drawable.ic_popup_reminder)
                            .setContentTitle("Sonda desconectada")
                            .setContentText("No se detecta la sonda")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("No estamos detectando tu sonda, podria ser que no hayas vinculado tu sonda, que la sonda este estropeada o que la bateria este agotada o este dando problemas. Por favor ponte en contacto con un administrador para solucionar este problema"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            //startForeground(NOTIFICACION_ID, notificacion.build());

            notificationManager.notify(NOTIFICACION_ID, notificacion.build());
        }

        Toast.makeText(this,"Servicio arrancado "+ idArranque,
                Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override public void onDestroy() {
        Toast.makeText(this,"Servicio detenido", Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
