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



}
