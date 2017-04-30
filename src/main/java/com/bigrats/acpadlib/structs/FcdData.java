package com.bigrats.acpadlib.structs;

import Jama.Matrix;

/**
 * Created by jqjiang on 2017/4/30.
 */
public class FcdData {
    public Matrix data_i = null;
    public Matrix data_q = null;

    public FcdData() {}

    public FcdData(Matrix data_i, Matrix data_q) {
        this.data_i = data_i;
        this.data_q = data_q;
    }
}
