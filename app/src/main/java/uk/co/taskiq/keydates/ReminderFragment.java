package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CustomRemListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowRemItem;
import model.RowSubItem;

/**
 * Created by Administrator on 26/08/2015.
 */
public class ReminderFragment extends Fragment {
    SQLiteHandler dbHandler;
    private SimpleCursorAdapter dataAdapter;
    CustomRemListViewAdapter adapterList;

    // Log tag
    private static final String TAG = ReminderFragment.class.getSimpleName();
   // Log.d(TAG, "reminder cursor interger: " +cursor.getString(12));
    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SQLiteDatabase mSampleDb;
    private List<String> mResults;
    private Cursor mCursor;
    private ProgressDialog pDialog;
    TextView todaysDateView;
    // Elements
    private android.widget.ListView ListView;
    private SimpleCursorAdapter mListAdapter;
    private String selectedRemInt = null;
    private String selectedCatID = null;
    private String selectedCategory = null;
    private String selectedReminder= null;
    private String selectedActDate= null;
    private String selectedUserID= null;
    private String selectedImagePathA= null;
    private String selectedImagePathB= null;
    private String Category;
    private String catID;
    private String userID;
    private String catUploadID;
    private String email = null;
    private String USERID = null;
    private String actDate = null;
    List<RowRemItem> rowRemItem;

    public static String DATA_CATEGORY;
    public static String DATA_CATID;
    public static String DATA_USERID;
    public static String DATA_ACTDATE;
    public static String DATA_CATUPLOADID;

    private boolean value1;
    //now on your entire fragment use context rather than getActivity()
    @Override
      public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        catID = bundle.getString("catID");
        Category = bundle.getString("Category");
        userID = bundle.getString("userID");
        actDate = bundle.getString("actDate");
        catUploadID = bundle.getString("catUploadID");
        Log.d(TAG, "oncreateview Reminderlist: " + Category);

        DATA_CATEGORY = Category;
        DATA_CATID = catID;
        DATA_ACTDATE = actDate;
        DATA_USERID = userID;
        DATA_CATUPLOADID = catUploadID;

        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");



        GregorianCalendar ob2 = new GregorianCalendar();
        //  ob1.setTimeInMillis(todaysDate);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;



        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.reminder_fragment, container, false);
        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        todaysDateView.setText(todaysDate_date);
        init(view, catID, Category);


        //registerForContextMenu(view);

        return view;
    }
    private void init(View v,String catID, String Category) {


        Cursor cursor = dbHandler.fetchAllRem(catID,  "0");
        rowRemItem = new ArrayList<RowRemItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                RowRemItem item = new RowRemItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),
                        printDate(cursor.getString(14)),cursor.getInt(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));


                rowRemItem.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.list);


        adapterList = new CustomRemListViewAdapter(getActivity(),R.layout.list_rem, rowRemItem);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowRemItem dataModel = (RowRemItem) parent.getItemAtPosition(position);
                String remInt = dataModel.getRemInt();
                String catID = dataModel.getCatID();
                String Category = dataModel.getCategory();
                String Reminder = dataModel.getReminder();
                String ActDate = dataModel.getActDate();


              //  Toast.makeText(getActivity(), catID, Toast.LENGTH_LONG).show();

              //  Intent intent = new Intent(getActivity(), ReminderActivity.class);
                //intent.putExtra("message", category);
                //startActivity(intent);
                Intent intent = new Intent(getActivity(), RemPageActivity.class);
                intent.putExtra("remInt", remInt);
                intent.putExtra("catID", catID);
                intent.putExtra("Category", Category);
                intent.putExtra("Reminder", Reminder);
                intent.putExtra("ActDate", ActDate);
                startActivity(intent);


            }


        });


    }

    public String printDate (String intDate){

        //   String date;

        String item1 = intDate;
        Long myInt2 = (Long.parseLong(item1))*1000;

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(myInt2);

        // access the fields
        int month = ob.get(GregorianCalendar.MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(ob.getTime());
        int day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int year = ob.get(GregorianCalendar.YEAR);
        // String date = day+"-"+(month+1)+"-"+year;
        String date = day+"-"+month_name+"-"+year;

        return date;

    }


    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        //menu.setHeaderTitle("Select The Action");
        //if (v.getId()==R.id.list) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        //menu.setHeaderTitle(Countries[info.position]);

        if (v.getId() == R.id.list) {
            android.widget.ListView lv = (android.widget.ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            RowRemItem obj = (RowRemItem) lv.getItemAtPosition(acmi.position);

            selectedRemInt = obj.getRemInt().toString();
            selectedCatID = obj.getCatID().toString();
            selectedCategory = obj.getCategory().toString();
            selectedReminder = obj.getReminder().toString();
            selectedImagePathA = obj.getImageA().toString();
            selectedImagePathB = obj.getImageB().toString();
            selectedActDate = obj.getActDate().toString();
            selectedUserID = obj.getUserID().toString();


            if(selectedActDate.equals("1") ||selectedActDate.equals("2")){

                menu.setHeaderTitle(obj.getCategory());
                menu.add(0, v.getId(), 0, "Delete Reminder");

            }

            else {

                if(selectedUserID.equals(USERID)||selectedUserID.equals("TaskIQ")){

                    menu.setHeaderTitle(obj.getReminder());
                    menu.add(0, v.getId(), 0, "Delete Reminder");//groupId, itemId, order, title
                    menu.add(0, v.getId(), 1, "Archive Reminder");//groupId, itemId, order, title
                    menu.add(0, v.getId(), 2, "Show Archived Reminder");
                    menu.add(0, v.getId(), 3, "Share Reminder");

                }

                else {

                    menu.setHeaderTitle(obj.getReminder());
                    menu.add(0, v.getId(), 0, "Delete Reminder");//groupId, itemId, order, title
                    menu.add(0, v.getId(), 1, "Archive Reminder");//groupId, itemId, order, title
                    menu.add(0, v.getId(), 2, "Show Archived Reminder");



                }

            }

        }
    }
    @Override
    public boolean onContextItemSelected (MenuItem item){
        super.onContextItemSelected(item);
        //    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //  int listPosition = info.position;

        //RowItem obj = (RowItem) listView.getItemAtPosition(listPosition);


        if (item.getTitle() == "Archive Reminder") {


            if (isNetworkAvaliable(getActivity())) {
            if(selectedUserID.equals(USERID)) {



                new AlertDialog.Builder(getActivity())
                        .setTitle("Archive Reminder")
                        .setMessage(
                                "Please note the Reminder will be permanently archived."
                                        + "\n" + "\n" + "If you have made the Category available for download you will not be able to provide any updates under this Reminder."

                                        + "\n" + "\n" +"Are you sure want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                archiveReminder("archiveReminder", email, selectedRemInt, selectedCatID,"0","0", selectedUserID, "0");

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
                                archiveReminder("archiveReminder", email, selectedRemInt, selectedCatID,"0","0", selectedUserID,"2");

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

        else if (item.getTitle() == "Show Archived Reminder") {
            Intent intent1 = new Intent(getActivity(), ArchivedRemActivity.class);

            intent1.putExtra("catID", selectedCatID);
            intent1.putExtra("Category", selectedCategory);
            startActivity(intent1);


        } else if (item.getTitle() == "Delete Reminder") {

                            if (isNetworkAvaliable(getActivity())) {   // continue with delete

                                if(selectedUserID.equals(USERID)) {

                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Delete Reminder")
                                            .setMessage(
                                                    "Please note the Reminder will be permanently deleted."
                                                            + "\n" + "\n" + "Also if you have made the Category available for download you will not be able to provide any updates under this Reminder."
                                                            + "\n" + "\n" + "Archiving maybe a better option."
                                                            + "\n" + "\n" +"Are you sure want to continue?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    archiveReminder("deleteReminder", email, selectedRemInt, selectedCatID,selectedImagePathA,selectedImagePathB, selectedUserID,"0");

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
                                                    "Please note the Reminder will be permanently deleted."
                                                            + "\n" + "\n" +"Are you sure want to continue?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    archiveReminder("deleteReminder", email, selectedRemInt, selectedCatID,selectedImagePathA,selectedImagePathB, selectedUserID,"2");

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
                                                    archiveReminder("deleteReminder", email, selectedRemInt, selectedCatID,"0","0", selectedUserID,"2");

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

        else if (item.getTitle() == "Share Reminder") {


            Intent intent = new Intent(getActivity(),ShareRemActivity.class);
            intent.putExtra("catID",selectedCatID);
            intent.putExtra("Category", selectedCategory);
            intent.putExtra("remInt", selectedRemInt);
            intent.putExtra("Reminder", selectedReminder);
            intent.putExtra("userID", userID);
            startActivity(intent);



        }







        else {
            return false;
        }
        return true;
    }

    /**
     * function to verify login details in mysql db
     * */
    private void archiveReminder(final String tag, final String email, final String remInt, final String CatID,final String imagePathA,final String imagePathB, final String userID, final String uploadCheck) {
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
                        Log.d(TAG, "Selected Row deleted Reminder Response: " + remInt);
                        rowRemItem.clear();

                        Cursor cursor = dbHandler.fetchAllRem(catID, "0");
                        // rowSubItems = new ArrayList<RowSubItem>();

                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                Log.d(TAG, "string repopulate Product: " + Category);
                                RowRemItem item = new RowRemItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                        cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),
                                        printDate(cursor.getString(14)),cursor.getInt(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));
                                rowRemItem.add(item);
                                cursor.moveToNext();
                            }
                        }
                        cursor.close();

                        adapterList.notifyDataSetChanged();
                        hideDialog();
                    }
                    // Check for error node in json
                   else if (!error) {
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


                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,  act_Rem, act_Expiry,act_Title, Reminder, rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID,uploadSum);
                            rowRemItem.clear();

                            Cursor cursor = dbHandler.fetchAllRem(cat_ID, "0");
                            // rowSubItems = new ArrayList<RowSubItem>();

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    Log.d(TAG, "Archive repopulate Reminders: " + Category);
                                    RowRemItem item = new RowRemItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                            cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),
                                            printDate(cursor.getString(14)),cursor.getInt(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                                    rowRemItem.add(item);
                                    cursor.moveToNext();
                                }
                            }
                            cursor.close();

                            adapterList.notifyDataSetChanged();
                            hideDialog();
                        }

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
                params.put("rem_Int", remInt);
                params.put("cat_ID", CatID);
                params.put("imagePathA", imagePathA);
                params.put("imagePathB", imagePathB);
                params.put("updateArchiveRem","1");
                params.put("userID",userID);
                params.put("uploadCheck",uploadCheck);
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
        outState.putString(DATA_CATEGORY, Category);
        outState.putString(DATA_CATID,catID);
        outState.putString(DATA_ACTDATE,actDate);
        outState.putString(DATA_USERID,userID);
    }

    @Override
    public void onResume() {
        super.onResume();
        rowRemItem.clear();

        Cursor cursor = dbHandler.fetchAllRem(DATA_CATID, "0");

        // rowSubItems = new ArrayList<RowSubItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "onresume repopulate Reminder: " + DATA_CATEGORY);


                RowRemItem item = new RowRemItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),
                        printDate(cursor.getString(14)),cursor.getInt(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                rowRemItem.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        adapterList.notifyDataSetChanged();
    }

    public void addProduct(){
        RowSubItem item = dbHandler.fetchCategory(catID);

        String catID = item.getCatID();
        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        String actDate = item.getActDate();
        String actDateTitle = item.getActTitle();
        String actDays = item.getActDays();
        String Upload = item.getUpload();
        String catUploadID = item.getCatUploadID();
        String uploadSum = item.getUploadSum();



        if (userID.equals(USERID)){
        if (actDate.equals("0")) {


            Intent intent = new Intent(getActivity(), CreateRemNoActivity.class);
            intent.putExtra("NewCat", "No");
            intent.putExtra("catID", catID);
            intent.putExtra("categoryTitle", Category);
            intent.putExtra("activationDate", actDate);
            intent.putExtra("activationDays", actDays);
            intent.putExtra("activationDateTitle", actDateTitle);
            intent.putExtra("Upload", Upload);
            intent.putExtra("uploadSum", uploadSum);
            startActivity(intent);


        }

        else {

            Intent intent = new Intent(getActivity(), CreateRemYesActivity.class);

            intent.putExtra("catID", catID);
            intent.putExtra("categoryTitle", Category);
            intent.putExtra("categoryDescription", catDesc);
            intent.putExtra("activationDate", actDate);
            intent.putExtra("actDays", actDays);
            intent.putExtra("activationDateTitle", actDateTitle);
            intent.putExtra("Upload", Upload);
            intent.putExtra("uploadSum", uploadSum);
            startActivity(intent);

        }}
else {
            Intent intent = new Intent(getActivity(), LifeIQRemActivity.class);

            intent.putExtra("userID", userID);
            intent.putExtra("catID", catID);
            intent.putExtra("categoryTitle", Category);
            intent.putExtra("categoryDescription", catDesc);
            intent.putExtra("activationDate", actDate);
            intent.putExtra("actDays", actDays);
            intent.putExtra("activationDateTitle", actDateTitle);
            intent.putExtra("catUploadID", catUploadID);
            startActivity(intent);


        }

       }

    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Select a Reminder to see in full or long click for options to Edit, Delete, Share, Archive and Renew.\n\n");
        builder.append("Options will vary depending on if you created, downloaded or shared a Reminder and if it was setup using a Activation date or not.\n\n");
        builder.append("Select + from the menu to add a new Reminder under this Category.\n\n");
        builder.append("You will not beable to add Reminders to Categories which were created by other users.\n\n");

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
        // Inflate the menu; this adds items to the action bar if it is present.

        if (userID.equals("TaskIQ") ||userID.equals(USERID)) {


            if (actDate.equals("1")){
                inflater.inflate(R.menu.menu_reminderactdate, menu);}

            else{     inflater.inflate(R.menu.menu_reminder, menu);}
        }
        else{   if (!actDate.equals("1")||!actDate.equals("2")){
            inflater.inflate(R.menu.menu_archviedrem, menu);}}
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click


            switch (item.getItemId()) {


                case R.id.action_add:
                    // Add a category
                    addProduct();

                    return true;

                case R.id.action_info:
                    // Information
                    info();
                    return true;


                case R.id.action_archivedreminders:
                    // Archived

                    Intent intent_archived = new Intent(getActivity(), ArchivedRemActivity.class);

                    intent_archived.putExtra("catID", catID);
                    intent_archived.putExtra("Category", Category);
                    startActivity(intent_archived);
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
