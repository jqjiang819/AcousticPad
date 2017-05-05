package com.bigrats.acpadlib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bigrats.acpadlib.structs.*;

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
    private double dist_init;

    // init interfaces
    private Handler hdl_calc = null;
    private Handler hdl_return = null;
    private SndHelper sndHelper = null;

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
        this.dist_init = 0;

        // interfaces
        this.sndHelper = new SndHelper();
        this.hdl_return = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (sndHelper.isRunning()) {
                    Bundle bundle = msg.getData();
                    double[] dist = bundle.getDoubleArray("DIST");
                    onDataReceive(dist);
                    super.handleMessage(msg);
                }
            }
        };
    }

    public void run() {
        this.init();
        hdl_calc = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                final short[] pcm_data = bundle.getShortArray("SND_DATA");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // convert snd data to double
                        double[] snd_data = Utilities.sndcnvrt(pcm_data);
                        // coherent detection
                        codData = Utilities.codetect(snd_data, codData.time);
                        // cic filtering
                        cicData_i = Utilities.cicdecim(cicData_i.setData(codData.data_i));
                        cicData_q = Utilities.cicdecim(cicData_q.setData(codData.data_q));
                        if (algorithm.equals("LEVD")) {
                            levdData_i = Utilities.levddetect(levdData_i.setData(cicData_i.data));
                            levdData_q = Utilities.levddetect(levdData_q.setData(cicData_q.data));
                            vec_i = cicData_i.data.minus(levdData_i.data).getArrayCopy()[0];
                            vec_q = cicData_q.data.minus(levdData_q.data).getArrayCopy()[0];
                        }
                        // fcd algorithm
                        if (algorithm.equals("FCD")) {
                            fcdData = Utilities.fcddetect(new FcdData(cicData_i.data, cicData_q.data));
                            vec_i = cicData_i.data.minus(fcdData.data_i).getArrayCopy()[0];
                            vec_q = cicData_q.data.minus(fcdData.data_q).getArrayCopy()[0];
                        }
                        // calc distance
                        double[] dist = Utilities.getDistData(vec_i, vec_q, dist_init);
                        dist_init = dist[dist.length-1];
                        // send data
                        Message msg = hdl_return.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putDoubleArray("DIST", dist);
                        msg.what = 0;
                        msg.setData(bundle);
                        hdl_return.sendMessage(msg);
                    }
                }).start();

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

    public void onDataReceive(double[] data) {
    }

    public void stop() {
        this.sndHelper.stop();
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
