package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;
import com.ide.photoeditor.youshoot.videoeditor.utils.VideoPlayerState;
import com.ide.photoeditor.youshoot.videoeditor.utils.VideoSliceSeekBar;
import com.ide.photoeditor.youshoot.videoeditor.utils.VideoSliceSeekBar.SeekBarChangeListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class VideoCutter extends AppCompatActivity implements MediaScannerConnectionClient, OnClickListener {
    static final boolean k = true;
    MediaScannerConnection a;
    int b = 0;
    int c = 0;
    TextView d;
    TextView e;
    TextView g;
    FloatingActionButton play;
    VideoSliceSeekBar seekBar;
    VideoView j;
    @BindView(R.id.mMbtnBack)
    MaterialButton mMbtnBack;
    private String m;
    private String n;
    private VideoPlayerState o = new VideoPlayerState();
    private long executionId;
    int videoLength = 0;
    String format;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cut);
        ButterKnife.bind(this);
        AdUtils.showNativeAd(VideoCutter.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        play = findViewById(R.id.buttonply1);
        this.seekBar = findViewById(R.id.seek_bar1);
        this.d = findViewById(R.id.left_pointer);
        this.e = findViewById(R.id.right_pointer);
        this.g = findViewById(R.id.dur);
        this.j = findViewById(R.id.videoView1);

        MaterialButton close = findViewById(R.id.mMbtnCancel);
        MaterialButton save = findViewById(R.id.mMbtnSave);
        close.setOnClickListener(this);
        mMbtnBack.setOnClickListener(this);
        save.setOnClickListener(this);
        this.play.setOnClickListener(this);

        this.m = getIntent().getStringExtra("path");
        if (this.m == null) {
            finish();
        }

        this.j.setVideoPath(this.m);
        this.j.seekTo(100);
        e();
        this.j.setOnCompletionListener(mediaPlayer -> VideoCutter.this.play.setImageResource(R.drawable.ic_baseline_play_arrow_24));
    }


    private void d() {

        String valueOf = valueOf(c);
        String valueOf2 = valueOf(b - c);
        Log.e("TAG", "d:=====" + valueOf + "==" + valueOf2);
        format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());
        File file1 = FileUtils.TEMP_EditDIRECTORY;
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file =new File(FileUtils.TEMP_DIRECTORY, "audio.txt");// new File(sb);
        if (!file.exists()) {
            file.mkdirs();
        }

       n= new File(FileUtils.TEMP_EditDIRECTORY, "VidMaker_Cut_" + format +".mp4").getAbsolutePath();
        Log.e("TAG", "d:=========path==========="+n );
        a(new String[]{"-ss", valueOf, "-t", valueOf2, "-i", m, n}, this.n);

    }

    private void a(String[] comand, final String str) {
        videoLength = (b - c) * 1000;
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
                    addVideo(str);
                    progBar.setProgress(0);
                    dialog.dismiss();
                    if (VideoEditorActivity.getInstance() != null) {
                        VideoEditorActivity.getInstance().finish();
                    }
                    Log.e("TAG", "d:=========path======saved====="+n );
                    Intent intent = new Intent(getApplicationContext(), VideoEditorActivity.class);
                    intent.putExtra("path", n);
                    startActivity(intent);
                    Toast.makeText(VideoCutter.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoCutter.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoCutter.this, "Execution failed", Toast.LENGTH_SHORT).show();
                }
            }

        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        Config.enableStatisticsCallback(new StatisticsCallback() {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
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
        MediaScannerConnection.scanFile(VideoCutter.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private void e() {
        this.j.setOnPreparedListener((OnPreparedListener) mediaPlayer -> {
            VideoCutter.this.seekBar.setSeekBarChangeListener(new SeekBarChangeListener() {
                @SuppressLint("DefaultLocale")
                public void SeekBarValueChanged(int i, int i2) {
                    if (VideoCutter.this.seekBar.getSelectedThumb() == 1) {
                        VideoCutter.this.j.seekTo(VideoCutter.this.seekBar.getLeftProgress());
                    }
                    VideoCutter.this.d.setText(formatTimeUnit((long) i));
                    VideoCutter.this.e.setText(formatTimeUnit((long) i2));
                    VideoCutter.this.o.setStart(i);
                    VideoCutter.this.o.setStop(i2);
                    VideoCutter.this.c = i / 1000;
                    VideoCutter.this.b = i2 / 1000;
                    TextView textView = VideoCutter.this.g;
                    textView.setText(format("%02d:%02d:%02d", (VideoCutter.this.b - VideoCutter.this.c) / 3600, ((VideoCutter.this.b - VideoCutter.this.c) % 3600) / 60, (VideoCutter.this.b - VideoCutter.this.c) % 60));
                }
            });
            VideoCutter.this.seekBar.setMaxValue(mediaPlayer.getDuration());
            VideoCutter.this.seekBar.setLeftProgress(0);
            VideoCutter.this.seekBar.setRightProgress(mediaPlayer.getDuration());
            VideoCutter.this.seekBar.setProgressMinDiff(0);
        });
    }

    private void f() {
        if (this.j.isPlaying()) {
            this.j.pause();
            this.seekBar.setSliceBlocked(true);
            this.play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            this.seekBar.removeVideoStatusThumb();
            return;
        }
        this.j.seekTo(this.seekBar.getLeftProgress());
        this.j.start();
        this.seekBar.videoPlayingProgress(this.seekBar.getLeftProgress());
        this.play.setImageResource(R.drawable.ic_baseline_pause_24);
    }

    @SuppressLint("DefaultLocale")
    public static String formatTimeUnit(long j2) {
        return format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(j2), TimeUnit.MILLISECONDS.toSeconds(j2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j2)));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonply1) {
            f();
        } else if (id == R.id.mMbtnCancel || id == R.id.mMbtnBack) {
            onBackPressed();
        } else if (id == R.id.mMbtnSave) {
            if (this.j.isPlaying()) {
                this.j.pause();
                this.play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            d();
        }
    }

    @Override
    public void onMediaScannerConnected() {
        String l = "";
        this.a.scanFile(l, "video/*");
    }

    @Override
    public void onScanCompleted(String str, Uri uri) {
        this.a.disconnect();
    }


    public void h() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("VidMaker is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> VideoCutter.this.finish()).create().show();
    }

    public void refreshGallery(String str) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(new File(str)));
        sendBroadcast(intent);
    }

}
