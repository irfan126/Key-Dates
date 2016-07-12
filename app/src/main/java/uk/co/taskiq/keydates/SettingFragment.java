package uk.co.taskiq.keydates;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;

import helper.SQLiteHandler;

/**
 * Created by Administrator on 04/02/2016.
 */
public class SettingFragment  extends Fragment {
    SQLiteHandler dbHandler;

    TextView todaysDateView;
    TextView userIDView;
    TextView deleteAccount;
    TextView passwordReset;

    private String email = null;
    private String USERID = null;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Log tag
    private static final String TAG = SettingFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();


        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();


        email = user.get("email");
        USERID = user.get("userID");


        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        registerForContextMenu(view);

        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        userIDView = (TextView) view.findViewById(R.id.userID);

        deleteAccount = (TextView) view.findViewById(R.id.deleteAccount);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(getActivity(), DelAccountActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


        passwordReset = (TextView) view.findViewById(R.id.passwordReset);

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        dbHandler = new SQLiteHandler(getActivity());


        printData();


        return view;
    }

    public void printData()  {

        // access the fields
       SimpleDateFormat month_date = new SimpleDateFormat("MMM");


        Long todaysDate = System.currentTimeMillis();
        GregorianCalendar ob2 = new GregorianCalendar();
        ob2.setTimeInMillis(todaysDate);

        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;

        todaysDateView.setText(todaysDate_date);
        userIDView.setText("Hello "+USERID);

    }





}
