package uk.co.taskiq.keydates;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;

import helper.SQLiteHandler;

/**
 * Created by Administrator on 18/05/2016.
 */
public class AddDownloadActivity extends AppCompatActivity {

        Menu mMenu;
        private TextView userIDView;
        private TextView dateView;
        // Log tag
        private static final String TAG = AddDownloadActivity.class.getSimpleName();
        private SQLiteHandler db;

        private String mSearchText;

        public String getSearchText() {
            return mSearchText;
        }

        public void setSearchText(String searchText) {
            this.mSearchText = searchText;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.adddownload_activity);

            Intent i = getIntent();
            Bundle extras = i.getExtras();

            String selectTab = extras.getString("selectTab");

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add a Category");
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.addTab(tabLayout.newTab().setText("Download"));
            tabLayout.addTab(tabLayout.newTab().setText("Request"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final AddDownPageAdapter adapter = new AddDownPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


            if (selectTab.equals("1")) {
                viewPager.setCurrentItem(1);
            }

            if (selectTab.equals("0")) {
                viewPager.setCurrentItem(0);
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









    }
