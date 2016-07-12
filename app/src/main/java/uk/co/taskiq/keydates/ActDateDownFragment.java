package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;

/**
 * Created by Administrator on 23/11/2015.
 */
public class ActDateDownFragment extends Fragment {

    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = ActDateDownFragment.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String userID = null;
    private String catID = null;
    private String Category = null;
    private String catDesc = null;
    private String actDate = null;
    private String act_Title = null;
    private String uploadSum = null;
    private String cat_UploadID = null;

    private ProgressDialog pDialog;
    private TextView categoryTitle;
    private TextView categoryDesc;
    private TextView userIDText;
    private TextView ActDateTitle;
    private TextView reminderView;
    private String reminderDateString = null;
    private long reminderDateLong1 = 0;
    private String reminderExpiryString = null;
    private long reminderExpiryLong1 = 0;
    private Button btnNext;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        userID = bundle.getString("userID");
        catID = bundle.getString("cat_ID");
        Category = bundle.getString("Category");
        catDesc = bundle.getString("catDesc");
        actDate = bundle.getString("act_Date");
        act_Title = bundle.getString("act_Title");
        uploadSum = bundle.getString("uploadSum");
        cat_UploadID = bundle.getString("cat_UploadID");




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
        View view =inflater.inflate(R.layout.actdatedown_fragment, container, false);

        categoryTitle = (TextView) view.findViewById(R.id.CategoryTitle);
        categoryTitle.setText("Category: " + Category);

        categoryDesc = (TextView) view.findViewById(R.id.CategoryDesc);
        categoryDesc.setText("Description: " + catDesc);

        userIDText = (TextView) view.findViewById(R.id.userID);
        userIDText.setText("User ID: " + userID);


        ActDateTitle = (TextView) view.findViewById(R.id.ActivationDateTitle);
        ActDateTitle.setText(act_Title);

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

                String remViewDate = reminderView.getText().toString();
                SimpleDateFormat sdf  = new SimpleDateFormat("dd-MMM-yyyy");


                try {
                    java.util.Date date = (java.util.Date) sdf.parse(remViewDate);
                    reminderDateLong1 = (date.getTime())/1000;

                } catch (ParseException e) {
                    Log.e("log", e.getMessage(), e);
                }
                reminderDateString = Long.toString(reminderDateLong1);
                if (isNetworkAvaliable(getActivity())) {downCategory("actdatedownCat", email, userID, catID, reminderDateString, cat_UploadID);}
                else {
                    Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                }





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

    private void downCategory(final String tag, final String email, final String userID, final String CatID, final String act_Days, final String cat_UploadID) {
        // Tag used to cancel the request
        String tag_string_req = "req_actDownloadCategory";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq1 = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Download ActDate Category Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray genreArry = jObj.getJSONArray("order");

                        Log.d(TAG, "Download ActDate Category Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {



                            // User successfully stored in MySQL
                            // Now store the user in sqlite
                            JSONObject obj = genreArry.getJSONObject(h);

                            String rem_Int =obj.getString("rem_Int");
                            String cat_ID = obj.getString("cat_ID");
                            String Category = obj.getString("Category");
                            String category_Archive = obj.getString("category_Archive");
                            String cat_Desc = obj.getString("cat_Desc");
                            String act_Date = obj.getString("act_Date");
                            String act_Days = obj.getString("act_Days");
                            String act_Rem = obj.getString("act_Rem");
                            String act_Expiry = obj.getString("act_Expiry");
                            String act_Title = obj.getString("act_Title");
                            String Reminder = obj.getString("Reminder");
                            String rem_Archived = obj.getString("rem_Archived");
                            String imageA = obj.getString("imageA");
                            String imageB = obj.getString("imageB");
                            Integer rem_Date = obj.getInt("rem_Date");
                            Integer rem_Expiry = obj.getInt("rem_Expiry");
                            String rem_Notes = obj.getString("rem_Notes");
                            String upload = obj.getString("upload");
                            String userID = obj.getString("userID");
                            String cat_UploadID = obj.getString("cat_UploadID");
                            String rem_UploadID = obj.getString("rem_UploadID");
                            String uploadSum = obj.getString("uploadSum");

                        //    dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder, rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);



                        }

                        Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("selectTab", "1");
                        Toast.makeText(getActivity(), "Download complete", Toast.LENGTH_SHORT).show();
                        startActivity(intent);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_SHORT).show();
                        hideDialog();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());

                if(error.getMessage().equals("null")){ Toast.makeText(getActivity(),
                        "Error processing your request. Please try again when you have network coverage.", Toast.LENGTH_SHORT).show();}
               else {
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();}
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", tag);
                params.put("email",email);
                params.put("userID",userID);
                params.put("cat_ID", CatID);
                params.put("act_Days", act_Days);
                params.put("cat_UploadID",cat_UploadID);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq1, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }


}
