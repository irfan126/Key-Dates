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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Administrator on 04/03/2016.
 */
public class ShareFragment extends Fragment {

    // Log tag
    private static final String TAG = ShareFragment.class.getSimpleName();

    private SessionManager session;

    SQLiteHandler dbHandler;

    TextView todaysDateView;
    TextView userIDView;
    TextView CategoryTitle;
    TextView CategoryDesc;
    EditText emailInput;
    private Button btnShare;

    private ProgressDialog pDialog;
    private String email = null;
    private String USERID = null;
    private String category = null;
    private String catDesc= null;
    private String catID = null;
    private String userID = null;

    private Context mContext;
    private LayoutInflater mLayoutInflater;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();
        catID = bundle.getString("catID");
        category = bundle.getString("Category");
        catDesc = bundle.getString("CatDesc");
        userID = bundle.getString("userID");

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


        View view = inflater.inflate(R.layout.share_fragment, container, false);
        registerForContextMenu(view);

        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        userIDView = (TextView) view.findViewById(R.id.userID);

        CategoryTitle = (TextView) view.findViewById(R.id.CategoryTitle);

        CategoryDesc = (TextView) view.findViewById(R.id.CategoryDesc);

        emailInput = (EditText) view.findViewById(R.id.email);


        btnShare = (Button) view.findViewById(R.id.btnShare);

        btnShare.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                String inputEmail = emailInput.getText().toString();

                if (!inputEmail.isEmpty()) {

                    if (isEmailValid(inputEmail)) {

                        if (isNetworkAvaliable(getActivity())) {
                            shareCat("shareCat", email, userID, inputEmail, catID);
                        } else {
                            Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getActivity(),
                                "Please enter a valid email address!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please enter a email address!", Toast.LENGTH_SHORT)
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

        CategoryTitle.setText("Category: " + category);

        CategoryDesc.setText("Description: " + catDesc);

    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * function to verify login details in mysql db
     * */
    private void shareCat(final String tag, final String email, final String userID,  final String inputEmail,  final String catID) {
        // Tag used to cancel the request
        String tag_string_req = "req_shareCat";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Share Category Response: " + response.toString());


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

                        String errorMsg = jObj.getString("error_msg");
                        intent.putExtra("selectTab", "1");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        startActivity(intent);


                    }



                    else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
                params.put("inputEmail",inputEmail);
                params.put("catID",catID);
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
