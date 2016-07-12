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
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
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

import org.json.JSONArray;
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
 * Created by Administrator on 04/09/2015.
 */
public class RemPageFragment extends Fragment {
    private Cursor mCursor;
    SQLiteHandler dbHandler;

    // Log tag
    private static final String TAG = RemPageFragment.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private String remInt;
    private String catID;
    private String Category;
    private String Reminder;
    private String ActDate;
    private String ActDays;
    private String CatUploadID;
    private String UploadID;
    private String Upload;

    private ProgressDialog pDialog;
    private String userID;
    private String email = null;
    private String USERID = null;
    private String CreateAbility = null;


    TextView todaysDateView;
    TextView userIDView;
    TextView UploadView;
    TextView CategoryView;
    TextView catDescView;
    TextView ReminderView;
    TextView reminderView;
    TextView reminderExpiry;
    TextView remNotesView;
    ImageView _ImageViewA;
    ImageView _ImageViewB;
    private String imagePathA;
    private String imagePathB;

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


        Bundle bundle = this.getArguments();
        remInt = bundle.getString("remInt");
        catID = bundle.getString("catID");
        Category = bundle.getString("Category");
        Reminder = bundle.getString("Reminder");
        ActDate = bundle.getString("ActDate");

        Log.d(TAG, "New Category " + catID + " " + remInt);



        DATA_REMINT = remInt;
        DATA_CATID = catID;
        DATA_CATEGORY = Category;
        DATA_REMINDER = Reminder;
        DATA_ACTDATE = ActDate;

        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();


        email = user.get("email");
        USERID = user.get("userID");
        CreateAbility = user.get("CreateAbility");

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        View view = inflater.inflate(R.layout.rempage_fragment, container, false);
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

        dbHandler = new SQLiteHandler(getActivity());


        printData(remInt, catID);


     //   View addButton = view.findViewById(R.id.fabedit);
       // addButton.setOnClickListener(new View.OnClickListener() {
         //   @Override
           // public void onClick(View view) {
         //       if (ActDate.equals("1")) {
           //         Intent intent = new Intent(getActivity(), EditRemYesActivity.class);
             //       intent.putExtra("remInt", remInt);
               //     intent.putExtra("catID", catID);
                 //   intent.putExtra("Category", Category);
                //    intent.putExtra("Reminder", Reminder);
                  //  startActivity(intent);

         //       } else {

           //         Intent intent = new Intent(getActivity(), EditRemActivity.class);
             //       intent.putExtra("remInt", remInt);
               //     intent.putExtra("catID", catID);
                 //   intent.putExtra("Category", Category);
                 //   intent.putExtra("Reminder", Reminder);
                   // startActivity(intent);
               // }

            //}


       // });

        return view;
    }


    public void printData(String remInt, String catID)  {

        RowSubItem item = dbHandler.fetchReminder(remInt, catID);

        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        ActDate = item.getActDate();
        ActDays = item.getActDays();
        String Reminder = item.getReminder();
        imagePathA = item.getImageA();
        imagePathB = item.getImageB();
        String remView = item.getRemDate();
        String remExpiry = item.getRemExpiry();
        String remNotes = item.getRemNotes();
        Upload = item.getUpload();
        userID = item.getUserID();
        CatUploadID = item.getCatUploadID();
        UploadID = item.getUpload();

        Long remViewLong = (Long.parseLong(remView))*1000;
        Long remExpiryLong = (Long.parseLong(remExpiry))*1000;


        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(remViewLong);

        GregorianCalendar ob1 = new GregorianCalendar();
        ob1.setTimeInMillis(remExpiryLong);



        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        SimpleDateFormat day_date = new SimpleDateFormat("EEE");
        String month_name = month_date.format(ob.getTime());
        String day_name = day_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date =day_name+", "+day+"-"+month_name+"-"+year;

        Log.d(TAG, "date123 " + date);

        // access the fields

        String remExpiry_month = month_date.format(ob1.getTime());
        String remExpiry_dayName = day_date.format(ob1.getTime());
        int remExpiry_day = ob1.get(GregorianCalendar.DAY_OF_MONTH);
        int remExpiry_year = ob1.get(GregorianCalendar.YEAR);
        String remExpiry_date = remExpiry_dayName+", "+remExpiry_day+"-"+remExpiry_month+"-"+remExpiry_year;

        Long todaysDate = System.currentTimeMillis();
        GregorianCalendar ob2 = new GregorianCalendar();
        ob1.setTimeInMillis(todaysDate);

        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;

        if(Upload.equals("2")||Upload.equals("3")||Upload.equals("5")){UploadView.setText(userID + " is not providing updates for this reminder.");}
        else if(Upload.equals("1")){UploadView.setText("You made this Category available to download.");}
        else if(Upload.equals("4")){UploadView.setText(userID + " has made changes. Please select the Refresh to update.");}

        else if(Upload.equals("0") && !userID.equals(USERID) && !userID.equals("TaskIQ")){UploadView.setText("Select the Update button to check for changes.");}


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

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(DATA_REMINT, remInt);
        outState.putString(DATA_CATID, catID);
        outState.putString(DATA_CATEGORY, Category);
        outState.putString(DATA_REMINDER,Reminder);
        outState.putString(DATA_ACTDATE, ActDate);
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
        Upload = item.getUpload();
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
        SimpleDateFormat day_date = new SimpleDateFormat("EEE");
        String month_name = month_date.format(ob.getTime());
        String day_name = day_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date =day_name+", "+day+"-"+month_name+"-"+year;

        Log.d(TAG, "date123 " + date);

        // access the fields

        String remExpiry_month = month_date.format(ob1.getTime());
        String remExpiry_dayName = day_date.format(ob1.getTime());
        int remExpiry_day = ob1.get(GregorianCalendar.DAY_OF_MONTH);
        int remExpiry_year = ob1.get(GregorianCalendar.YEAR);
        String remExpiry_date = remExpiry_dayName+", "+remExpiry_day+"-"+remExpiry_month+"-"+remExpiry_year;


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

        else if(Upload.equals("0") && !userID.equals(USERID) && !userID.equals("TaskIQ")){UploadView.setText("Select the Update button to check for changes.");}

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
        reminderView.setText(date);
        reminderExpiry.setText(remExpiry_date);
        remNotesView.setText(remNotes);


    }


    /**
     * function to verify login details in mysql db
     * */
    private void archiveReminder(final String tag, final String email, final String remInt, final String CatID, final String imagePathA, final String imagePathB,final String actDays, final String userID, final String uploadCheck, final String CatUploadID) {
        // Tag used to cancel the request
        String tag_string_req = "req_ArchiveReminder";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Archive Reminder Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (tag.equals("deleteReminder") && !error ){

                        dbHandler.deleteReminder(remInt, CatID);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        // intent.putExtra("remInt",rem_Int );
                        //intent.putExtra("catID",cat_ID );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("selectTab", "0");
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Toast.makeText(getActivity(), "Reminder deleted.", Toast.LENGTH_SHORT).show();
                        hideDialog();
                        startActivity(intent);



                    }
                    // Check for error node in json
                    else if (tag.equals("archiveReminder") && !error) {
                        JSONArray genreArry = jObj.getJSONArray("order");

                        Log.d(TAG, "Archive Product Response: " + genreArry.toString());
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


                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,  act_Rem, act_Expiry,act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID,uploadSum);

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            // intent.putExtra("remInt",rem_Int );
                            //intent.putExtra("catID",cat_ID );
                            //intent.putExtra("Category",Category);
                            //intent.putExtra("Reminder", Reminder);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("selectTab", "0");
                            // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            Toast.makeText(getActivity(), "Reminder archived.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);


                        }

                        hideDialog();
                    }

                    // Check for error node in json
                    else if (tag.equals("updateCat") && !error) {
                        JSONArray genreArry = jObj.getJSONArray("order");

                        dbHandler.deleteCategory(CatID);

                        Log.d(TAG, "Update Category Response: " + genreArry.toString());
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


                          //  dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,  act_Rem, act_Expiry,act_Title, Reminder,  rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID,uploadSum);




                        }


                        hideDialog();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        // intent.putExtra("remInt",rem_Int );
                        //intent.putExtra("catID",cat_ID );
                        //intent.putExtra("Category",Category);
                        //intent.putExtra("Reminder", Reminder);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        intent.putExtra("selectTab", "1");

                        Toast.makeText(getActivity(), "Category updated.", Toast.LENGTH_SHORT).show();
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
                params.put("rem_Int", remInt);
                params.put("cat_ID", CatID);
                params.put("imagePathA", imagePathA);
                params.put("imagePathB", imagePathB);
                params.put("actDays", actDays);
                params.put("updateArchiveRem","1");
                params.put("userID",userID);
                params.put("userID1",USERID);
                params.put("uploadCheck",uploadCheck);
                params.put("catUploadID",CatUploadID);
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


    public void shareRem(){


        RowSubItem item = dbHandler.fetchReminder(remInt, catID);

        String Category = item.getCategory();
        String Reminder = item.getReminder();
        String userID = item.getUserID();

        Intent intent = new Intent(getActivity(),ShareRemActivity.class);
        intent.putExtra("catID",catID);
        intent.putExtra("Category", Category);
        intent.putExtra("remInt", remInt);
        intent.putExtra("Reminder", Reminder);
        intent.putExtra("userID", userID);
        startActivity(intent);

    }


    public void archiveReminder(){

           if (isNetworkAvaliable(getActivity())) {
            if(userID.equals(USERID)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Archive Reminder")
                        .setMessage("Please note the Reminder will be permanently archived."
                                    + "\n" + "\n" + "If you have made the Category available for download you will not be able to provide any updates under this Reminder."
                                    + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // archiveReminder("archiveReminder", email, remInt, catID,"0","0",ActDays, userID,"0",CatUploadID);

                                dbHandler.updateRemArchive(remInt, catID, "1");

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                intent.putExtra("selectTab", "1");

                                Toast.makeText(getActivity(), "Reminder Archived.", Toast.LENGTH_SHORT).show();
                                hideDialog();
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
                        .setTitle("Archive Reminder")
                        .setMessage(
                                "Please note the Reminder will be permanently archived."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              //  archiveReminder("archiveReminder", email, remInt, catID,"0","0",ActDays, userID,"2",CatUploadID);

                                dbHandler.updateRemArchive(remInt, catID, "1");

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                intent.putExtra("selectTab", "1");

                                Toast.makeText(getActivity(), "Reminder Archived.", Toast.LENGTH_SHORT).show();
                                hideDialog();
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
        else {
               Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
        }
    }


    public void deleteReminder(){

            if(userID.equals(USERID)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Reminder")
                        .setMessage(
                                "Please note the Reminder will be permanently deleted."
                                        + "\n" + "\n" + "Also if you have made the Category available for download you will not be able to provide any updates under this Reminder."
                                        + "\n" + "\n" + "Archiving maybe a better option."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // archiveReminder("deleteReminder", email, remInt, catID,imagePathA,imagePathB,"0", userID,"0","0");

                                dbHandler.deleteReminder(remInt, catID);
                                getActivity().deleteFile(imagePathA);
                                getActivity().deleteFile(imagePathB);
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                intent.putExtra("selectTab", "1");

                                Toast.makeText(getActivity(), "Reminder Deleted.", Toast.LENGTH_SHORT).show();
                                hideDialog();
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
                             //   archiveReminder("deleteReminder", email, remInt, catID,imagePathA,imagePathB,"0", userID,"0","2");
                                dbHandler.deleteReminder(remInt, catID);
                                getActivity().deleteFile(imagePathA);
                                getActivity().deleteFile(imagePathB);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                intent.putExtra("selectTab", "1");

                                Toast.makeText(getActivity(), "Reminder Deleted.", Toast.LENGTH_SHORT).show();
                                hideDialog();
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
                                "Please note once you delete the Reminder it will be permanently destroyed."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                              //  archiveReminder("deleteReminder", email, remInt, catID,"0","0","0", userID,"0","2");

                                dbHandler.deleteReminder(remInt, catID);
                                getActivity().deleteFile(imagePathA);
                                getActivity().deleteFile(imagePathB);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                intent.putExtra("selectTab", "1");

                                Toast.makeText(getActivity(), "Reminder Deleted.", Toast.LENGTH_SHORT).show();
                                hideDialog();
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


    public void updateCat(){

        if (isNetworkAvaliable(getActivity())) {

            String remInt = dbHandler.fetchRemInt(catID, userID,CatUploadID);
            Log.d(TAG, "hideSubProduct cursor interger: " + " " + email + " " + remInt + " " + catID + " " + ActDays + " " + userID + " " + CatUploadID);

            archiveReminder("updateCat",email,remInt,catID,"0","0",ActDays,  userID, "0",CatUploadID);


        }
        else {
            Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
        }
    }

    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Options to Edit, Delete, Share, Archive or Renew will vary depending on if you created, downloaded or shared a Reminder and if it was setup using a Activation date or not.\n\n");
        builder.append("Select Renew to automatically Archive and replace the current Reminder. The Renew option is only available for Reminders you created of if a Task iQ Reminder and not setup using a Activation date.\n\n").append(" ");

        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_refresh_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Renew     ").append(" ");
        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_system_update_alt_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Update ");

        new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage(builder)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
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


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        if (userID.equals("TaskIQ") ||userID.equals(USERID)) {
        if (ActDate.equals("1")) {
              inflater.inflate(R.menu.menu_rempageyes, menu);
        }
           else {
                inflater.inflate(R.menu.menu_rempage, menu);
            }
        }
        else {
            if (ActDate.equals("1")||ActDate.equals("2")) {
                inflater.inflate(R.menu.menu_remact, menu);
            }
            else if(Upload.equals("6"))  {
                inflater.inflate(R.menu.menu_rempageuserid, menu);}
            else{
                inflater.inflate(R.menu.menu_rempageupload4, menu);
            }
    }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:



                if (CreateAbility.equals("1")) {

                    if (ActDate.equals("1")) {
                        Intent intent = new Intent(getActivity(), EditRemYesActivity.class);
                        intent.putExtra("remInt", remInt);
                        intent.putExtra("catID", catID);
                        intent.putExtra("Category", Category);
                        intent.putExtra("Reminder", Reminder);
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(getActivity(), EditRemActivity.class);
                        intent.putExtra("remInt", remInt);
                        intent.putExtra("catID", catID);
                        intent.putExtra("Category", Category);
                        intent.putExtra("Reminder", Reminder);
                        startActivity(intent);
                    }

                    return true;

                } else {
                    if (ActDate.equals("1")) {
                        Intent intent = new Intent(getActivity(), EditRemYesActivity.class);
                        intent.putExtra("remInt", remInt);
                        intent.putExtra("catID", catID);
                        intent.putExtra("Category", Category);
                        intent.putExtra("Reminder", Reminder);
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(getActivity(), EditDownRemActivity.class);
                        intent.putExtra("remInt", remInt);
                        intent.putExtra("catID", catID);
                        intent.putExtra("Category", Category);
                        intent.putExtra("Reminder", Reminder);
                        startActivity(intent);
                    }

                    return true;
                }



            case R.id.action_renew:
                // Renew Product
                Log.d(TAG, "string renew: " + Category);

                Intent intent_renew = new Intent(getActivity(), RenewRemActivity.class);
                intent_renew.putExtra("remInt",remInt);
                intent_renew.putExtra("catID", catID);
                intent_renew.putExtra("Category",Category );
                intent_renew.putExtra("Reminder",Reminder );
                startActivity(intent_renew);

                return true;
            case R.id.action_info:
                // Information
                info();
                return true;
            case R.id.action_share:
                shareRem();


                return true;


            case R.id.action_archive:
                archiveReminder();


                return true;

            case R.id.action_delete:

                deleteReminder();

                return true;

            case R.id.action_updateCat:

                updateCat();

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
