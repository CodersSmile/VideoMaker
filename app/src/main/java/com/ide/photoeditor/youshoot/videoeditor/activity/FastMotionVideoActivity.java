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

public class FastMotionVideoActivity extends AppCompatActivity {
    String a;
    String b = null;
    Boolean c = Boolean.FALSE;
    FloatingActionButton e;
    int h;
    int videoLength;
    int galleryVideoLength;
    private VideoPlayerState p = new VideoPlayerState();
    private VideoView videoView;
    private String output;
    private long executionId;
    private float mSpeed = 2f;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_fast_motion);
        AdUtils.showNativeAd(FastMotionVideoActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        d();
        Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
        if (lastNonConfigurationInstance != null) {
            this.p = (VideoPlayerState) lastNonConfigurationInstance;
        } else {
            Bundle extras = getIntent().getExtras();
            this.p.setFilename(extras.getString("videofilename"));
            this.b = extras.getString("videofilename");
        }
//        this.o.setText(new File(this.b).getName());
        this.videoView.setOnCompletionListener(mediaPlayer -> {
            FastMotionVideoActivity.this.c = Boolean.FALSE;
            e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        });
        g();
        FastMotionVideoActivity.this.c();

    }

    public void c() {

    }

    public void fastmotioncommand() {

        File file = FileUtils.TEMP_EditDIRECTORY;
        if (!file.exists()) {
            file.mkdirs();
        }

        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());

        output = new File(FileUtils.TEMP_EditDIRECTORY, "VidMaker_Fast_" + format + ".mp4").getAbsolutePath();

        String[] cmd = new String[0];

        if (h == 2) {
            cmd = new String[]{"-i", b, "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]", "-map", "[v]", "-map", "[a]", output};
        } else if (h == 4) {
            cmd = new String[]{"-i", b, "-filter_complex", "[0:v]setpts=0.5*PTS,setpts=0.5*PTS[v];[0:a]atempo=2.0,atempo=2.0[a]", "-map", "[v]", "-map", "[a]", output};
        }

        a(cmd, output);
    }

    private void a(String[] comand, final String str) {

        MediaPlayer mp = MediaPlayer.create(this, getUri(new File(b), FastMotionVideoActivity.this));

        galleryVideoLength = mp.getDuration();

        if (h == 2) {
            videoLength = mp.getDuration() / 2;
            mp.release();
        } else if (h == 4) {
            videoLength = mp.getDuration() / 4;
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
                    addVideo(str);
                    progBar.setProgress(0);
                    dialog.dismiss();
                    if (VideoEditorActivity.getInstance() != null) {
                        VideoEditorActivity.getInstance().finish();
                    }
                    Intent intent = new Intent(getApplicationContext(), VideoEditorActivity.class);
                    intent.putExtra("path", output);
                    startActivity(intent);
                    Toast.makeText(FastMotionVideoActivity.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(FastMotionVideoActivity.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(FastMotionVideoActivity.this, "Execution failed", Toast.LENGTH_SHORT).show();
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
        MediaScannerConnection.scanFile(FastMotionVideoActivity.this,
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

        MaterialButton doubleFast = findViewById(R.id.double_fast);
        MaterialButton quadreFast = findViewById(R.id.quadre_fast);

        h = 2;
        doubleFast.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
        quadreFast.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));

        doubleFast.setOnClickListener(view -> {
            h = 2;
            doubleFast.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
            quadreFast.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
            if (mSpeed != 2f) {
                mSpeed = 2f;
                videoView.stopPlayback();
                g();
            }
        });

        quadreFast.setOnClickListener(view -> {
            h = 4;
            doubleFast.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
            quadreFast.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
            if (mSpeed != 4f) {
                mSpeed = 4f;
                videoView.stopPlayback();
                g();
            }
        });

        MaterialButton close = findViewById(R.id.mMbtnCancel);
        close.setOnClickListener(view -> onBackPressed());

        MaterialButton mMbtnBack = findViewById(R.id.mMbtnBack);
        mMbtnBack.setOnClickListener(view -> onBackPressed());

        MaterialButton save = findViewById(R.id.mMbtnSave);
        save.setOnClickListener(view -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            fastmotioncommand();
        });

        this.e.setOnClickListener(view -> {
            if (c) {
                e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                FastMotionVideoActivity.this.c = Boolean.FALSE;
            } else {
                e.setImageResource(R.drawable.ic_baseline_pause_24);
                FastMotionVideoActivity.this.c = Boolean.TRUE;
            }
            FastMotionVideoActivity.this.h();
        });
        this.videoView = (VideoView) findViewById(R.id.videoView1);
    }


    public void f() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("VidMaker is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> FastMotionVideoActivity.this.finish()).create().show();
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

    private void g() {
        this.videoView.setOnPreparedListener(mediaPlayer -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PlaybackParams myPlayBackParams = new PlaybackParams();
                myPlayBackParams.setSpeed(mSpeed);
                mediaPlayer.setPlaybackParams(myPlayBackParams);
            }
            mediaPlayer.start();
            e.setImageResource(R.drawable.ic_baseline_pause_24);
        });
        this.videoView.setVideoPath(this.p.getFilename());
        this.a = getTimeForTrackFormat(this.videoView.getDuration());
    }

    public void h() {
        if (this.videoView.isPlaying()) {
            this.videoView.pause();
            return;
        }
        this.videoView.start();
    }

    @SuppressLint("DefaultLocale")
    public static String getTimeForTrackFormat(int i2) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) i2), TimeUnit.MILLISECONDS.toSeconds((long) i2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) i2)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.videoView.isPlaying()) {
            this.c = Boolean.FALSE;
            e.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
        this.videoView.seekTo(this.p.getCurrentTime());
    }

    @Override
    public void onPause() {
        super.onPause();
        this.p.setCurrentTime(this.videoView.getCurrentPosition());
    }

}
