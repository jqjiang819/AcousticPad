package com.bigrats.acpadlib.structs;

import Jama.Matrix;

/**
 * Created by jqjiang on 2017/4/30.
 */
public class CicData {
    public Matrix data = null;
    public Matrix dbuf = null;
    public Matrix ibuf = null;

    public CicData() {}

    public CicData(Matrix data) {
        this.data = data;
    }

    public CicData(Matrix data, Matrix dbuf, Matrix ibuf) {
        this.data = data;
        this.dbuf = dbuf;
        this.ibuf = ibuf;
    }

    public CicData setData(Matrix data) {
        this.data = data;
        return this;
    }
}
