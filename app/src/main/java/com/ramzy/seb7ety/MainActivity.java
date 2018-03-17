package com.ramzy.seb7ety;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mCountTV;
    private TextView mTotalTV;
    private Button mCounterBtn;
    private Button mResetBtn;
    private Button mResetAllBtn;
    private Button mFloatBtn;

    private int count;
    private int total;

    static int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 4444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.seb7a_small);
        }
*/
        count = 0;
        total = 0;

        mCountTV = (TextView) findViewById(R.id.countTV);
        mTotalTV = (TextView) findViewById(R.id.totalTV);
        mCounterBtn = (Button) findViewById(R.id.counterBtn);
        mCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count ++;
                total ++;
                mCountTV.setText("Count\n\n"+count);
                mTotalTV.setText("Total\n\n"+total);
            }
        });

        mResetBtn = (Button) findViewById(R.id.resetBtn);
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                mCountTV.setText("Count\n\n"+count);
            }
        });

        mResetAllBtn = (Button) findViewById(R.id.resetAllBtn);

        mResetAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                total = 0;
                mCountTV.setText("Count\n\n"+count);
                mTotalTV.setText("Total\n\n"+total);
            }
        });

        mFloatBtn = (Button) findViewById(R.id.floatViewBtn);
        mFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the application has draw over other apps permission or not?
                //This permission is by default available for API<23. But for API > 23
                //you have to ask for the permission in runtime.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {

                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    startFloatingService();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                startFloatingService();
            } else { //Permission is not available
                Toast.makeText(this, "Draw over other app permission not available.",Toast.LENGTH_SHORT).show();
                //finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startFloatingService() {
        //Check if the service is already running - not to start it again
        if(!isFloatingServiceRunning(FloatingCounterService.class)) {
            startService(new Intent(MainActivity.this, FloatingCounterService.class));
            //finish();
        }
    }

    /** Check if the service is already running */
    private boolean isFloatingServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.v("MainActivity"," Floating service is already running");
                return true;
            }
        }
        return false;
    }
}
