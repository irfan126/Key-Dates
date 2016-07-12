package uk.co.taskiq.keydates;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import helper.SessionManager;

/**
 * Created by Administrator on 13/02/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private AlarmManager alarmMgr1;
    private PendingIntent alarmIntent1;


    private SessionManager session;

    @Override
    public void onReceive(Context context, Intent intent) {

        session = new SessionManager(context);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            if (session.isLoggedIn()) {

                long _alarm = 0;
                Calendar now = Calendar.getInstance();
            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED");

            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(context, AlarmReciever.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE,50 );

                if(calendar.getTimeInMillis() < now.getTimeInMillis())
                { _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);}
                else
                {  _alarm = calendar.getTimeInMillis();}
// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, _alarm,
                    AlarmManager.INTERVAL_DAY, alarmIntent);


            alarmMgr1 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(context, AlarmReciever1.class);
            alarmIntent1 = PendingIntent.getBroadcast(context, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                long _alarm1 = 0;
            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.set(Calendar.HOUR_OF_DAY, 16);
            calendar1.set(Calendar.MINUTE, 02);

                if(calendar1.getTimeInMillis() < now.getTimeInMillis())
                { _alarm1 = calendar1.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);}
                else
                {  _alarm1 = calendar1.getTimeInMillis();}


// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr1.setRepeating(AlarmManager.RTC_WAKEUP, _alarm1,
                    AlarmManager.INTERVAL_DAY, alarmIntent1);
            }
        }
    }


}
