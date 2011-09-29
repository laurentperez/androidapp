package com.example;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MyActivity extends MenuActivity {

    private TextView labelExoTime;
    private String exoTime;
    private String pauseTime;
    private Button mPickExoTime;
    private TextView labelPauseTime;
    private Button mPickPauseTime;

    private Button startButton;
    private Button stopButton;
    private Chronometer exoChronometer;
    private Chronometer pauseChronometer;

    private static final int DIALOG_INTERVALLE_TEMPSEXO = 0;
    private static final int DIALOG_INTERVALLE_TEMPSRECUP = 1;

    private final CharSequence[] exoTimes = {"0'25", "2'00", "2'30", "3'00", "4'00", "5'00"};
    private final CharSequence[] recupTimes = {"0'10", "1'00", "1'30", "2'00", "2'30", "3'00"};

    private Context ctx;

    @Override
    protected void onResume() {
        System.out.println("onResume");
        // TODO reprise des chronos
        super.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        super.onPause();
        // TODO pauser les chronos
    }

    @Override
    protected void onRestart() {
        System.out.println("onRestart");
        exoChronometer.stop();
        pauseChronometer.stop();
        exoChronometer.setBase(SystemClock.elapsedRealtime());
        pauseChronometer.setBase(SystemClock.elapsedRealtime());
        super.onRestart();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ctx = this;
        // capture elements ui
        labelExoTime = (TextView) findViewById(R.id.labelExoTime);
        labelPauseTime = (TextView) findViewById(R.id.labelPauseTime);

        mPickExoTime = (Button) findViewById(R.id.pickExoTime);
        mPickPauseTime = (Button) findViewById(R.id.pickPauseTime);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        exoChronometer = (Chronometer) findViewById(R.id.exoChronometer);
        pauseChronometer = (Chronometer) findViewById(R.id.pauseChronometer);

        // dialog exo
        mPickExoTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_INTERVALLE_TEMPSEXO);
            }
        });
        // dialog pause
        mPickPauseTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_INTERVALLE_TEMPSRECUP);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                exoChronometer.stop();
                pauseChronometer.stop();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //exoChronometer.setFormat("mm:ss");
                final int cExoTime = toSeconds(exoTime);
                final int cPauseTime = toSeconds(pauseTime);

                exoChronometer.setBase(SystemClock.elapsedRealtime());
                exoChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer chrono) {
                        long seconds = ((SystemClock.elapsedRealtime() - chrono.getBase()) / 1000) % 60;
                        Log.d("chrono", "exoChronometer seconds = " + seconds + " cexoTime:" + cExoTime);
                        if (seconds >= cExoTime) {
                            // Toast.makeText(getApplicationContext(), "Début récupération", Toast.LENGTH_SHORT).show();
                            //
                            exoChronometer.stop();
                            pauseChronometer.setBase(SystemClock.elapsedRealtime());
                            setupAlarm(cPauseTime, "Période de récupération terminée");
                            pauseChronometer.start();
                        }

                    }
                });
                pauseChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer chrono) {
                        long seconds = ((SystemClock.elapsedRealtime() - chrono.getBase()) / 1000) % 60;
                        Log.d("chrono", "pauseChronometer seconds = " + seconds + " pauseTime:" + cPauseTime);
                        if (seconds >= cPauseTime) {
                            //Toast.makeText(getApplicationContext(), "Fin récupération", Toast.LENGTH_SHORT).show();
                            pauseChronometer.stop();
                            //exoChronometer.stop();
                            exoChronometer.setBase(SystemClock.elapsedRealtime());
                            setupAlarm(cExoTime, "Période de récupération en cours");
                            exoChronometer.start();
                        }
                    }
                });
                setupAlarm(cExoTime, "Période de récupération en cours");
                exoChronometer.start();
            }
        });

        // duree exo, pause par défaut
        exoTime = (String) exoTimes[0];
        pauseTime = (String) recupTimes[0];
        labelExoTime.setText(exoTimes[0]);
        labelPauseTime.setText(recupTimes[0]);
    }

    public void setupAlarm(int time, String text) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, time);
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("alarm_message", text);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    }

    public static int toSeconds(String duration) {
        String[] strings = duration.split("'");
        return (Integer.parseInt(strings[0]) * 60) + Integer.parseInt(strings[1]);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_INTERVALLE_TEMPSEXO:
                builder.setTitle("Temps exercice");
                builder.setItems(exoTimes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String time = (String) exoTimes[item];
                        exoTime = time;
                        labelExoTime.setText(exoTime);
                        Toast.makeText(getApplicationContext(), exoTimes[item], Toast.LENGTH_SHORT).show();
                    }
                });
                return builder.create();
            case DIALOG_INTERVALLE_TEMPSRECUP:
                builder.setTitle("Temps récupération");
                builder.setItems(recupTimes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String time = (String) recupTimes[item];
                        pauseTime = time;
                        labelPauseTime.setText(pauseTime);
                        Toast.makeText(getApplicationContext(), recupTimes[item], Toast.LENGTH_SHORT).show();
                    }
                });
                return builder.create();
        }
        return super.onCreateDialog(id);
    }

}
