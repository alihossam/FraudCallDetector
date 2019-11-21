package com.spaceballs.fraudcalldetector;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static com.spaceballs.fraudcalldetector.DisclaimerDialog.IDisclaimerDialogHandler;

public class MainActivity extends AppCompatActivity implements IDisclaimerDialogHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user didn't agree to the disclaimer
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
        setContentView(R.layout.activity_main);
        SharedPreferences disclaimerPref = getApplicationContext().getSharedPreferences(getString(R.string.UserPreferences), MODE_PRIVATE);
        if(disclaimerPref.getBoolean(getString(R.string.rememberKey), false)) {
            setupAndStartListener();
        } else {
            DialogFragment disclaimerDialog = DisclaimerDialog.createDialog(this);
            disclaimerDialog.show(getSupportFragmentManager(), "DisclaimerDialog");
        }
    }

    private void setupAndStartListener() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 10);
        Intent startServiceIntent = new Intent(getApplicationContext(), PhoneCallListenerService.class);
        startService(startServiceIntent);
        Toast.makeText(getApplicationContext(), R.string.service_started_success, Toast.LENGTH_SHORT);
    }

    private void setRememberChoiceToTrue() {
        SharedPreferences disclaimerPref = getApplicationContext().getSharedPreferences(getString(R.string.UserPreferences), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = disclaimerPref.edit();
        prefsEditor.putBoolean(getString(R.string.rememberKey), true);
        prefsEditor.apply();
    }

    @Override
    public void onAgree(Dialog dialog, boolean buttonChecked) {
        dialog.dismiss();
        if(buttonChecked) {
            setRememberChoiceToTrue();
        }

        setupAndStartListener();
    }

    @Override
    public void onDisagree(Dialog dialog) {
        dialog.dismiss();
        finish();
    }
}
