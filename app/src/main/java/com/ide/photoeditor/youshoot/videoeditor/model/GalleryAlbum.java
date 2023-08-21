package com.ide.photoeditor.youshoot.videoeditor.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GalleryAlbum {
    @SerializedName("mAlbumId")
    Long mAlbumId;

    @SerializedName("mAlbumName")
    public
    String mAlbumName;

    @SerializedName("mImageList")
    public
    ArrayList<ImageAlbum> mImageList = new ArrayList();

    @SerializedName("mTakenDate")
    public
    String mTakenDate;

    public GalleryAlbum(Long mAlbumId, String mAlbumName) {
        this.mAlbumId = mAlbumId;
        this.mAlbumName = mAlbumName;
    }

    public ArrayList<ImageAlbum> getmImageList() {
        return mImageList;
    }

    public void setmImageList(ImageAlbum list) {
        if (mImageList == null) {
            mImageList = new ArrayList<ImageAlbum>();
        }
        mImageList.add(list);

    }

    public void setSelectimage(int pos, boolean b) {
        mImageList.get(pos).setmSelect(b);
    }
}
