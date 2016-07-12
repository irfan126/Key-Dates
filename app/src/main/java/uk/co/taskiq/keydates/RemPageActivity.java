package uk.co.taskiq.keydates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Administrator on 04/09/2015.
 */
public class RemPageActivity extends AppCompatActivity {

    private String remInt = null;
    private String catID = null;
    private String Category = null;
    private String Reminder = null;

    // Log tag
    private static final String TAG = RemPageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rempage_activity);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        remInt = extras.getString("remInt");
        catID = extras.getString("catID");
        Category = extras.getString("Category");
        Reminder = extras.getString("Reminder");
        String toolbarTitle = Category +" - "+Reminder;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(toolbarTitle);
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
            RemPageFragment firstFragment = new RemPageFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, firstFragment).commit();
        }


    }

  //1  @Override
   //1 public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //1  getMenuInflater().inflate(R.menu.menu_rempage, menu);
       //1 return true;
  //1  }

    //1@Override
    //1public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Take appropriate action for each action item click
       //1 switch (item.getItemId()) {
          //1  case R.id.action_edit:
                // Edit Product
             //1   Log.d(TAG, "string: " + Category);

                //1Intent intent = new Intent(getApplicationContext(), EditRemActivity.class);
                //1intent.putExtra("remInt",remInt);
                //1intent.putExtra("catID", catID);
                //1intent.putExtra("Category",Category );
                //1intent.putExtra("Reminder",Reminder );

                //1startActivity(intent);

                //1return true;

            //1case R.id.action_renew:
                // Renew Product
               //1 Log.d(TAG, "string renew: " + Category);

                //1Intent intent_renew = new Intent(getApplicationContext(), RenewRemActivity.class);
                //1intent_renew.putExtra("remInt",remInt);
                //1intent_renew.putExtra("catID", catID);
                //1intent_renew.putExtra("Category",Category );
                //1intent_renew.putExtra("Reminder",Reminder );
                //1startActivity(intent_renew);

                //1return true;


            //1default:
               //1 return super.onOptionsItemSelected(item);
       //1 }
    //1}



}
