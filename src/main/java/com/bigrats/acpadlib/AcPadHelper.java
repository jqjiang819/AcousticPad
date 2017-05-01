package com.bigrats.acpadlib;

import Jama.Matrix;
import com.bigrats.acpadlib.structs.*;

/**
 * Created by jqjiang on 2017/5/1.
 */
public class AcPadHelper {
    // pre-init parameters
    private String respath;
    private String algorithm;

    // init datas
    private double[][] snd_data;
    private CodData codData;
    private CicData cicData_i, cicData_q;
    private LevdData levdData_i, levdData_q;
    private FcdData fcdData;
    private double[][] vec_i, vec_q;


    public AcPadHelper() {}

    public AcPadHelper(String respath, String algorithm) {
        this.setResPath(respath).setAlgorithm(algorithm);
    }

    private void preInit() {
        this.chkSettings();
    }

    private void init() {
        this.preInit();
        this.snd_data = Utilities.sndcnvrt(this.respath);
        this.codData = new CodData();
        this.cicData_i = new CicData();
        this.cicData_q = new CicData();
        this.levdData_i = new LevdData();
        this.levdData_q = new LevdData();
        this.fcdData = new FcdData();
        this.vec_i = new double[this.snd_data.length][Params.FRAME_SIZE/Params.CIC_DECIM_FACT];
        this.vec_q = new double[this.snd_data.length][Params.FRAME_SIZE/Params.CIC_DECIM_FACT];
    }

    public void run() {
        this.init();
        for (int i = 0; i < this.snd_data.length; i++) {
            // coherent detection
            this.codData = Utilities.codetect(this.snd_data[i], this.codData.time);
            // cic filtering
            this.cicData_i = Utilities.cicdecim(this.cicData_i.setData(this.codData.data_i));
            this.cicData_q = Utilities.cicdecim(this.cicData_q.setData(this.codData.data_q));
            // levd detection
            if (this.algorithm.equals("LEVD")) {
                this.levdData_i = Utilities.levddetect(levdData_i.setData(this.cicData_i.data));
                this.levdData_q = Utilities.levddetect(levdData_q.setData(this.cicData_q.data));
                this.vec_i[i] = this.cicData_i.data.minus(this.levdData_i.data).getArrayCopy()[0];
                this.vec_q[i] = this.cicData_q.data.minus(this.levdData_q.data).getArrayCopy()[0];
            }
            // fcd algorithm
            if (this.algorithm.equals("FCD")) {
                this.fcdData = Utilities.fcddetect(new FcdData(this.cicData_i.data, this.cicData_q.data));
                this.vec_i[i] = this.cicData_i.data.minus(this.fcdData.data_i).getArrayCopy()[0];
                this.vec_q[i] = this.cicData_q.data.minus(this.fcdData.data_q).getArrayCopy()[0];
            }
        }
    }

    public double[][] getData() {
        double[] data_i = new double[vec_i.length * vec_i[0].length];
        double[] data_q = new double[vec_i.length * vec_i[0].length];
        for (int i = 0; i < vec_i.length; i++) {
            System.arraycopy(vec_i[i],0, data_i, vec_i[i].length * i, vec_i[i].length);
            System.arraycopy(vec_q[i],0, data_q, vec_q[i].length * i, vec_q[i].length);
        }
        return new double[][]{data_i, data_q};
    }

    public AcPadHelper setResPath(String respath) {
        this.respath = respath;
        return this;
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
        if (this.respath == null) {
            throw new IllegalArgumentException("PreInit: respath can't be null.");
        }
        if (this.algorithm == null) {
            throw new IllegalArgumentException("PreInit: algorithm can't be null.");
        }
    }
}
