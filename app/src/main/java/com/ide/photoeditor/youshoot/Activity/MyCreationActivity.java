package com.ide.photoeditor.youshoot.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.adapter.MyCreationAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getRootFileForSave;
import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.hideProgressDialog;

public class MyCreationActivity extends BaseActivity implements MyCreationAdapter.VideoFilesHandler {

    @BindView(R.id.mTxtEmpty)
    TextView mTxtEmpty;
    @BindView(R.id.rlsph)
    RelativeLayout rlsph;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    ArrayList<File_Model> img_path;
    MyCreationAdapter myCreationAdapter;
    public static MyCreationActivity myCreationActivity;

    public static MyCreationActivity getInstance() {
        return myCreationActivity;
    }

    @Override
    public void onChoose(String position) {

    }

    public void mShowVideoAct(String uri) {
        AdUtils.showInterstitialAd(MyCreationActivity.this, new AppInterfaces.InterStitialADInterface() {
            @Override
            public void adLoadState(boolean isLoaded) {
                Intent intent = new Intent(MyCreationActivity.this, ShowVideoActivity.class);
                intent.putExtra("video_uri", uri);
                startActivity(intent);
            }
        });

    }

    public class File_Model {
        public String file_path;
        public String file_title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_creation;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        myCreationActivity = this;
        new LoadImages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.mMbtnBack)
    public void onViewClicked() {
        finish();
    }

    class LoadImages extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Global.showProgressDialog(MyCreationActivity.this, Global.PLEASE_WAIT);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            updateFileList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
            if (img_path.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                rlsph.setVisibility(View.VISIBLE);
            } else {
                AdUtils.showNativeAd(MyCreationActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), false);
                mRecyclerView.setVisibility(View.VISIBLE);
                rlsph.setVisibility(View.GONE);
                mSetAdapter();
            }
        }
    }

    private void mSetAdapter() {
        myCreationAdapter = new MyCreationAdapter(MyCreationActivity.this, img_path, this);
        GridLayoutManager manager = new GridLayoutManager(MyCreationActivity.this, 2);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(myCreationAdapter);
    }

    public void updateFileList() {
        String path = getRootFileForSave() +"/" + getResources().getString(R.string.app_name);
        File directory = new File(path);
        File[] files = directory.listFiles();

        img_path = new ArrayList();

        if (files != null) {

            for (int i = 0; i < files.length; i++) {
                File_Model file_model = new File_Model();
                file_model.file_path = files[i].getAbsolutePath();
                file_model.file_title = files[i].getName();
                img_path.add(file_model);
            }
        }
    }

}