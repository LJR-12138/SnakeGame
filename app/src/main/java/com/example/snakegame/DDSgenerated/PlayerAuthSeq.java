package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class PlayerAuthSeq extends ZRSequence<PlayerAuth> {

    protected Object[] alloc_element(int length) {
        PlayerAuth[] result = new PlayerAuth[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new PlayerAuth();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        PlayerAuth typedDst = (PlayerAuth)dstEle;
        PlayerAuth typedSrc = (PlayerAuth)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}