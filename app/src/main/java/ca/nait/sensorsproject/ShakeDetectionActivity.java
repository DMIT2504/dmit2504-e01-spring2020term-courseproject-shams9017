package ca.nait.sensorsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
public class ShakeDetectionActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    ListView mListView;
    ArrayList<String> mArrayList;
    ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shake_detection);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        //getting the list
        mListView = (ListView) findViewById(R.id.listview_shuffle);
        mArrayList = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i< fields.length; i++)
        {
            mArrayList.add(fields[i].getName());
        }

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mArrayList);
        mListView.setAdapter(mAdapter);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                //do what you want with the shake detection here.
                Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
                mListView = (ListView) findViewById(R.id.listview_shuffle);
                mArrayList = new ArrayList<>();
                Field[] fields = R.raw.class.getFields();
                for (int i = 0; i< fields.length; i++)
                {
                    mArrayList.add(fields[i].getName());
                }

                mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mArrayList);
                mListView.setAdapter(mAdapter);
                Collections.shuffle(mArrayList);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_audio_record)
        {
            Intent intent = new Intent(this, AudioRecorderActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_audio_player)
        {
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_shake_detection)
        {
            Intent intent = new Intent(this, ShakeDetectionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
