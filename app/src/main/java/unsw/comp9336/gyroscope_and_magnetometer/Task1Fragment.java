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

import static android.support.v4.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task1Fragment extends Fragment {
    private SensorManager sensorManager;
    private Sensor sensor;
    private Context context;
    private SensorEventListener gyroscopeSensorListener;
    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;

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
        ((FragmentActivity) context).setTitle(getResources().getString(R.string.menu_task1));
        View view = inflater.inflate(R.layout.fragment_task1, container, false);
        textViewX = (TextView) view.findViewById(R.id.x);
        textViewY = (TextView) view.findViewById(R.id.y);
        textViewZ = (TextView) view.findViewById(R.id.z);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // Create a listener
        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] values = sensorEvent.values;
                String format = "%.4f";
                textViewX.setText(String.format(format, values[0]));
                textViewY.setText(String.format(format, values[1]));
                textViewZ.setText(String.format(format, values[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        return view;
    }



}
