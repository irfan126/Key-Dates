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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import adapter.CustomDownloadListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowDownload;

/**
 * Created by Administrator on 16/11/2015.
 */
public class DownloadFragment extends Fragment {

  // Log tag
    private static final String TAG = DownloadFragment.class.getSimpleName();

    SQLiteHandler dbHandler;
    CustomDownloadListViewAdapter adapterList;
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
    private Button btnLifeIQ;
    private Button btnTopCat;


    List<RowDownload> rowDownloads;

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

        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        View view =inflater.inflate(R.layout.download_fragment, container, false);
        init(view);

        registerForContextMenu(view);

        btnLifeIQ = (Button) view.findViewById(R.id.BtnLifeIQ);

        // Login button Click Event
        btnLifeIQ.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {



                if (isNetworkAvaliable(getActivity())) {downCategory("TaskiQCat","search", email, "TaskIQ", "", "");}
                else {
                    Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                }

            }

        });

        btnTopCat = (Button) view.findViewById(R.id.BtnTopCat);

        // Login button Click Event
        btnTopCat.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {



                if (isNetworkAvaliable(getActivity())) { downCategory("downloadCount","search", email, USERID, "", "");}
                else {
                    Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                }

            }

        });

        return view;
    }



    private void init(View v) {





       rowDownloads = new ArrayList<RowDownload>();

        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.download_list);

        adapterList = new CustomDownloadListViewAdapter(getActivity(), R.layout.list_download, rowDownloads);
        listView.setAdapter(adapterList);
        if (isNetworkAvaliable(getActivity())) { downCategory("downloadCat","search", email, USERID, "", "");}
        else {
            Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
        }


                //Called when product is selected in the listview
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {


                        RowDownload dataModel = (RowDownload) parent.getItemAtPosition(position);
                        final String userID = dataModel.getUserID();
                        final String catID = dataModel.getCatID();
                        final String Category = dataModel.getCategory();
                        final String catDesc = dataModel.getCatDesc();
                        final String act_Date = dataModel.getAct_Date();
                        final String act_Title = dataModel.getAct_Title();
                        String upload = dataModel.getUpload();
                        final String uploadSum = dataModel.getUploadSum();
                        final String cat_UploadID = dataModel.getCat_UploadID();


                        new AlertDialog.Builder(getActivity())
                                .setTitle("Category Summary:")
                                .setMessage(uploadSum + "\n" + "\n" +
                                        "Continue with the download?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (act_Date.equals("1")) {

                                            Intent intent = new Intent(getActivity(), ActDateDownActivity.class);
                                            intent.putExtra("userID", userID);
                                            intent.putExtra("cat_ID", catID);
                                            intent.putExtra("Category", Category);
                                            intent.putExtra("catDesc", catDesc);
                                            intent.putExtra("act_Date", act_Date);
                                            intent.putExtra("act_Title", act_Title);
                                            intent.putExtra("uploadSum", uploadSum);
                                            intent.putExtra("cat_UploadID", cat_UploadID);
                                            startActivity(intent);

                                        } else {
                                            Log.d(TAG, "download1 Category Response: " + email + " " + userID + " " + catID + " " + cat_UploadID);


                                            if (isNetworkAvaliable(getActivity())) {

                                                if (userID.equals("TaskIQ")){

                                                    downCategory("downCatTaskIQ","search", email, userID, catID, cat_UploadID);

                                                }

                                             else{

                                                downCategory("downCat","search", email, userID, catID, cat_UploadID);
                                                }


                                            }
                                            else {
                                                Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                                            }
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

    private void downCategory(final String tag,final String search, final String email, final String userID, final String CatID, final String cat_UploadID) {
        // Tag used to cancel the request
        String tag_string_req = "req_DownloadCategory";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq1 = new StringRequest(Request.Method.POST,
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

                        if (tag.equals("downloadCat") || tag.equals("searchCat") || tag.equals("TaskiQCat")  || tag.equals("downloadCount") ) {

                            rowDownloads.clear();
                        }


                        Log.d(TAG, "Download Category Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {



                            // User successfully stored in MySQL
                            // Now store the user in sqlite
                            JSONObject obj = genreArry.getJSONObject(h);

                           if (tag.equals("downCat") ||tag.equals("downCatTaskIQ")) {

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

               //                dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);


                           }

                            else{

                               String cat_ID = obj.getString("cat_ID");
                               String Category = obj.getString("Category");
                               String category_Archive = obj.getString("category_Archive");
                               String cat_Desc = obj.getString("cat_Desc");
                               String act_Date = obj.getString("act_Date");
                               String act_Title = obj.getString("act_Title");
                               String upload = obj.getString("upload");
                               String userID = obj.getString("userID");
                               String cat_UploadID = obj.getString("cat_UploadID");
                               String uploadSum = obj.getString("uploadSum");
                               String downloadCount = obj.getString("downloadCount");

                               Log.d(TAG, "dowload2 Category Response: " + cat_ID + " " + Category + " " + category_Archive + " " + cat_Desc + " " + upload + " " + userID + " " + cat_UploadID + " " + uploadSum + " " + downloadCount);
                               RowDownload item = new RowDownload((String) cat_ID, Category,category_Archive, cat_Desc,act_Date,act_Title, upload,userID,cat_UploadID, uploadSum,downloadCount);


                               rowDownloads.add(item);
                           }
                        }

                        if (tag.equals("downCat")||tag.equals("downCatTaskIQ")) {

                            Toast.makeText(getActivity(),
                                    "Category Download complete", Toast.LENGTH_SHORT).show();
                            adapterList.notifyDataSetChanged();
                        }

                        else{

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
                }
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", tag);
                params.put("search", search);
                params.put("email",email);
                params.put("userID1",USERID);
                params.put("userID",userID);
                params.put("cat_ID", CatID);
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

    public void  doMySearch(String query) {

        if (isNetworkAvaliable(getActivity())) { downCategory("searchCat", query, email, USERID, "", "");}
        else {
            Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
        }



    }


    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Download a Category other Task iQ users have created. Please note you cannot amend any Categories you download unless it is a Task iQ Category.\n\n");


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

        inflater.inflate(R.menu.menu_downcat, menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.search:
                // search

                SearchView sv = new SearchView(((AddCategoryActivity) getActivity()).getSupportActionBar().getThemedContext());
                MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
                MenuItemCompat.setActionView(item, sv);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        System.out.println("search query submit");
                        doMySearch(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        System.out.println("tap");
                        return false;
                    }
                });
                return true;


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
