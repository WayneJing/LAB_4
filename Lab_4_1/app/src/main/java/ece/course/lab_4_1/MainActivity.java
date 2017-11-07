package ece.course.lab_4_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    private BackgroundService mBackgroundService;
    private TextView tvState;
    private boolean isBound=false;
    private boolean isStarted=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isBound = false;
        isStarted = false;
        setContentView(R.layout.activity_main);
        tvState = (TextView) findViewById(R.id.tvState);
        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isBound && (!isStarted)) {
                    mBackgroundService.startRun();
                    tvState.setText(R.string.state_running);
                    isStarted = true;
                }
            }
        });
        Button btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isBound && isStarted) {
                    mBackgroundService.stopRun();
                    tvState.setText(R.string.state_stopped);
                    isStarted = false;
                }
            }
        });
        Button btnReport = (Button) findViewById(R.id.btnReport);
        btnReport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        if (!isBound) {
            Intent intent = new Intent(MainActivity.this, BackgroundService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        if (isBound) {
            mBackgroundService.stopRun();
            tvState.setText(R.string.state_stopped);
            unbindService(mServiceConnection);
            mBackgroundService = null;
            isStarted = false;
            isBound = false;
        }
    }
    public void onBackPressed() {
        if (isStarted) { }
        else
            finish();
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            BackgroundService.BackgroundBinder binder = (BackgroundService.BackgroundBinder) service;
            mBackgroundService = binder.getService();
            isBound = true;
            isStarted = mBackgroundService.getStarted();
            if (isStarted)
                tvState.setText(R.string.state_running);
            else
                tvState.setText(R.string.state_stopped);
        }
        public void onServiceDisconnected(ComponentName componentName) {
            mBackgroundService.stopRun();
            tvState.setText(R.string.state_stopped);
            isStarted = false;
            isBound = false;
        }
    };
}
