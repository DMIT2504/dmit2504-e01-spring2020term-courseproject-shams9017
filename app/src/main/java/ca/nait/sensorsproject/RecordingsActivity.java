package ca.nait.sensorsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordingsActivity extends AppCompatActivity {
    ListView mListView;
    private String audioNames[];
    ArrayList<String> listFile = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        mListView = findViewById(R.id.listview_recordings);
try{

    String path = getApplicationContext().getExternalFilesDir("/")
            .getAbsolutePath() + "/"+ "Recordings/" ;
    File directory = new File(path);
    File[] file = directory.listFiles();
    //listFile.clear();
    for(File mfile : file)
    {
        listFile.add(mfile.getName());
    }
    ArrayAdapter<String> dir = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, listFile);
    mListView.setAdapter(dir);

}

catch(Exception e)
{
    e.printStackTrace();
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
            Intent intent = new Intent(this, ShakeDetectionActivity.class);
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
