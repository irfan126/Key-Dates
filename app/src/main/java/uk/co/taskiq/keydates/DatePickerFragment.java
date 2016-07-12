package uk.co.taskiq.keydates;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 14/09/2015.
 */

public class DatePickerFragment extends DialogFragment {

    // Log tag
    private static final String TAG = DatePickerFragment.class.getSimpleName();
    //   OnDateSetListener ondateSet;
    private boolean isCancelled = false; //Added to handle cancel

    // TheListener listener;

    //public interface TheListener{

    //   void returnDate(String date);

    //}



    //   public DatePickerFragment() {  }

    //   public void setCallBack(OnDateSetListener ondate) {
    //   ondateSet = ondate;
    //}

    private int year, month, day;
    private String remString = null;;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");

        remString = args.getString("remString");
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();
        if (isBrokenSamsungDevice()) {
            context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
        }

        final DatePickerDialog dateDialog;


        //  DatePickerDialog picker =
        Log.d(TAG, "string: " + day);
        //   listener = (TheListener) getActivity();
     final DatePickerDialog picker = new DatePickerDialog(context, myDateListener, year, month, day);


        picker.setCancelable(true);
        picker.setCanceledOnTouchOutside(true);
        picker.getDatePicker().setCalendarViewShown(false);
        picker.setButton(DialogInterface.BUTTON_POSITIVE,
                "Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCancelled = false; //Cancel flag, used in mTimeSetListener
                        DatePicker datePicker = picker.getDatePicker();
                        myDateListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());


                    }
                });
        picker.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCancelled = true; //Cancel flag, used in mTimeSetListener
                    }
                });
        // else{        return picker;}
        return picker;


    }


    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP));
    }

    private static boolean isBetweenAndroidVersions(int min, int max) {
        return 21 <= max;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = sdf.format(c.getTime());
        Log.d(TAG, "string1: " + formattedDate);
        if(!isCancelled) {

            if(remString.equals("reminderExpiry")){
                ((TextView) getActivity().findViewById(R.id.reminderExpiry)).setText(formattedDate);}

            else {

                ( (TextView) getActivity().findViewById(R.id.reminderView)).setText(formattedDate);}
        }
        //  if (listener != null)
        //{
        //  Log.d(TAG, "string2: " + formattedDate);
        //    listener.returnDate(formattedDate);
        //  Log.d(TAG, "string3: " + formattedDate);
        //}

    } };





}


