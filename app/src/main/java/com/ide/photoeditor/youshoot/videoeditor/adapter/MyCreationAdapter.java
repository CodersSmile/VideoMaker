package com.ide.photoeditor.youshoot.videoeditor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ide.photoeditor.youshoot.Activity.MyCreationActivity;
import com.ide.photoeditor.youshoot.Activity.ShowVideoActivity;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.dialog.DeleteDialog;

import java.io.File;
import java.util.ArrayList;

public class MyCreationAdapter extends RecyclerView.Adapter<MyCreationAdapter.ViewHolder> {

    private final ArrayList<MyCreationActivity.File_Model> list;
    private final VideoFilesHandler handler;
    Context mContext;

    public MyCreationAdapter(Context context, ArrayList<MyCreationActivity.File_Model> list, VideoFilesHandler handler) {
        this.list = list;
        this.handler = handler;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_creation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        holder.img_creation.setLayoutParams(new RelativeLayout.LayoutParams(width / 2, width / 2));

//            holder.img_creation.setImageURI(Uri.parse(paths[position].file_path))
        Glide.with(mContext)
                .load(list.get(position).file_path).placeholder(R.drawable.logo)
                .into(holder.img_creation);
        holder.txt_title.setText(list.get(position).file_title);
        holder.img_dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick();
                DeleteDialog coinsAlertDialog = new DeleteDialog(new DeleteDialog.OnClickListener() {
                    @Override
                    public void discard() {
                        String filepath = list.get(position).file_path;
                        if (new File(filepath).delete()) {
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                });
                coinsAlertDialog.show(MyCreationActivity.getInstance().getSupportFragmentManager(), "");

            }
        });
        holder.img_creation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick();
                String uri = list.get(position).file_path;

                if (uri.contains(".3gp") || uri.contains(".3GP") || uri.contains(".flv") || uri.contains(
                        ".FLv"
                ) || uri.contains(".mov") || uri.contains(".MOV") || uri.contains(".wmv") || uri.contains(
                        ".WMV"
                ) || uri.contains(".mp4") || uri.contains(".Mp4") || uri.contains(".MP4")
                ) {
                    try {
                       MyCreationActivity.getInstance().mShowVideoAct(uri);
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Long mLastClickTime = Long.valueOf(0);

    void checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_creation;
        ImageView img_dlt;
        TextView txt_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_creation = itemView.findViewById(R.id.img_creation);
            img_dlt = itemView.findViewById(R.id.img_dlt);
            txt_title = itemView.findViewById(R.id.txt_title);

        }
    }

    public interface VideoFilesHandler {
        void onChoose(String position);
    }
}
