package com.ide.photoeditor.youshoot.videoeditor.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ide.photoeditor.youshoot.MyApp;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.cutomView.theme.THEMES;
import com.ide.photoeditor.youshoot.services.ImageCreatorService;
import com.ide.photoeditor.youshoot.videoeditor.activity.ImageToVideoActivity;
import com.ide.photoeditor.youshoot.videoeditor.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {
    private MyApp application = MyApp.getInstance();
    private LayoutInflater inflater;
    private ArrayList<THEMES> list;
    private ImageToVideoActivity previewActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View clickableView;
        private ImageView ivThumb,mIVCheck;
        private View mainView;
        private TextView tvThemeName;

        public ViewHolder(View v) {
            super(v);
            this.mIVCheck = v.findViewById(R.id.mIVCheck);
            this.ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
            this.tvThemeName = (TextView) v.findViewById(R.id.tvThemeName);
            this.clickableView = v.findViewById(R.id.clickableView);
            this.mainView = v;
        }
    }

    public ThemeAdapter(ImageToVideoActivity previewActivity) {
        this.previewActivity = previewActivity;
        this.list = new ArrayList(Arrays.asList(THEMES.values()));
        this.inflater = LayoutInflater.from(previewActivity);
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new ViewHolder(this.inflater.inflate(R.layout.items_movie_theme, paramViewGroup, false));
    }

    public void onBindViewHolder(ViewHolder holder, final int pos) {
        THEMES themes = (THEMES) this.list.get(pos);
        Glide.with(this.application).load(Integer.valueOf(themes.getThemeDrawable())).into(holder.ivThumb);
        holder.tvThemeName.setText(themes.toString());
        if(themes == this.application.selectedTheme){
            holder.mIVCheck.setVisibility(View.VISIBLE);
        }else {
            holder.mIVCheck.setVisibility(View.GONE);
        }
        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (list.get(pos) != application.selectedTheme) {
                    deleteThemeDir(application.selectedTheme.toString());
                    application.videoImages.clear();
                    application.selectedTheme = (THEMES) list.get(pos);
                    application.setCurrentTheme(application.selectedTheme.toString());
                    previewActivity.reset();
                    Intent intent = new Intent(application, ImageCreatorService.class);
                    intent.putExtra(ImageCreatorService.EXTRA_SELECTED_THEME, application.getCurrentTheme());
                    application.startService(intent);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void deleteThemeDir(final String dir) {
        new Thread() {
            public void run() {
                FileUtils.deleteThemeDir(dir);
            }
        }.start();
    }

    public ArrayList<THEMES> getList() {
        return this.list;
    }

    public int getItemCount() {
        return this.list.size();
    }
}
