package com.online.course.manager.net.observer;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayDeque;
import java.util.Queue;

import retrofit2.Call;

public class NetworkObserverDialog extends DialogFragment {
    private Queue<Call<?>> mCalls;

    public void addNetworkRequest(Call<?> call) {
        if (mCalls == null) {
            mCalls = new ArrayDeque<>();
        }

        mCalls.add(call);
    }

    public void cancelRequests() {
        if (mCalls != null) {
            while (mCalls.size() > 0) {
                Call<?> poll = mCalls.poll();
                if (poll != null)
                    poll.cancel();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelRequests();
    }
}
