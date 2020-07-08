package ca.nait.sensorsproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ImageView speakButton;
    EditText speakText;
    ListView mListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> voiceToTextItems;

    private static final int RECOGNIZER_RESULT = 1;

    private ListDatabase listDataBase;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakButton = findViewById(R.id.imageView_voice_text);
        speakText = findViewById(R.id.voicetext_edit_text);
        mListView = findViewById(R.id.listview_vtext);

        listDataBase = new ListDatabase(this);

        //getting list from database
        sqLiteDatabase = listDataBase.getReadableDatabase();
        voiceToTextItems = new ArrayList<>();
        Cursor cursor = listDataBase.viewData();

        if (cursor.getCount() == 0)
        {
            Toast.makeText(this, "Nothing found!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext())
            {
                voiceToTextItems.add(cursor.getString(1));

            }
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, voiceToTextItems);
        mListView.setAdapter(adapter);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //extra language model informs the recognizer which
                //speech to model to access
                //and the language model free form uses language models
                // that are in free form speech recognition
                //this is the value that the extra language model needs

                Intent speakIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speakIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                //the extra prompt is an additional
                //prompt that tells the user when to speak.
                speakIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to the mic");
                startActivityForResult(speakIntent, RECOGNIZER_RESULT);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        listDataBase = new ListDatabase(this);
        //now to check the request code and the result code.
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK)
        {

            speakText.setText("");
            //here we need code to get the input
            assert data != null;

            ArrayList<String> voiceMatches = new ArrayList<>();
            voiceMatches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            speakText.setText(voiceMatches.get(0));

            String userVoiceInput = speakText.getText().toString();
            if (!userVoiceInput.isEmpty())
            {
                listDataBase.insert(speakText.getText().toString());
                //Toast.makeText(MainActivity.this, "New list item added!",
                // Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

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
