package edu.depaul.csc472.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private EditText titleText;
    private EditText bodyText;
    private String oldTitle;
    private String oldBody;
    private int id;
    private NoteActivity noteActivity;
    Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        titleText = findViewById(R.id.title);
        bodyText = findViewById(R.id.body);
        noteActivity = this;

        // if we are passing note for edit
        Intent intent = getIntent();
        if (intent.hasExtra("TITLE")) {
            this.oldTitle = intent.getStringExtra("TITLE");
            titleText.setText(oldTitle);
        }
        if (intent.hasExtra("BODY")) {
            this.oldBody = intent.getStringExtra("BODY");
            bodyText.setText(oldBody);
        }

        if (intent.hasExtra("ID")) {
            this.id = intent.getIntExtra("ID", 0);
            this.editMode = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notebar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Intent data = new Intent();
                if (editMode) {
                    if (!hasTitle()) { // when user remove title
                        Toast.makeText(this, "Missing Title - Not Saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED, data);
                        finish();
                    } else if (changed()) { // if the user modify note
                        data.putExtra("ID", this.id);
                        data.putExtra("USER TITLE", titleText.getText().toString());
                        data.putExtra("USER TEXT", bodyText.getText().toString());
                        setResult(RESULT_OK, data);
                        finish();
                    } else { // if user didn't do anything
                        setResult(RESULT_CANCELED, data);
                        finish();
                    }
                } else {
                    if (hasBody() && !hasTitle()) { // if user missing title
                        Toast.makeText(this, "Missing Title - Not Saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED, data);
                        finish();
                    } else if (hasTitle()) { // if they have title
                        data.putExtra("USER TITLE", titleText.getText().toString());
                        data.putExtra("USER TEXT", bodyText.getText().toString());
                        setResult(RESULT_OK, data);
                        finish();
                    } else { // no entry
                        setResult(RESULT_CANCELED, data);
                        finish();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        if (changed() || ((hasTitle() || hasBody()) & !editMode)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            // Code goes here
                            saveData();
                        }

                    });
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            // Code goes here
                            finish();
                        }

                    });
            builder.setMessage("Your note is not saved!");
            builder.setTitle(String.format("Save note '%s'?", titleText.getText().toString()));
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            super.onBackPressed();
        }

    }

    private void saveData() {
        Intent data = new Intent();
        if (editMode) {
            if (!hasTitle()) { // when user remove title
                Toast.makeText(this, "Missing Title - Not Saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED, data);
                finish();
            } else if (changed()) { // if the user modify note
                data.putExtra("ID", this.id);
                data.putExtra("USER TITLE", titleText.getText().toString());
                data.putExtra("USER TEXT", bodyText.getText().toString());
                setResult(RESULT_OK, data);
                finish();
            } else { // if user didn't do anything
                setResult(RESULT_CANCELED, data);
                finish();
            }
        } else {
            if (hasBody() && !hasTitle()) { // if user missing title
                Toast.makeText(this, "Missing Title - Not Saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED, data);
                finish();
            } else if (hasTitle()) { // if they have title
                data.putExtra("USER TITLE", titleText.getText().toString());
                data.putExtra("USER TEXT", bodyText.getText().toString());
                setResult(RESULT_OK, data);
                finish();
            } else { // no entry
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }

    }

    private boolean hasBody() {
        return !this.bodyText.getText().toString().isEmpty();
    }

    private boolean hasTitle() {
        return !this.titleText.getText().toString().trim().isEmpty();
    }

    private boolean changed() {
        if (!editMode) return false;
        Boolean title = !this.oldTitle.equals(this.titleText.getText().toString());
        Boolean body = !this.oldBody.equals(this.bodyText.getText().toString());
        return (title || body);
    }
}
