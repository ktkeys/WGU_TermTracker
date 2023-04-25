package com.wgu.termtracker;

public class Term {
    private String mText;
    private long mUpdateTime;

    public Term(){}

    public Term(String text){
        mText = text;
        mUpdateTime = System.currentTimeMillis();
    }

    public String getText(){
        return mText;
    }

    public void setText(String text){
        mText = text;
    }

    public long getUpdateTime(){
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime){
        mUpdateTime = updateTime;
    }
}
