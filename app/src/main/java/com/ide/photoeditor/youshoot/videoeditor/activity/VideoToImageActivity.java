package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getRootFileForSave;

public class VideoToImageActivity extends AppCompatActivity implements OnSeekBarChangeListener {
    public static String path = "";
    private ArrayList<Integer> myList = new ArrayList<>();
    static final boolean s = true;
    public static int time;
    Handler a = new Handler();
    VideoView b;
    int c = 0;
    TextView d;
    TextView e;
    FloatingActionButton g;
    SeekBar h;
    int i = 0;
    boolean j = false;
    Bitmap k;
    File l;
    String m;
    File n;
    String o;
    OnClickListener q = (view) -> {
        if (VideoToImageActivity.this.j) {
            b.pause();
            a.removeCallbacks(VideoToImageActivity.this.r);
            g.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        } else {
            b.seekTo(VideoToImageActivity.this.h.getProgress());
            b.start();
            a.postDelayed(VideoToImageActivity.this.r, 200);
            g.setImageResource(R.drawable.ic_baseline_pause_24);
        }
        VideoToImageActivity.this.j ^= VideoToImageActivity.s;
    };
    Runnable r = new Runnable() {
        public void run() {
            if (b.isPlaying()) {
                int currentPosition = b.getCurrentPosition();
                h.setProgress(currentPosition);
                d.setText(VideoEditorActivity.formatTimeUnit((long) currentPosition));
                if (currentPosition == i) {
                    h.setProgress(0);
                    d.setText("00:00");
                    a.removeCallbacks(r);
                    return;
                }
                a.postDelayed(r, 200);
                return;
            }
            h.setProgress(i);
            d.setText(VideoEditorActivity.formatTimeUnit((long) i));
            a.removeCallbacks(r);
        }
    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //nada
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //nada
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_to_image);
        AdUtils.showNativeAd(VideoToImageActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        myList.clear();
        b = findViewById(R.id.videoView_player);
        h = findViewById(R.id.sbVideo);
        h.setOnSeekBarChangeListener(this);
        d = findViewById(R.id.left_pointer);
        e = findViewById(R.id.right_pointer);
        g = findViewById(R.id.btnPlayVideo);
        path = getIntent().getStringExtra("videouri");
        b.setVideoPath(path);
        b.seekTo(100);
        b.setOnErrorListener((mediaPlayer, i, i2) -> {
            Toast.makeText(VideoToImageActivity.this.getApplicationContext(), "Video Player Not Supporting", Toast.LENGTH_SHORT).show();
            return VideoToImageActivity.s;
        });
        b.setOnPreparedListener((OnPreparedListener) mediaPlayer -> {
            VideoToImageActivity.this.i = VideoToImageActivity.this.b.getDuration();
            VideoToImageActivity.this.h.setMax(VideoToImageActivity.this.i);
            VideoToImageActivity.this.d.setText("00:00");
            VideoToImageActivity.this.e.setText(VideoEditorActivity.formatTimeUnit((long) VideoToImageActivity.this.i));
        });
        this.b.setOnCompletionListener(mediaPlayer -> {
            g.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            b.seekTo(0);
            h.setProgress(0);
            d.setText("00:00");
            a.removeCallbacks(VideoToImageActivity.this.r);
            j ^= VideoToImageActivity.s;
        });
        this.g.setOnClickListener(this.q);
        findViewById(R.id.mMbtnSave).setOnClickListener(view -> {
            VideoToImageActivity.time = VideoToImageActivity.this.b.getCurrentPosition() * 1000;
            k = VideoToImageActivity.getVideoFrame(VideoToImageActivity.path);

            storeImage();
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.fromFile(new File(o)));
            sendBroadcast(intent);

            finish();
        });

        findViewById(R.id.mMbtnCancel).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.mMbtnBack).setOnClickListener(view -> onBackPressed());
    }

    public void storeImage() {
        String sb = getRootFileForSave() + "/" +
                getResources().getString(R.string.app_name);
        this.l = new File(sb);
        if (!this.l.exists()) {
            this.l.mkdirs();
        }
        m = "Image_" + System.currentTimeMillis() +
                ".jpg";
        n = new File(l, m);
        o = n.getAbsolutePath();
        try {
            FileOutputStream t = new FileOutputStream(this.n);
            k.compress(CompressFormat.PNG, 90, t);
            Context applicationContext = getApplicationContext();
            Toast.makeText(applicationContext, "Saved\n" + this.o, Toast.LENGTH_LONG).show();
            t.flush();
            t.close();
            if (VideoEditorActivity.getInstance() != null) {
                VideoEditorActivity.getInstance().finish();
            }
            finish();
        } catch (Exception ignored) {
        }
    }

    public static Bitmap getVideoFrame(String str) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(str);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime((long) time);
        try {
            mediaMetadataRetriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
        if (z) {
            this.b.seekTo(i2);
            try {
                this.d.setText(formatTimeUnit((long) i2));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
        }
    }

    @SuppressLint({"NewApi", "DefaultLocale"})
    public static String formatTimeUnit(long j2) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(j2), TimeUnit.MILLISECONDS.toSeconds(j2) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j2)));
    }

    @Override
    public void onPause() {
        this.c = this.b.getCurrentPosition();
        super.onPause();
    }

    @Override
    public void onResume() {
        this.b.seekTo(this.c);
        super.onResume();
    }
}
