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
    static String FORMAT = "mpeg";
    static String FILENAME = "call.mpeg";
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
        configure();
        System.out.println("Config Done!");
    }

    void configure() {
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(format);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(1);
        recorder.setAudioSamplingRate(16000);
        recorder.setOutputFile(outputDir.getAbsolutePath()+"/call.m4a");
        // TODO see if default is good enough
    }

    public void start() {
        System.out.println("Starting to record");
        if(started) {
            stop();
        }
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
            System.out.println("Released!");
            started = false;
        }
    }

    public String getSavedFileAbsolutePath() {
        return outputDir.getAbsolutePath() + "/" + FILENAME;
    }

    private void cleanUp() {
        // TODO clean the recorded file so that we don't keep any user data
        File[] files = outputDir.listFiles();
        for(File f : files) {
            String filePath = f.getAbsolutePath();
            String extension = filePath.substring(filePath.lastIndexOf('.'));
            if(extension == CallRecordingService.FORMAT) {
                f.delete();
            }
        }
    }


}
