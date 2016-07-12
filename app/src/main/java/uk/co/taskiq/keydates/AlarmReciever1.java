package uk.co.taskiq.keydates;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import helper.SQLiteHandler;

/**
 * Created by Administrator on 08/02/2016.
 */
public class AlarmReciever1 extends BroadcastReceiver {


    private AlarmManager manager;

    private int MID = 0;
    private SQLiteHandler db;
    private String reminderDateString = null;
    private long reminderDateLong1 = 0;

    private static final String TAG = AlarmReciever1.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        db = new SQLiteHandler(context);



        Cursor cursor = db.fetchTodayReminders(getDateTime(),"0");
        int x = cursor.getCount(); //this will return number of records in current cursor
        Log.d(TAG, "todays date as string: " + x);
        if ( x != 0 ) {


                Log.d(TAG, "todays date as string: " + getDateTime());

            // here you can start an activity or service depending on your need
            // for ex you can start an activity to vibrate phone or to ring the phone


            Toast.makeText(context, "Notification Reminders", Toast.LENGTH_SHORT).show();
            // TODO Auto-generated method stub

            long when = System.currentTimeMillis();
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // notificationIntent.setAction("android.intent.action.MAIN");
            // notificationIntent.addCategory("android.intent.category.LAUNCHER");

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Task iQ")
                    .setContentText("Reminders which require your attention")
                    .setAutoCancel(true).setWhen(when)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(MID, mNotifyBuilder.build());
            MID++;



        }
        cursor.close();


    }


    private String getDateTime() {

        GregorianCalendar ob = new GregorianCalendar();
        ob.getTimeInMillis();

        // access the fields
        final    int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(ob.getTime());
        final  int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        final  int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date = day+"-"+month_name+"-"+year;

        String remViewDate = date.toString();
        SimpleDateFormat sdf  = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            java.util.Date date1 = (java.util.Date) sdf.parse(remViewDate);
            reminderDateLong1 = (date1.getTime())/1000;

        } catch (ParseException e) {
            Log.e("log", e.getMessage(), e);
        }
        reminderDateString = Long.toString(reminderDateLong1);

        return reminderDateString;
    }


}
