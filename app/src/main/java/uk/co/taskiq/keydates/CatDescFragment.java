package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowCategory;

/**
 * Created by Administrator on 05/10/2015.
 */
public class CatDescFragment extends Fragment {

    // Log tag
    private static final String TAG = CatDescFragment.class.getSimpleName();

    private Cursor mCursor;
    SQLiteHandler dbHandler;

    private String email = null;
    private String USERID = null;

    private String Category;
    private String catID;
    private String DownloadCount;

    private ProgressDialog pDialog;
    TextView todaysDateView;
    TextView userIDView;
    TextView CategoryText;
    TextView UploadText;
    TextView catDescText;
    TextView DownloadCountText;
    TextView UploadSumHeaderText;
    TextView UploadSumText;



    List<RowCategory> rowCategory;
    public static String DATA_Category;
    public static String DATA_CATID;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        dbHandler = new SQLiteHandler(getActivity());

        Bundle bundle = this.getArguments();
        catID = bundle.getString("catID");
        Category = bundle.getString("Category");

        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        DATA_Category = Category;
        DATA_CATID = catID;


        View view = inflater.inflate(R.layout.catdesc_fragment, container, false);


        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        userIDView = (TextView) view.findViewById(R.id.userID);

        UploadText = (TextView) view.findViewById(R.id.Upload);

        CategoryText = (TextView) view.findViewById(R.id.Category);

        catDescText = (TextView) view.findViewById(R.id.catDesc);

        DownloadCountText = (TextView) view.findViewById(R.id.DownloadCount);

        UploadSumHeaderText = (TextView) view.findViewById(R.id.UploadSumHeader);

        UploadSumText = (TextView) view.findViewById(R.id.UploadSum);

        dbHandler = new SQLiteHandler(getActivity());

        printDatabase(catID);





        return view;
    }

    public void printDatabase(String catID) {

        RowCategory item = dbHandler.fetchCatDesc(catID, "0");
        String Upload = item.getUpload();
        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        String UploadSum = item.getUploadSum();
        String userID = item.getUserID();

        if (Upload.equals("1")&& userID.equals(USERID)) {

            UploadText.setText("You made the Category available for others to download.");
            UploadSumText.setText(UploadSum);

            if (isNetworkAvaliable(getActivity())) {

                downloadcount("downloadCatCount", email, catID, Upload, userID);
            } else {
                Toast.makeText(getActivity(), "Currently there is no network. For the download count please try later.", Toast.LENGTH_SHORT).show();
            }
        } else if (userID.equals("TaskIQ")) {  UploadText.setText("The Category is not available for others to download.");

            DownloadCountText.setText("You cannot Upload a Task iQ category.");

            UploadSumText.setText(UploadSum);
        }
            else {  UploadText.setText("The Category is not available for others to download.");

            DownloadCountText.setText("To Upload the Category select + from the homepage and then the Upload tab.");

            UploadSumText.setText(UploadSum);
            }

            GregorianCalendar ob2 = new GregorianCalendar();
            //  ob1.setTimeInMillis(todaysDate);
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String todaysDate_month = month_date.format(ob2.getTime());
            int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
            int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
            String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;

            todaysDateView.setText(todaysDate_date);
            userIDView.setText("Created by: "+userID);


            UploadSumHeaderText.setText("Upload summary: ");







        CategoryText.setText(Category);
        catDescText.setText(catDesc);

    }


    public void downloadcount (final String tag,final String email, final String catID, final String Upload, final String userID){


        String tag_string_req = "req_DownloadCount";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Download Count Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray genreArry = jObj.getJSONArray("order");
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.d(TAG, "Download Count Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite

                            //   JSONObject p = (JSONObject)genreArry.get(h);
                            JSONObject obj = genreArry.getJSONObject(h);

                           String DownloadCount = obj.getString("downloadCount");
                           DownloadCountText.setText("Category has " + DownloadCount + " downloads.");


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
                params.put("email", email);
                params.put("catID", catID);
                params.put("Upload", Upload);
                params.put("userID", userID);
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


            inflater.inflate(R.menu.menu_catdesc, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Edit Product
                Log.d(TAG, "Menu Edit string: " + Category);

                Intent intent = new Intent(getActivity(), EditCatDescActivity.class);
                intent.putExtra("catID", catID);
                intent.putExtra("Category", Category);
                startActivity(intent);

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
