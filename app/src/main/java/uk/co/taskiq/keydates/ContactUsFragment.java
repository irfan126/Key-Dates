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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Administrator on 06/02/2016.
 */
public class ContactUsFragment extends Fragment {

    private SessionManager session;

    SQLiteHandler dbHandler;

    TextView todaysDateView;
    TextView userIDView;
    TextView ContactUsText;
    EditText ContactUs;
    private Button btnSend;

    private ProgressDialog pDialog;
    private String email = null;
    private String USERID = null;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Log tag
    private static final String TAG = ContactUsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();


        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        // session manager
        session = new SessionManager(getActivity());


        email = user.get("email");
        USERID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);


        View view = inflater.inflate(R.layout.contactus_fragment, container, false);
        registerForContextMenu(view);

        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        userIDView = (TextView) view.findViewById(R.id.userID);

        ContactUsText = (TextView) view.findViewById(R.id.ContactUsText);

        ContactUs = (EditText) view.findViewById(R.id.ContactUs);


        btnSend = (Button) view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                String ContactUsInput = ContactUs.getText().toString();

                // Check for empty data in the form
                if (ContactUsInput.trim().length() > 0) {

                        if (isNetworkAvaliable(getActivity())) {
                            contactUs("contactUs", email, USERID, ContactUsInput);}
                        else {
                            Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please complete the Contact us field!", Toast.LENGTH_SHORT)
                            .show();
                }
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
        userIDView.setText("Hello " + USERID);

        ContactUsText.setText("To provide any feedback or if you have a question please complete the Contact us field."
                + "\n" + "\n" +"We will respond via email as soon as possible.");

    }



    /**
     * function to verify login details in mysql db
     * */
    private void contactUs(final String tag, final String email, final String userID,  final String contactUs) {
        // Tag used to cancel the request
        String tag_string_req = "req_contactUs";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Contact Us Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (!error){

                        hideDialog();
                        Intent intent = new Intent(getActivity(), MainActivity.class);


                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


                        intent.putExtra("selectTab", "0");
                        Toast.makeText(getActivity(), "Query has been received and we will respond via email.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);


                    }



                    else {
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
                params.put("contactUs",contactUs);
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
