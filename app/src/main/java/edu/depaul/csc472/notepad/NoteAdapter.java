package edu.depaul.csc472.notepad;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private static final String TAG = "NoteAdapter";
    private ArrayList<Note> aList;
    private MainActivity mainActivity;

    NoteAdapter(ArrayList<Note> list, MainActivity mainActivity) {
        aList = list;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: CREATING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_item, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note selectedNote = aList.get(position);
        String text = selectedNote.getText();
        String toDisplay = (text.trim().length()>80)? text.substring(0,80)+"..." : text;
        holder.titleText.setText(selectedNote.getTitle());
        holder.bodyText.setText(toDisplay);
        holder.timeText.setText(convertTime(selectedNote.getTimestamp()));
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("E MMM dd, HH:mm a");
        return format.format(date);
    }

    @Override
    public int getItemCount() {
        return aList.size();
    }
}
