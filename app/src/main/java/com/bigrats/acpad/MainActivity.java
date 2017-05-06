package com.bigrats.acpad;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bigrats.acpad.utils.PermissionRequest;
import com.bigrats.acpadlib.AcPadHelper;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    ImageButton btn_start = null;
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

        this.btn_start = (ImageButton) findViewById(R.id.btn_start);
        this.tv_dist = (TextView) findViewById(R.id.tv_dist);

        btn_start.setContentDescription("START");
    }

    private void initListeners() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn = (ImageButton) v;
                if (btn.getContentDescription().equals("START")) {
                    acPadHelper = new AcPadHelper("LEVD") {
                        @Override
                        public void onDataReceive(double[] data) {
                            DecimalFormat df = new DecimalFormat("#0.00");
                            tv_dist.setText(df.format(data[data.length-1]));
                            super.onDataReceive(data);
                        }
                    };
                    acPadHelper.run();
                    btn.setImageResource(R.drawable.ic_stop_white_96dp);
                    btn.setBackgroundResource(R.drawable.oval_btn_stop);
                    btn.setContentDescription("STOP");
                } else {
                    acPadHelper.stop();
                    tv_dist.setText("0.00");
                    btn.setImageResource(R.drawable.ic_mic_white_96dp);
                    btn.setBackgroundResource(R.drawable.oval_btn_start);
                    btn.setContentDescription("START");
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
