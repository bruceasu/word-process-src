package me.asu.startdict;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Idx {
    final static int MAX_WORD=256; // 最长输入单词字符数
    byte[] rawData = new byte[MAX_WORD];
    int mark = 0;
    long offset;
    int length;

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) throws IOException {
        if(rawData == null || rawData.length == 0) this.mark = 0;
        if (rawData.length > MAX_WORD) throw new IOException("Too long, exceed limit " + MAX_WORD);
        this.mark = rawData.length;
        System.arraycopy(rawData, 0, this.rawData, 0, mark);
    }

    public String getWord() {
        return new String(this.rawData, 0, mark, StandardCharsets.UTF_8);
    }

    public int getMark() {
        return mark;
    }

    public void setMark(short mark) {
        this.mark = mark;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
