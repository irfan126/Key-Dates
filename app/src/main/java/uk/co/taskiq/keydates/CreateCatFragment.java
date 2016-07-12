package uk.co.taskiq.keydates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.CustomCategoryListViewAdapter;
import adapter.CustomListViewAdapter;
import helper.SQLiteHandler;
import helper.SessionManager;
import model.RowCategory;
import model.RowItem;

/**
 * Created by Administrator on 12/10/2015.
 */
public class CreateCatFragment extends Fragment {

    private SimpleCursorAdapter dataAdapter;
    CustomCategoryListViewAdapter adapterList;

    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = CreateCatFragment.class.getSimpleName();

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String USERID = null;
    private ProgressDialog pDialog;
    private EditText categoryTitle;

    private Button btnSubmit;

    private android.widget.ListView ListView;
    private SimpleCursorAdapter mListAdapter;
    List<RowCategory> rowCategory;
    private SessionManager session;
    private String Category;
    public static String DATA_Category;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize all your visual fields
     //   if (savedInstanceState != null) {

       //     categoryTitle.setText(DATA_Category);
         //   categoryDesc.setText(DATA_CatDesc);

           // activationDate.setSelection(savedInstanceState.getInt(DATA_ActDate, 0));
            //activationDateTitle.setText(DATA_ActDateTitle);
       // }
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

        // Let's inflate & return the view
        View view =inflater.inflate(R.layout.createcat_fragment, container, false);
        init(view);

        categoryTitle = (EditText) view.findViewById(R.id.CategoryTitle);




        btnSubmit = (Button) view.findViewById(R.id.BtnSubmit);

        // Login button Click Event
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Category = categoryTitle.getText().toString();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (!Category.isEmpty()) {

                        Intent intent = new Intent(getActivity(), CreateRemNoActivity.class);

                        intent.putExtra("NewCat", "Yes");
                        intent.putExtra("categoryTitle", Category);
                        intent.putExtra("catUploadID", 0);
                        startActivity(intent);
                }

                    else {
                        Toast.makeText(getActivity(),
                                "Please enter a Category Title!", Toast.LENGTH_SHORT)
                                .show();
                    }

            }

        });

        return view;

    }

    private void init(View v) {

        Cursor cursor = dbHandler.fetchUserCategories("0",USERID );
        rowCategory = new ArrayList<RowCategory>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // String data = cursor.getString(cursor.getColumnIndex("data"));
                // do what ever you want here

                RowCategory item = new RowCategory((String)cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10));
                // RowItem item = new RowItem((String)cursor.getString(0), cursor.getString(1), cursor.getString(3), "http://api.androidhive.info/json/movies/1.jpg");

                // Log.d(TAG, "string123: "+ cursor.getString(1));
                rowCategory.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        //   for (int i = 0; i < cursor.getCount(); i++) {
        //     RowItem item = new RowItem((String) cursor.getString(1), cursor.getString(2),"http://api.androidhive.info/json/movies/1.jpg");
        //   rowItems.add(item);
        //}

        android.widget.ListView listView = (android.widget.ListView) v.findViewById(R.id.list);


        adapterList = new CustomCategoryListViewAdapter(getActivity(),R.layout.list_item, rowCategory);
        listView.setAdapter(adapterList);

        registerForContextMenu(listView);


        //Called when product is selected in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                RowCategory dataModel = (RowCategory) parent.getItemAtPosition(position);
                String catID = dataModel.getCatID();
                String category = dataModel.getCategory();
                String userID = dataModel.getUserID();
                String actDate = dataModel.getActDate();
                String actDays = dataModel.getActDays();
                String actTitle = dataModel.getActTitle();
                String upload = dataModel.getUpload();
                String uploadSum = dataModel.getUploadSum();
                String catUploadID = dataModel.getCatUploadID();
                Log.d(TAG, "actDays: "+ actDays);

                    if (actDate.equals("0")) {

                        Intent intent = new Intent(getActivity(), CreateRemNoActivity.class);

                        intent.putExtra("NewCat", "No");
                        intent.putExtra("catID", catID);
                        intent.putExtra("categoryTitle", category);
                        intent.putExtra("activationDate", actDate);
                        intent.putExtra("activationDays", actDays);
                        intent.putExtra("activationDateTitle", actTitle);
                        intent.putExtra("CATUPLOADID", 0);
                        intent.putExtra("Upload", upload);
                        intent.putExtra("uploadSum", uploadSum);
                        intent.putExtra("catUploadID", catUploadID);

                        startActivity(intent);
                    }
                else{

                        Intent intent = new Intent(getActivity(), CreateRemYesActivity.class);
                        intent.putExtra("NewCat", "No");
                        intent.putExtra("catID", catID);
                        intent.putExtra("categoryTitle", category);
                        intent.putExtra("activationDate", actDate);
                        intent.putExtra("activationDays", actDays);
                        intent.putExtra("activationDateTitle", actTitle);
                        intent.putExtra("CATUPLOADID", 0);
                        intent.putExtra("Upload", upload);
                        intent.putExtra("uploadSum", uploadSum);
                        intent.putExtra("catUploadID", catUploadID);
                        startActivity(intent);


                    }



            }


        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
      //  outState.putString(DATA_Category, Category);
       // outState.putString(DATA_CatDesc,catDesc);
       // outState.putInt(DATA_ActDate, activationDate.getSelectedItemPosition());
       // outState.putString(DATA_ActDateTitle,actDateTitle);
    }

    @Override
    public void onResume() {
        super.onResume();


    }
    public void info(){

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("To add a reminder under a existing Category on the homepage, select the Category Tab, the Category and then +.\n\n");
        builder.append("Activation date will allow you to set Reminders x number of days from a given date. It is mainly used for Categories you intend to Upload.\n\n");
        builder.append("For example if you create a Category to Upload which sets Reminders X days after a date another Task iQ user sets then you will need to use the Activation date and title.\n\n");
        builder.append("Note if you create a Category using the Activation date the Reminders will not show in your Reminders Tab in the homepage. To view the Reminders select the Category from the Category Tab.\n\n");


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

        inflater.inflate(R.menu.menu_addcat, menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_info:
                // Information
                info();
                return true;


            case R.id.action_actDate:
                // Activation date reminders

                Intent intent = new Intent(getActivity(), ActDateActivity.class);
                intent.putExtra("NewCat", "Yes");
                intent.putExtra("catUploadID", 0);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
