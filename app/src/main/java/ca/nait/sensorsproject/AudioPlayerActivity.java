package ca.nait.sensorsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private String audioNames[];

    private Button btnPlay, btnForward, btnRewind;
    static private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private Runnable mRunnable;
    private Handler mHandler;
    int positionj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        btnPlay = findViewById(R.id.btnPlay);
        btnForward = findViewById(R.id.btnForward);
        btnRewind = findViewById(R.id.btnRewind);
        mHandler = new Handler();
        mSeekBar = findViewById(R.id.seekbar);

        btnForward.setOnClickListener(this);
        btnRewind.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        mListView = findViewById(R.id.audio_listview);

        try{

            final ArrayList<File> audio = readAudio(new File(Environment.getExternalStorageDirectory() + "/Recordings"));
            audioNames = new String[audio.size()];
            for (int i = 0; i<audio.size(); i++)
            {
                audioNames[i] = audio.get(i).getName()
                        .toString()
                        .replace(".mp3", "");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (getApplicationContext(), R.layout.audio_layout,
                            R.id.audio_textview, audioNames);

            mListView.setAdapter(adapter);

            mMediaPlayer = new MediaPlayer();

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        positionj = position;
                        if (positionj != 0)
                        {
                            try{
                                if(mMediaPlayer != null)
                                {
                                    mMediaPlayer.stop();
                                    mMediaPlayer.release();
                                }

                                Uri uri = Uri.parse(audio.get(positionj).toString());
                                mMediaPlayer = MediaPlayer.create(AudioPlayerActivity.this, uri);
                                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        try{
                                            mSeekBar.setMax(mMediaPlayer.getDuration());
                                            mMediaPlayer.start();
                                            changeSeekbar();
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }

                    }
                });


                mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser)
                        {
                            mMediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }




    }

    private void changeSeekbar()
    {
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());

        if(mMediaPlayer.isPlaying())
        {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            mHandler.postDelayed(mRunnable, 1000);
        }
    }


    private ArrayList<File> readAudio(File root)
    {
       // pathSave = Environment.getExternalStorageDirectory()
              //  .getAbsolutePath() + "/"
               // + "Recordings/" + newRecord +".3gp";

       // Environment.getExternalStorageDirectory()
        //  .getAbsolutePath() + "/"
        // + "Recordings/"

       // ArrayList<File> arrayList1 = new ArrayList<File>();
       // File files2[] = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Recordings/").lis

        ArrayList<File> arrayList = new ArrayList<File>();
        File files[] = root.listFiles();

        for(File file : files)
        {
            if(file.isDirectory())
            {
                arrayList.addAll(readAudio(file));
            }
            else{
               // if(file.getName().endsWith(".mp3"))
              //  {
                //    arrayList.add(file);
               // }
                    arrayList.add(file);
            }
        }
        return arrayList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnPlay:
                if (mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.pause();
                    btnPlay.setText(">");
                }
                else{
                    mMediaPlayer.start();
                    btnPlay.setText("||");
                    changeSeekbar();
                }
                break;
            case R.id.btnForward:
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition()+3000);
                break;
            case R.id.btnRewind:
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition()-3000);
                break;

        }
    }

//    public void getAudio()
//    {
//        ContentResolver contentResolver = getContentResolver();
//        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor audioCursor = contentResolver.query(audioUri, null, null, null);
//
//        if (audioCursor!=null && audioCursor.moveToFirst())
//        {
//            int audioTitle = audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            do{
//                String currentTitle = audioCursor.getString(audioTitle);
//                arraylist2.add(currentTitle);
//
//            }
//            while (audioCursor.moveToNext());
//        }
//
//    }
}
