package com.redcarpetup.loc8;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.redcarpetup.locationdetector.ProviderUtils.LocationCallback;
import com.redcarpetup.locationdetector.Loc8;

public class MainActivity extends AppCompatActivity {
    Context mcontext;
    TextView locationProvider, fusedProvider, mock;
    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mcontext = this;
        locationProvider = (TextView) findViewById(R.id.locationProvider);
        fusedProvider = (TextView) findViewById(R.id.fusedProvider);
        mock = (TextView) findViewById(R.id.isMock);
        getLocationProvider();
        getFusedProvider();

    }

    public void getLocationProvider() {
        Loc8 loc8 = Loc8.getInstance(mcontext, Loc8.LOCATION_MANAGER);
        loc8.getLocation(new LocationCallback() {
            @Override
            public void onError(String error) {
                locationProvider.setText(error);
            }

            @Override
            public void onSuccess(Location location) {
                mLocation = location;
                if(Loc8.isLocationFromMockProvider(mcontext,mLocation))
                {
                    mock.setText(""+Loc8.isLocationFromMockProvider(mcontext,mLocation));
                }
                locationProvider.setText("Lat = " + location.getLatitude() + " and " + "Long =" + location.getLongitude());

            }
        });

    }

    public void getFusedProvider() {
        Loc8 loc8 = Loc8.getInstance(mcontext, Loc8.LOCATION_API);
        loc8.getLocation(new LocationCallback() {
            @Override
            public void onError(String error) {
                fusedProvider.setText(error);
            }

            @Override
            public void onSuccess(Location location) {
                mLocation = location;
                if(Loc8.isLocationFromMockProvider(mcontext,mLocation))
                {
                    mock.setText(""+Loc8.isLocationFromMockProvider(mcontext,mLocation));
                }
                fusedProvider.setText("Lat = " + location.getLatitude() + " and " + "Long =" + location.getLongitude());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
