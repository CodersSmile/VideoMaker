package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ide.photoeditor.youshoot.Activity.HomeActivity;
import com.ide.photoeditor.youshoot.Activity.ShowVideoActivity;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.adapter.VideoToolAdapter;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getRootFileForSave;

public class VideoEditorActivity extends AppCompatActivity implements VideoToolAdapter.OnTItemSelected {
    public static VideoEditorActivity videoEditorActivity;
    static final boolean r = true;
    String path, n;
    VideoView videoView;
    ImageView mIVSave;
    MaterialButton mMbtnBack;
    Handler d = new Handler();
    FloatingActionButton floatingActionButton;
    boolean e = false;
    SeekBar seekBar;
    private VideoToolAdapter mVideoToolAdapter;
    int videoLength;
    RecyclerView recyclerView;
    private long executionId;

    public static VideoEditorActivity getInstance() {
        return videoEditorActivity;
    }

    View.OnClickListener q = view -> {
        if (VideoEditorActivity.this.e) {
            try {
                VideoEditorActivity.this.videoView.pause();
                VideoEditorActivity.this.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(videoEditorActivity, R.drawable.ic_baseline_play_arrow_24));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                VideoEditorActivity.this.videoView.start();
                VideoEditorActivity.this.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(videoEditorActivity, R.drawable.ic_baseline_pause_24));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        VideoEditorActivity.this.e ^= VideoEditorActivity.r;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor);
        videoEditorActivity = this;
        AdUtils.showNativeAd(this, com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);
        path = getIntent().getStringExtra("path");
        videoView = findViewById(R.id.video_layout);
        mMbtnBack = findViewById(R.id.mMbtnBack);
        mIVSave = findViewById(R.id.mIVSave);
        floatingActionButton = findViewById(R.id.btnPlayVideo);
        mVideoToolAdapter = new VideoToolAdapter(this, this);
        recyclerView = findViewById(R.id.recycler_video_tools);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(mVideoToolAdapter);
        recyclerView.setHasFixedSize(true);

        mMbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mIVSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                d();
            }
        });

        this.floatingActionButton.setOnClickListener(this.q);
        videoView.setVideoPath(path);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        });
        File file1 = FileUtils.TEMP_EditDIRECTORY;
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File savefolder =new File(getRootFileForSave(),getResources().getString(R.string.app_name));
        if (!savefolder.exists()) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/"+getResources().getString(R.string.app_name));
            String path = String.valueOf(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues));
            File folder = new File(path);
            boolean isCreada = folder.exists();
            if(!isCreada) {
                folder.mkdirs();

            }
        }

    }

    @SuppressLint({"NewApi", "DefaultLocale"})
    public static String formatTimeUnit(long j2) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(j2), TimeUnit.MILLISECONDS.toSeconds(j2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j2)));
    }

    @Override
    public void onToolSelected(int id) {
        AdUtils.showInterstitialAd(VideoEditorActivity.this, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                switch (id) {
                    case 0:
                        Intent cut = new Intent(VideoEditorActivity.this, VideoCutter.class);
                        cut.putExtra("path", path);
                        startActivity(cut);
                        break;
                    case 1:
                        Intent crop = new Intent(VideoEditorActivity.this, VideoCropActivity.class);
                        crop.putExtra("videofilename", path);
                        startActivity(crop);
                        break;
                    case 2:
                        Intent mute = new Intent(VideoEditorActivity.this, VideoCompressor.class);
                        mute.putExtra("videouri", path);
                        startActivity(mute);
                        break;
                    case 3:
                        Intent rotate = new Intent(VideoEditorActivity.this, VideoRotateActivity.class);
                        rotate.putExtra("videoPath", path);
                        startActivity(rotate);
                        break;
                    case 4:
                        Intent mirror = new Intent(VideoEditorActivity.this, VideoMirrorActivity.class);
                        mirror.putExtra("videouri", path);
                        startActivity(mirror);
                        break;
                    case 5:
                        Intent reverse = new Intent(VideoEditorActivity.this, VideoReverseActivity.class);
                        reverse.putExtra("videouri", path);
                        startActivity(reverse);
                        break;
                    case 6:
                        Intent toMp3 = new Intent(VideoEditorActivity.this, VideoToMP3ConverterActivity.class);
                        toMp3.putExtra("videopath", path);
                        startActivity(toMp3);
                        break;
                    case 7:
                        Intent toGif = new Intent(VideoEditorActivity.this, VideoToGIFActivity.class);
                        toGif.putExtra("videoPath", path);
                        startActivity(toGif);
                        break;
                    case 8:
                        Intent slow = new Intent(VideoEditorActivity.this, SlowMotionVideoActivity.class);
                        slow.putExtra("videofilename", path);
                        startActivity(slow);
                        break;
                    case 9:
                        Intent fast = new Intent(VideoEditorActivity.this, FastMotionVideoActivity.class);
                        fast.putExtra("videofilename", path);
                        startActivity(fast);
                        break;
                    case 10:
                        Intent toImage = new Intent(VideoEditorActivity.this, VideoToImageActivity.class);
                        toImage.putExtra("videouri", path);
                        startActivity(toImage);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void d() {
        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());
        String sb = getRootFileForSave() +"/" +
                getResources().getString(R.string.app_name);
        File file = new File(sb);
        if (!file.exists()) {
            file.mkdirs();
        }

        n = getRootFileForSave() +"/" +
                getResources().getString(R.string.app_name) +
                "/" +
                "VidMaker_Video_" +
                format +
                ".mp4";

        dialog();
        deleteFolder();

    }

    private void deleteFolder() {
        File dir = FileUtils.TEMP_EditDIRECTORY;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    private void dialog() {
        File fileSource = new File(path);
        File fileDestination = new File(n);
        Log.e("path", path);
        try {
            InputStream inputStream = new FileInputStream(fileSource);
            OutputStream outputStream = new FileOutputStream(fileDestination);

            byte[] byteArrayBuffer = new byte[1024];
            int intLength;
            while ((intLength = inputStream.read(byteArrayBuffer)) > 0) {
                outputStream.write(byteArrayBuffer, 0, intLength);
            }

            inputStream.close();
            outputStream.close();
            AdUtils.showInterstitialAd(VideoEditorActivity.this, new AppInterfaces.InterStitialADInterface() {
                @Override
                public void adLoadState(boolean isLoaded) {
                    Intent intent = new Intent(VideoEditorActivity.this, ShowVideoActivity.class);
                    intent.putExtra("video_uri", fileDestination.getAbsolutePath());
                    startActivity(intent);
                    finish();
                }
            });
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        deleteFolder();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        deleteFolder();
    }
}