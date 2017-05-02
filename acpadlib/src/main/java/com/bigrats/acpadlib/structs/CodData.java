package com.bigrats.acpadlib.structs;

import Jama.Matrix;

/**
 * Created by jqjiang on 2017/4/30.
 */
public class CodData {
    public Matrix data_i = null;
    public Matrix data_q = null;
    public double time = 0;

    public CodData() {}

    public CodData(Matrix data_i, Matrix data_q, double time) {
        this.data_i = data_i;
        this.data_q = data_q;
        this.time = time;
    }
}
