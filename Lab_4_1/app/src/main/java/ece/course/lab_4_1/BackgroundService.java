package ece.course.lab_4_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by chenjingwen on 2017/10/15.
 */

public class BackgroundService extends Service {

    private NotificationManager mNotificationManager;
    private LocationManager mLocationManager;
    private Context mContext;
    private IBinder mBinder;
    private boolean isStarted=false;
    private int mMsgCount=0;
    private ArrayList<String> mReports;
    private ArrayList<String> mLocations;
    private static final long DELAY_MS=10000;
    private static final String TAG_REPORT= "tagReport";
    private static final String TICKER_TEXT = "Lab 4";
    private static final String NOTIFICATION_TITLE = "Location Updated";

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            sendMsg(location);
        }
        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };
    public void onCreate() {
        super.onCreate();
        mBinder = new BackgroundBinder();
        isStarted = false;
        mMsgCount = 0;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mContext = getApplicationContext();
        mLocations = new ArrayList<String>();
    }
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public void onDestroy() {
        super.onDestroy();
        stopRun();
    }
    public void startRun() {
        if (isStarted)
            return;
        isStarted = true;
        mLocations.clear();
        sendMsg(mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }
    }
    public void stopRun() {
        if (!isStarted)
            return;
        isStarted = false;
        mLocationManager.removeUpdates(mLocationListener);
    }
    public boolean getStarted() {
        return isStarted;
    }
    public ArrayList<String> getReport() {
        return mLocations;
    }
    private void sendMsg(Location location) {
        if (isStarted && (location != null)) {
            long when = System.currentTimeMillis();
            Time time = new Time(when);
            Notification notification = new Notification(R.drawable.icon, TICKER_TEXT, when);
            String contentText = "Latitude: " + location.getLatitude() + ",\n Longitude: " + location.getLongitude();
            Intent notificationIntent = new Intent(this, BackgroundService.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            notification = builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.icon).setTicker(contentText).setWhen(when)
                    .setAutoCancel(true).setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(contentText).build();
            mNotificationManager.notify(mMsgCount++, notification);
            mLocations.add(contentText + "\n@" + time.toString());
        }
    }
    public class BackgroundBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

}
