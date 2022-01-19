package com.example.asyncman.asynctest;


import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTest {
    static Logger log = LoggerFactory.getLogger(String.valueOf(MyController.class));
    static AtomicInteger counter = new AtomicInteger(0);

    @SneakyThrows
    public static void main(String[] args){
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();

        //String urlpre ="http//localhost:8080/callable";
        String url = "http://127.0.0.1:8080/rest";

        StopWatch main = new StopWatch();
        main.start();


        for(int i=0 ; i<100; i++ ){
            es.execute(()->{
                int idx = counter.addAndGet(1);
                log.info("Thread " + idx);
                StopWatch sw = new StopWatch();
                sw.start();
                rt.getForObject(url, String.class);
                sw.stop();
                log.info("Elasped: " +idx + " -> "+sw.getTotalTimeSeconds());
            });
        }
        es.shutdown();
        es.awaitTermination(1000, TimeUnit.SECONDS);

        main.stop();
        log.info("Total time : {}", main.getTotalTimeSeconds());


    }


}
