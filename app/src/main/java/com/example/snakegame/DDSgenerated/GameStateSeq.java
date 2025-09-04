package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class GameStateSeq extends ZRSequence<GameState> {

    protected Object[] alloc_element(int length) {
        GameState[] result = new GameState[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new GameState();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        GameState typedDst = (GameState)dstEle;
        GameState typedSrc = (GameState)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}