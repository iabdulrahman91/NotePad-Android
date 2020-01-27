package edu.depaul.csc472.notepad;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    TextView titleText;
    TextView bodyText;
    TextView timeText;

    NoteViewHolder(View view) {
        super(view);

        titleText = view.findViewById(R.id.titleText);
        bodyText = view.findViewById(R.id.bodyText);
        timeText = view.findViewById(R.id.timeText);

    }

}
