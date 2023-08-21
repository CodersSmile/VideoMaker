package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.MyApp;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.cutomView.OnProgressReceiver;
import com.ide.photoeditor.youshoot.cutomView.theme.THEMES;
import com.ide.photoeditor.youshoot.services.CreateVideoService;
import com.ide.photoeditor.youshoot.services.ImageCreatorService;
import com.ide.photoeditor.youshoot.videoeditor.adapter.ThemeAdapter;
import com.ide.photoeditor.youshoot.videoeditor.model.MusicData;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.mSelectedImgPath;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class ImageToVideoActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private MyApp application;
    private ArrayList<String> arrayList = new ArrayList<>();
    //    private BottomSheetBehavior<View> behavior;
    private Float[] duration = new Float[]{Float.valueOf(1.0f), Float.valueOf(1.5f), Float.valueOf(2.0f), Float.valueOf(2.5f), Float.valueOf(3.0f), Float.valueOf(3.5f), Float.valueOf(4.0f)};
    int f21i = 0;
    private View flLoader;
    int frame;
    private RequestManager glide;
    private Handler handler = new Handler();
    protected int id;
    LayoutInflater inflater;
    boolean isFromTouch = false;
    private ImageView ivFrame;
    private View ivPlayPause;
    private ImageView ivPreview;
    ArrayList<String> lastData = new ArrayList();
    //    private LinearLayout llEdit;
    private LockRunnable lockRunnable = new LockRunnable();
    private MediaPlayer mPlayer;
    //    private RecyclerView rvDuration;
//    private RecyclerView rvFrame;
    private RecyclerView rvThemes;
    private float seconds = 2.0f;
    private SeekBar seekBar;
    private ThemeAdapter themeAdapter;
    //    private Toolbar toolbar;
    private TextView tvEndTime;
    private TextView tvTime;
    public static ImageToVideoActivity imageToVideoActivity;

    public static ImageToVideoActivity getInstance() {
        return imageToVideoActivity;
    }


    class C05864 extends Thread {
        C05864() {
        }

        public void run() {
            Glide.get(ImageToVideoActivity.this).clearDiskCache();
        }
    }

    class C05906 extends Thread {

        class C05892 implements Runnable {
            C05892() {
            }

            public void run() {
                reinitMusic();
                lockRunnable.play();
            }
        }

        C05906() {
        }

        public void run() {
            THEMES themes = application.selectedTheme;
            try {
                FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
                File tempFile = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
                if (tempFile.exists()) {
                    FileUtils.deleteFile(tempFile);
                }
                InputStream in = getResources().openRawResource(themes.getThemeMusic());
                FileOutputStream out = new FileOutputStream(tempFile);
                byte[] buff = new byte[1024];
                while (true) {
                    int read = in.read(buff);
                    if (read <= 0) {
                        break;
                    }
                    out.write(buff, 0, read);
                }
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(tempFile.getAbsolutePath());
                player.setAudioStreamType(3);
                player.prepare();
                final MusicData musicData = new MusicData();
                musicData.track_data = tempFile.getAbsolutePath();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        musicData.track_duration = (long) mp.getDuration();
                        mp.stop();
                    }
                });
                musicData.track_Title = "temp";
                application.setMusicData(musicData);
            } catch (Exception e) {
            }
            runOnUiThread(new C05892());
        }
    }

    class LockRunnable implements Runnable {
        ArrayList<String> appList = new ArrayList();
        boolean isPause = false;

        class C05921 implements Animation.AnimationListener {
            C05921() {
            }

            public void onAnimationStart(Animation animation) {
                ivPlayPause.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                ivPlayPause.setVisibility(View.INVISIBLE);
            }
        }

        class C05932 implements Animation.AnimationListener {
            C05932() {
            }

            public void onAnimationStart(Animation animation) {
                ivPlayPause.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        }

        LockRunnable() {
        }

        void setAppList(ArrayList<String> appList) {
            this.appList.clear();
            this.appList.addAll(appList);
        }

        public void run() {
            displayImage();
            if (!this.isPause) {
                handler.postDelayed(lockRunnable, (long) Math.round(50.0f * seconds));
            }
        }

        public boolean isPause() {
            return this.isPause;
        }

        public void play() {
            this.isPause = false;
            playMusic();
            handler.postDelayed(lockRunnable, (long) Math.round(50.0f * seconds));
            Animation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(500);
            animation.setFillAfter(true);
            animation.setAnimationListener(new C05921());
            ivPlayPause.startAnimation(animation);
//            if (llEdit.getVisibility() != View.VISIBLE) {
//                llEdit.setVisibility(View.VISIBLE);
//                application.isEditModeEnable = false;
//                if (ImageCreatorService.isImageComplate) {
//                    Intent intent = new Intent(getApplicationContext(), ImageCreatorService.class);
//                    intent.putExtra(ImageCreatorService.EXTRA_SELECTED_THEME, application.getCurrentTheme());
//                    startService(intent);
//                }
//            }
           /* if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }*/
        }

        public void pause() {
            this.isPause = true;
            pauseMusic();
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(500);
            animation.setFillAfter(true);
            ivPlayPause.startAnimation(animation);
            animation.setAnimationListener(new C05932());
        }

        public void stop() {
            pause();
            f21i = 0;
            if (mPlayer != null) {
                mPlayer.stop();
            }
            reinitMusic();
            seekBar.setProgress(f21i);
        }
    }

    class C10211 extends BottomSheetBehavior.BottomSheetCallback {
        C10211() {
        }

        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == 3 && !lockRunnable.isPause()) {
                lockRunnable.pause();
            }
        }

        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    }

    class C12412 extends SimpleTarget<Bitmap> {
        C12412() {
        }

     /*   public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
            ivPreview.setImageBitmap(resource);
        }*/

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            ivPreview.setImageBitmap(resource);

        }
    }

    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    private void reinitMusic() {
        Exception e;
        MusicData musicData = this.application.getMusicData();
        if (musicData != null) {
            this.mPlayer = MediaPlayer.create(this, Uri.parse(musicData.track_data));
            this.mPlayer.setLooping(true);
            try {
                this.mPlayer.prepare();
                return;
            } catch (Exception e2) {
                e = e2;
            }
        } else {
            return;
        }
        e.printStackTrace();
    }

    private void playMusic() {
        if (this.flLoader.getVisibility() != View.VISIBLE && this.mPlayer != null && !this.mPlayer.isPlaying()) {
            this.mPlayer.start();
        }
    }

    private void pauseMusic() {
        if (this.mPlayer != null && this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.application = MyApp.getInstance();
        this.application.videoImages.clear();
        MyApp.isBreak = false;
        Intent intent = new Intent(getApplicationContext(), ImageCreatorService.class);
        intent.putExtra(ImageCreatorService.EXTRA_SELECTED_THEME, this.application.getCurrentTheme());
        startService(intent);
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_image_to_video);

        AdUtils.showNativeAd(ImageToVideoActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);
        imageToVideoActivity = this;
        bindView();
        init();
        addListner();
    }

    private void bindView() {
        this.flLoader = findViewById(R.id.flLoader);
        this.ivPreview = (ImageView) findViewById(R.id.previewImageView1);
        this.ivFrame = (ImageView) findViewById(R.id.ivFrame);
        this.seekBar = (SeekBar) findViewById(R.id.sbPlayTime);
        this.tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        this.tvTime = (TextView) findViewById(R.id.tvTime);
        this.ivPlayPause = findViewById(R.id.ivPlayPause);
        this.rvThemes = (RecyclerView) findViewById(R.id.rvThemes);
        ((MaterialButton) findViewById(R.id.mMbtnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((MaterialButton) findViewById(R.id.mMbtnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProgress();
            }
        });
    }

    private void init() {
        this.seconds = this.application.getSecond();
        this.inflater = LayoutInflater.from(this);
        this.glide = Glide.with((FragmentActivity) this);
        this.application = MyApp.getInstance();
        this.arrayList.addAll(mSelectedImgPath);
        this.seekBar.setMax((this.arrayList.size() - 1) * 30);
        int total = (int) (((float) (this.arrayList.size() - 1)) * this.seconds);
        this.tvEndTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(total / 60), Integer.valueOf(total % 60)}));
        setUpThemeAdapter();
        this.glide.load(((String) mSelectedImgPath.get(0))).into(this.ivPreview);
        setTheme();
    }

    private void setUpThemeAdapter() {
        this.themeAdapter = new ThemeAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager((Context) this, 1, RecyclerView.HORIZONTAL, false);
        GridLayoutManager gridLayoutManagerFrame = new GridLayoutManager((Context) this, 1, RecyclerView.HORIZONTAL, false);
        this.rvThemes.setLayoutManager(gridLayoutManager);
        this.rvThemes.setItemAnimator(new DefaultItemAnimator());
        this.rvThemes.setAdapter(this.themeAdapter);
    }

    private void addListner() {
        findViewById(R.id.video_clicker).setOnClickListener(this);
        this.seekBar.setOnSeekBarChangeListener(this);
        findViewById(R.id.mMCVAddMusic).setOnClickListener(this);
    }

    private synchronized void displayImage() {
        try {
            if (this.f21i >= this.seekBar.getMax()) {
                this.f21i = 0;
                this.lockRunnable.stop();
            } else {
                if (this.f21i > 0 && this.flLoader.getVisibility() == View.VISIBLE) {
                    this.flLoader.setVisibility(View.GONE);
                    if (!(this.mPlayer == null || this.mPlayer.isPlaying())) {
                        this.mPlayer.start();
                    }
                }
                this.seekBar.setSecondaryProgress(this.application.videoImages.size());
                if (this.seekBar.getProgress() < this.seekBar.getSecondaryProgress()) {
                    this.f21i %= this.application.videoImages.size();
//                    this.glide.load((String) this.application.videoImages.get(this.f21i)).signature(new MediaStoreSignature("image/*", System.currentTimeMillis(), 0)).into(new C12412());
                    this.glide.load((String) this.application.videoImages.get(this.f21i)).signature(new MediaStoreSignature("image/*", System.currentTimeMillis(), 0)).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            ivPreview.setImageBitmap(((BitmapDrawable)resource).getBitmap());
                        }
                    });
                    this.f21i++;
                    if (!this.isFromTouch) {
                        this.seekBar.setProgress(this.f21i);
                    }
                    int j = (int) ((((float) this.f21i) / 30.0f) * this.seconds);
                    int mm = j / 60;
                    int ss = j % 60;
                    this.tvTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(mm), Integer.valueOf(ss)}));
                    int total = (int) (((float) (this.arrayList.size() - 1)) * this.seconds);
                    this.tvEndTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(total / 60), Integer.valueOf(total % 60)}));
                }
            }
        } catch (Exception e) {
            this.glide = Glide.with(this);
        }
    }

    @SuppressLint({"WrongConstant"})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mMCVAddMusic:
                this.flLoader.setVisibility(8);
                this.id = R.id.mMCVAddMusic;
                loadSongSelection();
                return;
            case R.id.video_clicker:
                if (this.lockRunnable.isPause()) {
                    this.lockRunnable.play();
                    return;
                } else {
                    this.lockRunnable.pause();
                    return;
                }
            default:
                return;
        }
    }

    protected void onPause() {
        super.onPause();
        this.lockRunnable.pause();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.application.isEditModeEnable = false;
        if (resultCode == -1) {
            int total;
            Intent intent;
            switch (requestCode) {
                case 101:
                    this.application.isFromSdCardAudio = true;
                    this.f21i = 0;
                    reinitMusic();
                    return;

                default:
                    return;
            }
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.f21i = progress;
        if (this.isFromTouch) {
            seekBar.setProgress(Math.min(progress, seekBar.getSecondaryProgress()));
            displayImage();
            seekMediaPlayer();
        }
    }

    private void seekMediaPlayer() {
        if (this.mPlayer != null) {
            try {
                this.mPlayer.seekTo(((int) (((((float) this.f21i) / 30.0f) * this.seconds) * 1000.0f)) % this.mPlayer.getDuration());
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.isFromTouch = true;
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.isFromTouch = false;
    }

    public void reset() {
        MyApp.isBreak = false;
        this.application.videoImages.clear();
        this.handler.removeCallbacks(this.lockRunnable);
        this.lockRunnable.stop();
        Glide.get(this).clearMemory();
        new C05864().start();
        FileUtils.deleteTempDir();
        this.glide = Glide.with((FragmentActivity) this);
        this.flLoader.setVisibility(View.VISIBLE);
        setTheme();
    }

    public void onBackPressed() {
        finish();
    }

    public void setTheme() {
        if (this.application.isFromSdCardAudio) {
            this.lockRunnable.play();
        } else {
            new C05906().start();
        }
    }

    private void loadSongSelection() {
        startActivityForResult(new Intent(this, SongEditActivity.class), 101);
    }

    private void loadProgress() {
        this.lockRunnable.pause();
        this.handler.removeCallbacks(this.lockRunnable);

        createVideo();

        /*startService(new Intent(this, CreateVideoService.class));
        Intent intent2 = new Intent(this.application, ProgressActivity.class);
        intent2.setFlags(268468224);
        startActivity(intent2);
        finish();*/
    }

    String timeRe;
    private float toatalSecond;
    String[] inputCode;
    long startTime;
    private File audioFile;
    private File audioIp;
    Dialog dialog;
    ProgressBar progBar;
    Button cancel;

    private void createVideo() {
         dialog = new Dialog(ImageToVideoActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_progress);

        cancel = dialog.findViewById(R.id.cancel);
        progBar = dialog.findViewById(R.id.progress_bar);
        progBar.setProgress(0);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpeg.cancel(executionId);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        startTime = System.currentTimeMillis();
        this.toatalSecond = (this.application.getSecond() * ((float) Global.mSelectedImgPath.size())) - 1.0f;
        Log.e("totalseconds", String.valueOf(this.toatalSecond) + "_totalSecond");
        mStartNextProcess();
    }

    private void joinAudio() {
        this.audioIp = new File(FileUtils.TEMP_DIRECTORY, "audio.txt");
        this.audioFile = new File(FileUtils.APP_DIRECTORY, "audio.mp3");
        this.audioFile.delete();
        this.audioIp.delete();
        int d = 0;
        Log.e("in_joinAudio", String.valueOf(this.toatalSecond) + "_in_joinAudio");
        while (true) {
            appendAudioLog(String.format("file '%s'", new Object[]{this.application.getMusicData().track_data}));
            if (this.toatalSecond * 1000.0f <= ((float) (this.application.getMusicData().track_duration * ((long) d)))) {
                break;
            }
            d++;
        }
        Log.e("in_joinAudio1", String.valueOf(this.toatalSecond) + "in_joinAudio1");
        String[] strCommand = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", this.audioIp.getAbsolutePath(), "-c", "copy", "-ac", "2", "-preset", "ultrafast", this.audioFile.getAbsolutePath()};

        mExecuteFfmpegCmd(strCommand);
//        Process process = null;
        /*try {
            process = Runtime.getRuntime().exec(new String[]{FileUtils.getFFmpeg(this), "-f", "concat", "-safe", "0", "-i", this.audioIp.getAbsolutePath(), "-c", "copy", "-preset", "ultrafast", "-ac", "2", this.audioFile.getAbsolutePath()});
            while (!Util.isProcessCompleted(process)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = reader.readLine();
                while (line != null) {
                    if (line != null) {
                        line = reader.readLine();
                        Log.e("audio", line + "_audio");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("audio", "io", e);
            Log.e("IOException", e.getMessage() + "_IOException");
        } finally {
            Util.destroyProcess(process);
            Log.e("finally", "_finally");
        }*/
    }

    private long executionId;

    private void mExecuteFfmpegCmd(String[] strCommand) {

        /*Dialog dialog = new Dialog(ImageToVideoActivity.getInstance());
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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
//        dialog.show();

        executionId = FFmpeg.executeAsync(strCommand, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {

//                    progBar.setProgress(0);
                    Log.e("TAG", "apply:=======================Savedddd....=======");
//                    dialog.dismiss();

                } else if (returnCode == RETURN_CODE_CANCEL) {
//                    progBar.setProgress(0);
//                    dialog.dismiss();

                } else {
//                    progBar.setProgress(0);
//                    dialog.dismiss();

                }
            }
        });

    }

    private void mStartNextProcess() {
        joinAudio();

        while (!ImageCreatorService.isImageComplate) {
            Log.e("isImageComplate", "ImageCreatorService.isImageComplate");
        }
        new File(FileUtils.TEMP_DIRECTORY, "video.txt").delete();
        for (int i = 0; i < this.application.videoImages.size(); i++) {
            appendVideoLog(String.format("file '%s'", new Object[]{this.application.videoImages.get(i)}));
        }
        String videoPath = new File(FileUtils.APP_DIRECTORY, getVideoName()).getAbsolutePath();
        if (this.application.getMusicData() == null) {
//            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-r", "30", "-c:v", "-preset", "-pix_fmt", "yuv420p", videoPath};
            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-r", "30", "-preset", "ultrafast", "-vsync", "vfr", "-vf", "scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,format=yuv420p", videoPath};
        }
        else {
//            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-safe", "0", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-i", this.audioFile.getAbsolutePath(), "-strict", "experimental", "-r", "30", "-t", String.valueOf(this.toatalSecond), "-c:v", videoPath};
            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-safe", "0", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-i", this.audioFile.getAbsolutePath(), "-strict", "experimental", "-r", "30", "-t", String.valueOf(this.toatalSecond), "-preset", "ultrafast", "-vsync", "vfr", "-vf", "scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,format=yuv420p", videoPath};
        }
        System.gc();
//        Process process = null;

        dialog.show();

        executionId = FFmpeg.executeAsync(inputCode, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {

                    progBar.setProgress(0);
                    dialog.dismiss();
//                    mBuilder.setContentText("Video created :" + FileUtils.getDuration(System.currentTimeMillis() - startTime)).setProgress(0, 0, false);
//                    mNotifyManager.notify(1001, mBuilder.build());
                    try {
                        long fileSize = new File(videoPath).length();
                        String artist = getResources().getString(R.string.artist_name);
                        ContentValues values = new ContentValues();
                        values.put("_data", videoPath);
                        values.put("_size", Long.valueOf(fileSize));
                        values.put("mime_type", "video/mp4");
                        values.put("artist", artist);
                        values.put("duration", Float.valueOf(toatalSecond * 1000.0f));
                        getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(videoPath), values);
                    } catch (Exception e3) {
                    }
                    try {
                        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(videoPath))));
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    application.clearAllSelection();
                    Log.e("TAG", "apply:=================Videooooo creataed=================");
                    Intent notificationIntent = new Intent(ImageToVideoActivity.this, VideoEditorActivity.class);
                    notificationIntent.setFlags(268435456);
                    notificationIntent.addFlags(67108864);
                    notificationIntent.putExtra("path", videoPath);
                    startActivity(notificationIntent);
                    final String str = videoPath;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            OnProgressReceiver receiver = application.getOnProgressReceiver();
                            if (receiver != null) {
                                receiver.onVideoProgressFrameUpdate(100.0f);
                                receiver.onProgressFinish(str);
                            }
                        }
                    });
                    FileUtils.deleteTempDir();
//        this.application.setFrame(-1);
                    application.clearAllSelection();
                    application.isFromSdCardAudio = false;
                    ImageCreatorService.isImageComplate = false;
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();

                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();

                }
            }
        });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {
                Log.d(Config.TAG, "Video Length: =====" + mPlayer.getDuration());

                float progress = Float.parseFloat(valueOf(newStatistics.getTime())) / mPlayer.getDuration();
                float progressFinal = progress * 100;
                Log.d(Config.TAG, "Video Length: " + progressFinal);
                progBar.setProgress((int) progressFinal);
            }
        });
    }

    private String getVideoName() {
        return "video_" + new SimpleDateFormat("yyyy_MMM_dd_HH_mm_ss", Locale.ENGLISH).format(new Date()) + ".mp4";
    }

    public static void appendVideoLog(String text) {
        if (!FileUtils.TEMP_DIRECTORY.exists()) {
            FileUtils.TEMP_DIRECTORY.mkdirs();
        }
        File logFile = new File(FileUtils.TEMP_DIRECTORY, "video.txt");
        Log.d("FFMPEG", "File append " + text);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void appendAudioLog(String text) {
        if (!FileUtils.TEMP_DIRECTORY.exists()) {
            FileUtils.TEMP_DIRECTORY.mkdirs();
        }
        File logFile = new File(FileUtils.TEMP_DIRECTORY, "audio.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}