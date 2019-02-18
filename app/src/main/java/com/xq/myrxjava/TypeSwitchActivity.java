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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lenovo on 2018/10/19.
 */

public class TypeSwitchActivity extends AppCompatActivity {

    private static final String TAG = "TypeSwitchActivity";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<String> list = new ArrayList<>();
        list.add("map");
        list.add("flatMap");
        list.add("concatMap");
        list.add("switchMap");
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
                    demo3();
                } else if (position == 3) {
                    demo4();
                }
            }

        });
    }

    private void demo1() {
        //发射interger
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        });

        //接受string
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        };

        //map()操作符就是用于变换Observable对象的，经过map操作符后返回一个Observable对象，这样就可以实现链式调用，
        //在一个Observable对象上多次使用map操作符，最终将最简洁的数据传递给Subscriber对象。
        observable.map(new Function<Integer, String>() {//integer转string
            @Override
            public String apply(Integer integer) throws Exception {//转为string类型
                return "map This is result " + integer;
            }
        }).subscribe(consumer);

    }

    private void demo2() {
        //发射interger
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        });

        //接受string
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        };

        //FlatMap将一个发送事件的上游Observable变换为多个发送事件的Observables，然后将它们发射的事件合并后放进一个单独的Observable里.
        //flatMap并不保证事件的顺序
        //flatmap可解决网络请求嵌套问题
        observable.flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {//转为observable
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("flatMap I am value " + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(consumer);


    }

    private void demo3() {
        //发射interger
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        });

        //接受string
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        };

        //类似flatmap
        //但保证事件的顺序
        observable.concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {//转为observable
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("concatMap I am value " + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(consumer);
    }

    private void demo4() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Observable.fromIterable(list)
                .switchMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return Observable.just("integer=" + integer);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {//依次打印1 2 3 4
                        System.out.println("---accept=" + s + " " + Thread.currentThread().getName());
                    }
                });

        //当在同一线程中时，任务是按次序的，一一被执行完。
        //而在不同线程中，如果前一个任务，尚未执行结束，就会被后一个任务给取消。
        Observable.fromIterable(list)
                .switchMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return Observable.just("integer=" + integer)
                                .subscribeOn(Schedulers.newThread());//多线程
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {//只打印4
                        System.out.println("===accept=" + s + " " + Thread.currentThread().getName());
                    }
                });


    }


}
