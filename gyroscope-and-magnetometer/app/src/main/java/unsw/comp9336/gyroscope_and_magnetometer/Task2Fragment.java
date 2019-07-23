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
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task2Fragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;
    private Context context;
    private SensorEventListener gyroscopeSensorListener;
    private TextView textView;
    private Button btnReset;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float radian = 0;
    private float timestamp;
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
                sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        ((FragmentActivity) context).setTitle(getResources().getString(R.string.menu_task2));
        View view = inflater.inflate(R.layout.fragment_task2, container, false);
        textView = (TextView) view.findViewById(R.id.degree);
        btnReset = (Button) view.findViewById(R.id.reset);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Create a listener
        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                    if (timestamp != 0) {
                        float time = (sensorEvent.timestamp - timestamp) * NS2S;
                        // Axis of the rotation sample, not normalized yet.
                        float rate = sensorEvent.values[2];
                        radian = radian + rate * time;
                    }
                    timestamp = sensorEvent.timestamp;
                    double degree = Math.toDegrees(radian);
                    textView.setText(String.format(format,degree));
                }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radian = 0;
                double degree = Math.toDegrees(radian);
                textView.setText(String.format(format,degree));
            }
        });
        return view;
    }
}
