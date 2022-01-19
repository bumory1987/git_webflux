package com.example.asyncman.asynctest;


import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
//import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;


@RestController
@EnableAsync
@SuppressWarnings("depracted")
public class MyController {

    @Autowired
    Myservice myservice;

    static Logger log = LoggerFactory.getLogger(String.valueOf(MyController.class));
    Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();
    RestTemplate rt = new RestTemplate();
    String url = "http://127.0.0.1:8081/service1?req=";
    String urltwo = "http://127.0.0.1:8081/service2?req=";
    //WebClient webClient = WebClient.create("http://localhost:8081");

    AsyncRestTemplate rts = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));



    @GetMapping("/restthird")
    public String rest(int idx) throws InterruptedException {
        Integer addval = idx;
        String addvaltwo ="pass_through_"+Integer.toString(addval);

        String response = rt.getForObject(url+addvaltwo, String.class);
        //rts.getForEntity(url+addvaltwo, String.class);
        return "rest/"+response;   /// text/html
    }


//    @GetMapping("/restfour")
//    public DeferredResult<String> restss(int idx) throws InterruptedException {
//        DeferredResult<String> obj = new DeferredResult<>();
//        Integer addval = idx;
//        String addvaltwo ="pass_through_"+Integer.toString(addval);
//        ListenableFuture<ResponseEntity<String>> ress = rts.getForEntity(url+addvaltwo, String.class);
//        ress.addCallback(s->{
//            ListenableFuture<ResponseEntity<String>> sec = rts.getForEntity(urltwo+addvaltwo, String.class);
//            sec.addCallback(s2->{
//                ListenableFuture<String>  third = myservice.work(s2.getBody());
//                third.addCallback(s3 ->  {
//                    obj.setResult(s.getBody()+"/modified1"+"/"+s3+"/connect");
//                }, e->{
//                    System.out.println("e = " + e);
//                });
//            }, e->{
//                System.out.println("e.getMessage() = " + e.getMessage());
//            });
//            //obj.setResult(s.getBody()+"/modified");
//        } , e->{
//            System.out.println("e = " + e.getMessage());
//        });
//        return obj; /// text/html
//    }



    @GetMapping("/dr")
    public DeferredResult<String> drcallable() throws InterruptedException{
        log.info("dr start");
        DeferredResult<String> dr = new DeferredResult<>(600000L);
        results.add(dr);
        return dr;
    }

    @GetMapping("/dr/count")
    public String drcuout(){
        return String.valueOf(results.size());
    }

    @GetMapping("/dr/event")
    public String drevent(String msg){
        for (DeferredResult<String> dr : results){
            dr.setResult("hello " + msg);
            results.remove(dr);
        }
        return "OK";
    }



    @GetMapping("callable")
    public String seccallable() throws InterruptedException{
        log.info("start original");
        Thread.sleep(2000);
        return "done";
    }


    @GetMapping("final")
    public Callable<String> callable() throws InterruptedException{
        log.info("start");
        return () -> {
            log.info("aysnc!!");
            Thread.sleep(2000);
            return "done";
        };
    }

}
