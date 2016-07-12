package uk.co.taskiq.keydates;

/**
 * Created by Administrator on 08/04/2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFirstname;
    private EditText inputSurname;
    private EditText inputUserID;
    private EditText inputEmail;
    private TextView EULA;

    private Boolean EULAAccept = false;

    private String countryString = "Select country of residence";
  //1  private EditText inputPassword;
   //1 private EditText inputPassword2;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFirstname = (EditText) findViewById(R.id.firstname);
        inputSurname = (EditText) findViewById(R.id.surname);
        inputUserID = (EditText) findViewById(R.id.userID);
        inputEmail = (EditText) findViewById(R.id.email);
       //1 inputPassword = (EditText) findViewById(R.id.password);
        //1inputPassword2 = (EditText) findViewById(R.id.password2);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        EULA = (TextView) findViewById(R.id.EULA);



        SpannableString content = new SpannableString(EULA.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        EULA.setText(content);
        EULA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PackageInfo versionInfo = getPackageInfo();
                // EULA title
                String title = RegisterActivity.this.getString(R.string.app_name) + " v " + versionInfo.versionName;

                // EULA text
                String message = RegisterActivity.this.getString(R.string.eula_string);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.accept,
                                new Dialog.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialogInterface, int i) {

                                        EULAAccept = true;
                                        // Close dialog
                                        dialogInterface.dismiss();

                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new Dialog.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        EULAAccept = false;


                                    }

                                });
                builder.create().show();
            }

        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Country_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                countryString = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        btnRegister = (Button) findViewById(R.id.btnRegister);
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
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String Firstname = inputFirstname.getText().toString();
                String Surname = inputSurname.getText().toString();
                String UserID = inputUserID.getText().toString();
                String email = inputEmail.getText().toString();
            //1    String password = inputPassword.getText().toString();
             //1   String password2 = inputPassword2.getText().toString();


                if (!Firstname.isEmpty() && !Surname.isEmpty() && !email.isEmpty() && !UserID.isEmpty()) {

                    if (isEmailValid(email)) {

                        if (!UserID.contains(" ")) {

                            if (!countryString.equals("Select country of residence")) {
                                if (EULAAccept.equals(true)) {

                                if (isNetworkAvaliable(getApplicationContext())) {
                                    registerUser(Firstname, Surname, UserID, email, countryString);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                                else {
                                    Toast.makeText(getApplicationContext(), "Please read and accept End User License Agreement.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please select a country of residence.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "In the User ID no spaces are allowed.", Toast.LENGTH_SHORT).show();
                        }



                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter a valid email address!", Toast.LENGTH_SHORT)
                                .show();
                    }




                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_SHORT)
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

    private PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String Firstname,final String Surname,final String UserID, final String email, final String countryString) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String Firstname = user.getString("firstname");
                        String Surname = user.getString("surname");
                        String UserID = user.getString("userID");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                      //  db.addUser(Firstname, Surname, email, uid, created_at);
                        Toast.makeText(getApplicationContext(),
                                "To complete registration please follow the confirmation emailed to you", Toast.LENGTH_SHORT).show();
                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
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
                params.put("tag", "register");
                params.put("firstname", Firstname);
                params.put("surname", Surname);
                params.put("userID", UserID);
                params.put("email", email);
                params.put("countryString", countryString);

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
