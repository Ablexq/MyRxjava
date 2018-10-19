package com.xq.myrxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xq.myrxjava.adapter.MyAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ThreadSwitchActivity extends AppCompatActivity {

    private static final String TAG = "ThreadSwitchActivity";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<String> list = new ArrayList<>();
        list.add("demo1");
        list.add("demo2");
        MyAdapter myAdapter = new MyAdapter(R.layout.activity_item, list);
        recyclerview.setAdapter(myAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                System.out.println("position==================" + position);
                if (position == 0) {
                    demo1();
                } else if (position == 1) {
                    demo2();
                } else if (position == 2) {

                }
            }

        });
    }


    //10-19 00:46:24.278 4009-4049: Observable thread is : RxNewThreadScheduler-3
    //10-19 00:46:24.278 4009-4049: emitter.onNext 1
    //10-19 00:46:24.279 4009-4049: emitter.onNext 2
    //10-19 00:46:24.280 4009-4009: After observeOn(mainThread), current thread is: main
    //10-19 00:46:24.280 4009-4009: After observeOn(mainThread), accept: 1
    //10-19 00:46:24.280 4009-4009: After observeOn(mainThread), current thread is: main
    //10-19 00:46:24.280 4009-4009: After observeOn(mainThread), accept: 2
    //10-19 00:46:24.280 4009-4049: emitter.onNext 3
    //10-19 00:46:24.280 4009-4041: After observeOn(io), current thread is : RxCachedThreadScheduler-1
    //10-19 00:46:24.280 4009-4041: After observeOn(io), accept: 1
    //10-19 00:46:24.280 4009-4041: accept thread is :RxCachedThreadScheduler-1
    //10-19 00:46:24.280 4009-4041: accept: 1
    //10-19 00:46:24.280 4009-4049: emitter.onComplete
    //10-19 00:46:24.280 4009-4049: emitter.onNext 4
    //10-19 00:46:24.280 4009-4041: After observeOn(io), current thread is : RxCachedThreadScheduler-1
    //10-19 00:46:24.280 4009-4041: After observeOn(io), accept: 2
    //10-19 00:46:24.280 4009-4041: accept thread is :RxCachedThreadScheduler-1
    //10-19 00:46:24.280 4009-4041: accept: 2
    //10-19 00:46:24.280 4009-4009: After observeOn(mainThread), current thread is: main
    //10-19 00:46:24.281 4009-4009: After observeOn(mainThread), accept: 3
    //10-19 00:46:24.281 4009-4041: After observeOn(io), current thread is : RxCachedThreadScheduler-1
    //10-19 00:46:24.281 4009-4041: After observeOn(io), accept: 3
    //10-19 00:46:24.281 4009-4041: accept thread is :RxCachedThreadScheduler-1
    //10-19 00:46:24.281 4009-4041: accept: 3
    private void demo2() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emitter.onNext 1");
                emitter.onNext(1);
                Log.d(TAG, "emitter.onNext 2");
                emitter.onNext(2);
                Log.d(TAG, "emitter.onNext 3");
                emitter.onNext(3);
                Log.d(TAG, "emitter.onComplete");
                emitter.onComplete();
                Log.d(TAG, "emitter.onNext 4");
                emitter.onNext(4);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "accept: " + integer);
            }
        };

        //subscribeOn() 指定的是上游发送事件的线程,
        //observeOn() 指定的是下游接收事件的线程.
        //多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
        //多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
        observable.subscribeOn(Schedulers.newThread())//第一次有效
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//每次都有效
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                        Log.d(TAG, "After observeOn(mainThread), accept: " + integer);
                    }
                })
                .observeOn(Schedulers.io())//每次都有效
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                        Log.d(TAG, "After observeOn(io), accept: " + integer);
                    }
                })
                .subscribe(consumer);
    }

    //10-19 00:43:42.731 3890-3937: Observable thread is : RxNewThreadScheduler-2
    //10-19 00:43:42.731 3890-3937: emitter.onNext 1
    //10-19 00:43:42.731 3890-3937: emitter.onNext 2
    //10-19 00:43:42.731 3890-3937: emitter.onNext 3
    //10-19 00:43:42.731 3890-3937: emitter.onComplete
    //10-19 00:43:42.731 3890-3937: emitter.onNext 4
    //10-19 00:43:42.731 3890-3890: accept thread is :main
    //10-19 00:43:42.731 3890-3890: accept: 1
    //10-19 00:43:42.731 3890-3890: accept thread is :main
    //10-19 00:43:42.731 3890-3890: accept: 2
    //10-19 00:43:42.731 3890-3890: accept thread is :main
    //10-19 00:43:42.731 3890-3890: accept: 3
    private void demo1() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emitter.onNext 1");
                emitter.onNext(1);
                Log.d(TAG, "emitter.onNext 2");
                emitter.onNext(2);
                Log.d(TAG, "emitter.onNext 3");
                emitter.onNext(3);
                Log.d(TAG, "emitter.onComplete");
                emitter.onComplete();
                Log.d(TAG, "emitter.onNext 4");
                emitter.onNext(4);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "accept: " + integer);
            }
        };

        observable.subscribeOn(Schedulers.newThread())
                //多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
                .observeOn(AndroidSchedulers.mainThread())
                //每调用一次observeOn() , 下游的线程就会切换一次.
                .subscribe(consumer);
    }
}