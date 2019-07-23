package jiang.jack.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button task1;   // Get GPS Status
    private Button task2;   // Get GPS Location
    private Button task3a;   // Launch GPS Tracker
    private Button task3b;   // Stop GPS Tracker
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentBestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        currentBestLocation = null;
        initializeButtons();
    }


    private void initializeButtons() {
        task1 = (Button) findViewById(R.id.task1);
        task2 = (Button) findViewById(R.id.task2);
        task3a = (Button) findViewById(R.id.task3a);
        task3b = (Button) findViewById(R.id.task3b);
        task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGpsStatus();
            }
        });
        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGpsLocation();
            }
        });
        task3a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defineLocationListener();
            }
        });
        task3b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLocationListener();
            }
        });
    }


    private void goToGpsSetting() {
        new AlertDialog.Builder(this)
                .setTitle("GPS Permission")
                .setMessage("GPS Permission required")
                .setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void getGpsStatus() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        System.out.println(isGPSEnabled);
        if (isGPSEnabled) {
            textView.setText("GPS is enabled");
        } else {
            textView.setText("GPS is disabled");
            goToGpsSetting();
        }
    }

    private void getGpsLocation() {
        String networkLocation;
        String gpsLocation;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textView.setText("No GPS Permission");
            return;
        }
        Location lastNetWorkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastNetWorkLocation == null) {
            networkLocation = "No last network location" + System.lineSeparator();
        } else {
            networkLocation = locationToString(lastNetWorkLocation);
        }
        Location lastGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastGpsLocation == null) {
            gpsLocation = "No last GPS location" + System.lineSeparator();
        } else {
            gpsLocation = locationToString(lastGpsLocation);
        }
        textView.setText(networkLocation + gpsLocation);
    }


    private String locationToString(Location location) {
        String time = timeToString(location.getTime());
        String provider = location.getProvider();
        String accuracy = String.valueOf(location.getAccuracy());
        String altitude = String.valueOf(location.getAltitude());
        String latitude = String.valueOf(location.getLatitude());
        String speed = String.valueOf(location.getSpeed());
        String locationString =
                "Date/Time: " + time + System.lineSeparator()
                        + "Provide: " + provider + System.lineSeparator()
                        + "Accurary: " + accuracy + System.lineSeparator()
                        + "Altitude: " + altitude + System.lineSeparator()
                        + "Latitude: " + latitude + System.lineSeparator()
                        + "Speed: " + speed + System.lineSeparator();
        return locationString;
    }


    private String timeToString(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeToString = simpleDateFormat.format(date);
        return timeToString;
    }


    private void defineLocationListener() {
        textView.setText("GPS Tracker is Starting...");
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (isBetterLocation(location, currentBestLocation)) {
                    currentBestLocation = location;
                    textView.setText(locationToString(location));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textView.setText("No GPS Permission");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    private void removeLocationListener() {
        textView.setText("GPS Tracker has Stopped.");
        locationManager.removeUpdates(locationListener);
    }



    /**
     - Author(s) name: developer.android.com
     - Date: 9 Jul. 2019
     - Title: Maintain a current best estimate
     - Code version: N.A.
     - Type: source code
     - Web address: https://developer.android.com/guide/topics/location/strategies.html
    */

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



}



