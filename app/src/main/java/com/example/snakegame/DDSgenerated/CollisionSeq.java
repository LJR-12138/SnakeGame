package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class CollisionSeq extends ZRSequence<Collision> {

    protected Object[] alloc_element(int length) {
        Collision[] result = new Collision[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new Collision();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        Collision typedDst = (Collision)dstEle;
        Collision typedSrc = (Collision)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}