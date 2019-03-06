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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class BaseUseActivity extends AppCompatActivity {

    private static final String TAG = "BaseUseActivity";
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
        list.add("zip：组装Observable结果，可能不完整");
        list.add("filter");
        list.add("sample");
        list.add("take repeat distinct");
        list.add("interval");
        list.add("timer");
        list.add("delay");
        list.add("doOn系列");
        list.add("concat");
        list.add("Merge:合并Observable：有序，完全");
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
                } else if (position == 4) {
                    demo5();
                } else if (position == 5) {
                    demo6();
                } else if (position == 6) {
                    demo7();
                } else if (position == 7) {
                    demo8();
                } else if (position == 8) {
                    demo9();
                } else if (position == 9) {
                    demo10();
                } else if (position == 10) {
                    demo11();
                } else if (position == 11) {
                    demo12();
                }
            }

        });

    }


    private void demo12() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
            }
        });

        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("一");
                e.onNext("二");
                e.onNext("三");
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("①");
                e.onNext("②");
                e.onNext("③");
                e.onNext("④");
                e.onNext("⑤");
                e.onNext("⑥");
            }
        });


        Observable<? extends Serializable> mergeObservable = Observable.merge(observable, observable1, observable2);

        mergeObservable.subscribe(new Observer<Serializable>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Serializable serializable) {
                System.out.println("serializable====================" + serializable);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /*============================================ concat ==================================================*/

    private void demo11() {
        Observable<Integer> just1 = Observable.just(1, 2, 3);
        Observable<Integer> just2 = Observable.just(4, 5, 6);
        Observable.concat(just1, just2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        //按顺序 123  -->  456
                        //可用于先取缓存再取网络数据
                        Log.e(TAG, "concat : " + integer + "\n");
                    }
                });
    }

    /*============================================ interval ==================================================*/
    //间隔一段时间就发送一个数据
    private void demo7() {
        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)//心跳，间隔执行
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println("interval============" + aLong);
                    }
                });

//        需要在界面ondestroy时取消
//        if (disposable != null) {
//            disposable.dispose();
//        }
    }

    /*============================================ timer ==================================================*/
    //在订阅之后，它会在等待一段时间之后发射一个0数据项，然后结束，因此它常常可以用来延时地发送时间
    private void demo8() {
        Observable.timer(10, TimeUnit.SECONDS)//延时执行
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        System.out.println("timer============" + aLong);
                    }
                });
    }

    /*============================================ delay ==================================================*/
    //用来延迟上游发射过来的数据
    private void demo9() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 100; i++) {
                    emitter.onNext(i);
                }
            }
        });

        observable.delay(10, TimeUnit.SECONDS)//延时执行
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("delay=========" + integer);
                    }
                });
    }


    // doOnSubscribe:
    // doOnLifecycle: false
    // doOnNext: 1
    // doOnEach: onNext
    // accept 收到消息: 1
    // doAfterNext: 1
    // doOnNext: 2
    // doOnEach: onNext
    // accept 收到消息: 2
    // doAfterNext: 2
    // doOnComplete:
    // doOnEach: onComplete
    // doFinally:
    // doAfterTerminate:
    private void demo10() {
        Observable.just("1", "2")
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "doOnNext: " + s);

                    }
                })
                .doAfterNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "doAfterNext: " + s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doOnComplete: ");
                    }
                })
                //订阅之后回调的方法
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.e(TAG, "doOnSubscribe: ");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doAfterTerminate: ");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doFinally: ");
                    }
                })
                //Observable每发射一个数据的时候就会触发这个回调，不仅包括onNext还包括onError和onCompleted
                .doOnEach(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(@NonNull Notification<String> stringNotification) throws Exception {
                        Log.e(TAG, "doOnEach: " + (stringNotification.isOnNext() ? "onNext" : stringNotification.isOnComplete() ? "onComplete" : "onError"));

                    }
                })
                //订阅后可以进行取消订阅
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.e(TAG, "doOnLifecycle: " + disposable.isDisposed());
                        //disposable.dispose();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doOnLifecycle run: ");

                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "accept 收到消息: " + s);
                    }
                });
    }

    private void demo6() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer + "");
            }
        };

        observable.take(10)//截取前N项
                .repeat(3)//重复
                .distinct()//去重
                .subscribe(consumer);
    }

    private void demo5() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("sample=====" + integer);
            }
        };

        observable.sample(1, TimeUnit.SECONDS)//获取指定时间最近发射的数据项
                .subscribe(consumer);
    }

    private void demo4() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    e.onNext(i);
                }
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer + "");
            }
        };

        observable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 7 == 0;
                    }
                }).subscribe(consumer);
    }

    /*============================================ zip ==================================================*/
    private void demo3() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
            }
        });

        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("一");
                e.onNext("二");
                e.onNext("三");
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("①");
                e.onNext("②");
                e.onNext("③");
                e.onNext("④");
                e.onNext("⑤");
                e.onNext("⑥");
            }
        });


        //RxJava中的zip操作符作用是将多条上游发送的事件进行结合到一起,发送到下游,并且按照顺序来进行结合,
        //如多条上游中发送的事件数量不一致,则以最少的那条中的事件为准,下游接收到的事件数量和其相等.
        Observable.zip(observable, observable1, observable2, new Function3<Integer, String, String, String>() {
            @Override
            public String apply(Integer integer, String s, String s2) throws Exception {
                return s + integer + s2;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("s==================" + s);
            }
        });
    }

    /*==============================================================================================*/

    private void demo1() {
        //创建一个上游 被观察者 Observable：
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emitter.onNext 1");
                emitter.onNext(1);
                Log.d(TAG, "emitter.onNext 2");
                emitter.onNext(2);
                Log.d(TAG, "emitter.onNext 3");
                emitter.onNext(3);
                Log.d(TAG, "emitter.onComplete");
                emitter.onComplete();//调用此句：接收者无法接收事件，但发送事件还会继续。
                Log.d(TAG, "emitter.onNext 4");
                emitter.onNext(4);
            }
        });

        //创建一个下游 观察者 Observer
        Observer<Integer> observer = new Observer<Integer>() {
            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "Observer onNext " + value);
                i++;
                if (i == 2) {
                    Log.d(TAG, "dispose");
                    mDisposable.dispose();
                    //调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.下游收不到事件.
                    Log.d(TAG, "isDisposed : " + mDisposable.isDisposed());
                    //当Disposable的 isDisposed() 返回为 false 的时候，接收器能正常接收事件，
                    //但当其为 true 的时候，接收器停止了接收。所以可以通过此参数动态控制接收事件了。
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        //建立连接关系
        observable.subscribe(observer);
    }

    /*=============================================================================================*/

    private void demo2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
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
        }).subscribe(new Consumer<Integer>() {//必须

            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Consumer accept: " + integer);
            }
        }, new Consumer<Throwable>() {//可选
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {//可选
            @Override
            public void run() throws Exception {

            }
        }, new Consumer<Disposable>() {//可选
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        });
    }

}
