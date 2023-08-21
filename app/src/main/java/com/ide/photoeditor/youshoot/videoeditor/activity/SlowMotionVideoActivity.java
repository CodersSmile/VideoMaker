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
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.ide.photoeditor.youshoot.videoeditor.ui.VideoPlayerState;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getUri;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class SlowMotionVideoActivity extends AppCompatActivity {
    static final boolean k = true;
    String a;
    String b = null;
    Boolean d = Boolean.FALSE;
    FloatingActionButton e;
    int multiplier;
    StringBuilder i = new StringBuilder();
    int videoLength;
    int galleryVideoLength;
    MediaPlayer mMediaPlayer;
//    private TextView p;

    private VideoPlayerState q = new VideoPlayerState();
    private final a r = new a();

    private VideoView videoView;
    private String output;
    private long executionId;
    private float mSpeed = 0.2f;

    private class a extends Handler {
        private boolean b;
        private final Runnable c;

        private a() {
            this.b = false;
            this.c = new Runnable() {
                public void run() {
                    a.this.a();
                }
            };
        }

        public void a() {
            if (!this.b) {
                this.b = SlowMotionVideoActivity.k;
                sendEmptyMessage(0);
            }
        }

        @Override
        public void handleMessage(Message message) {
            this.b = false;
            if (!SlowMotionVideoActivity.this.videoView.isPlaying()) {
                if (SlowMotionVideoActivity.this.videoView.isPlaying()) {
                    SlowMotionVideoActivity.this.videoView.pause();
                    SlowMotionVideoActivity.this.d = Boolean.FALSE;
                    e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                return;
            }
            postDelayed(this.c, 50);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_slow_motion);
        AdUtils.showNativeAd(SlowMotionVideoActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        d();
        Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
        if (lastNonConfigurationInstance != null) {
            this.q = (VideoPlayerState) lastNonConfigurationInstance;
        } else {
            Bundle extras = getIntent().getExtras();
            this.q.setFilename(extras.getString("videofilename"));
            b = extras.getString("videofilename");
        }
//        this.p.setText(new File(this.b).getName());
        this.videoView.setOnCompletionListener(mediaPlayer -> {
            SlowMotionVideoActivity.this.d = Boolean.FALSE;
            e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        });
        g();
        SlowMotionVideoActivity.this.c();

    }

    public void c() {

    }

    public void slowmotioncommand() {

        File file = FileUtils.TEMP_EditDIRECTORY;
        if (!file.exists()) {
            file.mkdirs();
        }

        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());

        output = new File(FileUtils.TEMP_EditDIRECTORY, "VidMaker_Slow_" + format + ".mp4").getAbsolutePath();

        String[] cmd = new String[0];

        if (multiplier == 2) {
            cmd = new String[]{"-i", b, "-filter_complex", "[0:v]setpts=" + (float) multiplier + "*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-crf", "27", "-preset", "veryfast", output};
        } else if (multiplier == 4) {
            cmd = new String[]{"-i", b, "-filter_complex", "[0:v]setpts=" + (float) multiplier + "*PTS[v];[0:a]atempo=0.5,atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-crf", "27", "-preset", "veryfast", output};
        }


        a(cmd, output);
    }

    private void a(String[] comand, final String str) {

        MediaPlayer mp = MediaPlayer.create(this, getUri(new File(b), SlowMotionVideoActivity.this));
        galleryVideoLength = mp.getDuration();


        if (multiplier == 2) {
            videoLength = mp.getDuration() * 2;
            mp.release();
        } else if (multiplier == 4) {
            videoLength = mp.getDuration() * 4;
            mp.release();
        }

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
//                    addVideo(str);
                    progBar.setProgress(0);
                    dialog.dismiss();
                    if (VideoEditorActivity.getInstance() != null) {
                        VideoEditorActivity.getInstance().finish();
                    }
                    Intent intent = new Intent(getApplicationContext(), VideoEditorActivity.class);
                    intent.putExtra("path", output);
                    startActivity(intent);
                    Toast.makeText(SlowMotionVideoActivity.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(SlowMotionVideoActivity.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(SlowMotionVideoActivity.this, "Execution failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {
                Log.d(Config.TAG, "Video Length: =====" + videoLength);

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
        MediaScannerConnection.scanFile(SlowMotionVideoActivity.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private void d() {
        e = findViewById(R.id.buttonply1);

        MaterialButton doubleSlow = findViewById(R.id.double_slow);
        MaterialButton quadreSlow = findViewById(R.id.quadre_slow);

        multiplier = 2;
        doubleSlow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
        quadreSlow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));

        doubleSlow.setOnClickListener(view -> {
            multiplier = 2;
            doubleSlow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
            quadreSlow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
            if (mSpeed != 0.2f) {
                mSpeed = 0.2f;
                videoView.stopPlayback();
                g();
            }
        });

        quadreSlow.setOnClickListener(view -> {
            multiplier = 4;
            doubleSlow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
            quadreSlow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
            if (mSpeed != 0.4f) {
                mSpeed = 0.4f;
                videoView.stopPlayback();
                g();
            }
        });

        e.setOnClickListener(view -> {
            if (d) {
                e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                d = Boolean.FALSE;
            } else {
                e.setImageResource(R.drawable.ic_baseline_pause_24);
                SlowMotionVideoActivity.this.d = Boolean.TRUE;
            }
            SlowMotionVideoActivity.this.h();
        });
        this.videoView = (VideoView) findViewById(R.id.videoView1);

        findViewById(R.id.mMbtnCancel).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.mMbtnBack).setOnClickListener(view -> onBackPressed());

        findViewById(R.id.mMbtnSave).setOnClickListener(view -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            slowmotioncommand();
        });
    }

    public void f() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("VidMaker is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> SlowMotionVideoActivity.this.finish()).create().show();
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

    @SuppressLint("NewApi")
    private void g() {
        this.videoView.setOnPreparedListener(mediaPlayer -> {
            PlaybackParams myPlayBackParams = new PlaybackParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                myPlayBackParams.setSpeed(mSpeed); //here set speed eg. 0.5 for slow 2 for fast mode
            }
            mediaPlayer.setPlaybackParams(myPlayBackParams);

            mediaPlayer.start();
            mMediaPlayer = mediaPlayer;
            e.setImageResource(R.drawable.ic_baseline_pause_24);
            SlowMotionVideoActivity.this.a = SlowMotionVideoActivity.getTimeForTrackFormat(mediaPlayer.getDuration());
        });
        this.videoView.setVideoPath(this.q.getFilename());
        this.a = getTimeForTrackFormat(this.videoView.getDuration());
    }

    public void h() {
        if (this.videoView.isPlaying()) {
            this.videoView.pause();
            e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            return;
        }
        this.videoView.start();
        e.setImageResource(R.drawable.ic_baseline_pause_24);
        this.r.a();
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeForTrackFormat(int i2) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) i2), TimeUnit.MILLISECONDS.toSeconds((long) i2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) i2)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.videoView.isPlaying()) {
            this.d = Boolean.FALSE;
            e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
        this.videoView.seekTo(this.q.getCurrentTime());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.q.setCurrentTime(this.videoView.getCurrentPosition());
    }

}
