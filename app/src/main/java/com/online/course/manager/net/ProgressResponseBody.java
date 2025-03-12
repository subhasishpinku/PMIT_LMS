package com.online.course.manager.net;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private OnDownloadProgressListener progressListener;
    private BufferedSource bufferedSource;
    private final Integer id;

    public ProgressResponseBody(ResponseBody responseBody, OnDownloadProgressListener progressListener, Integer id) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.id = id;
    }

    public void setProgressListener(OnDownloadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public void close() throws IOException {
                super.close();
            }

            @NotNull
            @Override
            public Timeout timeout() {
                return super.timeout();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                float percent = bytesRead == -1 ? 100f : (((float) totalBytesRead / (float) responseBody.contentLength()) * 100);

                if (progressListener != null) {
                    percent = Float.parseFloat(String.format("%.3f", percent));
                    percent = Float.parseFloat(String.format("%.2f", percent));
                    percent = Float.parseFloat(String.format("%.1f", percent));
                    progressListener.onAttachmentDownloadUpdate(percent, id);
                }

                return bytesRead;
            }
        };
    }
}
