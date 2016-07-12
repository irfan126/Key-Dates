package uk.co.taskiq.keydates;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import helper.SQLiteHandler;
import helper.SessionManager;
import model.RowItem;


public class MainActivity extends AppCompatActivity implements
        OnItemClickListener {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private AlarmManager alarmMgr1;
    private PendingIntent alarmIntent1;

    private ProgressDialog pDialog;
    String tag_string_req = "req_listview";
    ListView listView;
    List<RowItem> rowItems;

    private String selectTab = "0";
    public Context mContext;
    private TextView userIDView;
    private TextView dateView;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;

    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // session manager
        session = new SessionManager(getApplicationContext());



        Intent i = getIntent();
        Bundle extras = i.getExtras();
if (extras != null){
        selectTab = extras.getString("selectTab");}

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
       alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        long _alarm = 0;
        Calendar now = Calendar.getInstance();
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 03);

        if(calendar.getTimeInMillis() < now.getTimeInMillis())
        { _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);

           }
        else
        {  _alarm = calendar.getTimeInMillis();}

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, _alarm,
              AlarmManager.INTERVAL_DAY, alarmIntent);

   
        alarmMgr1 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
       Intent intent1 = new Intent(this, AlarmReciever1.class);
        alarmIntent1 = PendingIntent.getBroadcast(this, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        long _alarm1 = 0;
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());
        calendar1.set(Calendar.HOUR_OF_DAY, 06);
        calendar1.set(Calendar.MINUTE, 30);

        if(calendar1.getTimeInMillis() < now.getTimeInMillis())
        { _alarm1 = calendar1.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
          }
        else
        {  _alarm1 = calendar1.getTimeInMillis();}


// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr1.setRepeating(AlarmManager.RTC_WAKEUP, _alarm1,
               AlarmManager.INTERVAL_DAY, alarmIntent1);

      //  Intent intent1 = new Intent(MainActivity.this, AlarmReciever1.class);
      //  PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
      //  AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
      //  am.cancel(pendingIntent);
      //  am.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Reminders"));
        tabLayout.addTab(tabLayout.newTab().setText("Category"));
     //   tabLayout.addTab(tabLayout.newTab().setText("Details"));
       // tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

     //   if (selectTab.equals("2")) { viewPager.setCurrentItem(2); }

        if (selectTab.equals("1")) {
            viewPager.setCurrentItem(1);
        }




        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());






            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



            userIDView = (TextView) findViewById(R.id.userID);
          dateView = (TextView) findViewById(R.id.todaysDate);

            // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());

            // session manager
            session = new SessionManager(getApplicationContext());

            if (!session.isLoggedIn()) {
                logoutUser();
            }

            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            String firstname = user.get("firstname");
            String email = user.get("email");
            String userID = user.get("userID");

        Long todaysDate = System.currentTimeMillis();
        GregorianCalendar ob2 = new GregorianCalendar();
      //  ob1.setTimeInMillis(todaysDate);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;

            // Displaying the user details on the screen
          userIDView.setText("Hello "+userID);
           dateView.setText(todaysDate_date);









        }
   public void cancelAlarm() {

            alarmMgr.cancel(alarmIntent);



            alarmMgr1.cancel(alarmIntent1);



    }

  //  @Override
   // protected void onSavedInstanceState(Bundle outState) {
     //   super.onSaveInstanceState(outState);

//        outState.putStringArrayList("todoItemTag",orowItems);//it would be advised to make the tags a static final String
  //  }


    @Override
    public void onResume(){
        super.onResume();

        dateView = (TextView) findViewById(R.id.todaysDate);

        Long todaysDate = System.currentTimeMillis();
        GregorianCalendar ob2 = new GregorianCalendar();
        //  ob1.setTimeInMillis(todaysDate);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String todaysDate_month = month_date.format(ob2.getTime());
        int todaysDate_day = ob2.get(GregorianCalendar.DAY_OF_MONTH);
        int todaysDate_year = ob2.get(GregorianCalendar.YEAR);
        String todaysDate_date = todaysDate_day+"-"+todaysDate_month+"-"+todaysDate_year;

        // Displaying the user details on the screen

        dateView.setText(todaysDate_date);

    }

  @Override
protected void onSaveInstanceState(Bundle outState) {

       super.onSaveInstanceState(outState);
       ArrayList<RowItem> rowItems = new ArrayList<RowItem>();


       outState.putParcelableArrayList("savedList", rowItems);
        Log.d(TAG, "state saved");
        Log.d(TAG, rowItems.toString());



    }

   @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);


       savedInstanceState.getParcelableArrayList("savedList");



    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                      long id) {
 Toast toast = Toast.makeText(getApplicationContext(), "Item " + (position + 1) + ": " + rowItems.get(position), Toast.LENGTH_SHORT);
   toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
 toast.show();
 }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        db.deleteTable_Products();

        // Launching the login activity

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



}
