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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lenovo on 2018/10/19.
 */

public class FlowableActivity extends AppCompatActivity {

    private static final String TAG = "TypeSwitchActivity";
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    Subscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<String> list = new ArrayList<>();
        list.add("BackpressureStrategy.BUFFER");
        list.add("BackpressureStrategy.LATEST");
        list.add("BackpressureStrategy.DROP");
        list.add("BackpressureStrategy.ERROR");
        list.add("BackpressureStrategy.MISSING");
        list.add("request");
        MyAdapter myAdapter = new MyAdapter(R.layout.activity_item, list);
        recyclerview.setAdapter(myAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                System.out.println("position==================" + position);
                if (position == 0) {
                    init(BackpressureStrategy.BUFFER);
                } else if (position == 1) {
                    init(BackpressureStrategy.LATEST);
                } else if (position == 2) {
                    init(BackpressureStrategy.DROP);
                } else if (position == 3) {
                    init(BackpressureStrategy.ERROR);
                } else if (position == 4) {
                    init(BackpressureStrategy.MISSING);
                } else if (position == 5) {
                    mSubscription.request(50);
                }
            }

        });
    }

    private void init(BackpressureStrategy mode) {
        Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 500; i++) {//默认缓存池中128个【0,127】，其他先不缓存
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                }
            }
        }, mode);

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                mSubscription = s;
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
