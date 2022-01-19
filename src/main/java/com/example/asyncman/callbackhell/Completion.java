package com.example.asyncman.callbackhell;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;


public class Completion<T> {
    Completions next;
    Consumer<ResponseEntity<String>> con;
    Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> contwo;

    public Completion(){

    }
    public Completion(Consumer<ResponseEntity<String>> con) {
        this.con = con;
    }

    public Completion(Function<ResponseEntity<String>,
            ListenableFuture<ResponseEntity<String>>> contwo) {
        this.contwo = contwo;
    }

    public Completions from(ListenableFuture<ResponseEntity<String>> lf){
        Completions c = new Completions();
        lf.addCallback(s ->{
            c.complete(s);
        }, e ->{
            c.error(e);
        } );
        return c;
    }

    public void andAccept(Consumer<ResponseEntity<String>> con){
        Completions c = new Completions(con );
        this.next =c;
    }


    void error(Throwable e) {

    }

    void complete(ResponseEntity<String> s) {
        if(next != null) {next.run(s);};
    }

    void run(ResponseEntity<String> s) {
        if(con != null) con.accept(s);
        else if(contwo != null) {
            ListenableFuture<ResponseEntity<String>> lf = contwo.apply(s);

            lf.addCallback(ss -> complete(ss),  ee-> error(ee));
        }


    }


    public Completions andApply(Function<ResponseEntity<String>,
            ListenableFuture<ResponseEntity<String>>> contwo ) {
        Completions c = new Completions(contwo);
        this.next = c;
        return c;
    }

}



