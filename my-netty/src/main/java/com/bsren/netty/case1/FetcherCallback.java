package com.bsren.netty.case1;

import com.bsren.netty.case1.Data;

public interface FetcherCallback {

    void onData(Data data) throws Exception;

    void onError(Throwable r);
}
