package com.ide.photoeditor.youshoot.videoeditor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
//import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.model.VideoData;
//import com.emadyous.editingfyVideos.R;
//import com.emadyous.editingfyVideos.databinding.VideoFileItemBinding;
//import com.emadyous.editingfyVideos.model.VideoData;

import java.util.List;

public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.ViewHolder> {

    private final List<VideoData> list;
    private final VideoFilesHandler handler;

    public VideoFilesAdapter(List<VideoData> list, VideoFilesHandler handler) {
        this.list = list;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ImageView image = holder.videoPreview;

        Glide.with(image.getContext())
                .load(list.get(position).videouri)
                .into(image);

        holder.itemView.setOnClickListener(view -> handler.onChoose(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView videoPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoPreview = itemView.findViewById(R.id.video_preview);

        }
    }

    public interface VideoFilesHandler {
        void onChoose(int position);
    }
}
