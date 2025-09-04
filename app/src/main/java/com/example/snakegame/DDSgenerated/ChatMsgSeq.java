package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class ChatMsgSeq extends ZRSequence<ChatMsg> {

    protected Object[] alloc_element(int length) {
        ChatMsg[] result = new ChatMsg[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new ChatMsg();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        ChatMsg typedDst = (ChatMsg)dstEle;
        ChatMsg typedSrc = (ChatMsg)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}