package com.ide.photoeditor.youshoot.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.core.content.FileProvider;

import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.BuildConfig;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.activity.VideoEditorActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowVideoActivity extends BaseActivity {

    @BindView(R.id.video_view)
    VideoView videoView;
    String video_uri = null;
    private File saved_file;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_video;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        AdUtils.showNativeAd(ShowVideoActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        video_uri = getIntent().getStringExtra("video_uri");
        saved_file = new File(video_uri);

        videoView.setVideoPath(video_uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @OnClick({R.id.mMbtnBack, R.id.mMbtnHome, R.id.mTxtContinue, R.id.mIvShare})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mMbtnBack:
                finish();
                break;
            case R.id.mMbtnHome:
                finish();
                if (MyCreationActivity.getInstance() != null) {
                    MyCreationActivity.getInstance().finish();
                }
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;
            case R.id.mTxtContinue:
                mContinuewEditVideo();
                break;
            case R.id.mIvShare:
                mShareVideo();
                break;
        }
    }

    private void mContinuewEditVideo() {
        Intent i = new Intent(getApplicationContext(), VideoEditorActivity.class);
        i.putExtra("path", video_uri);
        startActivity(i);
    }

    private void mShareVideo() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_TEXT, "Share Via");
        share.putExtra(
                "android.intent.extra.STREAM",
                FileProvider.getUriForFile(
                        getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        saved_file
                )
        );
        share.setType("video/*");
        startActivity(Intent.createChooser(share, "Share Video"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!video_uri.equalsIgnoreCase(null)) {
            videoView.setVideoPath(video_uri);
            videoView.start();

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView = null;
    }
}