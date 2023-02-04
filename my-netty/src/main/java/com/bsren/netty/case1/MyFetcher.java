package com.bsren.netty.case1;

public class MyFetcher implements Fetcher{

    Data data;

    public MyFetcher(Data data) {
        this.data = data;
    }

    @Override
    public void fetchData(FetcherCallback fetcherCallback) {
        try {
            fetcherCallback.onData(data);
        }catch (Exception e){
            fetcherCallback.onError(e);
        }
    }
}
