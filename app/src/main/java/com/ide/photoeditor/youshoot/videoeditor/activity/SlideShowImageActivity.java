package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.button.MaterialButton;
import com.ide.photoeditor.youshoot.Activity.BaseActivity;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.adapter.GalleryAlbumAdapter;
import com.ide.photoeditor.youshoot.videoeditor.adapter.GalleryImagesAdapter;
import com.ide.photoeditor.youshoot.videoeditor.model.GalleryAlbum;
import com.ide.photoeditor.youshoot.videoeditor.model.ImageAlbum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getContentMediaUri;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.mSelectedImgPath;

public class SlideShowImageActivity extends BaseActivity {

    private String C;
    private long executionId;

    @BindView(R.id.mTxtName)
    TextView mTxtName;
    @BindView(R.id.mMbtnNext)
    MaterialButton mMbtnNext;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mRvImages)
    RecyclerView mRvImages;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    ArrayList<GalleryAlbum> mAlbums = new ArrayList<>();
    GalleryAlbumAdapter galleryAlbumAdapter;
    GalleryImagesAdapter galleryImagesAdapter;
    int mAlbumPos = -1;
    public static SlideShowImageActivity selectImageActivity;
    String[] strCommand;

    public static SlideShowImageActivity getInstance() {
        return selectImageActivity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_image;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        selectImageActivity = this;
        mSelectedImgPath = new ArrayList<>();
        new LoadAlbums().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick({R.id.mMbtnBack, R.id.mMbtnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mMbtnBack:
                onBackPressed();
                break;
            case R.id.mMbtnNext:
//                OnSavedVideos();
                OnImageToVideoAct();
                break;
        }
    }

    private void OnImageToVideoAct() {
        if (mSelectedImgPath.size() > 2) {
            if (mSelectedImgPath.size() <= 10) {
                mMbtnNext.setVisibility(View.GONE);
                Intent intent = new Intent(this, ImageToVideoActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(selectImageActivity, "You can choose upto 10 images..!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(selectImageActivity, "You must choose 2 images..!", Toast.LENGTH_SHORT).show();
        }
    }

    class LoadAlbums extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loadPhotoAlbums();
            return null;
        }

        @Override
        protected void onPostExecute(Void list) {
            super.onPostExecute(list);
            progressBar.setVisibility(View.GONE);
            if (mAlbums != null && mAlbums.size() > 0) {
                AdUtils.showNativeAd(SlideShowImageActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);
                mSetAdapter();
            } else {
                Toast.makeText(SlideShowImageActivity.this, "No Albums!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void mSetAdapter() {
        galleryAlbumAdapter = new GalleryAlbumAdapter(SlideShowImageActivity.this, mAlbums, new GalleryAlbumAdapter.OnAlbumClickListener() {
            @Override
            public void Onclick(GalleryAlbum album, int pos) {
                mAlbumPos = pos;
                mSetImagesAdapter(album);
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(SlideShowImageActivity.this, 1, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(galleryAlbumAdapter);
    }

    private void mSetImagesAdapter(GalleryAlbum album) {
        mSetVisibility();
        mTxtName.setText("" + album.mAlbumName);
        galleryImagesAdapter = new GalleryImagesAdapter(SlideShowImageActivity.this, album.getmImageList(), new GalleryImagesAdapter.OnClickListener() {
            @Override
            public void OnClick(String path, int pos) {
                if (mSelectedImgPath.size() == 10) {
                    Toast.makeText(SlideShowImageActivity.this, "You can select only 10 Images.!!", Toast.LENGTH_SHORT).show();
                } else {
                    mAlbums.get(mAlbumPos).setSelectimage(pos, true);
                    mSelectedImgPath.add(path);
                    galleryImagesAdapter.notifyDataSetChanged();
                    mSetVisibility();
                }
            }

            @Override
            public void OnRemove(String path, int pos) {
                mAlbums.get(mAlbumPos).setSelectimage(pos, false);
                mSelectedImgPath.remove(path);
                galleryImagesAdapter.notifyDataSetChanged();
                mSetVisibility();
            }
        });
        mRvImages.setLayoutManager(new GridLayoutManager(SlideShowImageActivity.this, 4, GridLayoutManager.VERTICAL, false));
        mRvImages.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mRvImages.setAdapter(galleryImagesAdapter);
    }

    private void mSetVisibility() {
        if (mSelectedImgPath.size() >=2) {
            Toast.makeText(selectImageActivity, "Total "+mSelectedImgPath.size()+" Images Selected.!", Toast.LENGTH_SHORT).show();
            mMbtnNext.setVisibility(View.VISIBLE);
        } else {
            mMbtnNext.setVisibility(View.GONE);
        }
    }

    private void loadPhotoAlbums() {
        LinkedHashMap<Long, GalleryAlbum> list = new LinkedHashMap<Long, GalleryAlbum>();
        String[] strings = {"_id", "_data", "bucket_id", "bucket_display_name", "datetaken"};
        Uri uri = getContentMediaUri();
        Cursor cursor = getContentResolver().query(uri, strings, null, null, "date_added DESC");
        ArrayList<GalleryAlbum> arrayList = new ArrayList<GalleryAlbum>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                do {
//                    var r1: String = cursor.getString(cursor.getColumnIndex("bucket_display_name"))
                    Long date = cursor.getLong(cursor.getColumnIndex("datetaken"));
                    String data = cursor.getString(cursor.getColumnIndex("_data"));
                    Long id = cursor.getLong(cursor.getColumnIndex("bucket_id"));
                    String name = cursor.getString(cursor.getColumnIndex("bucket_display_name"));

                    GalleryAlbum galleryAlbum = list.get(id);
                    if (galleryAlbum == null) {
                        galleryAlbum = new GalleryAlbum(id, name);
                        galleryAlbum.mTakenDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                        galleryAlbum.setmImageList(new ImageAlbum(data));
                        list.put(id, galleryAlbum);
                    } else {
                        galleryAlbum.setmImageList(new ImageAlbum(data));
                    }

                } while (cursor.moveToNext());
                arrayList.addAll(list.values());
            }
        }
        cursor.close();

        mAlbums.addAll(arrayList);
    }

    @Override
    public void onBackPressed() {
        if (mRvImages.getVisibility() == View.VISIBLE) {
            mRvImages.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mTxtName.setText("" + getResources().getString(R.string.photoLib));
        } else {
            finish();
        }
    }
}