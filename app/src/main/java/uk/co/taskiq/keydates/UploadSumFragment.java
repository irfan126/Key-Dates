package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowUpload;

/**
 * Created by Administrator on 15/11/2015.
 */
public class UploadSumFragment extends Fragment {

    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = UploadSumFragment.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String catID = null;
    private String userID = null;
    private String category = null;
    private String catDesc = null;
    private String uploadSummary = null;
    private ProgressDialog pDialog;
    private TextView categoryTitle;
    private TextView categoryDesc;
    private EditText uploadSum;
    private Button btnUpload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        catID = bundle.getString("cat_ID");
        category = bundle.getString("Category");
        catDesc = bundle.getString("catDesc");
        uploadSummary = bundle.getString("uploadSum");

        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        userID = user.get("userID");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.uploadsum_fragment, container, false);

        categoryTitle = (TextView) view.findViewById(R.id.CategoryTitle);
        categoryTitle.setText("Category Title: " + category);

        categoryDesc = (TextView) view.findViewById(R.id.CategoryDesc);
        categoryDesc.setText("Category Description: " + catDesc);


        uploadSum = (EditText) view.findViewById(R.id.uploadSum);
        if (!uploadSummary.equals("No Summary")) {

            uploadSum.setText( uploadSummary);


        }

        btnUpload = (Button) view.findViewById(R.id.BtnUpload);


        // Login button Click Event
        btnUpload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                uploadSummary = uploadSum.getText().toString();

                if (!uploadSummary.isEmpty()) {

                    if (isNetworkAvaliable(getActivity())) {

                        Cursor cursor = dbHandler.fetchAllRemUpload(catID, userID, "0");


                            JSONArray resultSet     = new JSONArray();

                            cursor.moveToFirst();
                            while (cursor.isAfterLast() == false) {

                                int totalColumn = cursor.getColumnCount();
                                JSONObject rowObject = new JSONObject();

                                for( int i=0 ;  i< totalColumn ; i++ )
                                {
                                    if( cursor.getColumnName(i) != null )
                                    {
                                        try
                                        {
                                            if( cursor.getString(i) != null )
                                            {



                                                if ((cursor.getColumnName(i).equals("imageA") ||cursor.getColumnName(i).equals("imageB")) && !cursor.getString(i).equals("0"))

                                                {Log.d(TAG, "Upload1 Iamge Response: " + cursor.getString(i));

                                                    Bitmap b = null;
                                                    FileInputStream fis;
                                                    try {
                                                        fis = getActivity().openFileInput(cursor.getString(i));
                                                        b = BitmapFactory.decodeStream(fis);
                                                        fis.close();

                                                    }
                                                    catch (FileNotFoundException e) {
                                                        Log.d(TAG, "file not found");
                                                        e.printStackTrace();
                                                    }
                                                    catch (IOException e) {
                                                        Log.d(TAG, "io exception");
                                                        e.printStackTrace();
                                                    }

                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
// Must compress the Image to reduce image size to make upload easy
                                                    b.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                                                    byte[] bb = stream.toByteArray();
// Encode Image to String
                                                   String imageString = Base64.encodeToString(bb, Base64.DEFAULT);

                                                    rowObject.put(cursor.getColumnName(i), imageString);
                                                    rowObject.put(cursor.getColumnName(i)+ "String", cursor.getString(i));

                                                    Log.d(TAG, "Upload1 Iamge Response: " + imageString);
                                                }

                                                else {

                                                    Log.d(TAG, "Upload1 Category Response: " + cursor.getString(i));
                                                    Log.d(TAG, "Upload1 Category Response: " + cursor.getColumnName(i));
                                                    rowObject.put(cursor.getColumnName(i), cursor.getString(i));


                                                }


                                            }
                                            else
                                            {
                                                rowObject.put( cursor.getColumnName(i) ,  "" );
                                            }
                                        }
                                        catch( Exception e )
                                        {
                                            Log.d("TAG_NAME", e.getMessage()  );
                                        }
                                    }
                                }
                                resultSet.put(rowObject);
                                cursor.moveToNext();
                            }
                            cursor.close();

                        Log.d(TAG, "Upload Category Response: " + resultSet.toString());


                        uploadCategory("uploadCategory", email, userID, catID, resultSet.toString(),"1",uploadSummary);
                    }
                    else {
                        Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                    }

                }

                else {
                    Toast.makeText(getActivity(),
                            "Please enter a Upload Summary!", Toast.LENGTH_SHORT)
                            .show();
                }
            }

        });

        return view;

    }

    private void uploadCategory(final String tag, final String email, final String userID, final String CatID,final String resultArray, final String upload, final String uploadSum) {
        // Tag used to cancel the request
        String tag_string_req = "req_UploadCategory";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
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


                        hideDialog();

                        dbHandler.editCategoryUpload(CatID, userID, upload, uploadSum);

                        Intent intent = new Intent(getActivity(), AddCategoryActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("selectTab", "2");
                        Toast.makeText(getActivity(), "Category is now available for other user to download", Toast.LENGTH_SHORT).show();
                        startActivity(intent);

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
                params.put("userID",userID);
                params.put("cat_ID", CatID);
                params.put("resultArray", resultArray);
                params.put("upload",upload);
                params.put("uploadSum",uploadSum);
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
