package com.example.btlealumnos2021;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmaHelper {

    public static void configurarAlarma(Context context, long tiempoEnMilisegundos) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MiReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Configura la alarma
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + tiempoEnMilisegundos, pendingIntent);
    }

    public static class MiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Lógica a realizar cuando se activa la alarma
            mostrarNotificacion(context);
        }

        private void mostrarNotificacion(Context context) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "mi_canal";
            String channelName = "Mi Canal";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle("Alarma Activada")
                    .setContentText("¡Es hora de hacer algo!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);

            // Agrega sonido
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            builder.setSound(alarmSound);

            // Agrega vibración
            long[] pattern = {0, 1000, 500, 1000};
            builder.setVibrate(pattern);

            notificationManager.notify(1, builder.build());
        }
    }
}