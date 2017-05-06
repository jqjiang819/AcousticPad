package com.bigrats.acpad.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiaqiu on 2016/12/10.
 */

public class PermissionRequest {
    private List<String> permissions, permissionsRequestList;
    private List<Integer> request_codes, permissionsRequestCodeList;
    private Activity activity;

    private static final int REQUEST_CODE = 1;

    public PermissionRequest(Activity activity){
        this.permissions = new ArrayList<String>();
        this.request_codes = new ArrayList<Integer>();
        this.permissionsRequestList = new ArrayList<String>();
        this.permissionsRequestCodeList = new ArrayList<Integer>();
        this.activity = activity;
    }

    public PermissionRequest addPermission(String permission, int req_code){
        this.permissions.add(permission);
        this.request_codes.add(req_code);
        return this;
    }

    public void request(){
        this.checkPermissions();
        if(this.permissionsRequestList.size()==0){
            return;
        }
        ActivityCompat.requestPermissions(this.activity,
                this.permissionsRequestList.toArray(new String[this.permissionsRequestList.size()]),
                this.REQUEST_CODE);
    }

    private void checkPermissions(){
        for (int i = 0; i < this.permissions.size(); i++) {
            String permission = this.permissions.get(i);
            int request_code = this.request_codes.get(i);
            if(ContextCompat.checkSelfPermission(this.activity,permission)!= PackageManager.PERMISSION_GRANTED){
                this.permissionsRequestList.add(permission);
                this.permissionsRequestCodeList.add(request_code);
            }
        }
    }

}