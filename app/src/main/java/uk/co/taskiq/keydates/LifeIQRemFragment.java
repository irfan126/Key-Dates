package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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

import adapter.CustomSubListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowSubItem;

/**
 * Created by Administrator on 05/12/2015.
 */
public class LifeIQRemFragment extends Fragment {


    // Log tag
    private static final String TAG = LifeIQRemFragment.class.getSimpleName();

    SQLiteHandler dbHandler;
    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String userID = null;
    private ProgressDialog pDialog;
    List<RowSubItem> rowSubItems;
    private android.widget.ListView ListView;
    CustomSubListViewAdapter adapterList;
    private String Category;
    private String catDesc;
    private String catID;
    private String USERID;
    private String catUploadID;
    private String actDays;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mLayoutInflater = inflater;
        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        Bundle bundle = this.getArguments();
        catID = bundle.getString("catID");
        Category = bundle.getString("categoryTitle");
        catDesc = bundle.getString("categoryDescription");
        userID = bundle.getString("userID");
        catUploadID = bundle.getString("catUploadID");
        actDays = bundle.getString("actDays");
        Log.d(TAG, "oncreateview Reminderlist: " + Category);




        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        View view =inflater.inflate(R.layout.lifeiqrem_fragment, container, false);
        init(view);

        registerForContextMenu(view);





        return view;
    }

    private void init(View v) {

        rowSubItems = new ArrayList<RowSubItem>();

        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.lifeiqrem_list);

        adapterList = new CustomSubListViewAdapter(getActivity(), R.layout.list_reminder, rowSubItems);
        listView.setAdapter(adapterList);

        Log.d(TAG, "Download TaskiQ Reminder Response: " + " " + catUploadID + " " + email);

        if (isNetworkAvaliable(getActivity())) {  downloadLifeIQRem("downloadTaskiQRem", email, "0", "0", catUploadID, "0");}
        else {
            Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
        }


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowSubItem dataModel = (RowSubItem) parent.getItemAtPosition(position);


              //  final String catID = dataModel.getCatID();
                final String act_Date = dataModel.getActDate();
                final String uploadSum = dataModel.getUploadSum();
                final String cat_UploadID = dataModel.getCatUploadID();
                final String rem_UploadID = dataModel.getRemUploadID();


                new AlertDialog.Builder(getActivity())
                        .setTitle("Category Summary:")
                        .setMessage(uploadSum + "\n" + "\n" +
                                "Continue with the download?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (isNetworkAvaliable(getActivity())) {     if (act_Date.equals("1")) {

                                    downloadLifeIQRem("downActRemTaskiQ", email, catID, actDays, cat_UploadID, rem_UploadID);

                                } else {

                                    downloadLifeIQRem("downRemTaskiQ1", email, catID, actDays, cat_UploadID, rem_UploadID);
                                }}
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


        });

    }
    private void  downloadLifeIQRem(final String tag, final String email,final String catID,final String actDays, final String cat_UploadID, final String rem_UploadID) {
        // Tag used to cancel the request
        String tag_string_req = "req_DownloadTaskiQRem";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq1 = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Download TaskiQ Reminder Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray genreArry = jObj.getJSONArray("order");

                        Log.d(TAG, "Download Category Response: " + genreArry.toString());
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
                                String rem_DateString = obj.getString("rem_Date");
                                String rem_ExpiryString = obj.getString("rem_Expiry");
                                Integer rem_Date = obj.getInt("rem_Date");
                                Integer rem_Expiry = obj.getInt("rem_Expiry");
                                String rem_Notes = obj.getString("rem_Notes");
                                String upload = obj.getString("upload");
                                String userID = obj.getString("userID");
                                String cat_UploadID = obj.getString("cat_UploadID");
                                String rem_UploadID = obj.getString("rem_UploadID");
                                String uploadSum = obj.getString("uploadSum");


                            if (tag.equals("downRemTaskiQ1") ) {
                                hideDialog();
                          //      dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder, rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                                Toast.makeText(getActivity(), "Reminder Download complete", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), EditRemActivity.class);
                                intent.putExtra("remInt",rem_Int);
                                intent.putExtra("catID", cat_ID);
                                intent.putExtra("Category",Category );
                                intent.putExtra("Reminder", Reminder);

                                startActivity(intent);


                            }

                            else if (tag.equals("downActRemTaskiQ") ) {
                                hideDialog();
                          //      dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder, rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                                Toast.makeText(getActivity(), "Reminder Download complete", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), EditRemYesActivity.class);
                                intent.putExtra("remInt",rem_Int);
                                intent.putExtra("catID", cat_ID);
                                intent.putExtra("Category",Category );
                                intent.putExtra("Reminder", Reminder);

                                startActivity(intent);


                            }

                            else {
                                Log.d(TAG, "Task iQ Reminder Downloads Response: " + cat_ID + " " + Category + " " + category_Archive + " " + cat_Desc + " " + upload + " " + userID + " " + cat_UploadID + " " + uploadSum);
                                RowSubItem item = new RowSubItem((String) rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder, rem_Archived,imageA,imageB, rem_DateString, rem_ExpiryString, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                                rowSubItems.add(item);
                                hideDialog();
                            }



                        }

                        if (tag.equals("downloadTaskiQRem")) {
                            adapterList.notifyDataSetChanged();
                            hideDialog();
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
                params.put("email",email);
                params.put("userID1",USERID);
                params.put("catID",catID);
                params.put("Category",Category);
                params.put("catDesc",catDesc);
                params.put("actDays",actDays);
                params.put("cat_UploadID",cat_UploadID);
                params.put("rem_UploadID",rem_UploadID);
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
