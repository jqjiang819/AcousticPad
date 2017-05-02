package com.bigrats.acpad;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigrats.acpadlib.AcPadHelper;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button btn_start = null;
    TextView tv_dist = null;
    Handler hdl_update = null;
    Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListeners();
    }

    private void initView() {
        this.btn_start = (Button) findViewById(R.id.btn_start);
        this.tv_dist = (TextView) findViewById(R.id.tv_dist);

        btn_start.setContentDescription("START");
    }

    private void initListeners() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                double value = 0;
                if (!btn.getContentDescription().equals("STOP")) {
                    btn.setText("STOP");
                    btn.setTextColor(getColor(R.color.colorAccent));
                    tv_dist.setText("1.00");
                    btn.setContentDescription("STOP");
                    new AcPadHelper("LEVD").run();
//                    hdl_update = new Handler() {
//                        @Override
//                        public void handleMessage(Message msg) {
//                            switch (msg.what) {
//                                case 0:
//                                    Bundle bundle = msg.getData();
//                                    double dist = bundle.getDouble("dist", 0);
//                                    DecimalFormat df = new DecimalFormat("#0.00");
//                                    tv_dist.setText(df.format(dist));
//                                    break;
//                            }
//                            super.handleMessage(msg);
//                        }
//                    };
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            timer = new Timer();
//                            TimerTask task = new TimerTask() {
//                                double value = 0;
//                                @Override
//                                public void run() {
//                                    Message msg = hdl_update.obtainMessage();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putDouble("dist",(value = getDistValue(value)));
//                                    msg.what = 0;
//                                    msg.setData(bundle);
//                                    hdl_update.sendMessage(msg);
//                                }
//                            };
//                            timer.schedule(task,0,100);
//                        }
//                    }).start();

                } else {
                    btn.setText("START");
                    btn.setTextColor(getColor(R.color.colorPrimaryDark));
                    tv_dist.setText("0.00");
                    btn.setContentDescription("START");
//                    timer.cancel();
                }
            }
        });
    }

    private double getDistValue(double init_value) {
        double value = init_value;
        for (int i = 0; i < 20; i++) {
            value += i * 0.01;
        }
        return value;
    }
}
