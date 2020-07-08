package ca.nait.sensorsproject;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class AudioRecorderActivity extends AppCompatActivity {

    private Switch swMotionRecord;

    Button btnRecord, btnStopRecord, btnPlay, btnStop;
    String pathSave = "";
    EditText newRecNameEdit;
    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayer;

    private GyroscopeSensor mGyroscope;

    private Handler mHandler = new Handler();

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        swMotionRecord = findViewById(R.id.switch_motion_record);
        btnRecord = findViewById(R.id.button_record_start);
        btnStopRecord = findViewById(R.id.button_record_stop);
        newRecNameEdit = findViewById(R.id.record_new_name);
        btnStopRecord.setEnabled(false);

        mGyroscope = new GyroscopeSensor(this);

        if (!checkPermissionFromDevice())
        {
            requestPermissions();
        }

        swMotionRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true)
                {   btnRecord.setEnabled(false);
                    btnStopRecord.setEnabled(false);
                    Toast.makeText(AudioRecorderActivity.this, "Motion record enabled", Toast.LENGTH_SHORT).show();

                    mGyroscope = new GyroscopeSensor(AudioRecorderActivity.this);
                    mGyroscope.register();

                    mGyroscope.setListener(new GyroscopeSensor.Listener() {
                        @Override
                        public void onRotation(float rx, float ry, float rz) {
                            btnStopRecord.setEnabled(false);
                            if(rx < -1.0)
                            {
                                btnRecord.setEnabled(false);
                                btnStopRecord.setEnabled(false);
                                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 75);
                                toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2,150);
                                mHandler.postDelayed(mRunnable, 1500);

                            }
                            else if (rx > 1.0f)
                            {
                                btnStopRecord.setEnabled(false);
                                try{
                                    mMediaRecorder.stop();
                                    mMediaRecorder.release();
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                                //record stop code


                                // btnRecord.setEnabled(true);
                                // mHandler.removeCallbacks(mRunnable);
                                Toast.makeText(AudioRecorderActivity.this,
                                        "Recording stopped.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }
                else{
                    btnRecord.setEnabled(true);
                    btnStopRecord.setEnabled(false);
                    mGyroscope.unregister();
                }
            }
        });


        btnRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(checkPermissionFromDevice())
                {
                    String newRecord = newRecNameEdit.getText().toString().trim();

                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/"
                            + "Recordings/" + newRecord +".3gp";

                    // pathSave = getExternalFilesDir("/").getAbsolutePath()
                    //  + UUID.randomUUID().toString() + "audio_record.3gp";

                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                    mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mMediaRecorder.setOutputFile(pathSave);
                    btnStopRecord.setEnabled(true);

                    try {
                        mMediaRecorder.prepare();
                        mMediaRecorder.start();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Toast.makeText(AudioRecorderActivity.this,
                            "Recording started...", Toast.LENGTH_SHORT).show();

                }
                else{
                    requestPermissions();
                }

            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(true);
                Toast.makeText(AudioRecorderActivity.this,
                        "Recording stopped.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //runnable
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //audio record code
            if(checkPermissionFromDevice())
            {
                String newRecord = newRecNameEdit.getText().toString().trim();

                pathSave = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/"
                        + "Recordings/" + newRecord +".3gp";

                // pathSave = getExternalFilesDir("/").getAbsolutePath()
                //  + UUID.randomUUID().toString() + "audio_record.3gp";

                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);


                mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                mMediaRecorder.setOutputFile(pathSave);
                btnStopRecord.setEnabled(true);

                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Toast.makeText(AudioRecorderActivity.this,
                        "Recording started...", Toast.LENGTH_SHORT).show();

            }
            else{
                requestPermissions();
            }
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(this, "permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGyroscope.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGyroscope.unregister();
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
            Intent intent = new Intent(this, RecordingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}