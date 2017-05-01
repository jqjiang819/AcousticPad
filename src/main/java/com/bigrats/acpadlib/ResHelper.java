package com.bigrats.acpadlib;

import java.io.InputStream;

/**
 * Created by jqjiang on 2017/5/1.
 */
class ResHelper {
    InputStream getRes(String name) throws Exception {
        return getClass().getResourceAsStream(name);
        //return new FileInputStream(new File(name));
    }
    int getLen(String name) throws Exception {
        return getClass().getResourceAsStream(name).available();
        //return (int)new File(name).length();
    }
}
