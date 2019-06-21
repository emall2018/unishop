package com.google.cloud.android.speech;

/**
 * Created by ASUS on 06/05/2019.
 */
import android.util.Log;

import org.webrtc.DataChannel;

import static android.content.ContentValues.TAG;

public class DataChannelAdapter implements DataChannel.Observer {
    @Override
    public void onMessage(DataChannel.Buffer buffer) {
        // message received here
    }
    @Override
    public void onBufferedAmountChange(long l) {

    }

    @Override
    public void onStateChange() {
        Log.d(TAG, "onStateChange: remote data channel state: " );
    }


}
