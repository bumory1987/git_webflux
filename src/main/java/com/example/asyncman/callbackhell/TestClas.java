package com.example.asyncman.callbackhell;

import java.util.function.Function;

public class TestClas<T,R> {
    Function<T, R> f;

    public TestClas() {
        f = (a) -> (R) a;
    }

    public void setFunc(Function<T,R> f ){
        this.f = f;
    }

    public R doit(T val){
        return f.apply(val);
    }

    public static void main(String[] args ){
        TestClas<Integer, Integer> test = new TestClas<>();
        test.setFunc((Function<Integer, Integer>) (a)-> a*100 );
        Integer s = test.doit(7);
        System.out.println("s = " + s);
    }

}
