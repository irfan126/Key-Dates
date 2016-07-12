package uk.co.taskiq.keydates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Administrator on 28/08/2015.
 */
public class ReminderActivity extends AppCompatActivity {
    private String catID;
    private String Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_activity);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        catID = extras.getString("catID");
        Category = extras.getString("Category");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(Category);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment) != null) {


            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
// Create a new Fragment to be placed in the activity layout
            ReminderFragment firstFragment = new ReminderFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, firstFragment).commit();
        }


    }
 //   @Override
   // public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_reminder, menu);
       // return true;
    //}

  //  @Override
    //public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
      //  switch (item.getItemId()) {
        //    case R.id.action_add:
                // Add a category
      // ReminderFragment fragment = (ReminderFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
//                fragment.addProduct();

//                return true;

  //          case R.id.action_ContactUs:
    //            // Contact Us
      //          return true;

//            case R.id.action_settings:
                // Settings
  //              return true;

    //        case R.id.action_archivedreminders:
                // Archived

      //          Intent intent_archived = new Intent(getApplicationContext(), ArchivedRemActivity.class);

//                intent_archived.putExtra("catID", catID);
  //              intent_archived.putExtra("Category", Category);
    //            startActivity(intent_archived);
      //          return true;

        //    default:
          //      return super.onOptionsItemSelected(item);
        //}
    //}
}
