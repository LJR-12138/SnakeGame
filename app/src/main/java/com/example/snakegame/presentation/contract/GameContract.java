package com.example.snakegame.presentation.contract;

import com.example.snakegame.data.model.GameWorld;
import com.example.snakegame.data.model.Player;
import java.util.List;

public interface GameContract {
    
    interface View {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void onGameWorldUpdated(GameWorld gameWorld);
        void onGameStarted();
        void onGameEnded();
        void showChatMessage(String playerName, String message);
        void updateLeaderboard(List<Player> leaderboard);
    }
    
    interface Presenter {
        void attachView(View view);
        void detachView();
        void initializeGame(long playerId, String nickname, String color);
        void handlePlayerMove(String direction);
        void sendChatMessage(String message);
        void startGame();
        void pauseGame();
        void endGame();
    }
}