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
import android.text.SpannableStringBuilder;
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

import adapter.CustomUploadListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowUpload;

/**
 * Created by Administrator on 13/11/2015.
 */
public class UploadFragment extends Fragment {
    SQLiteHandler dbHandler;
    CustomUploadListViewAdapter adapterList;

    // Log tag
    private static final String TAG = UploadFragment.class.getSimpleName();
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
    private String userID = null;
    List<RowUpload> rowUploads;


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
        userID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        View view =inflater.inflate(R.layout.upload_fragment, container, false);
        init(view);

        registerForContextMenu(view);

        return view;
    }

    private void init(View v) {

        Cursor cursor = dbHandler.fetchAllUserCategories("0", userID);
        rowUploads = new ArrayList<RowUpload>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowUpload item = new RowUpload((String) cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));


                rowUploads.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.upload_list);


        adapterList = new CustomUploadListViewAdapter(getActivity(), R.layout.list_upload, rowUploads);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowUpload dataModel = (RowUpload) parent.getItemAtPosition(position);
                final String catID = dataModel.getCatID();
                final String Category = dataModel.getCategory();
                final String catDesc = dataModel.getCatDesc();
                String upload = dataModel.getUpload();
                final String uploadSum = dataModel.getUploadSum();

                if (upload.equals("0")) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Upload Category")
                            .setMessage("Please note any Category you upload will be available for other users to download. " +
                                    "Therefore do not include any personal or sensitive information." + "\n" + "\n" +
                                    " Are you sure you want to Upload?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getActivity(), UploadSumActivity.class);
                                    intent.putExtra("cat_ID", catID);
                                    intent.putExtra("Category", Category);
                                    intent.putExtra("catDesc", catDesc);
                                    intent.putExtra("uploadSum", uploadSum);
                                    startActivity(intent);

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Undo Upload")
                            .setMessage("Please note if you undo Upload for this Category any new Reminders you create will not be available to other users. " +
                                    "\n" + "\n" +
                                    " Are you sure you want to undo Upload?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if (isNetworkAvaliable(getActivity())) {
                                        uploadCategory("undoUploadCategory", email, userID, catID, "2", uploadSum);
                                         }
                                    else {
                                        Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }


            }


        });

    }

    private void uploadCategory(final String tag, final String email, final String userID, final String CatID, final String upload, final String uploadSum) {
        // Tag used to cancel the request
        String tag_string_req = "req_UploadCategory";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Upload Category Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                     if (!error) {
                        JSONArray genreArry = jObj.getJSONArray("order");

                        Log.d(TAG, "Upload Category Response: " + genreArry.toString());
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


                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,  act_Rem, act_Expiry,act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID,uploadSum);

                        }

                         rowUploads.clear();

                         Cursor cursor = dbHandler.fetchAllUserCategories("0", userID);
                         // rowSubItems = new ArrayList<RowSubItem>();

                         if (cursor.moveToFirst()) {
                             while (!cursor.isAfterLast()) {
                                 RowUpload item = new RowUpload((String) cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

                                 rowUploads.add(item);
                                 cursor.moveToNext();
                             }
                         }
                         cursor.close();

                         adapterList.notifyDataSetChanged();
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
                }
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", tag);
                params.put("email", email);
                params.put("userID",userID);
                params.put("cat_ID", CatID);
                params.put("upload",upload);
                params.put("uploadSum",uploadSum);
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
        rowUploads.clear();
        Cursor cursor = dbHandler.fetchAllUserCategories("0", userID);


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowUpload item = new RowUpload((String) cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(5),cursor.getString(6));
                rowUploads.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        adapterList.notifyDataSetChanged();
    }


    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Upload a Category you created for other Task iQ users.\n\n");
        builder.append("Please note if you upload a Category any changes or new Reminders you create under the Category will be available to other user automatically, unless you Undo Upload.\n\n");
        builder.append("Please do not include any personal or sensitive information.\n\n");


        new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage(builder)

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
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_addcat, menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_info:
                // Information
                info();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
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
