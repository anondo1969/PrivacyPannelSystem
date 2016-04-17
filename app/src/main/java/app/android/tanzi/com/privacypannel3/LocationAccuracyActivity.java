package app.android.tanzi.com.privacypannel3;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class LocationAccuracyActivity extends AppCompatActivity {
    Switch locationSwitch;
    SeekBar locationSeekBar;
    TextView seekbarProgress;
    Button riskyAppView;
    boolean locationChecked = false;
    int fakeDistance = 0;
    SharedPreferences fakeLocationData;
    SharedPreferences.Editor fakeLocationDataEditor;

    LocationManager locationManager;
    Location getLastLocation;
    double realLongitude = 88.60114;
    double realLatitude = 24.374;

    boolean previousLocationChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_accuracy);

        locationSwitch =(Switch) findViewById(R.id.location_switch);
        locationSeekBar = (SeekBar) findViewById(R.id.location_seekBar);
        seekbarProgress = (TextView) findViewById(R.id.progress);
        riskyAppView = (Button) findViewById(R.id.riskyappbtn);

       // locationSeekBar.setProgress(10);
        //locationSeekBar.setEnabled(false);

        fakeLocationData = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE);

        locationChecked = fakeLocationData.getBoolean("setLocation:", false);
        fakeDistance = fakeLocationData.getInt("fakeDistance:", 0);

        previousLocationChecked = locationChecked;

     //   Toast.makeText(getApplicationContext(), "Inside onCreate: \n setLocation " + Boolean.toString(locationChecked)
       //         + " fakeDistance " + Integer.toString(fakeDistance), Toast.LENGTH_SHORT).show();

        locationSwitch.setChecked(locationChecked);
        seekbarProgress.setText("Fake Location Distance: " + fakeDistance + " km");
        locationSeekBar.setProgress(fakeDistance);

        locationSeekBar.setEnabled(locationChecked);
        seekbarProgress.setEnabled(locationChecked);

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationChecked = true;
                    locationSeekBar.setEnabled(true);
                    seekbarProgress.setEnabled(true);
                } else {
                    locationChecked = false;
                    locationSeekBar.setEnabled(false);
                    seekbarProgress.setEnabled(false);
                }
            }
        });

        locationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarProgress.setText("Fake Location Distance: " + progress + " km");
                fakeDistance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        riskyAppView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RiskyAppsViewActivity.class);
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



    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();

        boolean previousLocationChecked = fakeLocationData.getBoolean("setLocation:", false);

        ProfileSettings profileSettings = new ProfileSettings();


      //  Toast.makeText(getApplicationContext(), "Inside onPause: \n setLocation " + Boolean.toString(locationChecked)
           //     + " fakeDistance " + Integer.toString(fakeDistance), Toast.LENGTH_SHORT).show();

        if(locationChecked) {

           if(previousLocationChecked == false) {

                //stopService(new Intent(LocationAccuracyActivity.this, FakeGPSOnService.class));

                Intent gpsService = new Intent(AppContext.getContext(), FakeGPSOnService.class);

                // if(isMyServiceRunning(FakeGPSOnService.class, AppContext.getContext())){

                AppContext.getContext().stopService(gpsService);


                locationManager = (LocationManager) getSystemService(AppContext.getContext().LOCATION_SERVICE);
                getLastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (getLastLocation != null) {//
                    realLongitude = getLastLocation.getLongitude();
                    realLatitude = getLastLocation.getLatitude();

                    fakeLocationDataEditor = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE).edit(); // set
                    fakeLocationDataEditor.putFloat("realLatitude:", (float) realLatitude);
                    fakeLocationDataEditor.putFloat("realLongitude:", (float) realLongitude);
                    fakeLocationDataEditor.commit();

                    calculateFakeCoOrdinates(realLatitude, realLongitude);
                    profileSettings.setProfile(2);

                }
                else {

                 //   Log.d("values", "location is null");

                    locationChecked = false;

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                   //     Log.d("values", "GPS is disabled");

                        //show a alert dialog to tell user to on GPS
                        AlertDialog.Builder builder = new AlertDialog.Builder(AppContext.getContext());
                        builder.setMessage("Please Enable your GPS location first to enable fake Location. You can disable your GPS location later")
                                .setCancelable(false)
                                .setTitle("Please enable\"Your GPS Location\"!!")
                                .setPositiveButton("OK", null);
                        AlertDialog alert = builder.create();
                        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        alert.show();
                    }

                    fakeLocationDataEditor = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE).edit(); // set
                    fakeLocationDataEditor.putInt("Radio Button Id:", 0);
                    fakeLocationDataEditor.commit();

                    profileSettings.setProfile(0);


                    //realLongitude = fakeLocationData.getFloat("realLongitude:", (float) 88.60114);//
                    //realLatitude = fakeLocationData.getFloat("realLatitude:", (float) 24.374); //
                }

             //  Log.d("values", "Inside different:\nrealLatitude: " + realLatitude + "\nrealLongitude: " + realLongitude);


                //calculateFakeCoOrdinates(realLatitude, realLongitude);
                //profileSettings.setProfile(2);
            }

            else{

               realLongitude = fakeLocationData.getFloat("realLongitude:", (float) 88.60114);//
               realLatitude = fakeLocationData.getFloat("realLatitude:", (float) 24.374); //

             //  Log.d("values", "Inside same:\nrealLatitude: " + realLatitude + "\nrealLongitude: " + realLongitude);


               calculateFakeCoOrdinates(realLatitude, realLongitude);
               profileSettings.setProfile(2);

           }
        }
        else{

            SharedPreferences get_user_profile = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE); // get
            int id = get_user_profile.getInt("Radio Button Id:", 0); //0 is the default value.

            if(id==2) {

                fakeLocationDataEditor = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE).edit(); // set
                fakeLocationDataEditor.putInt("Radio Button Id:", 0);
                fakeLocationDataEditor.commit();

                profileSettings.setProfile(0);
            }
        }

        fakeLocationDataEditor = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE).edit(); // set
        fakeLocationDataEditor.putBoolean("setLocation:", locationChecked);
        fakeLocationDataEditor.putInt("fakeDistance:", fakeDistance);
        fakeLocationDataEditor.commit();

    }

    public void calculateFakeCoOrdinates(double realLatitude, double realLongitude){

        double distance = fakeDistance * 1000; //it will be input from shared preference
        double fakeLatitude = 0;
        double fakeLongitude = 0;
        double bearing = 1 + (Math.random() * ((360 - 1) + 1));
        double radius = 6371000;

        //double bearing = 60; //it wii be randomized

        double δ = distance / radius; // angular distance in radians
        double θ = Math.toRadians(bearing);

        double φ1 = Math.toRadians(realLatitude);
        double λ1 = Math.toRadians(realLongitude);

        double φ2 = Math.asin(Math.sin(φ1) * Math.cos(δ) + Math.cos(φ1) * Math.sin(δ) * Math.cos(θ));
        double x = Math.cos(δ) - Math.sin(φ1) * Math.sin(φ2);
        double y = Math.sin(θ) * Math.sin(δ) * Math.cos(φ1);
        double λ2 = λ1 + Math.atan2(y, x);

        fakeLatitude = Math.toDegrees(φ2);
        fakeLongitude = (Math.toDegrees(λ2)+540)%360-180;

       // Toast.makeText(getApplicationContext(), "FakeLatitude " + Double.toString(fakeLatitude)
        //        +" FakeLongitude "+Double.toString(fakeLongitude), Toast.LENGTH_LONG).show();

       // Log.d("values", "fakeLatitude: " + fakeLatitude + "\nfakeLongitude: " + fakeLongitude);


        fakeLocationDataEditor = getSharedPreferences("USER_PROFILE_PREFS_NAME", MODE_PRIVATE).edit(); // set
        fakeLocationDataEditor.putFloat("fakeLatitude:", (float) fakeLatitude);
        fakeLocationDataEditor.putFloat("fakeLongitude:", (float) fakeLongitude);
        fakeLocationDataEditor.putInt("Radio Button Id:",2);
        fakeLocationDataEditor.commit();

    }


}
