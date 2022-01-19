package com.example.asyncman.callbackhell;

import com.example.asyncman.asynctest.MyController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class CFuture {
    //CompletableFuture -> complete , completeExceptionally
    static Logger log = LoggerFactory.getLogger(String.valueOf(MyController.class));


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);

//        CompletableFuture<Integer> f = new CompletableFuture<>();
//        //f.complete(2);
//        f.completeExceptionally(new RuntimeException());
//        System.out.println("f.get() = " + f.get());



//        CompletableFuture.runAsync(()->{
//            log.info("runAsync");
//        }).thenRun(()->{
//             log.info("mid");
//        }).thenRun(()->{
//            log.info("final");});

        //supply-> 특정한 데이터를 제공
        //thenCompose -> flatmap(CompletableFuture)
        CompletableFuture.supplyAsync(()->{
            log.info("supplyAsync");
            return 1;
        }).thenCompose((a)->{
            log.info("thenApply {}", a);
            //f(1==1) throw new RuntimeException();
            return CompletableFuture.completedFuture(a+1);
        }).thenApply((a)->{
            log.info("thenApply {}", a);
            return a*3;

        })
        .exceptionally(e-> -10)
        .thenAcceptAsync(s2-> log.info("thenRun {}", s2), es);

        log.info("exit");
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }
}
