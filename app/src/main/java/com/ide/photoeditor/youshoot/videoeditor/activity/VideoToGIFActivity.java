package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.ui.VideoPlayerState;
import com.ide.photoeditor.youshoot.videoeditor.utils.VideoSliceSeekBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getRootFileForSave;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class VideoToGIFActivity extends AppCompatActivity {
    static final boolean i = true;
    private String outputPath;
    private Bitmap thumb;
    int videoLength;
    File a;
    String b;
    String c = null;
    Boolean d = Boolean.FALSE;
    String e = "00";
    FloatingActionButton f;
    VideoSliceSeekBar g;
    OnClickListener h = (view) -> {
        if (d) {
            f.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            VideoToGIFActivity.this.d = Boolean.FALSE;
        } else {
            f.setImageResource(R.drawable.ic_baseline_pause_24);
            VideoToGIFActivity.this.d = Boolean.TRUE;
        }
        e();
    };

    private TextView k;

    private TextView l;

    private TextView n;

    private VideoPlayerState o = new VideoPlayerState();
    private final a p = new a();

    public VideoView q;
    private long executionId;

    @SuppressLint("HandlerLeak")
    private class a extends Handler {
        private boolean b;
        private final Runnable c;

        private a() {
            this.b = false;
            this.c = () -> a.this.a();
        }


        public void a() {
            if (!this.b) {
                this.b = VideoToGIFActivity.i;
                sendEmptyMessage(0);
            }
        }

        @Override
        public void handleMessage(Message message) {
            this.b = false;
            VideoToGIFActivity.this.g.videoPlayingProgress(VideoToGIFActivity.this.q.getCurrentPosition());
            if (!VideoToGIFActivity.this.q.isPlaying() || VideoToGIFActivity.this.q.getCurrentPosition() >= VideoToGIFActivity.this.g.getRightProgress()) {
                if (VideoToGIFActivity.this.q.isPlaying()) {
                    VideoToGIFActivity.this.q.pause();
                    VideoToGIFActivity.this.d = Boolean.FALSE;
                    VideoToGIFActivity.this.q.seekTo(100);
                    f.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                VideoToGIFActivity.this.g.setSliceBlocked(false);
                VideoToGIFActivity.this.g.removeVideoStatusThumb();
                return;
            }
            postDelayed(this.c, 50);
        }
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_to_gif);
        AdUtils.showNativeAd(VideoToGIFActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        String sb3 = getRootFileForSave()+"/" +
                getResources().getString(R.string.app_name) +
                "/";
        a = new File(sb3);
        if (!this.a.exists()) {
            this.a.mkdirs();
        }

        k = (TextView) findViewById(R.id.left_pointer);
        l = (TextView) findViewById(R.id.right_pointer);
        f = findViewById(R.id.buttonply);
        g = (VideoSliceSeekBar) findViewById(R.id.seek_barr);
        q = (VideoView) findViewById(R.id.videoView1);
        n = (TextView) findViewById(R.id.dur);
        Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
        if (lastNonConfigurationInstance != null) {
            this.o = (VideoPlayerState) lastNonConfigurationInstance;
        } else {
            Bundle extras = getIntent().getExtras();
            this.o.setFilename(extras.getString("videoPath"));
            this.c = extras.getString("videoPath");
            thumb = ThumbnailUtils.createVideoThumbnail(this.o.getFilename(), 1);
        }
        d();
        this.q.setOnCompletionListener(mediaPlayer -> {
            VideoToGIFActivity.this.d = Boolean.FALSE;
            f.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        });
        this.f.setOnClickListener(this.h);

        findViewById(R.id.mMbtnCancel).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.mMbtnBack).setOnClickListener(view -> onBackPressed());

        findViewById(R.id.mMbtnSave).setOnClickListener(view -> {
            if (q.isPlaying()) {
                q.pause();
                f.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            gifcommand();
        });

    }

    public void gifcommand() {
        String valueOf = String.valueOf(this.o.getStart() / 1000);
        String valueOf2 = String.valueOf(this.o.getDuration() / 1000);

        outputPath = getRootFileForSave() +"/" +
                getResources().getString(R.string.app_name) +
                "/" +
                "VidMaker_GIF_" + System.currentTimeMillis() +
                ".gif";

        a(new String[]{"-ss", valueOf, "-t", valueOf2, "-i", c, "-pix_fmt", "rgb24", "-filter:v", "scale=-1:320", outputPath}, outputPath);
    }

    private void a(String[] comand, final String str) {
        videoLength = this.o.getDuration();
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_progress);

        Button cancel = dialog.findViewById(R.id.cancel);
        ProgressBar progBar = dialog.findViewById(R.id.progress_bar);
        progBar.setProgress(0);
        cancel.setOnClickListener(new OnClickListener() {
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
                    addImage(str);
                    progBar.setProgress(0);
                    dialog.dismiss();
                    if (VideoEditorActivity.getInstance() != null) {
                        VideoEditorActivity.getInstance().finish();
                    }
                    Toast.makeText(VideoToGIFActivity.this, "Saved at " + str, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoToGIFActivity.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoToGIFActivity.this, "Execution failed", Toast.LENGTH_SHORT).show();
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

    public void addImage(String videoFile) {
        MediaScannerConnection.scanFile(VideoToGIFActivity.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private void d() {
        this.q.setOnPreparedListener(mediaPlayer -> {
            VideoToGIFActivity.this.g.setSeekBarChangeListener((i, i2) -> {
                if (VideoToGIFActivity.this.g.getSelectedThumb() == 1) {
                    VideoToGIFActivity.this.q.seekTo(VideoToGIFActivity.this.g.getLeftProgress());
                }
                VideoToGIFActivity.this.k.setText(VideoToGIFActivity.getTimeForTrackFormat(i, VideoToGIFActivity.i));
                VideoToGIFActivity.this.l.setText(VideoToGIFActivity.getTimeForTrackFormat(i2, VideoToGIFActivity.i));
                VideoToGIFActivity.this.e = VideoToGIFActivity.getTimeForTrackFormat(i, VideoToGIFActivity.i);
                VideoToGIFActivity.this.o.setStart(i);
                VideoToGIFActivity.this.b = VideoToGIFActivity.getTimeForTrackFormat(i2, VideoToGIFActivity.i);
                VideoToGIFActivity.this.o.setStop(i2);
                TextView g = VideoToGIFActivity.this.n;
                int i3 = (i2 / 1000) - (i / 1000);
                String sb = "duration : " +
                        String.format("%02d:%02d:%02d", i3 / 3600, (i3 % 3600) / 60, i3 % 60);
                g.setText(sb);
            });
            VideoToGIFActivity.this.b = VideoToGIFActivity.getTimeForTrackFormat(mediaPlayer.getDuration(), VideoToGIFActivity.i);
            VideoToGIFActivity.this.g.setMaxValue(mediaPlayer.getDuration());
            VideoToGIFActivity.this.g.setLeftProgress(0);
            VideoToGIFActivity.this.g.setRightProgress(mediaPlayer.getDuration());
            VideoToGIFActivity.this.g.setProgressMinDiff(0);
            VideoToGIFActivity.this.q.seekTo(100);
        });
        this.q.setVideoPath(this.o.getFilename());
        this.q.seekTo(0);
        this.b = getTimeForTrackFormat(this.q.getDuration(), i);
    }

    public void e() {
        if (this.q.isPlaying()) {
            this.q.pause();
            this.g.setSliceBlocked(false);
            this.g.removeVideoStatusThumb();
            return;
        }
        this.q.seekTo(this.g.getLeftProgress());
        this.q.start();
        this.g.videoPlayingProgress(this.g.getLeftProgress());
        this.p.a();
    }

    @SuppressLint({"NewApi", "DefaultLocale"})
    public static String getTimeForTrackFormat(int i2, boolean z) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) i2), TimeUnit.MILLISECONDS.toSeconds((long) i2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) i2)));
    }

    @SuppressLint({"WakelockTimeout"})
    @Override
    public void onResume() {
        super.onResume();
        this.q.seekTo(this.o.getCurrentTime());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.o.setCurrentTime(this.q.getCurrentPosition());
    }

    public void g() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("Editingfy is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> VideoToGIFActivity.this.finish()).create().show();
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

}
