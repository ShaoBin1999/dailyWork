package com.bsren.netty.case1;

public class Worker {

    public static void main(String[] args) {
        Worker w = new Worker();
        w.doWorker();
    }

    public void doWorker(){
        Fetcher fetcher = new MyFetcher(new Data(1,2));
        fetcher.fetchData(new FetcherCallback() {
            @Override
            public void onData(Data data) throws Exception {
                System.out.println("data received "+data);
            }

            @Override
            public void onError(Throwable r) {
                System.out.println("error occur"+r);
            }
        });
    }
}
