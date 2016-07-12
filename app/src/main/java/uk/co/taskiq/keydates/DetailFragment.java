package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CustomCategoryListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;
import model.RowCategory;

/**
 * Created by Administrator on 27/09/2015.
 */
public class DetailFragment extends Fragment {

    SQLiteHandler dbHandler;
    private SimpleCursorAdapter dataAdapter;
    CustomCategoryListViewAdapter adapterList;

    // Log tag
    private static final String TAG = DetailFragment.class.getSimpleName();
    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SQLiteDatabase mSampleDb;
    private List<String> mResults;
    private Cursor mCursor;
    private ProgressDialog pDialog;
    // Elements
    private android.widget.ListView ListView;
    private SimpleCursorAdapter mListAdapter;
    private String selected = null;
    private String email = null;
    private String USERID = null;
    List<RowCategory> rowCategory;
    private SessionManager session;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");


        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // session manager
        session = new SessionManager(getActivity());

        View view =inflater.inflate(R.layout.detail_fragment, container, false);
        init(view);

        registerForContextMenu(view);

        return view;
    }

    private void init(View v) {

        Cursor cursor = dbHandler.fetchUserCategories("0",USERID);
        rowCategory = new ArrayList<RowCategory>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowCategory item = new RowCategory((String)  cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),"0",cursor.getString(7),cursor.getString(8),cursor.getString(10));
                rowCategory.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        //   for (int i = 0; i < cursor.getCount(); i++) {
        //     RowItem item = new RowItem((String) cursor.getString(1), cursor.getString(2),"http://api.androidhive.info/json/movies/1.jpg");
        //   rowItems.add(item);
        //}

        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.detail_list);


        adapterList = new CustomCategoryListViewAdapter(getActivity(),R.layout.list_item, rowCategory);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowCategory dataModel = (RowCategory) parent.getItemAtPosition(position);
                String catID = dataModel.getCatID();
                String Category = dataModel.getCategory();
                String actDate = dataModel.getActDate();

                if (actDate.equals("0")) {

                    Intent intent = new Intent(getActivity(), CatDescActivity.class);
                    intent.putExtra("catID", catID);
                    intent.putExtra("Category", Category);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(getActivity(), CatDescYesActivity.class);
                    intent.putExtra("catID", catID);
                    intent.putExtra("Category", Category);
                    startActivity(intent);
                }


            }


        });


    }


    @Override
    public void onResume() {
        super.onResume();
        rowCategory.clear();
        Cursor cursor = dbHandler.fetchUserCategories("0",USERID);
        //  rowItems = new ArrayList<RowItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowCategory item = new RowCategory((String) cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6), "0",cursor.getString(7),cursor.getString(8),cursor.getString(10));
                rowCategory.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        adapterList.notifyDataSetChanged();
    }

    public void info(){

        new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage("\n" +
                        "Select a Category to edit details for a Task iQ Category or one you have created." + "\n" + "\n" +"Please note you are unable to edit Categories which have been downloaded and created by other users")
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
                    JSONArray genreArry = jObj.getJSONArray("order");
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");


                    // Check for error node in json
                    if (!error) {
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


                  //          dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);
                            rowCategory.clear();
                            Cursor cursor = dbHandler.fetchUserCategories("0", USERID);
                            //  rowItems = new ArrayList<RowItem>();

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    // String data = cursor.getString(cursor.getColumnIndex("data"));
                                    // do what ever you want here

                                    RowCategory item = new RowCategory((String) cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6), cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                                    rowCategory.add(item);
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
                                "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();
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
                }
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

            case R.id.action_info:
                // Information
                info();
                return true;

            case R.id.action_settings:
                // Settings


                Intent intent1 = new Intent(getActivity(), SettingActivity.class);
                // intent.putExtra("message", category);
                startActivity(intent1);
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
