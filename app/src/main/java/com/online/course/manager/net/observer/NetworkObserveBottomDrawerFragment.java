package com.online.course.manager.net.observer;

import com.github.heyalex.bottomdrawer.BottomDrawerFragment;

import java.util.ArrayDeque;
import java.util.Queue;

import retrofit2.Call;

public class NetworkObserveBottomDrawerFragment extends BottomDrawerFragment {
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
