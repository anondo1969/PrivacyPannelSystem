package app.android.tanzi.com.privacypannel3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Arrays;

public class SpecialPermissionApplicationNamesActivity extends AppCompatActivity {

    //String[] dummy = {"the","la", "Blau","khau"};

    ApplicationViewController data_source = new ApplicationViewController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_permission_application_names);

        //load application names here

        Intent intent = getIntent();
        final String permissionName = intent.getExtras().getString("PName");

        String[] application_list = data_source.retrieveSpecialPermissionUsedAppsInformation(permissionName);

        Arrays.sort(application_list);

        SpecialPermissionApplicationNamesAdapter specialPermissionApplicationNamesAdapter = new SpecialPermissionApplicationNamesAdapter(this, application_list);

        ListView locationListView = (ListView) findViewById(R.id.special_permission_application_list_view);

        locationListView.setAdapter(specialPermissionApplicationNamesAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
