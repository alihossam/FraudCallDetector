package com.spaceballs.fraudcalldetector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 10);
        Intent startServiceIntent = new Intent(getApplicationContext(), PhoneCallListenerService.class);
        startService(startServiceIntent);
        Toast.makeText(getApplicationContext(),"Call Recording Started",Toast.LENGTH_SHORT).show();
    }

}
