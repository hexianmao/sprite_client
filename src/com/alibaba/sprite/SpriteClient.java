package com.alibaba.sprite;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Application;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.alibaba.sprite.util.BufferPool;

/**
 * @author xianmao.hexm
 */
public class SpriteClient extends Application {

    public static final int TOTAL_BUFFER_SIZE = 1024 * 1024;

    // 音频获取源 
    public static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025  
    public static int SAMPLE_RATE = 44100;

    // 设置音频的录制声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    public static int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    // 设置音频的播放声道CHANNEL_OUT_STEREO为双声道，CHANNEL_OUT_MONO为单声道  
    public static int CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;

    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    public static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private final int bufferSize;
    private final BufferPool bufferPool;
    private final BlockingQueue<ByteBuffer> sendQueue;
    private final BlockingQueue<ByteBuffer> recvQueue;

    public SpriteClient() {
        int s1 = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_CONFIG, AUDIO_FORMAT);
        int s2 = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT_CONFIG, AUDIO_FORMAT);
        this.bufferSize = s1 > s2 ? s1 : s2;
        this.bufferPool = new BufferPool(TOTAL_BUFFER_SIZE, bufferSize);
        this.sendQueue = new LinkedBlockingQueue<ByteBuffer>();
        this.recvQueue = new LinkedBlockingQueue<ByteBuffer>();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public BufferPool getBufferPool() {
        return bufferPool;
    }

    public BlockingQueue<ByteBuffer> getSendQueue() {
        return sendQueue;
    }

    public BlockingQueue<ByteBuffer> getRecvQueue() {
        return recvQueue;
    }

    public void free() {
        sendQueue.clear();
        recvQueue.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
