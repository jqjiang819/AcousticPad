package com.bigrats.acpadlib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bigrats.acpadlib.structs.*;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by jqjiang on 2017/5/1.
 */
public class AcPadHelper {
    // in-class constants
    private final static int duration = 60;

    // pre-init parameters
    private String algorithm;

    // init datas
    private SndData sndData;
    private CodData codData;
    private CicData cicData_i, cicData_q;
    private LevdData levdData_i, levdData_q;
    private FcdData fcdData;
    private double[] vec_i, vec_q;

    // init interfaces
    Handler hdl_calc = null;
    SndHelper sndHelper = null;

    public AcPadHelper() {}

    public AcPadHelper(String algorithm) {
        this.setAlgorithm(algorithm);
    }

    private void preInit() {
        this.chkSettings();
    }

    private void init() {
        this.preInit();

        // data variables
        this.codData = new CodData();
        this.cicData_i = new CicData();
        this.cicData_q = new CicData();
        this.levdData_i = new LevdData();
        this.levdData_q = new LevdData();
        this.fcdData = new FcdData();
        this.vec_i = new double[Params.FRAME_SIZE/Params.CIC_DECIM_FACT];
        this.vec_q = new double[Params.FRAME_SIZE/Params.CIC_DECIM_FACT];

        // interfaces
        this.sndHelper = new SndHelper();
    }

    public void run() {
        this.init();
//        for (int i = 0; i < this.snd_data.length; i++) {
//            // coherent detection
//            this.codData = Utilities.codetect(this.snd_data[i], this.codData.time);
//            // cic filtering
//            this.cicData_i = Utilities.cicdecim(this.cicData_i.setData(this.codData.data_i));
//            this.cicData_q = Utilities.cicdecim(this.cicData_q.setData(this.codData.data_q));
//            // levd detection
//            if (this.algorithm.equals("LEVD")) {
//                this.levdData_i = Utilities.levddetect(levdData_i.setData(this.cicData_i.data));
//                this.levdData_q = Utilities.levddetect(levdData_q.setData(this.cicData_q.data));
//                this.vec_i = this.cicData_i.data.minus(this.levdData_i.data).getArrayCopy()[0];
//                this.vec_q = this.cicData_q.data.minus(this.levdData_q.data).getArrayCopy()[0];
//            }
//            // fcd algorithm
//            if (this.algorithm.equals("FCD")) {
//                this.fcdData = Utilities.fcddetect(new FcdData(this.cicData_i.data, this.cicData_q.data));
//                this.vec_i = this.cicData_i.data.minus(this.fcdData.data_i).getArrayCopy()[0];
//                this.vec_q = this.cicData_q.data.minus(this.fcdData.data_q).getArrayCopy()[0];
//            }
//        }
        hdl_calc = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Bundle bundle = msg.getData();
                        short[] snd_data = bundle.getShortArray("SND_DATA");
                        break;
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                sndData = sndHelper.playrec();
                while (sndHelper.isRunning()) {
                    Message msg = hdl_calc.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putShortArray("SND_DATA", sndData.read());
                    msg.what = 0;
                    msg.setData(bundle);
                    hdl_calc.sendMessage(msg);
                }
            }
        }).start();
    }

    public double[][] getDistData() {
        double[] data_i = vec_i;
        double[] data_q = vec_q;

        int len = data_i.length;
        int fs_out = Params.FREQ_SAMP / Params.CIC_DECIM_FACT;
        double wavlen = Params.WAVE_LENGTH;
        double[] ang = new double[len];
        double[] pha = new double[len];
        double[] dist = new double[len];
        double[] time = new double[len];
        double ang_add = 0;

        ang[0] = Math.atan2(data_i[0], data_q[0]);
        pha[0] = ang[0];
        dist[0] = pha[0] / (2 * Math.PI) * wavlen;
        time[0] = 1.0 / (double) fs_out;
        for (int i = 1; i < len; i++) {
            ang[i] = Math.atan2(data_i[i], data_q[i]);
            if (ang[i] - ang[i - 1] < -Math.PI) {
                ang_add += 2 * Math.PI;
            }
            else if (ang[i] - ang[i - 1] > Math.PI) {
                ang_add -= 2 * Math.PI;
            }
            pha[i] = ang[i] + ang_add;
            dist[i] = (pha[i] / (2 * Math.PI) * wavlen - dist[0])/2;
            time[i] = (double) (i + 1) / (double) fs_out;
        }
        dist[0] = 0;

        return new double[][]{time, dist};
    }

    public AcPadHelper setAlgorithm(String algorithm) {
        if (algorithm.equals("LEVD") || algorithm.equals("FCD")) {
            this.algorithm = algorithm;
        }
        else {
            throw new IllegalArgumentException(String.format("Algorithm %s is invalid.", algorithm));
        }
        return this;
    }

    private void chkSettings() {
        if (this.algorithm == null) {
            throw new IllegalArgumentException("PreInit: algorithm can't be null.");
        }
    }
}
