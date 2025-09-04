package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class ItemTypeSeq extends ZRSequence<ItemType> {

    protected Object[] alloc_element(int length) {
        ItemType[] result = new ItemType[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new ItemType();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        ItemType typedDst = (ItemType)dstEle;
        ItemType typedSrc = (ItemType)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}