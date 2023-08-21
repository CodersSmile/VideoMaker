package com.ide.photoeditor.youshoot.videoeditor.activity;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getUri;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.text.format.Formatter;
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
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoCompressor extends AppCompatActivity {
    static final boolean u = true;
    private String videoPath;
    boolean a = u;
    private int maxtime;
    private int mintime;
    boolean s = false;
    private final int type = 0;
    private FloatingActionButton videoPlayBtn;
    private VideoView w;
    private String x;
    private String y;
    private long executionId;
    int videoLength;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_compress);
        AdUtils.showNativeAd(VideoCompressor.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        this.s = false;
        this.a = u;
        copyCreate();

    }

    @SuppressLint("InvalidWakeLockTag")
    public void copyCreate() {
        this.x = getIntent().getStringExtra("videouri");
        videoPath = this.x;
        this.w = (VideoView) findViewById(R.id.addcutsvideoview);
        this.videoPlayBtn = findViewById(R.id.videoplaybtn);
        MaterialButton close = findViewById(R.id.mMbtnCancel);
        close.setOnClickListener(view -> onBackPressed());
        MaterialButton mMbtnBack = findViewById(R.id.mMbtnBack);
        mMbtnBack.setOnClickListener(view -> onBackPressed());

        MaterialButton save = findViewById(R.id.mMbtnSave);
        save.setOnClickListener(view -> {
            try {
                if (w.isPlaying()) {
                    videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    w.pause();
                }
            } catch (Exception ignored) {
            }
            executeCompressCommand();
        });

        videoSeekBar();

    }

    public void executeCompressCommand() {
        String[] strArr;
        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());

        File file = FileUtils.TEMP_EditDIRECTORY;
        if (!file.exists()) {
            file.mkdirs();
        }

        y = new File(FileUtils.TEMP_EditDIRECTORY, "Compress" + format + ".mp4").getAbsolutePath();

        int width;
        int height;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        try {
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        strArr = new String[]{"-y", "-i", videoPath, "-s", width + "x" + height, "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.y};

        a(strArr, this.y);

    }

    private void a(String[] comand, final String str) {

        MediaPlayer mp = MediaPlayer.create(this, getUri(new File(videoPath), VideoCompressor.this));
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
                    intent.putExtra("path", y);
                    startActivity(intent);
                    Toast.makeText(VideoCompressor.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoCompressor.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoCompressor.this, "Execution failed", Toast.LENGTH_SHORT).show();
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
        MediaScannerConnection.scanFile(VideoCompressor.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public void videoSeekBar() {
        w.setVideoURI(Uri.parse(videoPath));
        w.setOnPreparedListener(mediaPlayer -> {
            if (a) {
                mintime = 0;
                maxtime = mediaPlayer.getDuration();
                w.start();
                w.pause();
                w.seekTo(300);
                return;
            }
            w.start();
            w.pause();
            w.seekTo(VideoCompressor.this.mintime);
        });
        this.w.setOnErrorListener((mediaPlayer, i, i2) -> false);
        this.videoPlayBtn.setOnClickListener(view -> VideoCompressor.this.d());
    }

    public void d() {
        if (this.w.isPlaying()) {
            this.w.pause();
            this.videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            return;
        }
        this.w.start();
        this.videoPlayBtn.setImageResource(R.drawable.ic_baseline_pause_24);
    }

    @Override
    public void onDestroy() {
        if (w.isPlaying()) {
            w.stopPlayback();
        }
        super.onDestroy();
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeForTrackFormat(int i2) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours((long) i2), TimeUnit.MILLISECONDS.toMinutes((long) i2) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) i2)), TimeUnit.MILLISECONDS.toSeconds((long) i2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) i2)));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.a = false;
        try {
            if (w.isPlaying()) {
                videoPlayBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                w.pause();
            }
        } catch (Exception ignored) {
        }
    }

    public void g() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("Editingfy is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> VideoCompressor.this.finish()).create().show();
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
        this.s = false;
        this.x = getIntent().getStringExtra("videouri");
        videoPath = this.x;
    }

}
