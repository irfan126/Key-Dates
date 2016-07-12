package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CustomDateListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;
import model.RowDateItem;

/**
 * Created by Administrator on 15/08/2015.
 */
public class DateFragment extends Fragment {


    SQLiteHandler dbHandler;
    CustomDateListViewAdapter adapterList;
    private SessionManager session;

    // Log tag
    private static final String TAG = DateFragment.class.getSimpleName();

    private ProgressDialog pDialog;

//Elements
    private String email = null;
    private String firstname = null;

    private String USERID = null;
    List<RowDateItem> rowDateItems;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        dbHandler = new SQLiteHandler(getActivity());

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // session manager
        session = new SessionManager(getActivity());

        HashMap<String, String> user = dbHandler.getUserDetails();

        firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");


        View view =inflater.inflate(R.layout.date_fragment, container, false);
        printData(view);

        return view;
    }

    private void printData(View v) {

        Cursor cursor = dbHandler.fetchAllDateReminders("0");
        rowDateItems = new ArrayList<RowDateItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                RowDateItem item = new RowDateItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),printDate(cursor.getString(7)),cursor.getInt(7),cursor.getString(8),cursor.getString(9));

                Log.d(TAG, "Date Fragment: " +cursor.getString(0) + " "+cursor.getString(1));

                rowDateItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        ListView listView = (ListView) v.findViewById(R.id.list);

        adapterList = new CustomDateListViewAdapter(getActivity(),R.layout.list_dateitem, rowDateItems);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);

        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                RowDateItem dataModel = (RowDateItem) parent.getItemAtPosition(position);

                String remInt = dataModel.getRemInt();
                String catID = dataModel.getCatID();
                String Category = dataModel.getCategory();
                String Reminder = dataModel.getReminder();
//                String remDateInt = dataModel.getRemInt().toString();

                //Log.d(TAG, "string PrdDate2: " + remDateInt);
                // Toast.makeText(getActivity(), category, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), RemPageActivity.class);
                intent.putExtra("remInt", remInt);
                intent.putExtra("catID", catID);
                intent.putExtra("Category", Category);
                intent.putExtra("Reminder", Reminder);

                startActivity(intent);

            }


        });


    }

public String printDate (String intDate){

 //   String date;

    String item1 = intDate;
    Long myInt2 = (Long.parseLong(item1))*1000;

    GregorianCalendar ob = new GregorianCalendar();
    ob.setTimeInMillis(myInt2);

    // access the fields
    int month = ob.get(GregorianCalendar.MONTH);

    SimpleDateFormat month_date = new SimpleDateFormat("MMM");
    String month_name = month_date.format(ob.getTime());
    int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
    int year = ob.get(GregorianCalendar.YEAR);
    // String date = day+"-"+(month+1)+"-"+year;
    String date = day+"-"+month_name+"-"+year;


    return date;

}

    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Select a Reminder to see in full and for more options.\n\n");
        builder.append("To Add a new Category select + from the menu.\n\n");
        builder.append("To add a Reminder under a existing Category select the Category in the Category Tab and then +.\n\n");
        builder.append("Refresh will check for updates for any Categories which you download.\n\n");
        builder.append("When updates are available the Update logo appears in the Reminder Tab. Select the Update logo in the Reminder.\n\n").append(" ");

        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_add_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Add     ").append(" ");
        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_cached_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Refresh     ").append(" ");
        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_system_update_alt_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Update ");

        new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage(builder)
                        //"Select a Category to see all Reminders."+ "\n" + "\n" + "Long press on a Category will give you options to Delete or Archive."
                          //      + "\n" + "\n" + "Please note you are able to edit Categories under the Details tab."
                            //    + "\n" + "\n" + "To Add a new Category select + from the menu.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...

            rowDateItems.clear();

            Cursor cursor = dbHandler.fetchAllDateReminders("0");

            // rowDateItems = new ArrayList<RowDateItem>();

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    RowDateItem item = new RowDateItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),printDate(cursor.getString(7)),cursor.getInt(7),cursor.getString(8),cursor.getString(9));
                    Log.d(TAG, "init Cursor DateFragment: " + cursor.getString(3));

                    rowDateItems.add(item);
                    cursor.moveToNext();
                }
            }
            cursor.close();


            adapterList.notifyDataSetChanged();
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible anymore.");

            }
        }
    }

    public void onResume() {
        super.onResume();


        rowDateItems.clear();

        Cursor cursor = dbHandler.fetchAllDateReminders("0");

        // rowDateItems = new ArrayList<RowDateItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                RowDateItem item = new RowDateItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),printDate(cursor.getString(7)),cursor.getInt(7),cursor.getString(8),cursor.getString(9));
                Log.d(TAG, "init Cursor DateFragment: " + cursor.getString(3));

                rowDateItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        adapterList.notifyDataSetChanged();
    }




    public void refreshReminders(){

        populateReminders(email);

    }


    private void populateReminders(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Refresh all Reminders.");
        showDialog();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Populate Products Response: " + response.toString());


                try {

                    JSONObject jObj = new JSONObject(response);
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");


                    // Check for error node in json
                    if (!error) {
                        JSONArray genreArry = jObj.getJSONArray("order");

                        dbHandler.deleteTable_Products();
                        Log.d(TAG, "genreArry Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite

                            //   JSONObject p = (JSONObject)genreArry.get(h);
                            JSONObject obj = genreArry.getJSONObject(h);



                            //  String uid = genreArry.get(h).getJSONObject("prd_int");


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
                            rowDateItems.clear();

                            Cursor cursor = dbHandler.fetchAllDateReminders("0");

                            // rowDateItems = new ArrayList<RowDateItem>();

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {

                                    RowDateItem item = new RowDateItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),printDate(cursor.getString(7)),cursor.getInt(7),cursor.getString(8),cursor.getString(9));
                                    Log.d(TAG, "init Cursor DateFragment: " + cursor.getString(3));

                                    rowDateItems.add(item);
                                    cursor.moveToNext();
                                }
                            }
                            cursor.close();


                            adapterList.notifyDataSetChanged();

                        }
                        hideDialog();
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
                        "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getActivity(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();}
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "productList");
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

            inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_add:
                // Add a category

                    Intent addCategory = new Intent(getActivity(), AddCategoryActivity.class);
                    addCategory.putExtra("selectTab", "0");
                    startActivity(addCategory);
                    return true;


            case R.id.action_logout:
                // logout User
                logoutUser();
                return true;
            case R.id.action_ContactUs:
                // Contact Us
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);

                startActivity(intent);
                return true;
            case R.id.action_settings:
                // Settings

                Intent intent1 = new Intent(getActivity(), SettingActivity.class);
                // intent.putExtra("message", category);
                startActivity(intent1);
                return true;

            case R.id.action_info:
                // Information
                info();
                return true;
            case R.id.refresh:
                // Settings

             if (isNetworkAvaliable(getActivity())) { refreshReminders();}
                else {
                 Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
             }

                return true;

            case R.id.action_archivedCategories:
                // Hidden Products

                Intent intent2 = new Intent(getActivity(), ArchiveCategoryActivity.class);
                // intent.putExtra("message", category);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        dbHandler.deleteUsers();
        dbHandler.deleteTable_Products();

        ((MainActivity)getActivity()).cancelAlarm();
        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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