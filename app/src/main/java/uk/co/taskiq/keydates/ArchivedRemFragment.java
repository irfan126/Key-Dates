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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CustomSubListViewAdapter;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowSubItem;

/**
 * Created by Administrator on 01/10/2015.
 */
public class ArchivedRemFragment extends Fragment {

    SQLiteHandler dbHandler;
    private SimpleCursorAdapter dataAdapter;
    CustomSubListViewAdapter adapterList;

    // Log tag
    private static final String TAG = ArchivedRemFragment.class.getSimpleName();
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
    private String selectedCatID = null;
    private String selectedRemInt = null;
    private String selectedUserID= null;
    private String selectedImagePathA= null;
    private String selectedImagePathB= null;
    private String catID;
    private String Category;
    private String email = null;
    private String USERID = null;
    List<RowSubItem> rowSubItems;

    public static String DATA_CATID;
    public static String DATA_CATEGORY;

    private boolean value1;
    //now on your entire fragment use context rather than getActivity()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        String catID = bundle.getString("catID");
        String Category = bundle.getString("Category");
        Log.d(TAG, "string oncreateview archivedReminder: " + Category + " " + catID);

        DATA_CATID = catID;
        DATA_CATEGORY = Category;

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
        View view =inflater.inflate(R.layout.arcreminder_fragment, container, false);
        todaysDateView = (TextView) view.findViewById(R.id.todaysDate);
        todaysDateView.setText(todaysDate_date);
        init(view, catID, Category);




        return view;
    }



    private void init(View v,String catID, String Category) {

        Cursor cursor = dbHandler.fetchAllReminders(catID, "1");
        rowSubItems = new ArrayList<RowSubItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "string cursor acrhivedProduct: " + Category);
                RowSubItem item = new RowSubItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),
                        cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                Log.d(TAG, "archivedReminder cursor interger: " + cursor.getString(0));

                rowSubItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();


        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.list);


        adapterList = new CustomSubListViewAdapter(getActivity(),R.layout.list_reminder, rowSubItems);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowSubItem dataModel = (RowSubItem) parent.getItemAtPosition(position);
                String remInt = dataModel.getRemInt();
                String CatID = dataModel.getCatID();
                String Category = dataModel.getCategory();
                String Reminder = dataModel.getReminder();
                String ActDate = dataModel.getActDate();

                Log.d(TAG, "string selected archivedProduct: " + dataModel.getReminder());
               // Toast.makeText(getActivity(),dataModel.getReminder(), Toast.LENGTH_SHORT).show();

                //  Intent intent = new Intent(getActivity(), ReminderActivity.class);
                //intent.putExtra("message", category);
                //startActivity(intent);
                Intent intent = new Intent(getActivity(), ArchivePageActivity.class);
                intent.putExtra("remInt", remInt);
                intent.putExtra("catID", CatID);
                intent.putExtra("Category", Category);
                intent.putExtra("Reminder", Reminder);
                intent.putExtra("ActDate", ActDate);




                startActivity(intent);


            }


        });


    }

   @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);


        if (v.getId() == R.id.list) {
            android.widget.ListView lv = (android.widget.ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            RowSubItem obj = (RowSubItem) lv.getItemAtPosition(acmi.position);

            selectedRemInt = obj.getRemInt().toString();
            selectedCatID = obj.getCatID().toString();
            selectedImagePathA = obj.getImageA().toString();
            selectedImagePathB = obj.getImageB().toString();
            selectedUserID = obj.getUserID().toString();
            menu.setHeaderTitle(obj.getReminder());
            menu.add(0, v.getId(), 0, "Delete Archived Reminder");//groupId, itemId, order, title

        }
    }

    @Override
    public boolean onContextItemSelected (MenuItem item){
        super.onContextItemSelected(item);

        if (item.getTitle() == "Delete Archived Reminder") {

                if(selectedUserID.equals(USERID)) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete Reminder")
                            .setMessage(
                                    "Please note the Reminder will be permanently deleted."
                                            + "\n" + "\n" +"Are you sure want to continue?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    dbHandler.deleteReminder(selectedRemInt, selectedCatID);
                                    getActivity().deleteFile(selectedImagePathA);
                                    getActivity().deleteFile(selectedImagePathB);
                                    rowSubItems.clear();
                                    Cursor cursor = dbHandler.fetchAllReminders(selectedCatID, "1");
                                    // rowSubItems = new ArrayList<RowSubItem>();

                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {

                                            RowSubItem item = new RowSubItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                                    cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),
                                                    cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                                            rowSubItems.add(item);
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
                            .setTitle("Delete Reminder")
                            .setMessage(
                                    "Please note the Reminder will be permanently deleted."
                                            + "\n" + "\n" +"Are you sure want to continue?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dbHandler.deleteReminder(selectedRemInt, selectedCatID);
                                    getActivity().deleteFile(selectedImagePathA);
                                    getActivity().deleteFile(selectedImagePathB);
                                    rowSubItems.clear();

                                    Cursor cursor = dbHandler.fetchAllReminders(selectedCatID, "1");
                                    // rowSubItems = new ArrayList<RowSubItem>();

                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {

                                            RowSubItem item = new RowSubItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                                    cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),
                                                    cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                                            rowSubItems.add(item);
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


        } else {
            return false;
        }
        return true;
    }



    /**
     * function to verify login details in mysql db
     * */
    private void undoArchiveReminder(final String tag, final String email, final String remInt, final String CatID,final String imagePathA,final String imagePathB,final String userID, final String uploadCheck) {
        // Tag used to cancel the request
        String tag_string_req = "req_undoArchiveReminder";

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
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    if (tag.equals("deleteReminder") && !error ){

                        dbHandler.deleteReminder(remInt, CatID);
                        Log.d(TAG, "Selected Row deleted Reminder Response: " + remInt);
                        rowSubItems.clear();

                        Cursor cursor = dbHandler.fetchAllReminders(CatID, "1");
                        // rowSubItems = new ArrayList<RowSubItem>();

                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {

                                RowSubItem item = new RowSubItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                        cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),
                                        cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                                rowSubItems.add(item);
                                cursor.moveToNext();
                            }
                        }
                        cursor.close();

                        adapterList.notifyDataSetChanged();
                        hideDialog();

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
                params.put("rem_Int", remInt);
                params.put("cat_ID", CatID);
                params.put("imagePathA", imagePathA);
                params.put("imagePathB", imagePathB);
                params.put("updateArchiveRem","0");
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
    }

    @Override
    public void onResume() {
        super.onResume();
        rowSubItems.clear();

        Cursor cursor = dbHandler.fetchAllReminders(DATA_CATID, "1");
        // rowSubItems = new ArrayList<RowSubItem>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                RowSubItem item = new RowSubItem((String) cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),
                        cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),
                        cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),cursor.getString(19),cursor.getString(20),cursor.getString(21));

                rowSubItems.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        adapterList.notifyDataSetChanged();
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
