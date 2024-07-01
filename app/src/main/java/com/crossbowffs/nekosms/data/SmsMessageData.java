package com.crossbowffs.nekosms.data;

import android.content.ContentUris;
import android.net.Uri;

import com.crossbowffs.nekosms.provider.DatabaseContract;

public class SmsMessageData {
    private long mId = -1;
    private String mSender;
    private String mBody;
    private long mTimeSent;
    private long mTimeReceived;
    private boolean mRead;
    private boolean mSeen;
    private int mSubId = -1;

    public void reset() {
        mId = -1;
        mSender = null;
        mBody = null;
        mTimeSent = 0;
        mTimeReceived = 0;
        mRead = false;
        mSeen = false;
        mSubId = -1;
    }

    public SmsMessageData setId(long id) {
        mId = id;
        return this;
    }

    public SmsMessageData setSender(String sender) {
        mSender = sender;
        return this;
    }

    public SmsMessageData setBody(String body) {
        mBody = body;
        return this;
    }

    public SmsMessageData setTimeSent(long timeSent) {
        mTimeSent = timeSent;
        return this;
    }

    public SmsMessageData setTimeReceived(long timeReceived) {
        mTimeReceived = timeReceived;
        return this;
    }

    public SmsMessageData setRead(boolean read) {
        mRead = read;
        return this;
    }

    public SmsMessageData setSeen(boolean seen) {
        mSeen = seen;
        return this;
    }

    public SmsMessageData setSubId(int subId) {
        mSubId = subId;
        return this;
    }

    public long getId() {
        return mId;
    }

    public String getSender() {
        return mSender;
    }

    public String getBody() {
        return mBody;
    }

    public long getTimeSent() {
        return mTimeSent;
    }

    public long getTimeReceived() {
        return mTimeReceived;
    }

    public boolean isRead() {
        return mRead;
    }

    public boolean isSeen() {
        return mSeen;
    }

    public int getSubId() {
        return mSubId;
    }

    public Uri getUri() {
        long id = getId();
        if (id < 0) {
            return null;
        }
        return ContentUris.withAppendedId(DatabaseContract.BlockedMessages.CONTENT_URI, id);
    }
}
