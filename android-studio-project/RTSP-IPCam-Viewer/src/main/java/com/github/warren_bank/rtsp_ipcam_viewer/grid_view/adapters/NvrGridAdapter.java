package com.github.warren_bank.rtsp_ipcam_viewer.grid_view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.warren_bank.rtsp_ipcam_viewer.R;
import java.util.List;

public class NvrGridAdapter extends RecyclerView.Adapter<NvrGridAdapter.ViewHolder> {

    private final Context context;
    private final List<String> cameraTitles;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public NvrGridAdapter(Context context, List<String> cameraTitles, OnItemClickListener listener) {
        this.context = context;
        this.cameraTitles = cameraTitles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_nvr, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = cameraTitles.get(position);
        holder.txtTitle.setText(title);

        // Hace que la casilla sea seleccionable con las flechas del D-Pad
        holder.itemView.setFocusable(true);
        holder.itemView.setClickable(true);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cameraTitles != null ? cameraTitles.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public PlayerView playerView;
        public TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.nvr_player_view);
            txtTitle = itemView.findViewById(R.id.txt_camera_title);
        }
    }
}
