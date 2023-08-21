package com.ide.photoeditor.youshoot.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.BuildConfig;
import com.ide.photoeditor.youshoot.DetailsDialog;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.dialog.ExitDialog;
import com.ide.photoeditor.youshoot.videoeditor.activity.SlideShowImageActivity;
import com.ide.photoeditor.youshoot.videoeditor.activity.VideoEditorActivity;
import com.ide.photoeditor.youshoot.videoeditor.activity.VideoListActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.checkClick;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.isLatestVersion;

public class HomeActivity extends BaseActivity {
    public static HomeActivity homeActivity;
    private String mCurrentVideoPath;
    @BindView(R.id.mDrawerlayout)
    DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle drawerToggle;
    String[] arrPermissionsCamera;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.draweropen, R.string.drawerclose);

        mDrawerlayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        homeActivity = this;

        AdUtils.showNativeAd(HomeActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);

        arrPermissionsCamera = new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        if (isLatestVersion())
            arrPermissionsCamera = new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE"};

        Dexter.withContext(homeActivity).withPermissions(arrPermissionsCamera)
                .withListener(new MultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(homeActivity, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();

    }

    @OnClick({R.id.mIVMenu, R.id.mIVSearch, R.id.mRlCamera, R.id.mRlStudio, R.id.mRlVideo, R.id.mRlSlideShow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIVMenu:
                mDrawerlayout.openDrawer(GravityCompat.START);
                break;
            case R.id.mIVSearch:
                break;
            case R.id.mRlCamera:
                mOpenCameraVideo();
                break;
            case R.id.mRlStudio:
                mOpenMyCreation();
                break;
            case R.id.mRlVideo:
                mOpenVideoList();
                break;
            case R.id.mRlSlideShow:
                mOpenSlideShow();
                break;
        }
    }

    private void mOpenSlideShow() {
        AdUtils.showInterstitialAd(HomeActivity.this, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                Dexter.withContext(homeActivity).withPermissions(arrPermissionsCamera).withListener(new MultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            checkClick();
                            startActivity(new Intent(homeActivity, SlideShowImageActivity.class));
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            DetailsDialog.showDetailsDialog(homeActivity);
                        }
                    }

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(homeActivity, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
            }
        });

    }

    private void mOpenMyCreation() {
        AdUtils.showInterstitialAd(HomeActivity.this, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {

                Dexter.withContext(homeActivity).withPermissions(arrPermissionsCamera)
                        .withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    startActivity(new Intent(homeActivity, MyCreationActivity.class));
                                }
                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    DetailsDialog.showDetailsDialog(homeActivity);
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(dexterError -> Toast.makeText(homeActivity, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
            }
        });
    }

    private void mOpenCameraVideo() {
        Dexter.withContext(homeActivity).withPermissions(arrPermissionsCamera)
                .withListener(new MultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            cameraIntent();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            DetailsDialog.showDetailsDialog(homeActivity);
                        }
                    }

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(homeActivity, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void cameraIntent() {

        try {
            Uri uri;
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File createImageFile = createImageFile();
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(homeActivity, getApplicationInfo().packageName + ".provider", createImageFile);
                } else {
                    uri = Uri.fromFile(createImageFile);
                }
                if (uri != null) {
                    intent.putExtra("output", uri);
                }
            }
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public File createImageFile() throws IOException {
        String str = "MP4_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".mp4";
        File externalStoragePublicDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalStoragePublicDirectory.exists() || externalStoragePublicDirectory.mkdir()) {
            File file = new File(externalStoragePublicDirectory, str);
            this.mCurrentVideoPath = file.getAbsolutePath();
            return file;
        }
//        Log.e("TAG", "Throwing Errors....");
        throw new IOException();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 1) {
            Intent intent = new Intent(getApplicationContext(), VideoEditorActivity.class);
            intent.putExtra("path", mCurrentVideoPath);
            startActivity(intent);
        }
    }

    private void mOpenVideoList() {
        AdUtils.showInterstitialAd(homeActivity, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {

                Dexter.withContext(homeActivity).withPermissions(arrPermissionsCamera).withListener(new MultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            checkClick();
                            Intent intent = new Intent(homeActivity, VideoListActivity.class);
                            intent.putExtra("rvValues", "video");
                            startActivity(intent);
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            DetailsDialog.showDetailsDialog(homeActivity);
                        }
                    }

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError -> Toast.makeText(homeActivity, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
            }
        });
    }

    public void mOnNavigationClick(View view) {
        switch (view.getId()) {
            case R.id.mTxtRate:
                mRateApp();
                break;
            case R.id.mTxtShare:
                mShareApp();
                break;
            case R.id.mTxtPrivacy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://clickmediainc.blogspot.com/2023/03/privacy-policy.html"));
                startActivity(browserIntent);
                break;

        }
        mDrawerlayout.closeDrawer(GravityCompat.START);
    }

    private void mRateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void mShareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerlayout.closeDrawer((GravityCompat.START));
        } else {
            ExitDialog exitDialog = new ExitDialog(new ExitDialog.OnClickListener() {
                @Override
                public void discard() {
                    finish();
                }
            });
            exitDialog.show(getSupportFragmentManager(), "");

        }

    }

}