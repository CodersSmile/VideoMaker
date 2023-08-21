package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

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

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getUri;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class VideoReverseActivity extends AppCompatActivity {
    static final boolean A = true;
    private String videoPath;
    private VideoView videoView;
    private String D;
    private String E;
    boolean a = A;
    int f = 0;
    int g = 4;
    public boolean isInFront = A;
    private FloatingActionButton videoPlayBtn;
    private long executionId;
    int videoLength;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_reverse);
        a = A;
        findId();
        AdUtils.showNativeAd(VideoReverseActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

    }

    public void findId() {
        isInFront = A;
        g = e() / 100;
        f = 0;
        E = getIntent().getStringExtra("videouri");
        videoPath = this.E;
        videoView = (VideoView) findViewById(R.id.addcutsvideoview);
        videoPlayBtn = findViewById(R.id.videoplaybtn);

        MaterialButton mMbtnBack = findViewById(R.id.mMbtnBack);
        MaterialButton close = findViewById(R.id.mMbtnCancel);
        MaterialButton save = findViewById(R.id.mMbtnSave);

        close.setOnClickListener(view -> onBackPressed());
        mMbtnBack.setOnClickListener(view -> onBackPressed());

        save.setOnClickListener(view -> {
            try {
                if (videoView.isPlaying()) {
                    videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    videoView.pause();
                }
            } catch (Exception ignored) {
            }
           /* D = Environment.getExternalStorageDirectory().getAbsoluteFile() +
                    "/Download/temp/";*/
            File file = FileUtils.TEMP_EditDIRECTORY;
            if (!file.exists()) {
                file.mkdirs();
            }
         /*   D = file.getAbsolutePath() +
                    "/VidMaker_Reverse_" +
                    System.currentTimeMillis() +
                    ".mp4";*/
            D= new File(FileUtils.TEMP_EditDIRECTORY, "VidMaker_Reverse_" + System.currentTimeMillis() +".mp4").getAbsolutePath();

            String[] strArr;
            strArr = new String[]{"-y", "-i", videoPath, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", "-filter_complex", "reverse", "-af", "areverse", D};
            a(strArr, D);

        });
        videoSeekBar();
    }

    private void a(String[] comand, final String str) {
        MediaPlayer mp = MediaPlayer.create(this, getUri(new File(videoPath),VideoReverseActivity.this));
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
                    intent.putExtra("path", D);
                    startActivity(intent);
                    Toast.makeText(VideoReverseActivity.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoReverseActivity.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoReverseActivity.this, "Execution failed", Toast.LENGTH_SHORT).show();
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

            }
        });
    }

    public void addVideo(String videoFile) {
        MediaScannerConnection.scanFile(VideoReverseActivity.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public void videoSeekBar() {
        this.videoView.setVideoURI(Uri.parse(videoPath));
        this.videoView.setOnPreparedListener(mediaPlayer -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PlaybackParams myPlayBackParams = new PlaybackParams();
                myPlayBackParams.setSpeed(-1f);
                mediaPlayer.setPlaybackParams(myPlayBackParams);
            }
            mediaPlayer.start();
            videoPlayBtn.setImageResource(R.drawable.ic_baseline_pause_24);
//            videoView.start();
        });
        this.videoView.setOnErrorListener((mediaPlayer, i, i2) -> false);
        this.videoPlayBtn.setOnClickListener(view -> VideoReverseActivity.this.d());
    }

    public void d() {
        if (this.videoView.isPlaying()) {
            this.videoView.pause();
            videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            return;
        }
        this.videoView.start();
        videoPlayBtn.setImageResource(R.drawable.ic_baseline_pause_24);
    }

    @Override
    public void onDestroy() {
        this.f = 0;
        super.onDestroy();
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
        a = false;
        try {
            if (videoView.isPlaying()) {
                videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                videoView.pause();
            }
        } catch (Exception ignored) {
        }
        isInFront = false;
    }

    public void g() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("Editingfy is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> VideoReverseActivity.this.finish()).create().show();
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
        this.isInFront = A;
        this.E = getIntent().getStringExtra("videouri");
        videoPath = this.E;
    }

}
