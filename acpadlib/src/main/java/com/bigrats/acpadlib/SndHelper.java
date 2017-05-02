package com.bigrats.acpadlib;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.bigrats.acpadlib.structs.SndData;

/**
 * Created by jqjiang on 2017/5/2.
 */

public class SndHelper {
    private final static int dura = 60;

    private boolean state = false;

    private AudioTrack audioTrack = null;
    private AudioRecord audioRecord = null;

    public SndHelper() {
        this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Params.FREQ_SAMP,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                2 * dura * Params.FREQ_SAMP, AudioTrack.MODE_STATIC);
        this.audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                Params.FREQ_SAMP, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                Params.FRAME_SIZE);
    }

    private byte[] genWave() {
        int numSamples = dura * Params.FREQ_SAMP;

        double[] sample = new double[numSamples];
        byte[] generatedSnd = new byte[2 * numSamples];

        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i /  ((double)Params.FREQ_SAMP/(double)Params.FREQ_CENTER));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }

        return generatedSnd;
    }

    private void play() {
        byte[] snd_data = genWave();
        this.audioTrack.write(snd_data, 0, snd_data.length);
        audioTrack.play();
    }

    private void rec() {
        audioRecord.startRecording();
    }

    public SndData playrec() {
        play();
        rec();
        this.state = true;
        return new SndData(audioRecord, Params.FRAME_SIZE);
    }

    public void stop() {
        audioRecord.stop();
        audioTrack.stop();
        audioRecord.release();
        audioTrack.release();
        this.state = false;
    }

    public boolean isRunning() {
        return this.state;
    }
}
