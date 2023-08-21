package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getUri;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class VideoMirrorActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    @BindView(R.id.mTextureView)
    TextureView textureView;
    @BindView(R.id.mMbtnHorizontally)
    MaterialButton mMbtnHorizontally;
    @BindView(R.id.mMbtnVertically)
    MaterialButton mMbtnVertically;
    private String videoPath = null;
    static final boolean y = true;

    //    private VideoView videoView;
    private String B;

    private String C;
    int a = 4;
    int b = 0;
    boolean c = y;
    private FloatingActionButton videoPlayBtn;
    private long executionId;
    int videoLength;
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mirror);
        ButterKnife.bind(this);
        AdUtils.showNativeAd(VideoMirrorActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);
        this.c = y;
        copyCreate();

    }

    @SuppressLint("InvalidWakeLockTag")
    public void copyCreate() {
        this.a = e() / 100;
        B = getIntent().getStringExtra("videouri");
        videoPath = B;
//        videoView = findViewById(R.id.addcutsvideoview);
        mPerformMirrorVideo(0);
        b = 1;

        videoPlayBtn = findViewById(R.id.videoplaybtn);
        videoPlayBtn.setOnClickListener(view -> d());

        MaterialButton close = findViewById(R.id.mMbtnCancel);
        close.setOnClickListener(view -> onBackPressed());

        MaterialButton save = findViewById(R.id.mMbtnSave);
        save.setOnClickListener(view -> {
            String[] strArr = new String[0];
            try {
                if (mediaPlayer.isPlaying()) {
                    videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
            } catch (Exception ignored) {
            }

            File file = FileUtils.TEMP_EditDIRECTORY;
            if (!file.exists()) {
                file.mkdirs();
            }

            C = new File(FileUtils.TEMP_EditDIRECTORY, "VidMaker_Mirror_" + System.currentTimeMillis() + ".mp4").getAbsolutePath();
            if (b != 0) {
                if (isMirrored) {
                    strArr = cmdleft(videoPath, C);
                    a(strArr, C);
                } else if (isVertical) {
                    strArr = cmdtop(videoPath, C);
                    a(strArr, C);
                }
            } else {
                Toast.makeText(VideoMirrorActivity.this, "Select Your Option", Toast.LENGTH_LONG).show();
            }
        });

        videoSeekBar();
    }

    public void videoSeekBar() {
       /* videoView.setVideoURI(Uri.parse(videoPath));

        videoView.setOnPreparedListener(mediaPlayer -> {
            videoPlayBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            videoView.start();
        });

        videoView.setOnErrorListener((mediaPlayer, i, i2) -> false);

        videoPlayBtn.setOnClickListener(view -> d());*/

        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(new Surface(surface));
        try {
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    private void a(String[] comand, final String str) {

        MediaPlayer mp = MediaPlayer.create(this, getUri(new File(videoPath), VideoMirrorActivity.this));
        videoLength = mp.getDuration();
        mp.release();


        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_progress);

        Button cancel = dialog.findViewById(R.id.cancel);
        ProgressBar progBar = dialog.findViewById(R.id.progress_bar);
        progBar.setProgress(0);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpeg.cancel(executionId);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        executionId = FFmpeg.executeAsync(comand, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    addVideo(str);
                    progBar.setProgress(0);
                    dialog.dismiss();
                    if (VideoEditorActivity.getInstance() != null) {
                        VideoEditorActivity.getInstance().finish();
                    }
                    Intent intent = new Intent(getApplicationContext(), VideoEditorActivity.class);
                    intent.putExtra("path", C);
                    startActivity(intent);
                    Toast.makeText(VideoMirrorActivity.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoMirrorActivity.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoMirrorActivity.this, "Execution failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {

                float progress = Float.parseFloat(valueOf(newStatistics.getTime())) / videoLength;
                float progressFinal = progress * 100;
                Log.d(Config.TAG, "Video Length: " + progressFinal);
                Log.d(Config.TAG, format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
                Log.d(Config.TAG, format("Quality: %f, time: %f", newStatistics.getVideoQuality(), newStatistics.getVideoFps()));
                progBar.setProgress((int) progressFinal);
//                progText.setText(format("%d%%", (int) progressFinal));

                String frame = "Frame: " + newStatistics.getVideoFrameNumber();
                String fps = "Fps: " + newStatistics.getVideoFps();
                String quality = "Quality: " + newStatistics.getVideoQuality();
                String time = "Time: " + simpleDateFormat.format(newStatistics.getTime());
                String bitrate = "Bitrate: " + newStatistics.getBitrate();
                String speed = "Speed: " + newStatistics.getSpeed();
                String size = "Size: " + Formatter.formatFileSize(getApplicationContext(), newStatistics.getSize());
            }
        });

    }

    public void addVideo(String videoFile) {
        MediaScannerConnection.scanFile(VideoMirrorActivity.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public String[] cmdleft(String str, String str2) {
        return new String[]{"-i", str, "-vf", "hflip", "-c:a", "copy", str2};
    }

    public String[] cmdtop(String str, String str2) {
        return new String[]{"-i", str, "-vf", "vflip", "-c:a", "copy", str2};
    }

    public void d() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            return;
        }
        mediaPlayer.start();
        videoPlayBtn.setImageResource(R.drawable.ic_baseline_pause_24);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private int e() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i2 = displayMetrics.heightPixels;
        int i3 = displayMetrics.widthPixels;
        return Math.min(i3, i2);
    }

    @Override
    public void onPause() {
        super.onPause();
        c = false;
        try {
            if (mediaPlayer.isPlaying()) {
                videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                mediaPlayer.pause();
            }
        } catch (Exception ignored) {
        }
    }

    public void g() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("VidMaker is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> VideoMirrorActivity.this.finish()).create().show();
    }

    public void deleteFromGallery(String str) {
        String[] strArr = {"_id"};
        String[] strArr2 = {str};
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor query = contentResolver.query(uri, strArr, "_data = ?", strArr2, null);
        if (query.moveToFirst()) {
            try {
                contentResolver.delete(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, query.getLong(query.getColumnIndexOrThrow("_id"))), null, null);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                boolean delete = new File(str).delete();
                if (delete) {

                    refreshGallery(str);
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        query.close();
    }

    public void refreshGallery(String str) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(new File(str)));
        sendBroadcast(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        this.B = getIntent().getStringExtra("videouri");
        videoPath = this.B;
    }

    @OnClick({R.id.mMbtnBack, R.id.mMbtnHorizontally, R.id.mMbtnVertically})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mMbtnBack:
                onBackPressed();
                break;
            case R.id.mMbtnHorizontally:
                mPerformMirrorVideo(0);
                break;
            case R.id.mMbtnVertically:
                mPerformMirrorVideo(1);
                break;
        }
    }

    boolean isMirrored = false;
    boolean isVertical = false;

    private void mPerformMirrorVideo(int i) {
        mMbtnHorizontally.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
        mMbtnVertically.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
        switch (i) {
            case 0:
                mMbtnHorizontally.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
                textureView.setScaleX(isMirrored ? -1 : 1);
                textureView.setScaleY(1);
                isVertical = false;
                if (isMirrored) {
                    isMirrored = false;
                } else {
                    isMirrored = true;
                }
                b = 1;
                break;
            case 1:
                mMbtnVertically.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
                textureView.setScaleX(1);
                isMirrored = false;
                textureView.setScaleY(isVertical ? -1 : 1);
                b = 2;
                if (isVertical) {
                    isVertical = false;
                } else {
                    isVertical = true;
                }
                break;
        }
    }
}
