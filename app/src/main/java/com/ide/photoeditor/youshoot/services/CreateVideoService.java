package com.ide.photoeditor.youshoot.services;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.MyApp;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.cutomView.OnProgressReceiver;
import com.ide.photoeditor.youshoot.videoeditor.activity.ImageToVideoActivity;
import com.ide.photoeditor.youshoot.videoeditor.activity.VideoEditorActivity;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;


//import com.google.android.exoplayer2.util.MimeTypes;

public class CreateVideoService extends IntentService {
    public static final int NOTIFICATION_ID = 1001;
    MyApp application;
    private File audioFile;
    private File audioIp;
    int last;
    private Builder mBuilder;
    private NotificationManager mNotifyManager;
    String timeRe;
    private float toatalSecond;
    String[] inputCode;
    long startTime;

    public CreateVideoService() {
        this(CreateVideoService.class.getName());
    }

    public CreateVideoService(String name) {
        super(name);
        this.timeRe = "\\btime=\\b\\d\\d:\\d\\d:\\d\\d.\\d\\d";
        this.last = 0;
    }

    protected void onHandleIntent(Intent intent) {
        this.application = MyApp.getInstance();
        this.mNotifyManager = (NotificationManager) getSystemService("notification");
        this.mBuilder = new Builder(this);
        this.mBuilder.setContentTitle("Creating Video").setContentText("Making in progress").setSmallIcon(R.mipmap.ic_launcher);
        createVideo();
    }

    private void createVideo() {
        startTime = System.currentTimeMillis();
        this.toatalSecond = (this.application.getSecond() * ((float) Global.mSelectedImgPath.size())) - 1.0f;
        Log.e("totalseconds", String.valueOf(this.toatalSecond) + "_totalSecond");
        joinAudio();

        /*try {
            process = Runtime.getRuntime().exec(inputCode);
            while (!Util.isProcessCompleted(process)) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = bufferedReader.readLine();
                while (line != null) {
                    if (line != null) {
                        line = bufferedReader.readLine();
                        Log.e("process", "process_" + line + "");
                        this.mBuilder.setProgress(100, ((int) ((75.0f * ((float) durationToprogtess(line + ""))) / 100.0f)) + 25, false);
                        this.mNotifyManager.notify(1001, this.mBuilder.build());
                    }
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            Util.destroyProcess(process);
        }*/

    }

    @SuppressLint({"WrongConstant"})
    private void buildNotification(String videoPath) {
        Log.e("TAG", "apply:==========buildNotification=======Videooooo creataed=================" );
        Intent notificationIntent = new Intent(this, VideoEditorActivity.class);
        notificationIntent.setFlags(268435456);
        notificationIntent.addFlags(67108864);
        notificationIntent.putExtra("path", videoPath);
        startActivity(notificationIntent);
       /* PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 268435456);
        Resources res = getResources();
        Builder builder = new Builder(this);
        builder.setContentIntent(contentIntent).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher)).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(getResources().getString(R.string.app_name)).setContentText("Video Created");
        Notification n = builder.build();
        n.defaults |= -1;
        this.mNotifyManager.notify(1001, n);*/
    }

    private int durationToprogtess(String input) {
        int progress = 0;
        Matcher matcher = Pattern.compile(this.timeRe).matcher(input);
        int HOUR = 60 * 60;
        if (TextUtils.isEmpty(input) || !input.contains("time=")) {
            Log.e("time", "not contain time " + input);
            return this.last;
        }
        while (matcher.find()) {
            String time = matcher.group();
            String[] splitTime = time.substring(time.lastIndexOf(61) + 1).split(":");
            float hour = ((Float.valueOf(splitTime[0]).floatValue() * ((float) HOUR)) + (Float.valueOf(splitTime[1]).floatValue() * ((float) 60))) + Float.valueOf(splitTime[2]).floatValue();
            Log.e("time", "totalSecond:" + hour);
            progress = (int) ((100.0f * hour) / this.toatalSecond);
            updateInMili(hour);
        }
        this.last = progress;
        return progress;
    }

    private void updateInMili(float time) {
        final double progress = (((double) time) * 100.0d) / ((double) this.toatalSecond);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                OnProgressReceiver receiver = CreateVideoService.this.application.getOnProgressReceiver();
                if (receiver != null) {
                    Log.e("timeToatal", "progress __" + progress);
                    receiver.onVideoProgressFrameUpdate((float) progress);
                }
            }
        });
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
        Dialog dialog = new Dialog(ImageToVideoActivity.getInstance());
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
//        dialog.show();

        executionId = FFmpeg.executeAsync(strCommand, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {

                    progBar.setProgress(0);
                    Log.e("TAG", "apply:=======================Savedddd....=======");
//                    dialog.dismiss();
                    mStartNextProcess();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
//                    dialog.dismiss();

                } else {
                    progBar.setProgress(0);
//                    dialog.dismiss();

                }
            }
        });

    }

    private void mStartNextProcess() {
        while (!ImageCreatorService.isImageComplate) {
            Log.e("isImageComplate", "ImageCreatorService.isImageComplate");
        }
        Log.e("createVideo", "video create start");
        new File(FileUtils.TEMP_DIRECTORY, "video.txt").delete();
        for (int i = 0; i < this.application.videoImages.size(); i++) {
            appendVideoLog(String.format("file '%s'", new Object[]{this.application.videoImages.get(i)}));
        }
        String videoPath = new File(FileUtils.APP_DIRECTORY, getVideoName()).getAbsolutePath();
        if (this.application.getMusicData() == null) {
//            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-r", "30", "-c:v", "-preset", "-pix_fmt", "yuv420p", videoPath};
            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-r", "30", "-preset", "ultrafast", "-vsync", "vfr", "-vf","scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,format=yuv420p", videoPath};
        }
//        else if (this.application.getFrame() != -1) {
//            if (!FileUtils.frameFile.exists()) {
//                try {
//                    Bitmap bm = BitmapFactory.decodeResource(getResources(), this.application.getFrame());
//                    if (!(bm.getWidth() == MyApp.VIDEO_WIDTH && bm.getHeight() == MyApp.VIDEO_HEIGHT)) {
//                        bm = ScalingUtilities.scaleCenterCrop(bm, MyApp.VIDEO_WIDTH, MyApp.VIDEO_HEIGHT);
//                    }
//                    FileOutputStream outStream = new FileOutputStream(FileUtils.frameFile);
//                    bm.compress(CompressFormat.PNG, 100, outStream);
//                    outStream.flush();
//                    outStream.close();
//                    bm.recycle();
//                    System.gc();
//                } catch (Exception e) {
//                }
//            }
//            inputCode = new String[]{FileUtils.getFFmpeg(this), "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-safe", "0", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-i", FileUtils.frameFile.getAbsolutePath(), "-i", this.audioFile.getAbsolutePath(), "-filter_complex", "overlay= 0:0", "-strict", "experimental", "-r", String.valueOf(30.0f / this.application.getSecond()), "-t", String.valueOf(this.toatalSecond), "-c:v", "libx264", "-preset", "ultrafast", "-pix_fmt", "yuv420p", "-ac", "2", videoPath};
//        }
        else {
//            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-safe", "0", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-i", this.audioFile.getAbsolutePath(), "-strict", "experimental", "-r", "30", "-t", String.valueOf(this.toatalSecond), "-c:v", videoPath};
            inputCode = new String[]{"-y", "-r", String.valueOf(30.0f / this.application.getSecond()), "-f", "concat", "-safe", "0", "-i", new File(FileUtils.TEMP_DIRECTORY, "video.txt").getAbsolutePath(), "-i", this.audioFile.getAbsolutePath(), "-strict", "experimental", "-r", "30", "-t", String.valueOf(this.toatalSecond), "-preset", "ultrafast", "-vsync", "vfr", "-vf","scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,format=yuv420p", videoPath};
        }
        System.gc();
        Process process = null;
        Dialog dialog = new Dialog(ImageToVideoActivity.getInstance());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_progress);

        Button cancel = dialog.findViewById(R.id.cancel);
        ProgressBar progBar = dialog.findViewById(R.id.progress_bar);
        progBar.setProgress(0);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpeg.cancel(executionId);
//                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();

        executionId = FFmpeg.executeAsync(inputCode, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {

                    progBar.setProgress(0);
//                    dialog.dismiss();
                    mBuilder.setContentText("Video created :" + FileUtils.getDuration(System.currentTimeMillis() - startTime)).setProgress(0, 0, false);
                    mNotifyManager.notify(1001, mBuilder.build());
                    try {
                        long fileSize = new File(videoPath).length();
                        String artist = getResources().getString(R.string.artist_name);
                        ContentValues values = new ContentValues();
                        values.put("_data", videoPath);
                        values.put("_size", Long.valueOf(fileSize));
                        values.put("mime_type", "video/mp4");
                        values.put("artist", artist);
                        values.put("duration", Float.valueOf(toatalSecond * 1000.0f));
                        getContentResolver().insert(Media.getContentUriForPath(videoPath), values);
                    } catch (Exception e3) {
                    }
                    try {
                        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(videoPath))));
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                    application.clearAllSelection();
                    Log.e("TAG", "apply:=================Videooooo creataed=================" );
                    buildNotification(videoPath);
                    final String str = videoPath;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            OnProgressReceiver receiver = CreateVideoService.this.application.getOnProgressReceiver();
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
                    stopSelf();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    progBar.setProgress(0);
//                    dialog.dismiss();

                } else {
                    progBar.setProgress(0);
//                    dialog.dismiss();

                }
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
