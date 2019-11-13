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
    MediaRecorder recorder;
    File outputFile;
    int format;
    boolean started = false;
    CallRecordingService(String path, int format) {
        outputFile = new File(path);
        if(!outputFile.exists())
            throw new IOError(new Throwable("File doesn't exist"));
        this.recorder = new MediaRecorder();
        this.format = format;
    }

    void configure() {
        recorder.setOutputFormat(format);
        recorder.setOutputFile(outputFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);// TODO see if default is good enough
    }

    public void start() {
        if(started) {
            stop();
        }
        configure();
        try {
            recorder.prepare();
        } catch (IOException e) {
            // TODO show the user a message saying couldn't start recording
            return;
        }
        recorder.start();
    }

    public void stop() {
        recorder.stop();
        recorder.reset();
        recorder.release();
    }


}
