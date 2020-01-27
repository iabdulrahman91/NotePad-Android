package edu.depaul.csc472.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private static final String TAG = "MainActivity";
    private static final int CODE_FOR_NEW_NOTE_ACTIVITY = 111;
    private static final int CODE_FOR_EDIT_NOTE_ACTIVITY = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        noteAdapter = new NoteAdapter(noteList, this);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doRead();
        setTitle(String.format("Note Pad (%d)", noteList.size()));
    }


    @Override
    protected void onPause() {
        super.onPause();
        doWrite();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
            case R.id.addnote:
                Intent intentToAdd = new Intent(this, NoteActivity.class);
                startActivityForResult(intentToAdd, CODE_FOR_NEW_NOTE_ACTIVITY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // expect new note, without ID
        if (requestCode == CODE_FOR_NEW_NOTE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra("USER TITLE");
                String text = data.getStringExtra("USER TEXT");
                // create new Note
                Note newNote = new Note(title, text, System.currentTimeMillis());
                // add note to noteList
                this.noteList.add(newNote);
                Collections.sort(noteList);
                noteAdapter.notifyDataSetChanged();
                setTitle(String.format("Note Pad (%d)", noteList.size()));
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }

            // expect data for existing Note, it has ID

        } else if (requestCode == CODE_FOR_EDIT_NOTE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Integer pos = data.getIntExtra("ID", -1);
                if (pos >= 0) {
                    Note n = noteList.get(pos);
                    String title = data.getStringExtra("USER TITLE");
                    String text = data.getStringExtra("USER TEXT");
                    n.setText(text);
                    n.setTitle(title);
                    Collections.sort(noteList);
                    noteAdapter.notifyDataSetChanged();
                    setTitle(String.format("Note Pad (%d)", noteList.size()));
                }


            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }

        } else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = noteList.get(pos);
        Intent intentToEdit = new Intent(this, NoteActivity.class);
        intentToEdit.putExtra("ID", pos);
        intentToEdit.putExtra("TITLE", n.getTitle());
        intentToEdit.putExtra("BODY", n.getText());
        startActivityForResult(intentToEdit, CODE_FOR_EDIT_NOTE_ACTIVITY);
    }

    @Override
    public boolean onLongClick(final View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        final Note n = noteList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // Code goes here
                        deleteNote(n);

                    }

                });
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // Code goes here

                    }

                });
        builder.setMessage(String.format("Delete Note '%s'?", n.getTitle()));
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    private void deleteNote(Note n) {
        noteList.remove(n);
        noteAdapter.notifyDataSetChanged();
        setTitle(String.format("Note Pad (%d)", noteList.size()));
        Toast.makeText(this, String.format("Note '%s' Deleted!", n.getTitle()), Toast.LENGTH_SHORT).show();
    }

    public void doWrite() {

        JSONArray jsonArray = new JSONArray();

        for (Note n : noteList) {
            try {
                JSONObject noteJSON = new JSONObject();
                noteJSON.put("titleText", n.getTitle());
                noteJSON.put("contentText", n.getText());
                noteJSON.put("time", n.getTimestamp());
                jsonArray.put(noteJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonText = jsonArray.toString();


        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(
                            openFileOutput("mydata.txt", Context.MODE_PRIVATE)
                    );

            outputStreamWriter.write(jsonText);
            outputStreamWriter.close();
//            Toast.makeText(this, "File write success!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "File write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void doRead() {

        noteList.clear();
        try {
            InputStream inputStream = openFileInput("mydata.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();

                String jsonText = stringBuilder.toString();

                try {
                    JSONArray jsonArray = new JSONArray(jsonText);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("titleText");
                        String content = jsonObject.getString("contentText");
                        long time = jsonObject.getLong("time");
                        Note n = new Note(title, content, time);
                        noteList.add(n);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "NEW JSON created", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d(TAG, "doRead: Can not read file: " + e.toString());
        }

        Collections.sort(noteList);
    }
}
