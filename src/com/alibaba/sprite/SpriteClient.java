package com.alibaba.sprite;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Application;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;

import com.alibaba.sprite.util.BufferPool;

/**
 * @author xianmao.hexm
 */
public class SpriteClient extends Application {

    private static final int TOTAL_SIZE = 1024 * 1024;

    private final int bufferSize;
    private final BufferPool bufferPool;
    private final BlockingQueue<ByteBuffer> bufferQueue;

    public SpriteClient() {
        int s1 = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int s2 = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        this.bufferSize = s1 > s2 ? s1 : s2;
        this.bufferPool = new BufferPool(TOTAL_SIZE, bufferSize);
        this.bufferQueue = new LinkedBlockingQueue<ByteBuffer>();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public BufferPool getBufferPool() {
        return bufferPool;
    }

    public BlockingQueue<ByteBuffer> getBufferQueue() {
        return bufferQueue;
    }

    public void free() {
        bufferQueue.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
