package com.example.asyncman.asynctest;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestTwo {

    static Logger log = LoggerFactory.getLogger(String.valueOf(LoadTestTwo.class));
    static AtomicInteger counter = new AtomicInteger(0);


    @SneakyThrows
    public static void main(String[] args){
        ExecutorService es = Executors.newFixedThreadPool(100);




        RestTemplate rt = new RestTemplate();

        //String urlpre ="http//localhost:8080/callable";
        String url = "http://127.0.0.1:8080/resttwo?idx=";

        StopWatch main = new StopWatch();
        main.start();

        CyclicBarrier barrier = new CyclicBarrier(101);

        for(int i=0 ; i<100; i++ ){
            es.submit(()->{
                int idx = counter.addAndGet(1);
                barrier.await();
                log.info("Thread " + idx);
                StopWatch sw = new StopWatch();
                sw.start();
                String newrul = url+idx;
                String res= rt.getForObject(newrul, String.class);
                System.out.println("res = " + res);
                sw.stop();
                log.info("Elasped: " +idx + " -> "+sw.getTotalTimeSeconds() +" "+res );
                return null;
            });
        }

        barrier.await();
        es.shutdown();
        es.awaitTermination(5000, TimeUnit.SECONDS);

        main.stop();
        log.info("Total time : {}", main.getTotalTimeSeconds());


    }


}
