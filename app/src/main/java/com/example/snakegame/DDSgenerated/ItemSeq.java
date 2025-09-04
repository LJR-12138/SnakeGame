package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class ItemSeq extends ZRSequence<Item> {

    protected Object[] alloc_element(int length) {
        Item[] result = new Item[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new Item();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        Item typedDst = (Item)dstEle;
        Item typedSrc = (Item)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}