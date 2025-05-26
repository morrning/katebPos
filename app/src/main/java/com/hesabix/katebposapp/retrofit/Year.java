package com.hesabix.katebposapp.retrofit;

import com.google.gson.annotations.SerializedName;

public class Year {
    private int id;
    private String label;
    private boolean head;
    private long start;
    private long end;
    private long now;
    @SerializedName("startShamsi")
    private String startShamsi;
    @SerializedName("endShamsi")
    private String endShamsi;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public boolean isHead() { return head; }
    public void setHead(boolean head) { this.head = head; }
    public long getStart() { return start; }
    public void setStart(long start) { this.start = start; }
    public long getEnd() { return end; }
    public void setEnd(long end) { this.end = end; }
    public long getNow() { return now; }
    public void setNow(long now) { this.now = now; }
    public String getStartShamsi() { return startShamsi; }
    public void setStartShamsi(String startShamsi) { this.startShamsi = startShamsi; }
    public String getEndShamsi() { return endShamsi; }
    public void setEndShamsi(String endShamsi) { this.endShamsi = endShamsi; }
}
