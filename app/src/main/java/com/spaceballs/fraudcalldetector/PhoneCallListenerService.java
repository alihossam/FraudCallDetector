package com.spaceballs.fraudcalldetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;

// TODO make this a singleton somehow (static maybe)
public class PhoneCallListenerService extends Service {
    TelephonyManager manager;
    CallRecordingService recorder;
    File directory;
    SpeechToTextService STTService;
    PhoneStateListener listener;
    SpamAPI spamAPI;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        directory = getApplicationContext().getExternalFilesDir(null);
        spamAPI = new SpamAPI("https://oopspam.p.rapidapi.com/v1/spamdetection", "");
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
                Log.i("PhoneCallListenerService", "Stopping");
                STTService.speechToTextUsingGoogle(
                        new File(recorder.getSavedFileAbsolutePath()),
                        new SpeechToTextService.CallbackWithTranscript() {
                    @Override
                    public void run(String text) {
                        Log.i("PhoneCallListenerService", "Text Provided to OOPSpam: " + text);
                        try {
                            boolean isTextSpam = spamAPI.isTextSpam(spamAPI.sendOopSpamRequestJson(text));
                            if (isTextSpam) {
                                Log.i("PhoneCallListenerService", "Spam: notification being pushed");
                                NotificationsHelper.pushNotification(
                                        getApplicationContext(),
                                        "WARNING!! YOUR CALL MIGHT BE A SCAM",
                                        "If you gave away any private information, inform the relevant parties immediately.");
                            } else {
                                Log.i("PhoneCallListenerService", "Not spam: notification not being pushed");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        attachListener();
        return START_STICKY;
    }

    void attachListener() {
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
