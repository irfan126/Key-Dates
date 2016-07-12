package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;

import helper.SQLiteHandler;

/**
 * Created by Administrator on 04/11/2015.
 */
public class ActDateFragment extends Fragment {
    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = ActDateFragment.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String catTitle = null;
    private String newCat = null;
    private String catUploadID = null;
    private String actDateTitle = null;
    private ProgressDialog pDialog;

    private TextView ActivationDateExplained;

    private EditText categoryTitle;
    private EditText categoryDesc;

    private TextView activationDateTitleHeader;
    private EditText ActDateTitle;

    private TextView ActivationDateText;
    private TextView reminderView;

    private String reminderDateString = null;
    private long reminderDateLong1 = 0;
    private String reminderExpiryString = null;
    private long reminderExpiryLong1 = 0;
    private Button btnNext;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        newCat = bundle.getString("NewCat");
        catUploadID = bundle.getString("catUploadID");;

        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.actdate_fragment, container, false);

        ActivationDateExplained = (TextView) view.findViewById(R.id.ActivationDateExplained);

        SpannableString content = new SpannableString(ActivationDateExplained.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        ActivationDateExplained.setText(content);
        ActivationDateExplained.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Activation date");
                alertDialog.setMessage("Activation date will allow you to set Reminders x number of days from a given date. It is mainly used for Categories you intend to Upload.\n\nFor example if you create a Category to Upload which sets Reminders X days after a date another Key Dates user sets then you will need to use the Activation date and title.\n\nNote if you create a Category using the Activation date the Reminders will not show in your Reminders Tab in the homepage. To view the Reminders select the Category from the Category Tab.\n");

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });

        categoryTitle = (EditText) view.findViewById(R.id.CategoryTitle);

        categoryDesc = (EditText) view.findViewById(R.id.CategoryDesc);

        activationDateTitleHeader = (TextView) view.findViewById(R.id.activationDateTitleHeader);
        SpannableString content1 = new SpannableString(activationDateTitleHeader.getText().toString());
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        activationDateTitleHeader.setText(content1);
        activationDateTitleHeader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
                alertDialog1.setTitle("Title for Activation date");
                alertDialog1.setMessage("Set a title for the Activation date. If you upload the Category it will help a other users to set a their Activation date when downloading.\n");

                alertDialog1.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.show();
            }

        });

        ActDateTitle = (EditText) view.findViewById(R.id.ActivationDateTitle);

        ActivationDateText = (TextView) view.findViewById(R.id.ActivationDateText);
        SpannableString content2 = new SpannableString(ActivationDateText.getText().toString());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        ActivationDateText.setText(content2);
        ActivationDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity()).create();
                alertDialog2.setTitle("Activation date");
                alertDialog2.setMessage("Activation date will allow you to set Reminders x number of days from a given date. It is mainly used for Categories you intend to Upload.\n\nFor example if you create a Category to Upload which sets Reminders X days after a date another Task iQ user sets then you will need to use the Activation date and title.\n\nNote if you create a Category using the Activation date the Reminders will not show in your Reminders Tab in the homepage. To view the Reminders select the Category from the Category Tab.\n");

                alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog2.show();
            }

        });




        reminderView = (TextView) view.findViewById(R.id.reminderView);
        reminderView.setText(getDateTime());
        reminderView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker("reminderView");
            }

        });

        btnNext = (Button) view.findViewById(R.id.BtnNext);

        // Login button Click Event
        btnNext.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                catTitle = categoryTitle.getText().toString();

                actDateTitle = ActDateTitle.getText().toString();

                String remViewDate = reminderView.getText().toString();
                SimpleDateFormat sdf  = new SimpleDateFormat("dd-MMM-yyyy");


                try {
                    java.util.Date date = (java.util.Date) sdf.parse(remViewDate);
                    reminderDateLong1 = (date.getTime())/1000;

                } catch (ParseException e) {
                    Log.e("log", e.getMessage(), e);
                }
                reminderDateString = Long.toString(reminderDateLong1);

                Intent intent = new Intent(getActivity(), CreateRemYesActivity.class);
                intent.putExtra("NewCat", newCat);
                intent.putExtra("categoryTitle", catTitle);
                intent.putExtra("activationDateTitle", actDateTitle);
                intent.putExtra("actDays", reminderDateString);
                intent.putExtra("catUploadID", catUploadID);



                startActivity(intent);

            }

        });

        return view;

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



        return date;
    }

    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";
    static final String CALENDAR = "calendar";

    private void showDatePicker(String remString) {
        String reminderDate = null;
         reminderDate = reminderView.getText().toString();


        SimpleDateFormat sdf  = new SimpleDateFormat("dd-MMM-yyyy");
        long reminderDateLong = 0;
        try {
            java.util.Date date = (java.util.Date) sdf.parse(reminderDate);
            reminderDateLong = date.getTime();
        } catch (ParseException e) {
            Log.e("log", e.getMessage(), e);
        }

        Log.d(TAG, "ReminderDate Long: " + reminderDateLong);

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(reminderDateLong);

        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);

        DatePickerFragment date = new DatePickerFragment();

        Bundle args = new Bundle();

        args.putInt(YEAR, year);
        args.putInt(MONTH, month);
        args.putInt(DAY,day);
        args.putString("remString", remString);
        date.setArguments(args);

        date.show(getFragmentManager(), "Date Picker");
    }




}
