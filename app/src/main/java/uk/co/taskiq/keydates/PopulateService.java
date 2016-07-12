package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;

/**
 * Created by Administrator on 12/02/2016.
 */
public class PopulateService extends Service {

    private String email = null;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private static final String TAG = PopulateService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Start of Scheduled refresh of all Reminders", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate");
// TODO Auto-generated method stub
        db = new SQLiteHandler(getApplicationContext());

        // Progress dialog
        //   pDialog = new ProgressDialog(context);
        // pDialog.setCancelable(false);

        // Fetching user details from sqlite

        HashMap<String, String> user = db.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
    //    Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();


        populateReminders(email);

       stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "End of Scheduled refresh of all Reminders", Toast.LENGTH_SHORT).show();
    }

    private void populateReminders(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //  pDialog.setMessage("Automatic refresh of all Reminders.");
        // showDialog();


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

                       // Toast.makeText(getApplicationContext(), "Scheduled refresh of all Reminders", Toast.LENGTH_SHORT).show();
                        db.deleteTable_Products();
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


                         //   db.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_SHORT).show();
                        //   hideDialog();
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

                if(error.getMessage().equals("null")){ Toast.makeText(getApplicationContext(),
                        "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
}
