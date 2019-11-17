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
        this.format = MediaRecorder.OutputFormat.MPEG_4;
    }

    void configure() {
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(format);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(outputDir.getAbsolutePath()+"/call.mpeg");
        // TODO see if default is good enough
    }

    public void start() {
        System.out.println("Starting to record");
        if(started) {
            stop();
        }
        configure();
        System.out.println("Config Done!");
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
        System.out.println("Stopping1");
        if(started) {
            System.out.println("Stopping2");
            recorder.stop();
            recorder.reset();
            recorder.release();
            System.out.println("Released!");
            started = false;
        }
    }


}
