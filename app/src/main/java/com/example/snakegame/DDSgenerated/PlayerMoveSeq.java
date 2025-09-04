package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class PlayerMoveSeq extends ZRSequence<PlayerMove> {

    protected Object[] alloc_element(int length) {
        PlayerMove[] result = new PlayerMove[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new PlayerMove();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        PlayerMove typedDst = (PlayerMove)dstEle;
        PlayerMove typedSrc = (PlayerMove)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}