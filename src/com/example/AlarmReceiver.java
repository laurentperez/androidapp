package com.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: laurent
 * Date: 20/09/11
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("alarm_message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            //vibre pendant 1 seconde, puis attend 1/2 secondes avant de recommencer.
            vibrator.vibrate(new long[]{0, 600, 100, 600, 100}, -1);

            final MediaPlayer mp = MediaPlayer.create(context, R.raw.beep2);
            mp.setLooping(false);
            mp.start();
            Handler h = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        mp.stop();
                        mp.release();
                    }
                    super.handleMessage(msg);
                }
            };
            Message m = new Message();
            m.what = 0;
            h.sendMessageDelayed(m, 6000);

            /*
            NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int icon = R.drawable.icon;
            CharSequence tickerText = "EasyMuscu";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);
            CharSequence contentTitle = "Temps imparti atteint";
            CharSequence contentText = message;
            Intent notificationIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            mgr.notify(1, notification);
            */

        } catch (Exception e) {
            Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }
}
