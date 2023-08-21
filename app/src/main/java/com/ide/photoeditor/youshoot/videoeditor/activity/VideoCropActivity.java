package com.ide.photoeditor.youshoot.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Media;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.edmodo.cropper.CropImageView;
import com.edmodo.cropper.cropwindow.edge.Edge;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.ui.VideoPlayerState;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;
import com.ide.photoeditor.youshoot.videoeditor.utils.VideoSliceSeekBar;
import com.ide.photoeditor.youshoot.videoeditor.utils.VideoSliceSeekBar.SeekBarChangeListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getRootFileForSave;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class VideoCropActivity extends AppCompatActivity {
    public static VideoCropActivity videoCropActivity;
    TextView rotaa;
    static final boolean af = true;
    int A;
    int B;
    String C;
    String D;
    String E;
    String F;
    String G = "00";
    String H;
    TextView S;
    Bitmap U;
    FloatingActionButton V;
    VideoPlayerState W = new VideoPlayerState();
    VideoSliceSeekBar X;
    VideoView Z;
    CropImageView a;
    float aa;
    float ab;
    float ac;
    float ad;
    long ae;
    int ag;
    int ah;
    String b;
    String l;
    int m;
    int n;
    int o;
    int p;
    int q;
    int r;
    int s;
    int t;
    int u;
    int v;
    int w;
    int x;
    int y;
    int z;
    @BindView(R.id.mMbtnCustom)
    MaterialButton mMbtnCustom;
    @BindView(R.id.mMbtnSqaure)
    MaterialButton mMbtnSqaure;
    @BindView(R.id.mMbtnPortrait)
    MaterialButton mMbtnPortrait;
    @BindView(R.id.mMbtnLandscape)
    MaterialButton mMbtnLandscape;
    private long executionId;
    int videoLength;

    public static VideoCropActivity getInstance(){
        return videoCropActivity;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);
        videoCropActivity=this;
        AdUtils.showNativeAd(VideoCropActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        this.E = getIntent().getStringExtra("videofilename");

        if (this.E != null) {
            this.U = ThumbnailUtils.createVideoThumbnail(this.E, 1);
        }

        this.x = FileUtils.getScreenWidth();
        this.a = findViewById(R.id.cropperView);
        d();
        e();
    }

    public void cropcommand() {
        h();
        getPath();
    }

    @SuppressLint("InvalidWakeLockTag")
    public void getPath() {
        if (this.w == 90) {
            try {
                this.o = this.B;
                int i2 = this.z;
                u = this.B;
                v = this.A;
                m = this.y;
                n = this.z;
                s = this.y;
                t = this.A;
                ag = this.m - this.o;
                ah = this.v - i2;
                p = this.q - (this.ah + i2);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else if (this.w == 270) {
            try {
                int i3 = this.B;
                int i4 = this.z;
                u = this.B;
                v = this.A;
                m = this.y;
                n = this.z;
                s = this.y;
                t = this.A;
                ag = this.m - i3;
                ah = this.v - i4;
                o = this.r - (this.ag + i3);
                p = i4;
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        } else {
            try {
                o = this.z;
                p = this.B;
                u = this.A;
                v = this.B;
                m = this.z;
                n = this.y;
                s = this.A;
                t = this.y;
                ag = this.u - this.o;
                ah = this.n - this.p;
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
        this.H = valueOf(this.W.getStart() / 1000);
        this.F = valueOf(this.W.getDuration() / 1000);
        this.l = this.E;
        if (this.l.contains(".3gp") || this.l.contains(".3GP")) {
            try {
                this.C = FileUtils.getTargetFileName(this, this.l.replace(".3gp", ".mp4"));
            } catch (Exception e5) {
                e5.printStackTrace();
            }
        } else if (this.l.contains(".flv") || this.l.contains(".FLv")) {
            try {
                this.C = FileUtils.getTargetFileName(this, this.l.replace(".flv", ".mp4"));
            } catch (Exception e6) {
                e6.printStackTrace();
            }
        } else if (this.l.contains(".mov") || this.l.contains(".MOV")) {
            try {
                this.C = FileUtils.getTargetFileName(this, this.l.replace(".mov", ".mp4"));
            } catch (Exception e7) {
                e7.printStackTrace();
            }
        } else if (this.l.contains(".wmv") || this.l.contains(".WMV")) {
            try {
                this.C = FileUtils.getTargetFileName(this, this.l.replace(".wmv", ".mp4"));
            } catch (Exception e8) {
                e8.printStackTrace();
            }
        } else {
            try {
                this.C = FileUtils.getTargetFileName(this, this.l);
            } catch (Exception e9) {
                e9.printStackTrace();
            }
        }
        this.D = FileUtils.getTargetFileName(this, this.C);
//        D= new File(FileUtils.TEMP_EditDIRECTORY, C).getAbsolutePath();
        Log.e("TAG", "getPath:====crop=="+D );
        StatFs statFs = new StatFs(getRootFileForSave());
        long availableBlocks = ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
        File file = new File(this.W.getFilename());
        Log.d("TAG", "getpath: " + file.getAbsolutePath());
        this.ae = 0;
        this.ae = file.length() / 1024;
        if ((availableBlocks / 1024) / 1024 >= this.ae / 1024) {
            try {
                String sb = "crop=w=" +
                        this.ag +
                        ":h=" +
                        this.ah +
                        ":x=" +
                        this.o +
                        ":y=" +
                        this.p;


                a(new String[]{"-y", "-ss", H, "-t", F, "-i", l, "-strict", "experimental", "-filter_complex", sb, "-r", "15", "-ab", "128k", "-vcodec", "mpeg4", "-acodec", "copy", "-b:v", "2500k", "-sample_fmt", "s16", "-ss", "0", "-t", this.F, this.D}, this.D);


            } catch (Exception unused) {
                File file2 = new File(this.D);
                if (file2.exists()) {
                    file2.delete();
                    finish();
                    return;
                }
                Toast.makeText(this, "please select any option!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Out of Memory!......", Toast.LENGTH_SHORT).show();
        }
    }

    private void a(String[] comand, final String str) {

        videoLength = Integer.parseInt(F) * 1000;

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
                    if(VideoEditorActivity.getInstance()!=null){
                        VideoEditorActivity.getInstance().finish();
                    }
                    Intent intent = new Intent(getApplicationContext(), VideoEditorActivity.class);
                    intent.putExtra("path", D);
                    startActivity(intent);
                    Toast.makeText(VideoCropActivity.this, "Execution completed successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoCropActivity.this, "Execution cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    progBar.setProgress(0);
                    dialog.dismiss();
                    Toast.makeText(VideoCropActivity.this, "Execution failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        com.arthenica.mobileffmpeg.Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {

                float progress = Float.parseFloat(valueOf(newStatistics.getTime())) / videoLength;
                float progressFinal = progress * 100;
                Log.d(com.arthenica.mobileffmpeg.Config.TAG, "Video Length: " + progressFinal);
                Log.d(com.arthenica.mobileffmpeg.Config.TAG, format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
                Log.d(com.arthenica.mobileffmpeg.Config.TAG, format("Quality: %f, time: %f", newStatistics.getVideoQuality(), newStatistics.getVideoFps()));
                progBar.setProgress((int) progressFinal);

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
        MediaScannerConnection.scanFile(VideoCropActivity.this,
                new String[]{videoFile}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    @Override
    public void onResume() {
        this.Z.seekTo(this.W.getCurrentTime());
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.W.setCurrentTime(this.Z.getCurrentPosition());
    }

    private void d() {
        this.rotaa = (TextView) findViewById(R.id.left_pointer);
        this.S = (TextView) findViewById(R.id.right_pointer);
        this.X = (VideoSliceSeekBar) findViewById(R.id.seek_bar);
        this.V = findViewById(R.id.buttonply);

        MaterialButton save = findViewById(R.id.mMbtnSave);
        save.setOnClickListener(view -> {
            if (Z != null && Z.isPlaying()) {
                Z.pause();
            }
            cropcommand();
        });

        MaterialButton close = findViewById(R.id.mMbtnBack);
        close.setOnClickListener(view -> onBackPressed());
        MaterialButton mMbtnCancel = findViewById(R.id.mMbtnCancel);
        mMbtnCancel.setOnClickListener(view -> onBackPressed());

        this.V.setOnClickListener(view -> {
            if (Z == null || !Z.isPlaying()) {
                V.setImageResource(R.drawable.ic_baseline_pause_24);
            } else {
                V.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            g();
        });
        Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
        if (lastNonConfigurationInstance != null) {
            this.W = (VideoPlayerState) lastNonConfigurationInstance;
        } else {
            this.W.setFilename(this.E);
        }

    }

    @SuppressLint({"NewApi"})
    private void e() {
        this.Z = (VideoView) findViewById(R.id.videoview);
        this.Z.setVideoPath(this.E);
        this.b = getTimeForTrackFormat(this.Z.getDuration(), af);

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this.E);
        this.r = Integer.parseInt(mediaMetadataRetriever.extractMetadata(18));
        this.q = Integer.parseInt(mediaMetadataRetriever.extractMetadata(19));
        this.w = Integer.parseInt(mediaMetadataRetriever.extractMetadata(24));
        try {
            mediaMetadataRetriever.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.a.getLayoutParams();
        if (this.w == 90 || this.w == 270) {
            if (this.r >= this.q) {
                if (this.r >= this.x) {
                    layoutParams.height = this.x;
                    layoutParams.width = (int) (((float) this.x) / (((float) this.r) / ((float) this.q)));
                } else {
                    layoutParams.width = this.x;
                    layoutParams.height = (int) (((float) this.q) * (((float) this.x) / ((float) this.r)));
                }
            } else if (this.q >= this.x) {
                layoutParams.width = this.x;
                layoutParams.height = (int) (((float) this.x) / (((float) this.q) / ((float) this.r)));
            } else {
                layoutParams.width = (int) (((float) this.r) * (((float) this.x) / ((float) this.q)));
                layoutParams.height = this.x;
            }
        } else if (this.r >= this.q) {
            if (this.r >= this.x) {
                layoutParams.width = this.x;
                layoutParams.height = (int) (((float) this.x) / (((float) this.r) / ((float) this.q)));
            } else {
                layoutParams.width = this.x;
                layoutParams.height = (int) (((float) this.q) * (((float) this.x) / ((float) this.r)));
            }
        } else if (this.q >= this.x) {
            layoutParams.width = (int) (((float) this.x) / (((float) this.q) / ((float) this.r)));
            layoutParams.height = this.x;
        } else {
            layoutParams.width = (int) (((float) this.r) * (((float) this.x) / ((float) this.q)));
            layoutParams.height = this.x;
        }
        this.a.setLayoutParams(layoutParams);
        this.a.setImageBitmap(Bitmap.createBitmap(layoutParams.width, layoutParams.height, Config.ARGB_8888));
        try {
            searchVideo(this.E);
        } catch (Exception ignored) {
        }
        this.Z.setOnPreparedListener(mediaPlayer -> {
            X.setSeekBarChangeListener(new SeekBarChangeListener() {
                public void SeekBarValueChanged(int i, int i2) {
                    if (X.getSelectedThumb() == 1) {
                        Z.seekTo(X.getLeftProgress());
                    }
                    rotaa.setText(VideoCropActivity.getTimeForTrackFormat(i, VideoCropActivity.af));
                    S.setText(VideoCropActivity.getTimeForTrackFormat(i2, VideoCropActivity.af));
                    G = VideoCropActivity.getTimeForTrackFormat(i, VideoCropActivity.af);
                    W.setStart(i);
                    b = VideoCropActivity.getTimeForTrackFormat(i2, VideoCropActivity.af);
                    W.setStop(i2);
                }
            });
            b = VideoCropActivity.getTimeForTrackFormat(mediaPlayer.getDuration(), VideoCropActivity.af);
            X.setMaxValue(mediaPlayer.getDuration());
            X.setLeftProgress(0);
            X.setRightProgress(mediaPlayer.getDuration());
            X.setProgressMinDiff(0);
            Z.seekTo(100);
        });
    }

    public void g() {
        if (this.Z.isPlaying()) {
            this.Z.pause();
            this.X.setSliceBlocked(false);
            this.X.removeVideoStatusThumb();
            return;
        }
        this.Z.seekTo(this.X.getLeftProgress());
        this.Z.start();
        this.X.videoPlayingProgress(this.X.getLeftProgress());
    }

    public static String getTimeForTrackFormat(int i2, boolean z2) {
        String str;
        int i3 = i2 / 60000;
        int i4 = (i2 - ((i3 * 60) * 1000)) / 1000;
        StringBuilder sb = new StringBuilder(valueOf((!z2 || i3 >= 10) ? "" : "0"));
        sb.append(i3 % 60);
        sb.append(":");
        String sb2 = sb.toString();
        if (i4 < 10) {
            String sb3 = valueOf(sb2) + "0" +
                    i4;
            str = sb3;
        } else {
            str = valueOf(sb2) + i4;
        }
        String sb5 = "Display Result" +
                str;
        Log.e("", sb5);
        return str;
    }

    private void h() {
        if (this.w == 90 || this.w == 270) {
            this.aa = (float) this.q;
            this.ab = (float) this.r;
            this.ac = (float) this.a.getWidth();
            this.ad = (float) this.a.getHeight();
            this.z = (int) ((Edge.LEFT.getCoordinate() * this.aa) / this.ac);
            this.A = (int) ((Edge.RIGHT.getCoordinate() * this.aa) / this.ac);
            this.B = (int) ((Edge.TOP.getCoordinate() * this.ab) / this.ad);
            this.y = (int) ((Edge.BOTTOM.getCoordinate() * this.ab) / this.ad);
            return;
        }
        this.aa = (float) this.r;
        this.ab = (float) this.q;
        this.ac = (float) this.a.getWidth();
        this.ad = (float) this.a.getHeight();
        this.z = (int) ((Edge.LEFT.getCoordinate() * this.aa) / this.ac);
        this.A = (int) ((Edge.RIGHT.getCoordinate() * this.aa) / this.ac);
        this.B = (int) ((Edge.TOP.getCoordinate() * this.ab) / this.ad);
        this.y = (int) ((Edge.BOTTOM.getCoordinate() * this.ab) / this.ad);
    }

    public void searchVideo(String str) {
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] strArr = {"_data", "_id"};
        String sb = "%" +
                str +
                "%";
        Cursor managedQuery = managedQuery(uri, strArr, "_data  like ?", new String[]{sb}, " _id DESC");
        int count = managedQuery.getCount();
        String sb2 = "count" +
                count;
        Log.e("", sb2);
        if (count > 0) {
            managedQuery.moveToFirst();
            managedQuery.getLong(managedQuery.getColumnIndexOrThrow("_id"));
            managedQuery.moveToNext();
        }
    }

    public void j() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("VidMaker is not supported on your device").setCancelable(false).setPositiveButton("Ok", (dialogInterface, i) -> finish()).create().show();
    }

    public void deleteFromGallery(String str) {
        String[] strArr = {"_id"};
        String[] strArr2 = {str};
        Uri uri = Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor query = contentResolver.query(uri, strArr, "_data = ?", strArr2, null);
        if (query.moveToFirst()) {
            try {
                contentResolver.delete(ContentUris.withAppendedId(Images.Media.EXTERNAL_CONTENT_URI, query.getLong(query.getColumnIndexOrThrow("_id"))), null, null);
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

    @OnClick({R.id.mMbtnCustom, R.id.mMbtnSqaure, R.id.mMbtnPortrait, R.id.mMbtnLandscape})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mMbtnCustom:
                mSetCropFrame(0);
                break;
            case R.id.mMbtnSqaure:
                mSetCropFrame(1);
                break;
            case R.id.mMbtnPortrait:
                mSetCropFrame(2);
                break;
            case R.id.mMbtnLandscape:
                mSetCropFrame(3);
                break;
        }
    }

    private void mSetCropFrame(int i) {
        mMbtnCustom.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
        mMbtnSqaure.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
        mMbtnPortrait.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
        mMbtnLandscape.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_edit)));
        switch (i) {
            case 0:
                mMbtnCustom.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
                a.setFixedAspectRatio(false);
                break;
            case 1:
                mMbtnSqaure.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
                a.setFixedAspectRatio(VideoCropActivity.af);
                a.setAspectRatio(10, 10);
                break;
            case 2:
                mMbtnPortrait.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
                a.setFixedAspectRatio(VideoCropActivity.af);
                a.setAspectRatio(8, 16);
                break;
            case 3:
                mMbtnLandscape.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.clr_select)));
                a.setFixedAspectRatio(VideoCropActivity.af);
                a.setAspectRatio(16, 8);
                break;
        }
    }
}
