package com.example.snakegame.SnakeGame;

import com.zrdds.infrastructure.ZRSequence;

public class LeaderboardEntrySeq extends ZRSequence<LeaderboardEntry> {

    protected Object[] alloc_element(int length) {
        LeaderboardEntry[] result = new LeaderboardEntry[length];
        for (int i = 0; i < result.length; ++i) {
             result[i] = new LeaderboardEntry();
        }
        return result;
    }

    protected Object copy_from_element(Object dstEle, Object srcEle){
        LeaderboardEntry typedDst = (LeaderboardEntry)dstEle;
        LeaderboardEntry typedSrc = (LeaderboardEntry)srcEle;
        return typedDst.copy(typedSrc);
    }

    public void pull_from_nativeI(long nativeSeq){

    }

    public void push_to_nativeI(long nativeSeq){

    }
}