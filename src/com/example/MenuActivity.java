package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by IntelliJ IDEA.
 * User: laurent
 * Date: 29/09/11
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public class MenuActivity extends Activity {

    private static final int DIALOG_CHANGE_SCREEN_TO_INTERVALLES = 8;
    private static final int DIALOG_CHANGE_SCREEN_TO_SERIES = 9;
    private static final int DIALOG_CHANGE_SCREEN_TO_INFO = 10;

    private Context ctx;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    private boolean isOnInfoActivity() {
        return this instanceof InfoActivity;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        ctx = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Changer d'écran mettra fin à l'exercice en cours et stoppera le décompte des temps," +
                "êtes vous sûr de vouloir changer ?");
        builder.setNegativeButton("Non, continuer l'exercice", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        switch (id) {
            case DIALOG_CHANGE_SCREEN_TO_SERIES:
                if (isOnInfoActivity()) {
                    startActivityForResult(new Intent(ctx, SeriesActivity.class), 0);
                    return null;
                }
                builder.setPositiveButton("Oui, terminer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(ctx, SeriesActivity.class), 0);
                    }
                });
                return builder.create();
            case DIALOG_CHANGE_SCREEN_TO_INTERVALLES:
                if (isOnInfoActivity()) {
                    startActivityForResult(new Intent(ctx, MyActivity.class), 0);
                    return null;
                }
                builder.setPositiveButton("Oui, terminer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(ctx, MyActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                });
                return builder.create();
            case DIALOG_CHANGE_SCREEN_TO_INFO:
                builder.setPositiveButton("Oui, terminer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(ctx, InfoActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                });
                return builder.create();
        }
        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.toChrono:
                showDialog(DIALOG_CHANGE_SCREEN_TO_INTERVALLES);
                return true;
            case R.id.toSeries:
                showDialog(DIALOG_CHANGE_SCREEN_TO_SERIES);
                return true;
            case R.id.toInfo:
                showDialog(DIALOG_CHANGE_SCREEN_TO_INFO);
                return true;
        }
        return super.onOptionsItemSelected(item); // unhandheld
    }

}