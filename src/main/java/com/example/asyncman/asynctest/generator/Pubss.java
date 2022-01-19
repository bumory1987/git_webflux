package com.example.asyncman.asynctest.generator;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Function;


class SubSupport<T> implements Flow.Subscriber<T> {
    List<T> list = new ArrayList<>();

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("Start!");
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T item) {
        list.add(item);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("throwable = " + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
    public List<T> getData(){
        return list;
    }

}


class SumSupport<T extends Integer> implements Flow.Subscriber<T> {
    Integer sum = 0 ;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("Start!");
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T item) {
        Integer val = (Integer) item;
        sum = sum+ val;
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("throwable = " + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("sum =" + sum);
    }

}







public class Pubss<T> {





    public static <T, S >Flow.Publisher<S> mapPub(Flow.Publisher<T> pub,
                                                  Function<T, S> f
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
                        subtwo.onNext(f.apply(item));
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



                pub.subscribe(newSub);
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

        });   };


//        Flow.Publisher<String> firstpub =  mapPub(
//                pub, (i)-> {return i.toString();}
//        );
//
//        Flow.Publisher<Integer> secondpub =  mapPub(
//                firstpub, (i)-> i*10
//        );





        //SubSupport<String> sub = new SubSupport<String>();
//        firstpub.subscribe(sub);
//        for (String i : sub.getData()){
//            System.out.println("i = " + i);
//        }

    }
}





