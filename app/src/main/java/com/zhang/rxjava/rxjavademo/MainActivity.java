package com.zhang.rxjava.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // begin();
        //starts();
        //starts2();
        //backpressure();
        //test();
       // test1();
        test2();
    }

    private void test2() {

        Integer numbers[]={1,2,3,4,5,};
        Observable observable=Observable.fromArray(numbers);
        observable.scan(new BiFunction<Integer,Integer,Integer>(){

                         @Override
                         public Integer apply(Integer integer, Integer integer2) throws Exception {
                             return integer+integer2;
                         }
                     }


                ).map(new Function<Integer,String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return String.valueOf(integer);
            }


        })
            .subscribe(new Consumer<String>() {
                           @Override
                           public void accept(String s) throws Exception {
                               Log.e(TAG, s);
                           }




                });



    }

    private void test1() {
        Observable.fromArray("hello","word","ni","hao ")
       /* Observable.just("hello","word","ni","hao ")*/.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, String.valueOf(d.isDisposed()));
            }

            @Override
            public void onNext(String value) {
                Log.e(TAG, value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });


    }


    /**
     * ObservableEmitter： Emitter是发射器的意思，
     * 发出事件，它可以发出三种类型的事件，
     * 通过调用emitter的onNext(T value)、onComplete()和onError(Throwable error)
     * 就可以分别发出next事件、complete事件和error事件
     * onComplete和onError必须唯一并且互斥
     */
    private void starts() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
                Log.e(TAG, "emit 2");
                emitter.onNext(2);
                Log.e(TAG, "emit 3");
                emitter.onNext(3);
                Log.e(TAG, "emit complete");
                emitter.onComplete();
                Log.e(TAG, "emit 4");
                emitter.onNext(4);


            }
        })
                .subscribe(new Observer<Integer>() {
                    //Disposable  一次性的
                    private Disposable mDisposable;
                    private int j;

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "subscribe");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG, "onNext :" + value);

                        j++;
                        if (j == 3) {
                            mDisposable.dispose();
                            Log.e(TAG, "dispose");
                            mDisposable.dispose();
                            Log.e(TAG, "isDisposed : " + mDisposable.isDisposed());


                        }
                        Log.e("j=", "" + j);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "complete");
                    }
                });
    }

    private void begin() {

        Observable<Integer> observable = Observable.create(
                new ObservableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onComplete();
                    }
                }
        );
        //创建一个 Observer
        Observer<Integer> observer = new Observer<Integer>() {
            //Disposable  一次性的 用于解除订阅
            private Disposable mDisposable;
            private int j;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e("subscribe", "subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.e("onNext", "" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("onError", "error");
            }

            @Override
            public void onComplete() {
                Log.e("onComplete", "complete");
            }
        };
        //建立连接
        observable.subscribe(observer);

    }

    private void starts2() {
        Observable<Integer> observable = Observable.create(
                new ObservableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onComplete();
                    }
                }
        );

        Disposable disposable = observable.subscribe(
                new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //接受数据
                        Log.e(TAG, "integer:" + integer);
                    }
                }
                , new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //这里接收onError
                        Log.e(TAG, "error");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //这里接收onComplete。
                        Log.e(TAG, "complete");
                    }
                });

    }

    private void backpressure() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {

            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {

                for (int i = 0; i < 10000; i++) {
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR) //指定背压处理策略，抛出异常
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.newThread())  //指定线程（子线程）
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, integer.toString());
                        //Thread.sleep(1000);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    private void test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
                e.onNext(1);
                e.onComplete();
            }
        })
                .delay(2, TimeUnit.SECONDS)//延迟两秒发送
                .delaySubscription(2,TimeUnit.SECONDS)//延迟两秒订阅
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "After observeOn(mainThread)，Current thread is " + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "After observeOn(io)，Current thread is " + Thread.currentThread().getName());
                    }
                });

    }


}