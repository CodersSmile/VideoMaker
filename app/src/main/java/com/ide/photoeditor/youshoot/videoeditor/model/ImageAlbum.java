package com.ide.photoeditor.youshoot.videoeditor.model;

public class ImageAlbum {

    public String mImage;

    public boolean mSelect = false;

    public ImageAlbum(String string) {
        this.mImage = string;
    }

    public boolean ismSelect() {
        return mSelect;
    }

    public void setmSelect(boolean mSelect) {
        this.mSelect = mSelect;
    }
    public String getmImage() {
        return mImage;
    }

}
