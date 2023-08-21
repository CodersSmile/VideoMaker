package com.ide.photoeditor.youshoot.videoeditor.ui;

public class VideoPlayerState {
    private int a = 0;
    private String b;
    private int d = 0;
    private int e = 0;

    public String getFilename() {
        return this.b;
    }

    public void setFilename(String str) {
        this.b = str;
    }

    public int getStart() {
        return this.d;
    }

    public void setStart(int i) {
        this.d = i;
    }

    public void setStop(int i) {
        this.e = i;
    }


    public int getDuration() {
        return this.e - this.d;
    }

    public int getCurrentTime() {
        return this.a;
    }

    public void setCurrentTime(int i) {
        this.a = i;
    }
}
