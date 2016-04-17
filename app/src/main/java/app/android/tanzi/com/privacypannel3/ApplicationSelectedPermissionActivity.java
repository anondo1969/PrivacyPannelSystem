package app.android.tanzi.com.privacypannel3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ApplicationSelectedPermissionActivity extends AppCompatActivity {

    Button viewPermission;
    ApplicationViewController data_source = new ApplicationViewController(this);

    //String[] specialPermissionNames = {"the","la", "Blau","khau"};


    ArrayList<Boolean> checkValueList = new ArrayList <Boolean>();

    ApplicationSelectedPermissionAdapter applicationSelectedPermissionAdapter;

    ArrayList<String> savedCheckValueList;


    int count = 4;
    int pos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_selected_permission);

        //এই ভেলু গুলা কোনো একটা মেথড থেকে আনতে হবে।

        //***************************************************************
        String[] specialPermissionNames = data_source.loadSpecialPermissionNames();
        count = specialPermissionNames.length;
        for (int i = 0; i < count; i++) {
            Log.d("Values", Integer.toString(i)+" "+ specialPermissionNames[i]);
        }

        //**************************************************************

        //initialize all the values
        //**************************************************************

        if (getPreferences(MODE_PRIVATE).getBoolean("execute_it_once", true)) {

            //for executing only once
            getPreferences(MODE_PRIVATE).edit().putBoolean("execute_it_once", false).commit();

            for (int i = 0; i < count; i++) {
                checkValueList.add(false);
            }
        }
        else{

            //retrieve data from shared preference and assigned it to location arraylist

            savedCheckValueList = getStringArrayPref(this, "SpecialPermissions");

            for (pos = 0; pos < savedCheckValueList.size(); pos++) {
                //checkValueList.set(i, "false");
                checkValueList.add(false);
            }
            pos=-1;

            for(String i : savedCheckValueList){
                pos++;

                if(i.equals("true")) {
                    checkValueList.set(pos, true);
                }
                else{
                    checkValueList.set(pos, false);
                }
            }
        }

        //**************************************************************


        applicationSelectedPermissionAdapter = new ApplicationSelectedPermissionAdapter(this, specialPermissionNames, checkValueList);
        ListView applicationPermissionView = (ListView) findViewById(R.id.application_selected_permission_list_view);
        applicationPermissionView.setAdapter(applicationSelectedPermissionAdapter);

        viewPermission = (Button) findViewById(R.id.view_permission_btn);

        viewPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplicationAllPermissionActivity.class);
                //based on item add info to intent
                startActivity(intent);
            }
        });

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

    /*************enabling Back Navication *******************/

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

    @Override
    protected void onPause() {
        super.onPause();
//
        int pos=-1;
        Boolean[] values = applicationSelectedPermissionAdapter.saveValues();

        /********Sending user fake gps input to database********/

        //এখানে পারমিশন স্পেসিফিক চেকড ভেলু ডাটাবেইজ এ সেভ করতে হবে
        //***********************************************
       data_source = new ApplicationViewController(this);

        String[] specialPermissionNames = data_source.loadSpecialPermissionNames();

       data_source.changeSpecialPermissionCheckValues(specialPermissionNames, values);
        //*****************************************************************


//the list containing all checkbox information


        savedCheckValueList = new ArrayList<String>();

        for(Boolean i : values){
            pos++;
            Log.d("permissions: ", Integer.toString(pos)+" "+Boolean.toString(i)+"\n");
            savedCheckValueList.add(Boolean.toString(i));

        }


        setStringArrayPref(this, "SpecialPermissions", savedCheckValueList);
    }

    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("SpecialPermissions", Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

}
