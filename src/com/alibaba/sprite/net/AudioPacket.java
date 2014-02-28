package com.alibaba.sprite.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.alibaba.sprite.util.StreamUtil;

/**
 * @author hexianmao
 */
public class AudioPacket {
    public static final byte COM_INIT = 0;
    public static final byte COM_UID = 1;
    public static final byte COM_OK = 2;
    public static final byte COM_ERR = 3;
    public static final byte COM_CALL = 4;
    public static final byte COM_STREAM = 5;

    public int length;
    public byte type;
    public byte[] data;

    public void read(InputStream in) throws IOException {
        length = StreamUtil.readUB3(in);
        type = StreamUtil.read(in);
        byte[] ab = new byte[length];
        StreamUtil.read(in, ab, 0, ab.length);
        data = ab;
    }

    public void write(OutputStream out) throws IOException {
        StreamUtil.writeUB3(out, calcPacketSize());
        StreamUtil.write(out, type);
        out.write(data);
    }

    public int calcPacketSize() {
        return data == null ? 0 : data.length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type=").append(type).append(",data=").append(Arrays.toString(data));
        return sb.toString();
    }

}
