package com.example.asyncman.callbackhell;



import com.example.asyncman.asynctest.MyController;
import com.example.asyncman.asynctest.Myservice;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.CompleteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;


@RestController
@EnableAsync
@SuppressWarnings("depracted")
public class MyControllerCall {


    public static class ErrorCompletions extends Completions{
        public Consumer<Throwable> econ;
        public ErrorCompletions(Consumer<Throwable> econ){
            this.econ = econ;
        }

        @Override
        void run(ResponseEntity<String> value){
            if(next!=null) next.run(value);
        }

        @Override
        void error(Throwable e){
            econ.accept(e);
        }

    }


    public static class AcceptCompletions extends Completions{
        public Consumer<ResponseEntity<String>> con;
        public AcceptCompletions(Consumer<ResponseEntity<String>>con){
            this.con = con;
        }

        @Override
        void run(ResponseEntity<String> value){
            con.accept(value);
        }

    }

    public static class ApplyCompletions extends Completions {
        public Function<ResponseEntity<String>,
                ListenableFuture<ResponseEntity<String>>> fn;

        public ApplyCompletions(Function<ResponseEntity<String>,
                ListenableFuture<ResponseEntity<String>>> fn) {
            this.fn = fn;

        }

        @Override
        void run(ResponseEntity<String> value){
            ListenableFuture<ResponseEntity<String>> lf = fn.apply(value);
            lf.addCallback(s->complete(s), e->error(e));
        }

    }


    public static Completions generator(){
        return new Completions();
    }


    @Autowired
    Myservice myservice;


    static Logger log = LoggerFactory.getLogger(String.valueOf(MyController.class));
    //Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();
    RestTemplate rt = new RestTemplate();
    String url = "http://127.0.0.1:8081/service1?req=";
    String urltwo = "http://127.0.0.1:8081/service2?req=";
    //WebClient webClient = WebClient.create("http://localhost:8081");

    AsyncRestTemplate rts = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));



    @GetMapping("/rest")
    public String rest(int idx) throws InterruptedException {
        Integer addval = idx;
        String addvaltwo ="pass_through_"+Integer.toString(addval);

        String response = rt.getForObject(url+addvaltwo, String.class);
        //rts.getForEntity(url+addvaltwo, String.class);
        return "rest/"+response;   /// text/html
    }

    <T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
        CompletableFuture<T> cf = new CompletableFuture<>();
        lf.addCallback(s -> {
            cf.complete(s);
        }, e->{
            cf.completeExceptionally(e);
        } );
        return cf;
    }


    @GetMapping("/resttwo")
    public DeferredResult<String> restss(int idx) throws InterruptedException {
        Integer addval = idx;
        //String addvaltwo ="pass_through_"+Integer.toString(addval);

        DeferredResult<String> dr = new DeferredResult<>();
//        generator().from(rts.getForEntity(url+addval, String.class))
//                .andApply(newsq-> rts.getForEntity(urltwo+newsq.getBody(), String.class))
//                .andError(e-> dr.setErrorResult(e.toString()))
//                .andAccept(s-> dr.setResult(s.getBody()));


        ListenableFuture<ResponseEntity<String>> f1 = rts.getForEntity(url+addval, String.class);
        CompletableFuture<ResponseEntity<String>> cf = toCF(f1);
        cf.thenCompose(s1->{
            return toCF(rts.getForEntity(urltwo+s1.getBody(), String.class));
        }).thenApplyAsync(s2->{
            if(1==1) throw new RuntimeException();
            return myservice.work(s2.getBody());
        }).thenAccept(
            s3 ->  dr.setResult(s3)
        ).exceptionally(e -> {dr.setErrorResult(e.getMessage());
                              return (Void) null ; });


        return dr; /// text/html
    }




}
