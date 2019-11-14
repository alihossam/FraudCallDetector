package com.spaceballs.fraudcalldetector;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

/*
 * A wrapper class for a media recorder.
 * An instance will rewrite to the same file
 */
public class CallRecordingService {
    private MediaRecorder recorder;
    private File outputDir;
    private int format;
    private boolean started = false;
    CallRecordingService(File dir) {
        outputDir = dir;
        if(!outputDir.exists() || !outputDir.isDirectory())
            throw new IOError(new Throwable("Not a Directory or Directory doesn't exist"));
        System.out.println("Output Dir:");
        System.out.println(outputDir.getAbsoluteFile());
        this.recorder = new MediaRecorder();
        this.format = MediaRecorder.OutputFormat.THREE_GPP;
    }

    void configure() {
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(format);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(outputDir.getAbsolutePath()+"/call.3gpp");
        // TODO see if default is good enough
    }

    public void start() {
        if(started) {
            stop();
        }
        configure();
        started = true;
        try {
            recorder.prepare();
        } catch (IOException e) {
            // TODO show the user a message saying couldn't start recording
            return;
        }
        recorder.start();
    }

    public void stop() {
        if(started) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            started = false;
        }
    }


}
