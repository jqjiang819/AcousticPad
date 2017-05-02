package com.bigrats.acpadlib.structs;

import android.media.AudioRecord;


/**
 * Created by jqjiang on 2017/5/2.
 */

public class SndData {
    private AudioRecord audioRecord = null;
    private short[] data;
    private int bufSize;

    public SndData(AudioRecord audioRecord, int bufSize) {
        this.data = new short[bufSize];
        this.audioRecord = audioRecord;
        this.bufSize = bufSize;
    }

    public short[] read() {
        this.audioRecord.read(this.data, 0, this.bufSize);
        return this.data;
    }
}
