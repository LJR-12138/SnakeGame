package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class GameSettingSeq extends ZRSequence<GameSetting> {

    protected Object[] alloc_element(int length) {
        GameSetting[] result = new GameSetting[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new GameSetting();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        GameSetting typedDst = (GameSetting)dstEle;
        GameSetting typedSrc = (GameSetting)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}