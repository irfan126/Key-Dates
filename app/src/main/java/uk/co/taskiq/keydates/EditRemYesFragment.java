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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import model.RowSubItem;

/**
 * Created by Administrator on 11/12/2015.
 */
public class EditRemYesFragment extends Fragment {

    SQLiteHandler dbHandler;
    // Log tag
    private static final String TAG = EditRemYesFragment.class.getSimpleName();


    private ProgressDialog pDialog;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String email = null;
    private String catID = null;
    private String USERID = null;
    private String remInt = null;
    private String Category = null;
    private String Reminder = null;
    private String upload = null;
    private String picNameA = null;
    private String picNameB = null;

    TextView UploadText;
    private TextView categoryTitle;
    private TextView categoryDesc;
    private EditText reminderTitle;
    private TextView ActivationDaysTitle;
    private TextView ActDateTitle;
    private TextView DaysDateText;
    private EditText reminderView;
    private TextView DaysExpiryText;
    private EditText reminderExpiry;
    private EditText reminderNotes;
    private String reminderDateString = null;
    private long reminderDate1 = 0;
    private Button btnSave;

    ImageView _ImageViewA;
    ImageView _ImageViewB;
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_IMAGE_REQUEST1 = 2;

    private String imageButton1 = "1";
    private String imageButton2 = "1";

    private String imageStringA = null;
    private String imageStringB = null;

    ImageLoader imageLoader;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        remInt = bundle.getString("remInt");
        catID = bundle.getString("catID");
        Category = bundle.getString("Category");
        Reminder = bundle.getString("Reminder");

        Log.d(TAG, "1234567: " + remInt + " " + catID);

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
        View view =inflater.inflate(R.layout.editremyes_fragment, container, false);

        UploadText = (TextView) view.findViewById(R.id.Upload);

        categoryTitle = (TextView) view.findViewById(R.id.CategoryTitle);

        categoryDesc = (TextView) view.findViewById(R.id.CategoryDesc);

        reminderTitle = (EditText) view.findViewById(R.id.ReminderTitle);

        imageLoader = AppController.getInstance().getImageLoader();

        _ImageViewA = (ImageView) view.findViewById(R.id.imageView1);


        _ImageViewA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }

        });

        _ImageViewA.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //your stuff

                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove image?")
                        .setMessage(
                                "Would you like to remove the selected image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                imageButton1 = "0";
                                //  _ImageViewA.setImageUrl("https://www.taskiq.co.uk/android_login_api/include/upload/app_images/ic_refresh_black_24dp.png", imageLoader);
                                // _ImageViewA.setErrorImageResId(R.mipmap.ic_add_black_24dp);

                                _ImageViewA.setImageResource(R.drawable.add_image_black);


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                return true;
            }
        });


        _ImageViewB = (ImageView) view.findViewById(R.id.imageView2);

        _ImageViewB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST1);




            }

        });


        _ImageViewB.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //your stuff

                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove image?")
                        .setMessage(
                                "Would you like to remove the selected image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                imageButton2 = "0";

                                //      _ImageViewB.setImageUrl("https://www.taskiq.co.uk/android_login_api/include/upload/app_images/ic_refresh_black_24dp.png", imageLoader);
                                //    _ImageViewB.setErrorImageResId(R.mipmap.ic_add_black_24dp);
                                _ImageViewB.setImageResource(R.drawable.add_image_black);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                return true;
            }
        });

        ActDateTitle = (TextView) view.findViewById(R.id.ActivationDays);

        ActivationDaysTitle = (TextView) view.findViewById(R.id.ActivationDateTitle);

        DaysDateText = (TextView) view.findViewById(R.id.DaysRemDateText);

        SpannableString content = new SpannableString(DaysDateText.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        DaysDateText.setText(content);
        DaysDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Days to Reminder");
                alertDialog.setMessage("Enter the number of days you would like the Reminder to appear after the Activation date.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });

        reminderView = (EditText) view.findViewById(R.id.reminderView);


        DaysExpiryText = (TextView) view.findViewById(R.id.DaysRemExpiryText);
        SpannableString content1 = new SpannableString(DaysExpiryText.getText().toString());
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        DaysExpiryText.setText(content1);
        DaysExpiryText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Days to Expiry");
                alertDialog.setMessage("Enter the number of days you would like the Expiry date to be set after the Activation date.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });




        reminderExpiry = (EditText) view.findViewById(R.id.reminderExpiry);


        reminderNotes = (EditText) view.findViewById(R.id.ReminderNotes);

        btnSave = (Button) view.findViewById(R.id.BtnSave);


        // Login button Click Event
        btnSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String act_Rem = reminderView.getText().toString();

                String act_Expiry = reminderExpiry.getText().toString();
                Long act_RemLong = (Long.parseLong(act_Rem));
                Long act_ExpiryLong = (Long.parseLong(act_Expiry));

                if (act_RemLong<7501 &&  act_ExpiryLong<7501) {

                   saveProduct();

                }


                else {
                    Toast.makeText(getActivity(),
                            "For either Reminder or Expiry the maximum number of days you can enter is 7500", Toast.LENGTH_SHORT)
                            .show();
                }

            }

        });

        printData(remInt, catID);

        return view;
    }


    public void printData(String remInt, String catID)  {

        RowSubItem item = dbHandler.fetchReminder(remInt, catID);

        String Category = item.getCategory();
        String catDesc = item.getCatDesc();
        String Reminder = item.getReminder();
        String imageA = item.getImageA();
        String imageB = item.getImageB();
        String ActTitle = item.getActTitle();
        String ActRem = item.getActRem();
        String ActExpiry = item.getActExpiry();
        String ActDays = item.getActDays();
        String remNotes = item.getRemNotes();
        String userID = item.getUserID();
        upload = item.getUpload();

        Long ActDaysLong = (Long.parseLong(ActDays))*1000;

        GregorianCalendar ob = new GregorianCalendar();
        ob.setTimeInMillis(ActDaysLong);


        // access the fields


        SimpleDateFormat month_date = new SimpleDateFormat("MMM");

        // access the fields

        String ActDays_month = month_date.format(ob.getTime());
        int ActDays_day = ob.get(GregorianCalendar.DAY_OF_MONTH);
        int ActDays_year = ob.get(GregorianCalendar.YEAR);
        String ActDays_date = ActDays_day+"-"+ActDays_month+"-"+ActDays_year;

        if (upload.equals("1") && userID.equals(USERID)){

            UploadText.setText("You made this Category available to download."+ "\n" +  "\n" + "Changes made will also update Reminders for users who downloaded the Category.");


        }


        categoryTitle.setText("Category: " + Category);
        categoryDesc.setText("Description: " + catDesc);


        reminderTitle.setText(Reminder);
        imageLoader = AppController.getInstance().getImageLoader();

        if(!imageA.equals("0")){

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imageA);
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
        else { imageButton1 = "0";

            _ImageViewA.setImageResource(R.drawable.no_image_black);
        }

        if(!imageB.equals("0")){

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imageB);
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

        else { imageButton2 = "0";


            _ImageViewB.setImageResource(R.drawable.no_image_black);
        }

        ActivationDaysTitle.setText("Activation title: " +ActTitle);

        ActDateTitle.setText("Activation date: " + ActDays_date);

        reminderView.setText(ActRem);

        reminderExpiry.setText(ActExpiry);

        reminderNotes.setText(remNotes);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  PICK_IMAGE_REQUEST  && null != data) {

            Log.d(TAG, "Image1");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.d(TAG, "Picture Path " + picturePath);
            cursor.close();
            imageButton1 = "2";
            _ImageViewA = (ImageView) getActivity().findViewById(R.id.imageView1);

            Bitmap myImg = BitmapFactory.decodeFile(picturePath);

            _ImageViewA.setImageBitmap(myImg);
            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            long dateLongA = 0;

            try {
                java.util.Date date1 = (java.util.Date) df.parse(date);
                dateLongA = (date1.getTime())/1000;

            } catch (ParseException e) {
                Log.e("log", e.getMessage(), e);
            }


            picNameA = Long.toString(dateLongA) + "A.jpeg";

            Log.d(TAG, "Picture Name A " + picNameA);

            FileOutputStream fos;
            try {
                fos = getActivity().openFileOutput(picNameA, Context.MODE_PRIVATE);
                myImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }
            catch (FileNotFoundException e) {
                Log.d(TAG, "file not found");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.d(TAG, "io exception");
                e.printStackTrace();
            }



        }

        if (requestCode ==  PICK_IMAGE_REQUEST1  && null != data) {

            Log.d(TAG, "Image2");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageButton2 = "2";

            _ImageViewB = (ImageView) getActivity().findViewById(R.id.imageView2);

            Bitmap myImgB = BitmapFactory.decodeFile(picturePath);

            _ImageViewB.setImageBitmap(myImgB);

            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            long dateLongB = 0;

            try {
                java.util.Date date1 = (java.util.Date) df.parse(date);
                dateLongB = (date1.getTime())/1000;

            } catch (ParseException e) {
                Log.e("log", e.getMessage(), e);
            }


            picNameB = Long.toString(dateLongB) + "B.jpeg";

            Log.d(TAG, "Picture Name B " + picNameB);
            FileOutputStream fos;
            try {
                fos = getActivity().openFileOutput(picNameB, Context.MODE_PRIVATE);
                myImgB.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }
            catch (FileNotFoundException e) {
                Log.d(TAG, "file not found");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.d(TAG, "io exception");
                e.printStackTrace();
            }





        }


    }

    public void saveProduct(){

        RowSubItem item = dbHandler.fetchReminder(remInt, catID);

        String imageA = "0";

        if (imageButton1.equals("2")){
            imageA=picNameA;

            String imageAdelete = item.getImageA();

            if(!imageAdelete.equals("0")) {

                getActivity().deleteFile(imageAdelete);
            }


        }

        else if (imageButton1.equals("1")){
            imageA = item.getImageA();
        }

        else if (imageButton1.equals("0")){
            String imageAdelete = item.getImageA();
            getActivity().deleteFile(imageAdelete);
        }

        String imageB = "0";

        if (imageButton2.equals("2")){
            imageB=picNameB;

            String imageBdelete = item.getImageB();

            if(!imageBdelete.equals("0")) {

                getActivity().deleteFile(imageBdelete);

            }
        }

        else if (imageButton2.equals("1")){
            imageB = item.getImageB();
        }

        else if (imageButton2.equals("0")){

            String imageBdelete = item.getImageB();
            getActivity().deleteFile(imageBdelete);
        }

        String actDays = item.getActDays();

        String remTitle = reminderTitle.getText().toString();
        String act_Rem = reminderView.getText().toString();

        String act_Expiry = reminderExpiry.getText().toString();
        String remNotes = reminderNotes.getText().toString();

        Long act_RemLong = (Long.parseLong(act_Rem))*86400;
        Long act_ExpiryLong = (Long.parseLong(act_Expiry))*86400;
        Long act_DaysLong = (Long.parseLong(actDays));
        Long rem_date = act_DaysLong + act_RemLong;
        Long rem_Expiry = act_DaysLong + act_ExpiryLong;

        String red_dateString = Long.toString(rem_date);
        String red_ExpiryString = Long.toString(rem_Expiry);

        String imagePathA = item.getImageA();
        String imagePathB = item.getImageB();

       // Log.d(TAG, "123456: " + rem_date + " " + actDays + " " + act_Rem + " " + act_RemLong + " " + red_dateString + " " + userID);


        dbHandler.editYesReminder(remInt, catID, act_Rem, act_Expiry, remTitle, imageA, imageB, rem_date,rem_Expiry, remNotes);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("selectTab", "1");
        Toast.makeText(getActivity(), "Select + to add more Reminders", Toast.LENGTH_SHORT).show();
        startActivity(intent);





    }


    public void updateReminderYes (final String tag,final String email,final String userID,final String remInt, final String catID,final String act_Rem,final String act_Expiry,
                               final String remTitle,final String imageA,final String imageB,final String imagePathA,final String imagePathB,final String remDate,final String remExpiry,final String remNotes,final String upload){


        String tag_string_req = "req_EditReminderYes";

        pDialog.setMessage("Processing request...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Reminder Yes Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray genreArry = jObj.getJSONArray("order");
                    //   JSONObject result = jObj.getJSONObject("order");
                    // JSONArray genreArry = result.getJSONArray("order");
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.d(TAG, "Create Category JSON Response: " + genreArry.toString());
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

                            dbHandler.replaceCategory(rem_Int, cat_ID, Category, category_Archive, cat_Desc, act_Date, act_Days, act_Rem, act_Expiry, act_Title, Reminder,  rem_Archived,imageA,imageB,  rem_Date, rem_Expiry, rem_Notes, upload, userID, cat_UploadID, rem_UploadID, uploadSum);

                            AppController.getInstance().getRequestQueue().getCache().remove("https://www.taskiq.co.uk/android_login_api/" + imagePathA);
                            AppController.getInstance().getRequestQueue().getCache().remove("https://www.taskiq.co.uk/android_login_api/" + imagePathB);


                            Intent intent = new Intent(getActivity(), MainActivity.class);

                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("selectTab", "1");
                            Toast.makeText(getActivity(), "Changes to the Reminder have been saved", Toast.LENGTH_SHORT).show();
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
                params.put("userID", userID);
                params.put("cat_ID", catID);
                params.put("rem_Int", remInt);
                params.put("act_Rem", act_Rem);
                params.put("act_Expiry", act_Expiry);
                params.put("Reminder", remTitle);
                params.put("imageA",imageA);
                params.put("imageB",imageB);
                params.put("imagePathA",imagePathA);
                params.put("imagePathB",imagePathB);
                params.put("rem_Date", remDate);
                params.put("rem_Expiry", remExpiry);
                params.put("remNotes", remNotes);
                params.put("upload", upload);

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



    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_remedit, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_save:

               saveProduct();


                return true;
          //  case R.id.action_cancel:

            //    return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
