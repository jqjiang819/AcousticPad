package com.bigrats.acpad;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigrats.acpad.utils.PermissionRequest;
import com.bigrats.acpadlib.AcPadHelper;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    Button btn_start = null;
    TextView tv_dist = null;
    AcPadHelper acPadHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
        initListeners();
    }

    private void initView() {
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.btn_start = (Button) findViewById(R.id.btn_start);
        this.tv_dist = (TextView) findViewById(R.id.tv_dist);

        btn_start.setContentDescription("START");
    }

    private void initListeners() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                if (!btn.getContentDescription().equals("STOP")) {
                    btn.setText("STOP");
                    btn.setTextColor(getColor(R.color.colorAccent));
                    btn.setContentDescription("STOP");
                    acPadHelper = new AcPadHelper("LEVD") {
                        @Override
                        public void onDataReceive(double[] data) {
                            DecimalFormat df = new DecimalFormat("#0.00");
                            tv_dist.setText(df.format(data[data.length-1]));
                            super.onDataReceive(data);
                        }
                    };
                    acPadHelper.run();
                } else {
                    btn.setText("START");
                    btn.setTextColor(getColor(R.color.colorPrimaryDark));
                    tv_dist.setText("0.00");
                    btn.setContentDescription("START");
                    acPadHelper.stop();
                }
            }
        });
    }

    private void requestPermissions(){
        PermissionRequest permissionRequest = new PermissionRequest(this);
        permissionRequest.addPermission(Manifest.permission.RECORD_AUDIO, 0);
        permissionRequest.request();
    }

}
