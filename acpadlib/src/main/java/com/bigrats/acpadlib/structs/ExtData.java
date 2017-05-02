package com.bigrats.acpadlib.structs;

/**
 * Created by jqjiang on 2017/5/1.
 */
public class ExtData {
    public double value;
    public int type;

    public ExtData() {
        this.value = 0;
        this.type = 0;
    }

    public void setType(String type) {
        switch (type) {
            case "init":
                this.type = 0;
                break;
            case "min":
                this.type = -1;
                break;
            case "max":
                this.type = 1;
                break;
        }
    }

    public void copy(ExtData extData) {
        this.value = extData.value;
        this.type = extData.type;
    }

    public boolean isInit() {
        return this.type == 0;
    }

    public boolean isMax() {
        return this.type == 1;
    }

    public boolean isMin() {
        return this.type == -1;
    }
}
