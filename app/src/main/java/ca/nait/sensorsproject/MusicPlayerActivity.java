package ca.nait.sensorsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements SensorEventListener {

    MediaPlayer mMediaPlayer;
    ImageButton imgPlay, imgPause;
    ListView mListView;
    ArrayList<String> mArrayList;
    ArrayAdapter<String> mAdapter;

    //sensor code

    private SensorManager mSensorManager;
    private Sensor proximitySensor;
    private Boolean isProxSensorAvailable;

    //Audio Editor implementation
    SeekBar positionBar;
    SeekBar volumeBar;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;

    float speed = 1.0f;
    float pitch = 1.0f;
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        //Audio Editor implementation
        elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);

        //sensor code

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null)
        {
            proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            isProxSensorAvailable = true;
        }
        else{
            Toast.makeText(this, "Proximity sensor not available!",
                    Toast.LENGTH_SHORT).show();
            isProxSensorAvailable = false;
        }

        imgPause = findViewById(R.id.imageButton_pause);
        imgPlay = findViewById(R.id.imageButton_play);

        mListView = (ListView) findViewById(R.id.listview);
        mArrayList = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i< fields.length; i++)
        {
            mArrayList.add(fields[i].getName());
        }

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mArrayList);
        mListView.setAdapter(mAdapter);

        //Audio editor implementation
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        try{
                            float volumeNum = progress / 100f;
                            mMediaPlayer.setVolume(volumeNum, volumeNum);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
        positionBar = (SeekBar) findViewById(R.id.positionBar);


        //listview onclick

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMediaPlayer!= null)
                {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                   // Toast.makeText(MusicPlayerActivity.this, "player released",
                    // Toast.LENGTH_SHORT).show();
                }

                int resId = getResources().getIdentifier(mArrayList.get(position),
                        "raw", getPackageName());
                mMediaPlayer = MediaPlayer.create(MusicPlayerActivity.this, resId);


                //Audio Editor implementation
                mMediaPlayer.setLooping(true);
                mMediaPlayer.seekTo(0);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setPitch(pitch));
                totalTime = mMediaPlayer.getDuration();
                mMediaPlayer.start();

                positionBar.setMax(totalTime);
                positionBar.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    mMediaPlayer.seekTo(progress);
                                    positionBar.setProgress(progress);
                                }
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        }
                );

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mMediaPlayer != null) {
                            try {
                                Message msg = new Message();
                                msg.what = mMediaPlayer.getCurrentPosition();
                                handler.sendMessage(msg);
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {}
                        }
                    }
                }).start();

            }
        });

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if (mMediaPlayer == null)
                        {
                            // mMediaPlayer = MediaPlayer.create(MusicPlayerActivity.this, R
                            //.raw.sample_audio2);

                            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    if (mMediaPlayer!= null)
                                    {
                                        mMediaPlayer.release();
                                        mMediaPlayer = null;
                                        // Toast.makeText(MusicPlayerActivity.this,
                                        // "player released", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        if(mMediaPlayer != null)
                        {
                            mMediaPlayer.start();
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }


                }
            });

        imgPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer !=null)
                {
                    mMediaPlayer.pause();
                }

            }
        });
//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              stopPlayer();
//
//            }
//        });

        //switch to enable/disable prox sensor control

        mSensorManager.registerListener((SensorEventListener) this, proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL);

    }



    //audio editor implementation

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    // Speed Buttons
    public void speedDecreaseBtn(View view) {
        try{
            speed = speed - 0.15f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void speedIncreaseBtn(View view) {
        try{
            speed = speed + 0.15f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void speedResetBtn(View view) {
        try{
            speed = 1.0f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // Pitch Buttons
    public void pitchDecreaseBtn(View view) {
        try{
            pitch = pitch - 0.025f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setPitch(pitch));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void pitchIncreaseBtn(View view) {
        try{
            pitch = pitch + 0.025f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setPitch(pitch));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void pitchResetBtn(View view) {
        try{
            pitch = 1.0f;
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setPitch(pitch));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //audio editor implementation
    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


    @Override
    public void onSensorChanged(final SensorEvent event)
    {

        try{
            //  mSensorManager.registerListener(MusicPlayerActivity.this,
            //  proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(MusicPlayerActivity.this, "values: " + event.values[0],
                    Toast.LENGTH_SHORT).show();
            if (event.values[0] == 0.0) {
                if (mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.pause();
                }

                else if (event.values[0] == 0.0) {

                    mMediaPlayer.start();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String[] getAllRawResources() {
        Field fields[] = R.raw.class.getDeclaredFields() ;
        String[] names = new String[fields.length] ;

        try {
            for( int i=0; i< fields.length; i++ ) {
                Field f = fields[i] ;
                names[i] = f.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names ;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isProxSensorAvailable)
        {
            mSensorManager.unregisterListener(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isProxSensorAvailable)
        {
            mSensorManager.registerListener(this, proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

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
        else if (item.getItemId() == R.id.menu_voice_to_text)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_recs)
        {
            Intent intent = new Intent(this, ShakeDetectionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
