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
    PhoneStateListener listener;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        directory = getApplicationContext().getExternalFilesDir(null);
        recorder = new CallRecordingService(directory);
        manager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        STTService = new SpeechToTextService();
        listener = new PhoneStateListener() {
            int prevState = TelephonyManager.CALL_STATE_IDLE;
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // TODO maybe we can leverage the incoming number in some way
                System.out.println("State Received:");
                System.out.println(state);
                if(state == TelephonyManager.CALL_STATE_IDLE && prevState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    try {
                        stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    try {
                        start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            void start() {
                prevState = TelephonyManager.CALL_STATE_OFFHOOK;
                recorder.start();
            }

            void stop() {
                prevState = TelephonyManager.CALL_STATE_IDLE;
                recorder.stop();
                STTService.speechToTextUsingGoogle(new File(recorder.getSavedFileAbsolutePath()));
                // TODO remove FLAC stuff and unneeded dependencies
            }
        };
        attachListener();
        return START_STICKY;
    }

    void attachListener() {
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
