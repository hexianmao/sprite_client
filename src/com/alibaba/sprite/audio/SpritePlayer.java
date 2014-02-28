package com.alibaba.sprite.audio;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.alibaba.sprite.SpriteClient;

public class SpritePlayer extends Thread {

    protected SpriteClient client;
    protected boolean keepRunning;
    protected AudioTrack track;

    public void init(SpriteClient client) {
        this.client = client;
        this.keepRunning = true;
        this.track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SpriteClient.SAMPLE_RATE,
                SpriteClient.CHANNEL_OUT_CONFIG,
                SpriteClient.AUDIO_FORMAT,
                client.getBufferSize(),
                AudioTrack.MODE_STREAM);

        Log.d("Sprite", "bufferSize:" + client.getBufferSize());
        Log.d("Sprite", "bufferPoolSize:" + client.getBufferPool().size());
    }

    public void run() {
        track.play();

        ByteBuffer bb = null;
        while (keepRunning) {
            try {
                if ((bb = client.getBufferQueue().poll(500, TimeUnit.MILLISECONDS)) != null) {
                    //Log.d("##Track", "position:" + bb.position() + ",limit:" + bb.limit());
                    track.write(bb.array(), 0, bb.capacity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bb != null) {
                    client.getBufferPool().recycle(bb);
                    bb = null;
                }
            }
        }

        track.stop();
        track = null;
    }

    public void free() {
        keepRunning = false;
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Log.d("sleep exceptions...\n", "");
        }
    }

}
