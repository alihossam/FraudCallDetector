package com.spaceballs.fraudcalldetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.File;

// TODO make this a singleton somehow (static maybe)
public class PhoneCallListenerService extends Service {
    TelephonyManager manager;
    CallRecordingService recorder;
    File directory;
    SpeechToTextService STTService;
    String callTranscripts;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //directory = getApplicationContext().getDir("RecordedCallsCache", Context.MODE_PRIVATE);
        directory = getApplicationContext().getExternalFilesDir(null);
        recorder = new CallRecordingService(directory);
        manager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        STTService = new SpeechToTextService();
        attachListener();
        // TODO prob want to use START_STICKY instead
        return START_STICKY;
    }

    void attachListener() {
        manager.listen(new PhoneStateListener() {
            int prevState = TelephonyManager.CALL_STATE_IDLE;
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // TODO maybe we can leverage the incoming number in some way
                System.out.println("State Received:");
                System.out.println(state);
                if(state == TelephonyManager.CALL_STATE_IDLE && prevState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    prevState = TelephonyManager.CALL_STATE_IDLE;
                    recorder.stop();
                    STTService.getTranscripts(directory.getAbsolutePath()+"/call.webm");
                    cleanUp();
                }
                else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    prevState = TelephonyManager.CALL_STATE_OFFHOOK;
                    recorder.start();
                }

            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    void cleanUp() {
        // TODO clean the recorded file so that we don't keep any user data
    }
}