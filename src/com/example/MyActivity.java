package com.example;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MyActivity extends Activity {

    private TextView labelExoTime;
    private String exoTime;
    private String pauseTime;

    private Button mPickExoTime;

    private TextView labelPauseTime;
    private Button mPickPauseTime;
    private int mPauseSeconds;

    private Button startButton;
    private Button stopButton;
    private Chronometer exoChronometer;
    private Chronometer pauseChronometer;

    static final int TIME_DIALOG_ID_EXO = 0;
    static final int TIME_DIALOG_ID_PAUSE = 1;

    final CharSequence[] exoTimes = {"0'25", "2'00", "2'30", "3'00", "4'00", "5'00"};
    final CharSequence[] recupTimes = {"0'10", "1'00", "1'30", "2'00", "2'30", "3'00"};

    private Context ctx;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

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
        super.onRestart();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ChronoActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
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
                showDialog(TIME_DIALOG_ID_EXO);
            }
        });
        // dialog pause
        mPickPauseTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID_PAUSE);
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
                        System.out.println("exoChronometer seconds = " + seconds + " cexoTime:" + cExoTime);
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
                        System.out.println("pauseChronometer seconds = " + seconds + " pauseTime:" + cPauseTime);
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

    private void setupAlarm(int time, String text) {
        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, time);
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("alarm_message", text);
        // In reality, you would want to have a static variable for the request code instead of 192837
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    }

    private int toSeconds(String duration) {
        String[] strings = duration.split("'");
        int secs = Integer.parseInt(strings[0]) * 60 + Integer.parseInt(strings[1]);
        return secs;
    }

    private Dialog getAlertDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case TIME_DIALOG_ID_EXO:
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
            case TIME_DIALOG_ID_PAUSE:
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
        return null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return getAlertDialog(id);
    }


    // updates the time we display in the TextView
    private void updateDisplay() {
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


}
