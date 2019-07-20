package unsw.comp9336.gyroscope_and_magnetometer;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task5Fragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;
    private Context context;
    private SensorEventListener gyroscopeSensorListener;
    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
    private TextView textViewMagnetHeading;
    private TextView textViewTrueHeading;
    private String format = "%.2f";
    // for GPS
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentBestLocation;
    private int PERMISSION_CODE = 23;

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorListener);
        removeLocationListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(gyroscopeSensorListener,
                sensor, SensorManager.SENSOR_DELAY_NORMAL);
        defineLocationListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        ((FragmentActivity) context).setTitle(getResources().getString(R.string.menu_task5));
        View view = inflater.inflate(R.layout.fragment_task5, container, false);
        textViewX = (TextView) view.findViewById(R.id.x);
        textViewY = (TextView) view.findViewById(R.id.y);
        textViewZ = (TextView) view.findViewById(R.id.z);
        textViewMagnetHeading = (TextView) view.findViewById(R.id.mHeading);
        textViewTrueHeading = (TextView) view.findViewById(R.id.tHeading);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        currentBestLocation = null;
        //        checkGpsPermission();
        // Create a listener for magnet field
        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] values = sensorEvent.values;
                double x = (double) values[0];
                double y = (double) values[1];
                double magnetHeading;
                if (x > 0) {
                    magnetHeading = 270 + Math.toDegrees(Math.atan(Math.sin(y / x)));
                } else if (x < 0) {
                    magnetHeading = 90 + Math.toDegrees(Math.atan(Math.sin(y / x)));
                } else {
                    if (y > 0) {
                        magnetHeading = 0;
                    } else {
                        magnetHeading = 180;
                    }
                }
                textViewMagnetHeading.setText(String.format(format, magnetHeading));
                if (currentBestLocation != null) {

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        return view;
    }


    private void checkGpsPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_CODE);
    }



    private void defineLocationListener() {
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (isBetterLocation(location, currentBestLocation)) {
                    currentBestLocation = location;
                    textViewX.setText(String.valueOf(currentBestLocation.getLatitude()));
                    textViewY.setText(String.valueOf(currentBestLocation.getLongitude()));
                    textViewZ.setText(String.valueOf(currentBestLocation.getAltitude()));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkGpsPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    private void removeLocationListener() {
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
