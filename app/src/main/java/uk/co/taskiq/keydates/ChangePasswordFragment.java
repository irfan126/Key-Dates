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
 * Created by Administrator on 07/02/2016.
 */
public class ChangePasswordFragment extends Fragment {

    private SessionManager session;

    SQLiteHandler dbHandler;

    TextView todaysDateView;
    TextView userIDView;
    TextView passwordReset;
    EditText inputPassword;
    EditText inputPassword1;
    EditText inputPassword2;
    private Button btnSubmit;

    private ProgressDialog pDialog;
    private String email = null;
    private String USERID = null;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Log tag
    private static final String TAG = ChangePasswordFragment.class.getSimpleName();

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


        View view = inflater.inflate(R.layout.changepassword_fragment, container, false);
        registerForContextMenu(view);

        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        userIDView = (TextView) view.findViewById(R.id.userID);

        passwordReset = (TextView) view.findViewById(R.id.passwordReset);

        inputPassword = (EditText) view.findViewById(R.id.password);

        inputPassword1 = (EditText) view.findViewById(R.id.password1);

        inputPassword2 = (EditText) view.findViewById(R.id.password2);

        btnSubmit = (Button) view.findViewById(R.id.Submitbtn);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                String password = inputPassword.getText().toString();
                String password1 = inputPassword1.getText().toString();
                String password2 = inputPassword2.getText().toString();

                if (!password.isEmpty()&& !password1.isEmpty() && !password2.isEmpty()) {


                    if (password1.length() > 5 && isAlphaNumeric(password1) ) {

                        if (password1.equals(password2)) {

                            if (isNetworkAvaliable(getActivity())) {changePassword("newPassword",email, password,password1);
                            }
                            else {
                                Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(getActivity(),
                                    "New passwords do not match please re-enter!", Toast.LENGTH_SHORT)
                                    .show();
                        }



                    } else {
                        Toast.makeText(getActivity(),
                                "Password has to be greater than five characters and contain letters and numbers!", Toast.LENGTH_SHORT)
                                .show();
                    }


                } else {
                    Toast.makeText(getActivity(),
                            "Please complete all password fields!", Toast.LENGTH_SHORT)
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

        passwordReset.setText("Please enter your existing and new passwords."
                + "\n" + "\n" +"Please note your password should be greater than five characters and must contain both letters and numbers.");

    }



    /**
     * function to verify login details in mysql db
     * */
    private void changePassword(final String tag, final String email, final String passowrd, final String passowrd1) {
        // Tag used to cancel the request
        String tag_string_req = "req_changePassword";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Change Password Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (!error){


                        dbHandler.deleteUsers();
                        dbHandler.deleteTable_Products();

                        session.setLogin(false);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        // intent.putExtra("remInt",rem_Int );
                        //intent.putExtra("catID",cat_ID );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Toast.makeText(getActivity(), "Password has been reset. Please login with new passwords.", Toast.LENGTH_SHORT).show();
                        hideDialog();
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
                params.put("password",passowrd);
                params.put("password1",passowrd1);
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

    public boolean isAlphaNumeric(String password){
        String pattern= "^[a-zA-Z0-9]*$";
        if(password.matches(pattern)){
            return true;
        }
        return false;
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
