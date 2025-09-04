package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class PlayerInfoSeq extends ZRSequence<PlayerInfo> {

    protected Object[] alloc_element(int length) {
        PlayerInfo[] result = new PlayerInfo[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new PlayerInfo();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        PlayerInfo typedDst = (PlayerInfo)dstEle;
        PlayerInfo typedSrc = (PlayerInfo)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}