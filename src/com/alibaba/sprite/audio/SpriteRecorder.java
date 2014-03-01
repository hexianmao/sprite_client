package com.alibaba.sprite.audio;

import java.nio.ByteBuffer;

import android.media.AudioRecord;
import android.util.Log;

import com.alibaba.sprite.SpriteClient;

public class SpriteRecorder extends Thread {

    protected SpriteClient client;
    protected boolean keepRunning;
    protected AudioRecord record;

    public void init(SpriteClient client) {
        this.client = client;
        this.keepRunning = true;
        this.record = new AudioRecord(
                SpriteClient.AUDIO_SOURCE,
                SpriteClient.SAMPLE_RATE,
                SpriteClient.CHANNEL_IN_CONFIG,
                SpriteClient.AUDIO_FORMAT,
                client.getBufferSize());
    }

    public void run() {
        record.startRecording();

        ByteBuffer bb = null;
        while (keepRunning) {
            try {
                bb = client.getBufferPool().allocate();
                record.read(bb.array(), 0, bb.capacity());
                //Log.d("##Record", "position:" + bb.position() + ",limit:" + bb.limit());
                client.getSendQueue().offer(bb);
                bb = null;
            } catch (Exception e) {
                if (bb != null) {
                    client.getBufferPool().recycle(bb);
                    bb = null;
                }
                e.printStackTrace();
            }
        }

        record.stop();
        record = null;
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
