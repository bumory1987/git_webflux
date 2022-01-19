package com.example.asyncman.asynctest.generator;

import java.util.concurrent.Flow;
import java.util.function.Function;




public class Pubssb {



    public static <T, S > Flow.Publisher<S> mapPub(Flow.Publisher<T> pub,
                                                   Function<T, T> f,
                                                   Function<T, S> g
    ){



        return  new Flow.Publisher<S>() {

            @Override
            public void subscribe(Flow.Subscriber<? super S> subtwo) {

                Flow.Subscriber<T> newSub = new Flow.Subscriber<T>(){

                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        subtwo.onSubscribe(subscription);
                    }


                    @Override
                    public void onNext(T item) {
                        subtwo.onNext(g.apply(item));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("throwable = " + throwable);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("OnComplete");
                    }
                };


                Flow.Subscriber<T> finalsub = new Flow.Subscriber<T>(){

                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        newSub.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(T item) {
                        newSub.onNext(f.apply(item));
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };


                pub.subscribe(finalsub);
            }

        };
    }



    public static void main(String[] args){
        Flow.Publisher<Integer> pub = sub ->
        {  sub.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(long n) {
                for(int i = 0 ; i < 10 ; i ++ ){
                    sub.onNext(i+1 );

                }
                sub.onComplete();

            }

            @Override
            public void cancel() {

            }

            });


        };


        Flow.Publisher<String> finalpub = mapPub(pub,
                (i)-> i*100,
                (j)-> j.toString());

        Flow.Subscriber<String> endsub = new Flow.Subscriber<String>(){

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(100);
            }

            @Override
            public void onNext(String item) {
                System.out.println("item = " + item + " dodo!!");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onCompletet");
            }
        };

        finalpub.subscribe(endsub);




    }

}
