package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class InRoomSeq extends ZRSequence<InRoom> {

    protected Object[] alloc_element(int length) {
        InRoom[] result = new InRoom[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new InRoom();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        InRoom typedDst = (InRoom)dstEle;
        InRoom typedSrc = (InRoom)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}