package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class GetFoodSeq extends ZRSequence<GetFood> {

    protected Object[] alloc_element(int length) {
        GetFood[] result = new GetFood[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new GetFood();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        GetFood typedDst = (GetFood)dstEle;
        GetFood typedSrc = (GetFood)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}