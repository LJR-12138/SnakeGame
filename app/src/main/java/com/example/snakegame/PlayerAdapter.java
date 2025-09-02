package com.example.snakegame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    
    private List<RoomActivity.Player> playerList;
    
    public PlayerAdapter(List<RoomActivity.Player> playerList) {
        this.playerList = playerList;
    }
    
    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        RoomActivity.Player player = playerList.get(position);
        holder.bind(player);
    }
    
    @Override
    public int getItemCount() {
        return playerList.size();
    }
    
    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlayerAvatar, tvPlayerNickname, tvPlayerStatus, tvHostBadge;
        
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerAvatar = itemView.findViewById(R.id.tv_player_avatar);
            tvPlayerNickname = itemView.findViewById(R.id.tv_player_nickname);
            tvPlayerStatus = itemView.findViewById(R.id.tv_player_status);
            tvHostBadge = itemView.findViewById(R.id.tv_host_badge);
        }
        
        public void bind(RoomActivity.Player player) {
            tvPlayerNickname.setText(player.getNickname());
            tvPlayerStatus.setText(player.getStatus());
            tvHostBadge.setVisibility(player.isHost() ? View.VISIBLE : View.GONE);
        }
    }
}
