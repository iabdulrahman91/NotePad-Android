package edu.depaul.csc472.notepad;

import java.util.Date;

public class Note implements Comparable {
    private String title;
    private String text;
    private long timestamp;

    public Note(String title, String text, long timestamp) {
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Object o) {
        return Long.compare(((Note) o).getTimestamp(), this.getTimestamp());
    }
}
