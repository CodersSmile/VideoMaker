package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ide.photoeditor.youshoot.Activity.HomeActivity;
import com.ide.photoeditor.youshoot.Activity.IntroActivity;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.adapter.VideoFilesAdapter;
import com.ide.photoeditor.youshoot.videoeditor.model.VideoData;
import com.ide.photoeditor.youshoot.videoeditor.utils.ContentUtill;

import java.util.ArrayList;
import java.util.List;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getContentVideoUri;

public class VideoListActivity extends AppCompatActivity implements View.OnClickListener, VideoFilesAdapter.VideoFilesHandler {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_list);

        RecyclerView videos = findViewById(R.id.videos_rv);
        MaterialButton close = findViewById(R.id.mMbtnBack);

        close.setOnClickListener(this);

        TextView emptyState = findViewById(R.id.empty_state);

        if (!getVideos().isEmpty()) {
            AdUtils.showNativeAd(VideoListActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

            emptyState.setVisibility(View.INVISIBLE);
            VideoFilesAdapter adapter = new VideoFilesAdapter(getVideos(), this);
            GridLayoutManager manager = new GridLayoutManager(this, 3);
            videos.setAdapter(adapter);
            videos.setLayoutManager(manager);
        } else {
            emptyState.setVisibility(View.VISIBLE);
        }

    }

    private List<VideoData> getVideos() {
        List<VideoData> list = new ArrayList<>();
        Cursor managedQuery = managedQuery(getContentVideoUri(), new String[]{"_data", "_id", "_display_name", "duration"}, null, null, " _id DESC");
        int count = managedQuery.getCount();
        managedQuery.moveToFirst();
        for (int i = 0; i < count; i++) {
            Uri withAppendedPath = Uri.withAppendedPath(getContentVideoUri(), ContentUtill.getLong(managedQuery));
            list.add(new VideoData(managedQuery.getString(managedQuery.getColumnIndexOrThrow("_display_name")), withAppendedPath, managedQuery.getString(managedQuery.getColumnIndex("_data")), ContentUtill.getTime(managedQuery, "duration")));
            managedQuery.moveToNext();
        }
        return list;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mMbtnBack) {
            onBackPressed();
        }
    }

    @Override
    public void onChoose(int position) {
        AdUtils.showInterstitialAd(VideoListActivity.this, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                Intent intent = new Intent(VideoListActivity.this, VideoEditorActivity.class);
                intent.putExtra("path", getVideos().get(position).videoPath);
                startActivity(intent);
                finish();
            }
        });

    }
}
