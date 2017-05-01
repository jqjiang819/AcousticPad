package com.bigrats.acpadlib.structs;

import Jama.Matrix;

/**
 * Created by jqjiang on 2017/4/30.
 */
public class LevdData {
    public Matrix data = null;
    public double s_init = 0;
    public ExtData[] ext = null;

    public LevdData() {}

    public LevdData(Matrix data) {
        this.data = data;
    }

    public LevdData(Matrix data, double s_init, ExtData[] ext) {
        this.data = data;
        this.s_init = s_init;
        this.ext = ext;
    }

    public LevdData setData(Matrix data) {
        this.data = data;
        return this;
    }
}
