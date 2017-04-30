package com.bigrats.acpad;

import Jama.Matrix;
import com.bigrats.acpad.structs.CicData;
import com.bigrats.acpad.structs.CodData;

/**
 * Created by jqjiang on 2017/4/30.
 */
public class Utilities {

    /**** SNDCNVRT ****/

    private static double[] loadRes(String respath) {
        /*** This remains to be filled ***/
        return new double[1];
    }

    public static double[][] sndcnvrt(String respath) {
        int frameSize = Params.FRAME_SIZE;
        double[] pcmdata = loadRes(respath);

        double[][] data = new double[pcmdata.length/frameSize][frameSize];

        if (pcmdata.length % frameSize != 0) {
            throw new IllegalArgumentException("sndcnvrt: resource length error.");
        }
        for (int i = 0; i < (pcmdata.length/frameSize); i++) {
            System.arraycopy(pcmdata,frameSize*i,data[i],0,frameSize);
        }

        return data;
    }

    /**** CODETECT ****/

    private static double[][] getCodRef(double init_time, int len) {
        int fs = Params.FREQ_SAMP;
        int fc = Params.FREQ_CENTER;
        double[][] ref = new double[2][len];
        for (int i = 0; i < len; i++) {
            ref[0][i] = Math.cos(2 * Math.PI * fc * (init_time + (i + 1) / fs));
            ref[1][i] = - Math.sin(2 * Math.PI * fc * (init_time + (i + 1) / fs));
        }
        return ref;
    }

    public static CodData codetect(double[] sndres, double init_time) {
        int fs = Params.FREQ_SAMP;
        int fc = Params.FREQ_CENTER;

        CodData codData = new CodData();

        double[][] codref = getCodRef(init_time,sndres.length);
        Matrix res = new Matrix(sndres,1);
        Matrix ref_cos = new Matrix(codref[0],1);
        Matrix ref_sin = new Matrix(codref[1],1);

        codData.data_i = res.arrayTimes(ref_cos);
        codData.data_q = res.arrayTimes(ref_sin);
        codData.time = init_time + sndres.length / fs;

        return codData;
    }




}
