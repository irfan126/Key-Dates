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
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CustomListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;
import model.RowItem;

/**
 * Created by Administrator on 15/08/2015.
 */
public class CategoryFragment extends Fragment {
    SQLiteHandler dbHandler;
    private SimpleCursorAdapter dataAdapter;
    CustomListViewAdapter adapterList;

    // Log tag
    private static final String TAG = CategoryFragment.class.getSimpleName();
    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SQLiteDatabase mSampleDb;
    private List<String> mResults;
    private Cursor mCursor;
    private ProgressDialog pDialog;
    // Elements
    private android.widget.ListView ListView;
    private SimpleCursorAdapter mListAdapter;
    private String selectedCatID = null;
    private String selectedCategory = null;
    private String selectedUserID= null;
    private String selectedActDate= null;
    private String email = null;
    private String USERID = null;
    List<RowItem> rowItems;
    private SessionManager session;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // registerForContextMenu(listView);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dbHandler = new SQLiteHandler(getActivity());
        // Set our attributes
        mContext = getActivity();

        mLayoutInflater = inflater;
        // Fetching user details from sqlite
        HashMap<String, String> user = dbHandler.getUserDetails();

        String firstname = user.get("firstname");
        email = user.get("email");
        USERID = user.get("userID");


        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        //pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        // session manager
        session = new SessionManager(getActivity());

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.category_fragment, container, false);
        init(view);


        registerForContextMenu(view);

        return view;



    }

    private void init(View v) {

        Cursor cursor = dbHandler.fetchAllCategories("0");
        rowItems = new ArrayList<RowItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

               RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
               // RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1), cursor.getString(3), "http://api.androidhive.info/json/movies/1.jpg");

               // Log.d(TAG, "string123: "+ cursor.getString(1));
                rowItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        //   for (int i = 0; i < cursor.getCount(); i++) {
        //     RowItem item = new RowItem((String) cursor.getString(1), cursor.getString(2),"http://api.androidhive.info/json/movies/1.jpg");
        //   rowItems.add(item);
        //}

        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.list);


        adapterList = new CustomListViewAdapter(getActivity(),R.layout.list_item, rowItems);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowItem dataModel = (RowItem) parent.getItemAtPosition(position);
                String catID = dataModel.getCatID();
                String category = dataModel.getCategory();
                String userID = dataModel.getUserID();
                String actDate = dataModel.getActDate();


//
                Log.d(TAG, "string: " + dataModel.getCategory() + " " + dataModel.getCatID());
                // Get the cursor, positioned to the corresponding row in the result set
                //  Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                // Get the Product from this row in the database.
                // String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

                Intent intent = new Intent(getActivity(), ReminderActivity.class);
                intent.putExtra("catID", catID);
                intent.putExtra("Category", category);
                intent.putExtra("userID", userID);
                intent.putExtra("actDate", actDate);
                startActivity(intent);

                //   FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //ReminderFragment sp_fragment = new ReminderFragment();

                //Bundle bundle = new Bundle();
//                bundle.putString("key", category);
                //              sp_fragment.setArguments(bundle);

                //  fragmentTransaction.remove(CategoryFragment.this);

//                fragmentTransaction.add(android.R.id.content , sp_fragment);
                //              fragmentTransaction.addToBackStack(null);
                //            fragmentTransaction.commit();

            }


        });


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
                RowItem obj = (RowItem) lv.getItemAtPosition(acmi.position);

                selectedCatID = obj.getCatID().toString();
                selectedCategory = obj.getCategory().toString();
                selectedUserID = obj.getUserID().toString();
                selectedActDate = obj.getActDate().toString();

                if(selectedUserID.equals(USERID)||selectedUserID.equals("TaskIQ")) {

                    if (selectedActDate.equals("1")) {

                        menu.setHeaderTitle(obj.getCategory());
                        menu.add(0, v.getId(), 0, "Edit Category");
                        menu.add(0, v.getId(), 1, "Delete Category");

                    } else {
                        menu.setHeaderTitle(obj.getCategory());
                        menu.add(0, v.getId(), 0, "Edit Category");
                        menu.add(0, v.getId(), 1, "Delete Category");
                        menu.add(0, v.getId(), 2, "Share Category");
                        menu.add(0, v.getId(), 3, "Show Archived Categories");
                        menu.add(0, v.getId(), 4, "Archive Category");
                    }
                }

                else {
                    if (selectedActDate.equals("1") || selectedActDate.equals("2")) {

                        menu.setHeaderTitle(obj.getCategory());
                        menu.add(0, v.getId(), 0, "Delete Category");

                    } else {
                        menu.setHeaderTitle(obj.getCategory());
                        menu.add(0, v.getId(), 0, "Delete Category");//groupId, itemId, order, title
                        menu.add(0, v.getId(), 1, "Show Archived Categories");
                        menu.add(0, v.getId(), 2, "Archive Category");
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

            Log.v(TAG, "Current item:" + selectedCategory + ", " + selectedCatID);

            if (item.getTitle() == "Archive Category") {


                    if(selectedUserID.equals(USERID)) {

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Archive Category")
                                .setMessage(
                                        "Please note all Reminders will be permanently archived."
                                        + "\n" + "\n" + "If you have made the Category available for download you will not be able to provide any future updates."
                                        + "\n" + "\n" + "Archiving the individual Reminders maybe a better option."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dbHandler.CatArchive(selectedCatID, "1");
                                        rowItems.clear();
                                        Cursor cursor = dbHandler.fetchAllCategories("0");
                                        //  rowItems = new ArrayList<RowItem>();

                                        if (cursor.moveToFirst()) {
                                            while (!cursor.isAfterLast()) {
                                                // String data = cursor.getString(cursor.getColumnIndex("data"));
                                                // do what ever you want here

                                                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                                rowItems.add(item);
                                                cursor.moveToNext();
                                            }
                                        }
                                        cursor.close();


                                        adapterList.notifyDataSetChanged();

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
                                .setTitle("Archive Category")
                                .setMessage(
                                        "Please note all Reminders will be permanently archived."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbHandler.CatArchive(selectedCatID, "1");
                                        rowItems.clear();
                                        Cursor cursor = dbHandler.fetchAllCategories("0");
                                        //  rowItems = new ArrayList<RowItem>();

                                        if (cursor.moveToFirst()) {
                                            while (!cursor.isAfterLast()) {
                                                // String data = cursor.getString(cursor.getColumnIndex("data"));
                                                // do what ever you want here

                                                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                                rowItems.add(item);
                                                cursor.moveToNext();
                                            }
                                        }
                                        cursor.close();


                                        adapterList.notifyDataSetChanged();
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



               // Toast.makeText(getActivity(), selectedCategory, Toast.LENGTH_LONG).show();

            } else if (item.getTitle() == "Show Archived Categories") {
                Intent intent = new Intent(getActivity(), ArchiveCategoryActivity.class);
               // intent.putExtra("message", category);
                startActivity(intent);

              //  Toast.makeText(getActivity(), selected, Toast.LENGTH_LONG).show();
            }
            else if (item.getTitle() == "Delete Category") {


                    if(selectedUserID.equals(USERID)) {

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete Category")
                                .setMessage(
                                        "Please note all Reminders (including Archived) will be permanently deleted."
                                        + "\n" + "\n" + "Also if you have made the Category available for download you will not be able to provide any future updates."
                                        + "\n" + "\n" + "Archiving maybe a better option."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dbHandler.deleteCategory(selectedCatID);
                                        rowItems.clear();
                                        Cursor cursor = dbHandler.fetchAllCategories("0");
                                        //  rowItems = new ArrayList<RowItem>();

                                        if (cursor.moveToFirst()) {
                                            while (!cursor.isAfterLast()) {
                                                // String data = cursor.getString(cursor.getColumnIndex("data"));
                                                // do what ever you want here

                                                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                                rowItems.add(item);
                                                cursor.moveToNext();
                                            }
                                        }
                                        cursor.close();


                                        adapterList.notifyDataSetChanged();

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

                    else if(selectedUserID.equals("TaskIQ")){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete Category")
                                .setMessage(
                                        "Please note once you delete the Category all Reminders (including Archived) will be permanently destroyed."
                                                + "\n" + "\n" +"Are you sure want to continue?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dbHandler.deleteCategory(selectedCatID);
                                        rowItems.clear();
                                        Cursor cursor = dbHandler.fetchAllCategories("0");
                                        //  rowItems = new ArrayList<RowItem>();

                                        if (cursor.moveToFirst()) {
                                            while (!cursor.isAfterLast()) {
                                                // String data = cursor.getString(cursor.getColumnIndex("data"));
                                                // do what ever you want here

                                                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                                rowItems.add(item);
                                                cursor.moveToNext();
                                            }
                                        }
                                        cursor.close();


                                        adapterList.notifyDataSetChanged();

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
                                .setTitle("Delete Category")
                                .setMessage(
                                        "Please note once you delete the Category all Reminders (including Archived) will be permanently destroyed and you will not receive any future updates."
                                        + "\n" + "\n" +"Are you sure want to continue?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dbHandler.deleteCategory(selectedCatID);

                                        rowItems.clear();
                                        Cursor cursor = dbHandler.fetchAllCategories("0");
                                        //  rowItems = new ArrayList<RowItem>();

                                        if (cursor.moveToFirst()) {
                                            while (!cursor.isAfterLast()) {
                                                // String data = cursor.getString(cursor.getColumnIndex("data"));
                                                // do what ever you want here

                                                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                                rowItems.add(item);
                                                cursor.moveToNext();
                                            }
                                        }
                                        cursor.close();


                                        adapterList.notifyDataSetChanged();

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


            else if (item.getTitle() == "Edit Category") {

                if (selectedActDate.equals("1")) {

                    Intent intent = new Intent(getActivity(), EditCatDescYesActivity.class);
                    intent.putExtra("catID",selectedCatID);
                    intent.putExtra("Category", selectedCategory);
                    startActivity(intent);


                } else {
                    Intent intent = new Intent(getActivity(), EditCatDescActivity.class);
                    intent.putExtra("catID",selectedCatID);
                    intent.putExtra("Category", selectedCategory);
                    startActivity(intent);
                }


            }

            else if (item.getTitle() == "Share Category") {


                    Intent intent = new Intent(getActivity(),ShareActivity.class);
                    intent.putExtra("catID",selectedCatID);
                    intent.putExtra("Category", selectedCategory);
                    intent.putExtra("userID", selectedUserID);
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
    private void archiveCategory(final String tag,final String email,final String catID, final String userID, final String uploadCheck) {
        // Tag used to cancel the request
        String tag_string_req = "req_ArchiveProduct";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Archive Category Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (tag.equals("deleteCategory") || tag.equals("TaskiQdeleteCategory") && !error ){

                        dbHandler.deleteCategory(catID);
                        Log.d(TAG, "Selected Row deleted Reminder Response: " + catID);
                        rowItems.clear();

                        rowItems.clear();
                        Cursor cursor = dbHandler.fetchAllCategories("0");
                        //  rowItems = new ArrayList<RowItem>();

                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                // String data = cursor.getString(cursor.getColumnIndex("data"));
                                // do what ever you want here

                                RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                rowItems.add(item);
                                cursor.moveToNext();
                            }
                        }
                        cursor.close();


                        adapterList.notifyDataSetChanged();
                        hideDialog();
                    }

                    // Check for error node in json
                   else if (tag.equals("archiveCategory") && !error) {

                        JSONArray genreArry = jObj.getJSONArray("order");
                        Log.d(TAG, "archiveCategory Response: " + genreArry.toString());
                        for (int h = 0; h < genreArry.length(); h++) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite

                            //   JSONObject p = (JSONObject)genreArry.get(h);
                            JSONObject obj = genreArry.getJSONObject(h);

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


                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days,act_Rem,act_Expiry, act_Title, Reminder,  rem_Archived, imageA,imageB,rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID,uploadSum);


                            rowItems.clear();
                            Cursor cursor = dbHandler.fetchAllCategories("0");
                            //  rowItems = new ArrayList<RowItem>();

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    // String data = cursor.getString(cursor.getColumnIndex("data"));
                                    // do what ever you want here

                                    RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                    rowItems.add(item);
                                    cursor.moveToNext();
                                }
                            }
                            cursor.close();


                            adapterList.notifyDataSetChanged();
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
                        "Error processing your request. Please try again when you have network coverage.", Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getActivity(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();}
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", tag);
                params.put("email", email);
                params.put("cat_ID", catID);
                params.put("updateArchiveCat","1");
                params.put("userID",userID);
                params.put("userID1",USERID);
                params.put("uploadCheck",uploadCheck);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




    @Override
    public void onResume() {
        super.onResume();
        rowItems.clear();
        Cursor cursor = dbHandler.fetchAllCategories("0");
        //  rowItems = new ArrayList<RowItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowItem item = new RowItem((String) cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4), cursor.getString(5));
                rowItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        adapterList.notifyDataSetChanged();
    }
    public void refreshReminders(){



        populateReminders(email);

    }

    private void populateReminders(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Refresh all Reminders.");
        showDialog();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Populate Products Response: " + response.toString());


                try {

                    JSONObject jObj = new JSONObject(response);
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");


                    // Check for error node in json
                    if (!error) {
                        JSONArray genreArry = jObj.getJSONArray("order");
                        dbHandler.deleteTable_Products();
                        Log.d(TAG, "genreArry Response: " + genreArry.toString());
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


                    //        dbHandler.addReminders(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder, rem_Archived,imageA,imageB, rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);
                            rowItems.clear();
                            Cursor cursor = dbHandler.fetchAllCategories("0");
                            //  rowItems = new ArrayList<RowItem>();

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    // String data = cursor.getString(cursor.getColumnIndex("data"));
                                    // do what ever you want here

                                    RowItem item = new RowItem((String) cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4), cursor.getString(5));
                                    rowItems.add(item);
                                    cursor.moveToNext();
                                }
                            }
                            cursor.close();


                            adapterList.notifyDataSetChanged();

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
                params.put("tag", "productList");
                params.put("email", email);

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

    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Select a Category to see all Reminders.\n\n");
        builder.append("Long press on a Category will give options to Edit, Delete or Archive.\n\n");
        builder.append("To Add a new Category select + from the menu.\n\n");
        builder.append("To add a Reminder under a existing Category select the Category and then +.\n\n");
        builder.append("Refresh will check for updates for any Categories which you download.\n\n");
        builder.append("When updates are available the Update logo appears in the Reminder Tab. Select the Update logo in the Reminder.\n\n").append(" ");

        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_add_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Add     ").append(" ");
        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_cached_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Refresh     ").append(" ");
        builder.setSpan(new ImageSpan(getActivity(), R.mipmap.ic_system_update_alt_black_24dp),
                builder.length() - 1, builder.length(), 0);
        builder.append(" Update ");

        new AlertDialog.Builder(getActivity())

                .setTitle("Information")


                .setMessage(builder)
                       // "Select a Category to see all Reminders." + "\n" + "\n" + "Long press on a Category will give you options to Delete or Archive."
                         //       + "\n" + "\n" + "Please note you are able to edit Categories under the Details tab."
                           //     + "\n" + "\n" + "To Add a new Category select + from the menu.")


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

        inflater.inflate(R.menu.menu_main, menu);

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

                Intent addCategory = new Intent(getActivity(), AddCategoryActivity.class);
                addCategory.putExtra("selectTab", "0");
                startActivity(addCategory);
                return true;

            case R.id.action_logout:
                // logout User
                logoutUser();
                return true;
            case R.id.action_ContactUs:
                // Contact Us
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);

                startActivity(intent);

                return true;

            case R.id.action_info:
                // Information
                info();
                return true;

            case R.id.action_settings:
                // Settings


                Intent intent1 = new Intent(getActivity(), SettingActivity.class);
                // intent.putExtra("message", category);
                startActivity(intent1);
                return true;
            case R.id.refresh:
                // Settings

                if (isNetworkAvaliable(getActivity())) { refreshReminders();}
                else {
                    Toast.makeText(getActivity(), "Currently there is no network. Please try later.", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.action_archivedCategories:
                // Hidden Products

                Intent intent2 = new Intent(getActivity(), ArchiveCategoryActivity.class);
                // intent.putExtra("message", category);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        dbHandler.deleteUsers();
        dbHandler.deleteTable_Products();

        ((MainActivity)getActivity()).cancelAlarm();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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




