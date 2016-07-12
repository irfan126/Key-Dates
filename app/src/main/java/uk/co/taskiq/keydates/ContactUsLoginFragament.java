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
import helper.SessionManager;

/**
 * Created by Administrator on 18/04/2016.
 */
public class ContactUsLoginFragament extends Fragment {
    // Log tag
    private static final String TAG = ContactUsLoginFragament.class.getSimpleName();
    private SessionManager session;


    TextView todaysDateView;
    TextView ContactUsText;
    EditText ContactUs;

    private Button btnLinkToLogin;
    private EditText inputEmail;
    private Button btnSend;

    private ProgressDialog pDialog;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();

        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;

        // session manager
        session = new SessionManager(getActivity());



        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);


        View view = inflater.inflate(R.layout.contactuslogin_fragment, container, false);
        registerForContextMenu(view);

        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);

        ContactUsText = (TextView) view.findViewById(R.id.ContactUsText);

        ContactUs = (EditText) view.findViewById(R.id.ContactUs);


        inputEmail = (EditText) view.findViewById(R.id.email);

        btnSend = (Button) view.findViewById(R.id.btnSend);


        btnLinkToLogin = (Button) view.findViewById(R.id.btnLinkToLoginScreen);


        btnSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                String ContactUsInput = ContactUs.getText().toString();

                String email = inputEmail.getText().toString();

                // Check for empty data in the form
                if (ContactUsInput.trim().length() > 0) {

                    if (isEmailValid(email)) {

                    if (isNetworkAvaliable(getActivity())) {
                        contactUs("contactUsLogin", email, ContactUsInput);}
                    else {
                        Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(),
                            "Please enter a valid email address!", Toast.LENGTH_SHORT)
                            .show();
                }}
                else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please complete the Contact us field!", Toast.LENGTH_SHORT)
                            .show();
                }
            }

        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(),
                        LoginActivity.class);
                startActivity(i);

            }
        });

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

        ContactUsText.setText("To provide any feedback or if you have a question please complete the Contact us field."
                + "\n" + "\n" + "We will respond via email as soon as possible."    + "\n" + "\n" + "If you are not receiving the confirmation email please check the spam folder.");

    }

    /**
     * function to verify login details in mysql db
     * */
    private void contactUs(final String tag, final String email, final String contactUs) {
        // Tag used to cancel the request
        String tag_string_req = "req_contactUsLogin";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Contact Us Login Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (!error){

                        hideDialog();

                        // Launch login activity
                        Intent intent = new Intent(getActivity(),LoginActivity.class);


                     //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


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
