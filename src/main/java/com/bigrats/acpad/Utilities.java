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

    /**** CICDECIM ****/

    public static CicData cicdecim(CicData cicdata_in) {
        int frameSize = Params.FRAME_SIZE;
        int decf = Params.CIC_DECIM_FACT;
        int diffd = Params.CIC_DIFF_DELAY;

        double[] combbuf = new double[3];
        Matrix dbuf = cicdata_in.dbuf;
        Matrix ibuf = cicdata_in.ibuf;
        Matrix data_in = cicdata_in.data;
        Matrix data_out = new Matrix(1,frameSize/decf);

        CicData cicdata_out = new CicData();

        if (cicdata_in.dbuf == null) {
            dbuf = new Matrix(3,diffd);
        }
        if (cicdata_in.ibuf == null) {
            ibuf = new Matrix(3,1);
        }

        for (int i = 0; i < data_in.getColumnDimension(); i++) {
            // integrator 1
            ibuf.set(0,0,ibuf.get(0,0) + data_in.get(0, i));
            // integrator 2
            ibuf.set(1,0,ibuf.get(1,0) + ibuf.get(0, 0));
            // integrator 3
            ibuf.set(2,0,ibuf.get(2,0) + ibuf.get(1, 0));

            // decimation
            if (i % decf == 0) {
                // comb section 1
                combbuf[0] = ibuf.get(2,0) - dbuf.get(0, 0);
                dbuf.setMatrix(0,0,0,diffd-2, dbuf.getMatrix(0, 0, 1, diffd-1));
                dbuf.set(0, diffd-1, ibuf.get(2, 0));
                // comb section 2
                combbuf[1] = combbuf[0] - dbuf.get(1, 0);
                dbuf.setMatrix(1,1,0,diffd-2, dbuf.getMatrix(1, 1, 1, diffd-1));
                dbuf.set(1, diffd-1, combbuf[0]);
                // comb section 3
                combbuf[2] = combbuf[1] - dbuf.get(2, 0);
                dbuf.setMatrix(2,2,0,diffd-2, dbuf.getMatrix(2, 2, 1, diffd-1));
                dbuf.set(2, diffd-1, combbuf[1]);
                // get output
                data_out.set(0, i/decf, combbuf[2]);
            }
        }

        cicdata_out.data = data_out;
        cicdata_out.ibuf = ibuf;
        cicdata_out.dbuf = dbuf;
        return cicdata_out;
    }



}
