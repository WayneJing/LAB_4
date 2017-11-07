package ece.course.lab_4_1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chenjingwen on 2017/10/15.
 */

public class ReportActivity extends Activity {
    private ArrayAdapter<String> mReports;
    private boolean isBound;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        mReports = new ArrayAdapter<String>(this, R.layout.report_line);
        ListView lvReport = (ListView) findViewById(R.id.lvReport);
        lvReport.setAdapter(mReports);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveToFile();
            }
        });
    }

    public void onStart() {
        super.onStart();
        if (!isBound) {
            Intent intent = new Intent(this, BackgroundService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void onStop() {
        super.onStop();
        if (isBound)
            unbindService(mServiceConnection);
        finish();
    }

    private void saveToFile() {
        File target = new File(Environment.getExternalStorageDirectory(), "Test.txt");
        try {
            FileWriter fileWriter = new FileWriter(target);
            int length = mReports.getCount();
            for (int i = length - 1; i >= 0; i--)
                fileWriter.write(mReports.getItem(i) + "\n");
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(getBaseContext(), "Report Saved!!", Toast.LENGTH_LONG).show();
        }
        catch (IOException ioException) {
            Toast.makeText(getBaseContext(), "Failed To Save Report...", Toast.LENGTH_LONG).show();
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            BackgroundService.BackgroundBinder binder = (BackgroundService.BackgroundBinder) service;
            BackgroundService backgroundService = binder.getService();
            ArrayList<String> reports = backgroundService.getReport();
            for (String report : reports)
                mReports.add(report);
            isBound = true;
        }
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };
}
