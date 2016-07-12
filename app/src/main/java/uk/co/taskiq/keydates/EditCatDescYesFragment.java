package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowCategory;
import model.RowRemItem;

/**
 * Created by Administrator on 30/12/2015.
 */
public class EditCatDescYesFragment extends Fragment {


    // Log tag
    private static final String TAG = EditCatDescYesFragment.class.getSimpleName();

    SQLiteHandler dbHandler;

    TextView UploadText;
    EditText CategoryText;
    EditText catDescText;
    EditText ActivationDateTitleText;
    TextView reminderViewText;
    TextView UploadSumHeaderText;
    EditText UploadSumText;

    public static String DATA_CATEGORY;
    public static String DATA_CATID;
    private Button btnSave;

    private String email = null;
    private String userID = null;
    private String Upload = null;
    private String USERID = null;
    private String catID = null;
    private String Category = null;
    private String reminderDateString = null;
    private long reminderDateLong1 = 0;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        catID = bundle.getString("catID");
        Category = bundle.getString("Category");

        DATA_CATEGORY = Category;
        DATA_CATID = catID;

        View view = inflater.inflate(R.layout.editcatdescyes_fragment, container, false);

        UploadText = (TextView) view.findViewById(R.id.Upload);

        CategoryText = (EditText) view.findViewById(R.id.Category);

        catDescText = (EditText) view.findViewById(R.id.catDesc);

        ActivationDateTitleText  = (EditText) view.findViewById(R.id.ActivationDateTitle);

        reminderViewText = (TextView) view.findViewById(R.id.reminderView);
        reminderViewText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker("reminderView");
            }

        });

        UploadSumHeaderText = (TextView) view.findViewById(R.id.UploadSumHeader);

        UploadSumText = (EditText) view.findViewById(R.id.UploadSum);

        btnSave = (Button) view.findViewById(R.id.BtnSave);

        dbHandler = new SQLiteHandler(getActivity());

        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        printDatabase(catID);

        btnSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                saveProduct();
            }

        });

        return view;
    }

    public void printDatabase(String catID) {

        RowCategory item = dbHandler.fetchCatDesc(catID, "0");

        Upload = item.getUpload();
        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        String actTitle = item.getActTitle();
        String actDays = item.getActDays();
        String UploadSum = item.getUploadSum();
        String catUploadID = item.getCatUploadID();
        userID = item.getUserID();

        Long remViewLong = (Long.parseLong(actDays))*1000;

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(remViewLong);

        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date = day+"-"+month_name+"-"+year;

        if (Upload.equals("1")&& userID.equals(USERID)){

            UploadText.setText("You made this Category available to download."+ "\n" +  "\n" + "Changes you make will also update for users who downloaded the Category.");
            UploadSumHeaderText.setText("Upload summary: ");
            UploadSumText.setText(UploadSum);
        }

        CategoryText.setText(Category);
        catDescText.setText(catDesc);
        ActivationDateTitleText.setText(actTitle);
        reminderViewText.setText(date);
    }

    public void saveProduct(){
        String catDesc = catDescText.getText().toString();
        String Category = CategoryText.getText().toString();
        String UploadSum = UploadSumText.getText().toString();
        String actTitle = ActivationDateTitleText.getText().toString();
        String actDays = reminderViewText.getText().toString();

        SimpleDateFormat sdf  = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            Date date = (Date) sdf.parse(actDays);
            reminderDateLong1 = (date.getTime())/1000;

        } catch (ParseException e) {
            Log.e("log", e.getMessage(), e);
        }
        reminderDateString = Long.toString(reminderDateLong1);

        if (!Category.isEmpty() && !actTitle.isEmpty()) {

            if (!Upload.equals("1")){

                Cursor cursor = dbHandler.fetchAllRemYes(catID);

                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                     //   COLUMN_REMINT, COLUMN_CATID, COLUMN_ACTREM, COLUMN_ACTEXPIRY, COLUMN_REMDATE, COLUMN_REMEXPIRY

                        String remIntYes = cursor.getString(0);
                        String catIDYes = cursor.getString(1);
                        String actRemYes = cursor.getString(2);
                        String actExpiryYes = cursor.getString(3);

                        Long act_RemLong = (Long.parseLong(actRemYes))*86400;
                        Long act_ExpiryLong = (Long.parseLong(actExpiryYes))*86400;

                        Long rem_date = reminderDateLong1 + act_RemLong;
                        Long rem_Expiry = reminderDateLong1 + act_ExpiryLong;

                        dbHandler.editYesDaysReminder(remIntYes, catIDYes, rem_date, rem_Expiry);

                       cursor.moveToNext();
                    }
                }
                cursor.close();

                dbHandler.editCategoryYes(catID,Category, actTitle, reminderDateString);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("selectTab", "1");
                Toast.makeText(getActivity(), "Category amended.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

        }
        else {
            Toast.makeText(getActivity(),
                    "Please enter a Category and Activation Title!", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void updateProduct(final String tag,final String email, final String catID, final String category, final String catDesc,final String actTitle,final String actDays, final String Upload, final String userID, final String UploadSum){

        String tag_string_req = "req_EditCatDesc";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Category Description Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray genreArry = jObj.getJSONArray("order");
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.d(TAG, "EditProduct Response: " + genreArry.toString());
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

                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,act_Rem,act_Expiry, act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            // intent.putExtra("remInt",rem_Int );
                            //intent.putExtra("catID",cat_ID );
                            //intent.putExtra("Category",Category);
                            //intent.putExtra("Reminder", Reminder);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra("selectTab", "1");
                            Toast.makeText(getActivity(), "Category amended.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);

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
                        "Error processing your request. Please try again when you have network coverage.", Toast.LENGTH_SHORT).show();}
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
                params.put("Category", category);
                params.put("cat_Desc", catDesc);
                params.put("actTitle", actTitle);
                params.put("actDays", actDays);
                params.put("Upload", Upload);
                params.put("userID", userID);
                params.put("UploadSum", UploadSum);
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

    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";
    static final String CALENDAR = "calendar";

    private void showDatePicker(String remString) {
        String reminderDate = null;
        reminderDate = reminderViewText.getText().toString();


        SimpleDateFormat sdf  = new SimpleDateFormat("dd-MMM-yyyy");
        long reminderDateLong = 0;
        try {
            java.util.Date date = (java.util.Date) sdf.parse(reminderDate);
            reminderDateLong = date.getTime();
        } catch (ParseException e) {
            Log.e("log", e.getMessage(), e);
        }

        Log.d(TAG, "ReminderDate Long: " + reminderDateLong);

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(reminderDateLong);

        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);

        DatePickerFragment date = new DatePickerFragment();

        Bundle args = new Bundle();

        args.putInt(YEAR, year);
        args.putInt(MONTH, month);
        args.putInt(DAY,day);
        args.putString("remString", remString);
        date.setArguments(args);

        date.show(getFragmentManager(), "Date Picker");
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
