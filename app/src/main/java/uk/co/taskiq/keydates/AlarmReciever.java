package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import helper.SQLiteHandler;

/**
 * Created by Administrator on 26/12/2015.
 */
public class AlarmReciever extends BroadcastReceiver {

    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private static final String TAG = AlarmReciever.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        db = new SQLiteHandler(context);

        // Progress dialog
     //   pDialog = new ProgressDialog(context);
       // pDialog.setCancelable(false);

        // Fetching user details from sqlite

        HashMap<String, String> user = db.getUserDetails();

        String firstname = user.get("firstname");
        String email = user.get("email");
        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        Log.d(TAG, "Populate Products Response called");


        Toast.makeText(context, "Scheduled refresh of all Reminders", Toast.LENGTH_SHORT).show();



        Intent background = new Intent(context, PopulateService.class);
        context.startService(background);




    }



}
