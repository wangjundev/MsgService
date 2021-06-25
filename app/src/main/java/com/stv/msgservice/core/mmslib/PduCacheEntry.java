package com.stv.msgservice.core.mmslib;

import com.stv.msgservice.core.mmslib.pdu.GenericPdu;

public final class PduCacheEntry {
    private final GenericPdu mPdu;
    private final int mMessageBox;
    private final long mThreadId;

    public PduCacheEntry(GenericPdu pdu, int msgBox, long threadId) {
        mPdu = pdu;
        mMessageBox = msgBox;
        mThreadId = threadId;
    }

    public GenericPdu getPdu() {
        return mPdu;
    }

    public int getMessageBox() {
        return mMessageBox;
    }

    public long getThreadId() {
        return mThreadId;
    }
}
