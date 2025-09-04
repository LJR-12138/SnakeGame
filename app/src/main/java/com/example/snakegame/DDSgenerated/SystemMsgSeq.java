package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class SystemMsgSeq extends ZRSequence<SystemMsg> {

    protected Object[] alloc_element(int length) {
        SystemMsg[] result = new SystemMsg[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new SystemMsg();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        SystemMsg typedDst = (SystemMsg)dstEle;
        SystemMsg typedSrc = (SystemMsg)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}