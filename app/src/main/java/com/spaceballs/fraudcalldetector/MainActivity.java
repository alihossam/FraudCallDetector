package com.spaceballs.fraudcalldetector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 10);
        Intent startServiceIntent = new Intent(getApplicationContext(), PhoneCallListenerService.class);
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                System.out.println("FFMPEG loaded successfully");
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                System.out.println("Error: " + error.getMessage());
            }
        });
        startService(startServiceIntent);
        Toast.makeText(getApplicationContext(),"Call Recording Started",Toast.LENGTH_SHORT).show();
    }

}
