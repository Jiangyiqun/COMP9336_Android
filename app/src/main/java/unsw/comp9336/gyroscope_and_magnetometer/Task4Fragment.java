package unsw.comp9336.gyroscope_and_magnetometer;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task4Fragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;
    private Context context;
    private SensorEventListener gyroscopeSensorListener;
    private TextView textView;
    private String format = "%.2f";

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(gyroscopeSensorListener,
                sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        ((FragmentActivity) context).setTitle(getResources().getString(R.string.menu_task4));
        View view = inflater.inflate(R.layout.fragment_task4, container, false);
        textView = (TextView) view.findViewById(R.id.heading);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // Create a listener
        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] values = sensorEvent.values;
                double x = (double)values[0];
                double y = (double)values[1];
                double heading;
                if (x > 0) {
                    heading = 270 + Math.toDegrees(Math.atan(y / x));
                } else if (x < 0) {
                    heading = 90 + Math.toDegrees(Math.atan(y / x));
                } else {
                    if (y > 0) {
                        heading = 0;
                    } else {
                        heading = 180;
                    }
                }
                textView.setText(String.format(format, heading));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        return view;
    }

}
