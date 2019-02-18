
form的enctype属性为编码方式，

常用有两种：application/x-www-form-urlencoded 和 multipart/form-data，

默认为application/x-www-form-urlencoded。

参考：[关于Content-Type中application/x-www-form-urlencoded 和 multipart/form-data的区别及用法](https://www.cnblogs.com/kaibin/p/6635134.html)



# 重要的关系
### 创建被观察者Observable、Flowable



> 创建数据

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {

    public static <T> Observable<T> just(T item) {


> 转换type

    public final <R> Observable<R> map(Function<? super T, ? extends R> mapper) {

    public final <R> Observable<R> flatMap(Function<? super T,
					? extends ObservableSource<? extends R>> mapper) {

    public final <R> Observable<R> concatMap(Function<? super T,
					? extends ObservableSource<? extends R>> mapper) {

    public final <R> Observable<R> switchMap(Function<? super T,
					? extends ObservableSource<? extends R>> mapper) {


> 线程切换

    public final Observable<T> subscribeOn(Scheduler scheduler) {

    public final Observable<T> observeOn(Scheduler scheduler) {

示例：

	//subscribeOn() 指定的是上游发送事件的线程,
	//observeOn() 指定的是下游接收事件的线程.
	//多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
	//多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
	.subscribeOn(Schedulers.newThread())//第一次有效
	.subscribeOn(Schedulers.io())
	.observeOn(AndroidSchedulers.mainThread())//每次都有效

### 创建观察者Consumer、Observer

Consumer源码：

	public interface Consumer<T> {
	    void accept(T t) throws Exception;
	}

Observer源码：

	public interface Observer<T> {
	    void onSubscribe(@NonNull Disposable d);
	    void onNext(@NonNull T t);
	    void onError(@NonNull Throwable e);
	    void onComplete();
	}

### 建立关系

Observable类中subscribe方法

	public final Disposable subscribe() {}
	public final Disposable subscribe(Consumer<? super T> onNext) {}
	public final Disposable subscribe(Consumer<? super T> onNext,
									  Consumer<? super Throwable> onError) {}
	public final Disposable subscribe(Consumer<? super T> onNext,
									  Consumer<? super Throwable> onError,
									  Action onComplete) {}
	public final Disposable subscribe(Consumer<? super T> onNext,
									  Consumer<? super Throwable> onError,
									  Action onComplete,
									  Consumer<? super Disposable> onSubscribe) {}
	public final void subscribe(Observer<? super T> observer) {}



# 线程调度

### observeOn()

只对其之后的操作起作用；observeOn()可以使用多次，每次使用对其之后的operator起作用，对之前的操作没有影响。

### subscribeOn()

多次调用 subscribeOn() 只有第一次的有效, 其余的会被忽略.



# RxJava2 中的背压




> 在RxJava2里,引入了 Flowable 这个类来处理backpressure,而 Observable 不包含backpressure处理。
只有在需要处理背压问题时，才需要使用Flowable。

> 由于只有在上下游运行在不同的线程中，且上游发射数据的速度大于下游接收处理数据的速度时，才会产生背压问题；
所以，如果能够确定：
1、上下游运行在同一个线程中，
2、上下游工作在不同的线程中，但是下游处理数据的速度不慢于上游发射数据的速度，
3、上下游工作在不同的线程中，但是数据流中只有一条数据
则不会产生背压问题，就没有必要使用Flowable，以免影响性能。

> 处理Backpressure的策略仅仅是处理Subscriber接收事件的方式，并不影响Flowable发送事件的方法。
即使采用了处理Backpressure的策略，Flowable原来以什么样的速度产生事件，现在还是什么样的速度不会变化，
主要处理的是Subscriber接收事件的方式。




```
public enum BackpressureStrategy {

    //未设置策略
    MISSING,

    //如果放入Flowable的异步缓存池中的数据超限了（默认128），则会抛出MissingBackpressureException异常。
    ERROR,

    //无限缓冲池，可能oom
    BUFFER,

    //缓冲池中取，超过设定值则直接丢弃数据不缓存数据
    DROP,

    //缓冲池中取，超过设定值则缓存最新的一条数据，其余抛弃
    LATEST
}
```

在异步调用时，RxJava中有个缓存池，用来缓存消费者处理不了暂时缓存下来的数据，缓存池的默认大小为128，即只能缓存128个事件。无论request()中传入的数字比128大或小，缓存池中在刚开始都会存入128个事件。当然如果本身并没有这么多事件需要发送，则不会存128个事件。
在ERROR策略下，如果缓存池溢出，就会立刻抛出MissingBackpressureException异常。
在BUFFER策略下，相当于把缓存池无限放大，所以有OOM的风险。

### Flowable 与 Subscription：
```
public interface Subscription {
    public void request(long n);//请求数据
    public void cancel();//取消请求
}
```

### Observable的subscribe方法 与 Disposable类：

	public final Disposable subscribe() {}
	public final Disposable subscribe(Consumer<? super T> onNext) {}
	public final Disposable subscribe(Consumer<? super T> onNext,
									  Consumer<? super Throwable> onError) {}
	public final Disposable subscribe(Consumer<? super T> onNext,
									  Consumer<? super Throwable> onError,
									  Action onComplete) {}
	public final Disposable subscribe(Consumer<? super T> onNext,
									  Consumer<? super Throwable> onError,
									  Action onComplete,
									  Consumer<? super Disposable> onSubscribe) {}
	public final void subscribe(Observer<? super T> observer) {}

	//Disposable类
	public interface Disposable {
	    void dispose();//切断水流
	    boolean isDisposed();//是否切断水流
	}





### Function、BiFunction、Function3....Function9的区别：
	public interface Function<T, R> {
	    R apply(@NonNull T t) throws Exception;
	}

	public interface BiFunction<T1, T2, R> {
	    R apply(@NonNull T1 t1, @NonNull T2 t2) throws Exception;
	}

	public interface Function3<T1, T2, T3, R> {
	    R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3) throws Exception;
	}

	//...

	public interface Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
	    R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3,
				@NonNull T4 t4, @NonNull T5 t5, @NonNull T6 t6,
				@NonNull T7 t7, @NonNull T8 t8, @NonNull T9 t9) throws Exception;
	}

分别为传参123...9，有返回值的函数。


