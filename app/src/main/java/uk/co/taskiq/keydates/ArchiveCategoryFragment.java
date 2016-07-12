package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
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

import adapter.CustomListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowItem;

/**
 * Created by Administrator on 02/09/2015.
 */
public class ArchiveCategoryFragment extends Fragment {

    SQLiteHandler dbHandler;
    private SimpleCursorAdapter dataAdapter;
    CustomListViewAdapter adapterList;

    // Log tag
    private static final String TAG = ArchiveCategoryFragment.class.getSimpleName();
    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SQLiteDatabase mSampleDb;
    private List<String> mResults;
    private Cursor mCursor;
    private ProgressDialog pDialog;
    TextView todaysDateView;
    // Elements
    private android.widget.ListView ListView;
    private SimpleCursorAdapter mListAdapter;
    private String selected = null;
    private String email = null;
    List<RowItem> rowItems;



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

        GregorianCalendar ob2 = new GregorianCalendar();
        //  ob1.setTimeInMillis(todaysDate);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.arccategory_fragment, container, false);
        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        todaysDateView.setText(todaysDate_date);
        init(view);


        registerForContextMenu(view);

        return view;



    }


    private void init(View v) {

        Cursor cursor = dbHandler.fetchAllCategories("1");
        rowItems = new ArrayList<RowItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                rowItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        //   for (int i = 0; i < cursor.getCount(); i++) {
        //     RowItem item = new RowItem((String) cursor.getString(1), cursor.getString(2),"http://api.androidhive.info/json/movies/1.jpg");
        //   rowItems.add(item);
        //}

        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.list);


        adapterList = new CustomListViewAdapter(getActivity(),R.layout.list_item, rowItems);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowItem dataModel = (RowItem) parent.getItemAtPosition(position);
                final String catID = dataModel.getCatID();
                final String Category = dataModel.getCategory();
//
                Log.d(TAG, "string: " + dataModel.getCategory());

                Intent intent_archived = new Intent(getActivity(), ArchivedRemActivity.class);
                intent_archived.putExtra("catID", catID);
                intent_archived.putExtra("Category", Category);
                startActivity(intent_archived);


            }


        });


    }



    /**
     * function to verify login details in mysql db
     * */
    private void undoArchiveProduct(final String email,final String catID, final String Category) {
        // Tag used to cancel the request
        String tag_string_req = "req_undoArchiveCategory";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Undo Archive Category Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray genreArry = jObj.getJSONArray("order");
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.d(TAG, "Undo Archive Category Response: " + genreArry.toString());
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


                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,act_Rem,act_Expiry, act_Title, Reminder, rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID,uploadSum);

                            rowItems.clear();
                            Cursor cursor = dbHandler.fetchAllCategories("1");
                            //  rowItems = new ArrayList<RowItem>();

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    // String data = cursor.getString(cursor.getColumnIndex("data"));
                                    // do what ever you want here

                                    RowItem item = new RowItem((String) cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                    rowItems.add(item);
                                    cursor.moveToNext();
                                }
                            }
                            cursor.close();


                            adapterList.notifyDataSetChanged();
                        }

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
                }
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "archiveCategory");
                params.put("email", email);
                params.put("cat_ID", catID);
                params.put("Category", Category);
                params.put("updateArchiveCat","0");
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
    public void onResume() {
        super.onResume();
        rowItems.clear();
        Cursor cursor = dbHandler.fetchAllCategories("1");
        //  rowItems = new ArrayList<RowItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                rowItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        adapterList.notifyDataSetChanged();
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
