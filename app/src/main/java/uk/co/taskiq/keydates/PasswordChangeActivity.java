package uk.co.taskiq.keydates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by Administrator on 12/08/2015.
 */
public class PasswordChangeActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button submitbtn;
    private Button btnLinkToLogin;
    private EditText inputFirstname;
    private EditText inputSurname;

    private EditText inputPassword;
    private EditText inputPassword2;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_change);
        Intent intent = getIntent();

            final String inputEmail= intent.getStringExtra("Key");


        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);

        submitbtn = (Button) findViewById(R.id.Submitbtn);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent1 = new Intent(PasswordChangeActivity.this,
                    MainActivity.class);
            startActivity(intent1);
            finish();
        }

        // Register Button Click event
        submitbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {



                String email = inputEmail;

                String password = inputPassword.getText().toString();
                String password2 = inputPassword2.getText().toString();

                if (!password.isEmpty() && !password2.isEmpty()) {


                        if (password.length() > 5 && isAlphaNumeric(password) ) {

                            if (password.equals(password2)) {

                                if (isNetworkAvaliable(getApplicationContext())) {changePassword(email, password);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Passwords do not match please re-enter!", Toast.LENGTH_SHORT)
                                        .show();
                            }



                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Passwords has to be greater than five characters and contain letters and numbers!", Toast.LENGTH_SHORT)
                                    .show();
                        }


                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter complete all fields!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void changePassword( final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_changePassword";

        pDialog.setMessage("Submitting...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Password Change Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        // Launch login activity
                        hideDialog();
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                PasswordChangeActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }


                    else {
                        hideDialog();
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    hideDialog();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Password Change Error: " + error.getMessage());
                if(error.getMessage().equals("null")){ Toast.makeText(getApplicationContext(),
                        "Error processing your request. Please try when you have network coverage.", Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "PasswordChange");
                params.put("email", email);
                params.put("password", password);

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

