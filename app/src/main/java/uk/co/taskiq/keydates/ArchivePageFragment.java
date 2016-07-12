package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowSubItem;

/**
 * Created by Administrator on 01/10/2015.
 */
public class ArchivePageFragment extends Fragment {

    // Log tag
    private static final String TAG = ArchivePageFragment.class.getSimpleName();

    private Cursor mCursor;
    SQLiteHandler dbHandler;


    private String email = null;
    private String USERID = null;
    private String userID= null;

    private String remInt;
    private String catID;
    private String Category;
    private String Reminder;
    private String ActDate;

    private String myCategory;
    private String myProduct;
    private String prd_ID;
    private ProgressDialog pDialog;

    TextView todaysDateView;
    TextView userIDView;
    TextView UploadView;
    TextView CategoryView;
    TextView catDescView;
    TextView ReminderView;
    TextView reminderView;
    TextView reminderExpiry;
    TextView remNotesView;
    TextView undoArchive;


    ImageView _ImageViewA;
    ImageView _ImageViewB;
    private String imagePathA;
    private String imagePathB;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    List<RowSubItem> rowSubItems;

    public static String DATA_REMINT;
    public static String DATA_CATID;
    public static String DATA_CATEGORY;
    public static String DATA_REMINDER;
    public static String DATA_ACTDATE;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        dbHandler = new SQLiteHandler(getActivity());
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");

        Bundle bundle = this.getArguments();
        remInt = bundle.getString("remInt");
        catID = bundle.getString("catID");
        Category = bundle.getString("Category");
        Reminder = bundle.getString("Reminder");
        ActDate = bundle.getString("ActDate");

        DATA_REMINT = remInt;
        DATA_CATID = catID;
        DATA_CATEGORY = Category;
        DATA_REMINDER = Reminder;
        DATA_ACTDATE = ActDate;


        View view = inflater.inflate(R.layout.arcpage_fragment, container, false);

        registerForContextMenu(view);

        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        userIDView = (TextView) view.findViewById(R.id.userID);
        UploadView = (TextView) view.findViewById(R.id.Upload);
        CategoryView = (TextView) view.findViewById(R.id.Category);
        catDescView = (TextView) view.findViewById(R.id.catDesc);
        ReminderView = (TextView) view.findViewById(R.id.Reminder);


        _ImageViewA = (ImageView) view.findViewById(R.id.imageView1);

        _ImageViewA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayImageActivity.class);
                intent.putExtra("imagePath", imagePathA);

                startActivity(intent);
            }
        });

        _ImageViewB = (ImageView) view.findViewById(R.id.imageView2);

        _ImageViewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayImageActivity.class);
                intent.putExtra("imagePath", imagePathB);

                startActivity(intent);

            }
        });

        reminderView = (TextView) view.findViewById(R.id.reminderView);

        reminderExpiry = (TextView) view.findViewById(R.id.reminderExpiry);
        remNotesView = (TextView) view.findViewById(R.id.remNotes);

        undoArchive = (TextView) view.findViewById(R.id.undoArchive);

        printData(remInt, catID);

        SpannableString content = new SpannableString("Archived Reminder");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        undoArchive.setText(content);



        dbHandler = new SQLiteHandler(getActivity());


        printData(remInt, catID);


        return view;
    }

    public void printData(String remInt, String catID) {

        RowSubItem item = dbHandler.fetchReminder(remInt, catID);

        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        String Reminder = item.getReminder();
        String remView = item.getRemDate();
        imagePathA = item.getImageA();
        imagePathB = item.getImageB();
        String remExpiry = item.getRemExpiry();
        String remNotes = item.getRemNotes();
        String Upload = item.getUpload();
        userID = item.getUserID();

        Long remViewLong = (Long.parseLong(remView))*1000;
        Long remExpiryLong = (Long.parseLong(remExpiry))*1000;

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(remViewLong);

        GregorianCalendar ob1 = new GregorianCalendar();
        ob1.setTimeInMillis(remExpiryLong);

        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date = day+"-"+month_name+"-"+year;

        // access the fields

        String remExpiry_month = month_date.format(ob1.getTime());
        int remExpiry_day = ob1.get(GregorianCalendar.DAY_OF_MONTH);
        int remExpiry_year = ob1.get(GregorianCalendar.YEAR);
        String remExpiry_date = remExpiry_day+"-"+remExpiry_month+"-"+remExpiry_year;

        Long todaysDate = System.currentTimeMillis();
        GregorianCalendar ob2 = new GregorianCalendar();
        ob1.setTimeInMillis(todaysDate);

        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;



        if(Upload.equals("2")||Upload.equals("3")||Upload.equals("5")){UploadView.setText(userID + " is not providing updates for this reminder.");}
        else if(Upload.equals("1")){UploadView.setText("You made this Category available to download.");}
        else if(Upload.equals("4")){UploadView.setText(userID + " has made changes. Please select the Update.");}





        todaysDateView.setText(todaysDate_date);
        userIDView.setText("Created by: "+userID);
        CategoryView.setText(Category);
        catDescView.setText(catDesc);
        ReminderView.setText(Reminder);

        imageLoader = AppController.getInstance().getImageLoader();

        if(!imagePathA.equals("0")){
            Log.d(TAG, "file" + imagePathA);

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imagePathA);
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

            _ImageViewA.setImageBitmap(b);
        }
        else {
            _ImageViewA.setImageResource(R.drawable.no_image_black);}

        if(!imagePathB.equals("0")){

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imagePathB);
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

            _ImageViewB.setImageBitmap(b);
        }

        else {
            _ImageViewB.setImageResource(R.drawable.no_image_black);}



        reminderView.setText(date);
        reminderExpiry.setText(remExpiry_date);
        remNotesView.setText(remNotes);
    }

    /**
     * function to verify login details in mysql db
     * */

    private void deleteReminder(final String tag,final String email, final String remInt, final String CatID,final String imagePathA, final String imagePathB, final String userID, final String upload) {
        // Tag used to cancel the request
        String tag_string_req = "req_ArchiveReminder";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "undo Archive Reminder Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                   // JSONArray genreArry = jObj.getJSONArray("order");

                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (tag.equals("deleteReminder") && !error ){

                        dbHandler.deleteReminder(remInt, CatID);
                        Log.d(TAG, "Selected Row deleted Reminder Response: " + remInt);

                        hideDialog();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        // intent.putExtra("remInt",rem_Int );
                        //intent.putExtra("catID",cat_ID );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Toast.makeText(getActivity(), "Archived Reminder deleted", Toast.LENGTH_SHORT).show();
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
                params.put("rem_Int", remInt);
                params.put("cat_ID", CatID);
                params.put("imagePathA", imagePathA);
                params.put("imagePathB", imagePathB);
                params.put("updateArchiveRem","0");
                params.put("userID",userID);
                params.put("uploadCheck",upload);
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
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(DATA_REMINT, remInt);
        outState.putString(DATA_CATID, catID);
        outState.putString(DATA_CATEGORY,Category);
        outState.putString(DATA_REMINDER,Reminder);
        outState.putString(DATA_ACTDATE,ActDate);
    }

    @Override
    public void onResume() {
        super.onResume();
        RowSubItem item = dbHandler.fetchReminder(DATA_REMINT, DATA_CATID);

        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        String Reminder = item.getReminder();
        imagePathA = item.getImageA();
        imagePathB = item.getImageB();
        String remView = item.getRemDate();
        String remExpiry = item.getRemExpiry();
        String remNotes = item.getRemNotes();
        String Upload = item.getUpload();
        userID = item.getUserID();

        Long remViewLong = (Long.parseLong(remView))*1000;
        Long remExpiryLong = (Long.parseLong(remExpiry))*1000;

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(remViewLong);

        GregorianCalendar ob1 = new GregorianCalendar();
        ob1.setTimeInMillis(remExpiryLong);


        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date = day+"-"+month_name+"-"+year;

        // access the fields

        String remExpiry_month = month_date.format(ob1.getTime());
        int remExpiry_day = ob1.get(GregorianCalendar.DAY_OF_MONTH);
        int remExpiry_year = ob1.get(GregorianCalendar.YEAR);
        String remExpiry_date = remExpiry_day+"-"+remExpiry_month+"-"+remExpiry_year;

        if(Upload.equals("2")||Upload.equals("3")||Upload.equals("5")){UploadView.setText(userID + " is not providing updates for this reminder.");}
        else if(Upload.equals("1")){UploadView.setText("You made this Category available to download.");}

        else if(Upload.equals("4")){UploadView.setText(userID + " has made changes. Please select the Refresh to update.");}

        CategoryView.setText(Category);
        catDescView.setText(catDesc);
        ReminderView.setText(Reminder);
        reminderView.setText(date);
        reminderView.setText(date);
        reminderExpiry.setText(remExpiry_date);
        remNotesView.setText(remNotes);

        imageLoader = AppController.getInstance().getImageLoader();

        if(!imagePathA.equals("0")){

            Log.d(TAG, "file" + imagePathA);

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imagePathA);
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

            _ImageViewA.setImageBitmap(b);
        }
        else {
            _ImageViewA.setImageResource(R.drawable.no_image_black);}

        if(!imagePathB.equals("0")){

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imagePathB);
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

            _ImageViewB.setImageBitmap(b);
        }

        else {
            _ImageViewB.setImageResource(R.drawable.no_image_black);}




    }

    public void deleteReminder(){


            if(userID.equals(USERID)) {

                         new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Archived Reminder")
                        .setMessage(
                                "Please note the Reminder will be permanently deleted."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dbHandler.deleteReminder(remInt, catID);
                                getActivity().deleteFile(imagePathA);
                                getActivity().deleteFile(imagePathB);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                // intent.putExtra("remInt",rem_Int );
                                //intent.putExtra("catID",cat_ID );
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Toast.makeText(getActivity(), "Archived Reminder deleted", Toast.LENGTH_SHORT).show();
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
            else if (userID.equals("TaskIQ")) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Reminder")
                        .setMessage(
                                "Please note once you delete the Reminder it will be permanently destroyed."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dbHandler.deleteReminder(remInt, catID);
                                getActivity().deleteFile(imagePathA);
                                getActivity().deleteFile(imagePathB);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                // intent.putExtra("remInt",rem_Int );
                                //intent.putExtra("catID",cat_ID );
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Toast.makeText(getActivity(), "Archived Reminder deleted", Toast.LENGTH_SHORT).show();
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


            }
            else{

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Reminder")
                        .setMessage(
                                "Please note the Reminder will be permanently deleted."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dbHandler.deleteReminder(remInt, catID);
                                getActivity().deleteFile(imagePathA);
                                getActivity().deleteFile(imagePathB);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                // intent.putExtra("remInt",rem_Int );
                                //intent.putExtra("catID",cat_ID );
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Toast.makeText(getActivity(), "Archived Reminder deleted", Toast.LENGTH_SHORT).show();
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

            }





    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.menu_arcpage, menu);

          }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.action_delete:

                deleteReminder();
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
