package com.alibaba.sprite.audio;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.alibaba.sprite.SpriteClient;

public class Saudioclient extends Thread {

    protected SpriteClient client;
    protected boolean keepRunning;
    protected AudioRecord record;
    protected int m_in_buf_size;
    protected byte[] m_in_bytes;

    protected Socket s;
    protected DataOutputStream dout;
    protected LinkedList<byte[]> m_in_q;

    public void init(SpriteClient client) {
        this.client = client;
        this.keepRunning = true;
        this.record = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
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
                client.getBufferQueue().offer(bb);
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
