package com.bigrats.acpadlib;

import Jama.Matrix;
import com.bigrats.acpadlib.structs.*;

/**
 * Created by jqjiang on 2017/4/30.
 */
public class Utilities {

    /**** SNDCNVRT ****/

    /**** CODETECT ****/

    private static double[][] getCodRef(double init_time, int len) {
        int fs = Params.FREQ_SAMP;
        int fc = Params.FREQ_CENTER;
        double[][] ref = new double[2][len];
        double time;
        for (int i = 0; i < len; i++) {
            time = 2 * Math.PI * fc * (init_time + (double) (i + 1) / fs);
            ref[0][i] = Math.cos(time);
            ref[1][i] = - Math.sin(time);
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

        return new CodData(res.arrayTimes(ref_cos), res.arrayTimes(ref_sin), init_time + sndres.length / fs);
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

        return new CicData(data_out, dbuf, ibuf);
    }

    /**** LEVDDETECT ****/
    private static double sum(double[] data, int i1, int i2) {
        double sum_data = 0;
        for (int i = i1; i < i2 + 1; i++) {
            sum_data += data[i];
        }
        return sum_data;
    }

    private static double mean(double[] data, int i1, int i2) {
        return sum(data, i1, i2) / (i2 - i1 + 1);
    }

    private static int isminmax(double[] data, int i) {
        int num = Params.MEAN_RADIUS;
        int len = data.length;
        double val_left = 0, val_right = 0;
        // get left value
        if (i < num && i > 0) {
            val_left = mean(data, 0, i - 1);
        }else if (i != 0) {
            val_left = mean(data, i - num, i - 1);
        }
        // get right value
        if (i > len - num - 1 && i < len - 1) {
            val_right = mean(data, i + 1, len - 1);
        }else if (i != len - 1) {
            val_right = mean(data, i + 1, i + num);
        }
        // check center value
        if (i == 0 || i == len - 1) {
            return 0;
        }
        if (data[i] > val_left && data[i] > val_right) {
            return 1;
        }
        if (data[i] < val_left && data[i] < val_right) {
            return -1;
        }
        return 0;
    }

    private static double getextmean(ExtData[] ext) {
        if (ext[0].isInit() || ext[1].isInit()) {
            return 0;
        }
        return (ext[0].value + ext[1].value) / 2;
    }

    private static double getstaticvec(double[] data, double[] s, int i, ExtData[] ext) {
        double extmean = getextmean(ext);
        if (extmean != 0) {
            return 0.9 * s[i] + 0.1 * extmean;
        }
        return 0.9 * data[i];
    }

    public static LevdData levddetect(LevdData levddata_in) {

        int data_len = levddata_in.data.getColumnDimension();
        double[] s = new double[data_len+1];
        ExtData[] ext = levddata_in.ext;
        Matrix data_in = levddata_in.data;
        Matrix data_out;

        LevdData levddata_out = new LevdData();

        s[0] = levddata_in.s_init;
        double[] data_in_arr = data_in.getArrayCopy()[0];

        if (ext == null) {
            ext = new ExtData[2];
            ext[0] = new ExtData();
            ext[1] = new ExtData();
        }

        for (int i = 0; i < data_len; i++) {
            // get local extremes
            switch (isminmax(data_in_arr, i)) {
                case -1:
                    if (ext[1].isInit()) {
                        ext[1].setType("min");
                        ext[1].value = data_in_arr[i];
                        break;
                    }
                    if (ext[1].isMax() && Math.abs(data_in_arr[i] - ext[1].value) > 2e5) {
                        ext[0].copy(ext[1]);
                        ext[1].setType("min");
                        ext[1].value = data_in_arr[i];
                        break;
                    }
                    if (ext[1].isMin() && data_in_arr[i] < ext[1].value) {
                        ext[1].value = data_in_arr[i];
                        break;
                    }
                case 1:
                    if (ext[1].isInit()) {
                        ext[1].setType("max");
                        ext[1].value = data_in_arr[i];
                        break;
                    }
                    if (ext[1].isMin() && Math.abs(data_in_arr[i] - ext[1].value) > 2e5) {
                        ext[0].copy(ext[1]);
                        ext[1].setType("max");
                        ext[1].value = data_in_arr[i];
                        break;
                    }
                    if (ext[1].isMax() && data_in_arr[i] > ext[1].value) {
                        ext[1].value = data_in_arr[i];
                        break;
                    }
            }
            // calculate static vector
            s[i+1] = getstaticvec(data_in_arr, s, i, ext);
        }

        data_out = new Matrix(s,1).getMatrix(0,0,1,data_len);
        return new LevdData(data_out, s[s.length-1], ext);
    }

    /**** FCDDETECT ****/

    private static double sum(Matrix data) {
        double[] data_arr = data.getArrayCopy()[0];
        double sum_data = 0;
        for (int i = 0; i < data_arr.length; i++) {
            sum_data += data_arr[i];
        }
        return sum_data;
    }

    public static FcdData fcddetect(FcdData fcddata_in) {
        int frameSize = Params.FRAME_SIZE;
        int decf = Params.CIC_DECIM_FACT;
        int N = frameSize / decf;

        double s_x = sum(fcddata_in.data_i);
        double s_y = sum(fcddata_in.data_q);
        double s_xx = sum(fcddata_in.data_i.arrayTimes(fcddata_in.data_i));
        double s_xy = sum(fcddata_in.data_i.arrayTimes(fcddata_in.data_q));
        double s_yy = sum(fcddata_in.data_q.arrayTimes(fcddata_in.data_q));
        double s_xxx = sum(fcddata_in.data_i.arrayTimes(fcddata_in.data_i).arrayTimes(fcddata_in.data_i));
        double s_xxy = sum(fcddata_in.data_i.arrayTimes(fcddata_in.data_i).arrayTimes(fcddata_in.data_q));
        double s_xyy = sum(fcddata_in.data_i.arrayTimes(fcddata_in.data_q).arrayTimes(fcddata_in.data_q));
        double s_yyy = sum(fcddata_in.data_q.arrayTimes(fcddata_in.data_q).arrayTimes(fcddata_in.data_q));

        double p1 = N * s_xx - Math.pow(s_x, 2);
        double p2 = N * s_xy - s_x * s_y;
        double p3 = N * (s_xxx + s_xyy) - s_x * (s_xx + s_yy);
        double p4 = N * s_xy - s_x * s_y;
        double p5 = N * s_yy - Math.pow(s_y, 2);
        double p6 = N * (s_xxy + s_yyy) - s_y * (s_xx + s_yy);

        double a = (p2 * p6 - p3 * p5) / (p1 * p5 - p2 * p4);
        double b = (p3 * p4 - p1 * p6) / (p1 * p5 - p2 * p4);

        Matrix data_out_i = new Matrix(1, N, a / (-2));
        Matrix data_out_q = new Matrix(1, N, b / (-2));

        return new FcdData(data_out_i, data_out_q);
    }


}
