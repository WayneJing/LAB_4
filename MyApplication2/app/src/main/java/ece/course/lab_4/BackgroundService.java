package ece.course.lab_4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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
    private Context mContext;
    private IBinder mBinder;
    private boolean isStarted=false;
    private int mMsgCount=0;
    private ArrayList<String> mReports;
    private static final long DELAY_MS=10000;
    private static final String TAG_REPORT= "tagReport";
    private static final String TICKER_TEXT = "Crazy Timer";
    private static final String NOTIFICATION_TITLE = "Timer Tick";

    private Handler mTimer = new Handler () {
        public void handleMessage(Message msg) {
            if (isStarted) {
                long when = System.currentTimeMillis();
                Time time = new Time(when);
                Notification notification = new Notification(R.drawable.icon, TICKER_TEXT, when);
                String contentText = "Timer Tick @" + time.toString();
                Intent notificationIntent = new Intent(BackgroundService.this, BackgroundService.class);
                PendingIntent contentIntent = PendingIntent.getActivity(BackgroundService.this, 0, notificationIntent, 0);
//notification.setLatestEventInfo(mContext, NOTIFICATION_TITLE, contentText, contentIntent);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(BackgroundService.this);
                notification = builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker(contentText).setWhen(when)
                        .setAutoCancel(true).setContentTitle(NOTIFICATION_TITLE)
                        .setContentText(contentText).build();
                mNotificationManager.notify(mMsgCount++, notification);
                mReports.add(contentText);
                this.sleep(DELAY_MS);
            }
        }
        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    public void onCreate() {
        super.onCreate();
        mReports = new ArrayList<String>();
        mBinder = new BackgroundBinder();
        isStarted = false;
        mMsgCount = 0;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mContext = getApplicationContext();
    }

    public void onDestroy() {
        super.onDestroy();
        stopRun();
    }

    public void startRun() {
        if (isStarted)
            return;
        isStarted = true;
        mReports.clear();
        long when = System.currentTimeMillis();
        Time time = new Time(when);
        Notification notification = new Notification(R.drawable.icon, "Service Say Hi!!", when);
        String contentText = "Start Background Service @" + time.toString();
        Intent notificationIntent = new Intent(this, BackgroundService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//notification.setLatestEventInfo(mContext, "Background Service", contentText, contentIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        notification = builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(contentText).setWhen(when)
                                                .setAutoCancel(true).setContentTitle("Background Service")
                                                .setContentText(contentText).build();
        mNotificationManager.notify(mMsgCount++, notification);
        mReports.add(contentText);
        mTimer.sendMessageDelayed(mTimer.obtainMessage(0), DELAY_MS);

    }

    public void stopRun() {
        if (!isStarted)
            return;
        isStarted = false;
        long when = System.currentTimeMillis();
        Time time = new Time(when);
        Notification notification = new Notification(R.drawable.icon, "Service Say Bye!!", when);
        String contentText = "Stop Background Service @" + time.toString();
        Intent notificationIntent = new Intent(this, BackgroundService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//notification.setLatestEventInfo(mContext, "Background Service", contentText, contentIntent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        notification = builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(contentText).setWhen(when)
                .setAutoCancel(true).setContentTitle("Background Service")
                .setContentText(contentText).build();
        mNotificationManager.notify(mMsgCount++, notification);
        mReports.add(contentText);

    }

    public boolean getStarted() {
        return isStarted;
    }

    public ArrayList<String> getReport() {
        return mReports;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  mBinder;
    }



    public class BackgroundBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }
}
