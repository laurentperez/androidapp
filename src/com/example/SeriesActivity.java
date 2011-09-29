package com.example;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: laurent
 * Date: 20/09/11
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class SeriesActivity extends MenuActivity {

    private Chronometer serieChronometer;

    private TextView labelSeriesCount;

    private Context ctx;

    private boolean inPause = false;

    @Override
    protected void onRestart() {
        System.out.println("onRestart");
        serieChronometer.stop();
        serieChronometer.setBase(SystemClock.elapsedRealtime());
        super.onRestart();
    }

    private void increment() {
        labelSeriesCount.setText(String.valueOf(Integer.valueOf((String) labelSeriesCount.getText()) + 1));
    }

    public void registerListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (inPause) {
                    Toast.makeText(ctx, "Déjà en cours de récupération !", Toast.LENGTH_SHORT).show();
                    return;
                }
                inPause = true;
                button.setEnabled(false);
                final int time = MyActivity.toSeconds((String) button.getText());
                serieChronometer.setBase(SystemClock.elapsedRealtime());
                serieChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer chrono) {
                        long chronoBase = chrono.getBase();
                        long msec = (SystemClock.elapsedRealtime() - chronoBase) / 1000;
                        long seconds = msec % 60;
                        Log.d("series", "serieChronometer seconds = " + seconds + " time:" + time + " msec:" + msec);
                        if (msec >= time) {
                            serieChronometer.stop();
                            button.setEnabled(true);
                            increment();
                            inPause = false;
                        }
                    }
                });
                setupAlarm(time, "Période de récupération terminée");
                serieChronometer.start();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                AlertDialog.Builder builder;
                AlertDialog alertDialog;
                Context mContext = this;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_dialog,
                        (ViewGroup) findViewById(R.id.layout_root));
                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Commencez l'exercice puis appuyez sur un bouton pour déclencher un temps de récupération");
                builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);
                alertDialog = builder.create();
                return alertDialog;
        }
        return super.onCreateDialog(id);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        ctx = this;
        labelSeriesCount = (TextView) findViewById(R.id.labelSeriesCount);
        final Button bone = (Button) findViewById(R.id.bone);
        final Button btwo = (Button) findViewById(R.id.btwo);
        final Button bthree = (Button) findViewById(R.id.bthree);
        final Button bfour = (Button) findViewById(R.id.bfour);
        final ImageButton baide = (ImageButton) findViewById(R.id.baide);
        baide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(0);
            }
        });
        serieChronometer = (Chronometer) findViewById(R.id.serieChronometer);
        registerListener(bone);
        registerListener(btwo);
        registerListener(bthree);
        registerListener(bfour);
        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                labelSeriesCount.setText("0");
            }
        });
    }

    public void setupAlarm(int time, String text) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, time);
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("alarm_message", text);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 192838, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    }
}