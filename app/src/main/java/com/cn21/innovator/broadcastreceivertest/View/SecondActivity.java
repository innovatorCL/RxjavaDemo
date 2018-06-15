package com.cn21.innovator.broadcastreceivertest.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cn21.innovator.broadcastreceivertest.Model.bean.TranslateBean;
import com.cn21.innovator.broadcastreceivertest.Model.net.ITranslateRequest;
import com.cn21.innovator.broadcastreceivertest.R;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondActivity extends AppCompatActivity {

  @BindView(R.id.edit1)
  EditText editText;

  @BindView(R.id.translate_button1)
  Button translateButton;

  @BindView(R.id.show_text1)
  TextView showTip;

  @BindView(R.id.go_button1)
  Button go;

  Observable<TranslateBean> observable;
  Observable<TranslateBean> observable1;
  Observable<TranslateBean> observable2;
  ITranslateRequest request;
  Retrofit retrofit;

  int i = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);
    ButterKnife.bind(this);

    // a. 创建Retrofit对象
    retrofit = new Retrofit.Builder()
            .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
            .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
            .build();

    // b. 创建 网络请求接口 的实例
    request = retrofit.create(ITranslateRequest.class);

    translateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        translateFixedTimes();
        aa();
      }
    });

    go.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(SecondActivity.this,ThirdActivity.class);
        startActivity(i);
      }
    });
  }

  public void useMap(){
    // 采用RxJava基于事件流的链式操作
    Observable.just(1, 2, 3, 4, 5)
            .buffer(3,1)
            .subscribe(new Observer<List<Integer>>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onNext(List<Integer> integers) {
                Log.d("TAG", " 缓存区里的事件数量 = " +  integers.size());
                for (Integer value : integers) {
                  Log.d("TAG", " 事件 = " + value);
                }
              }

              @Override
              public void onError(Throwable e) {
              }

              @Override
              public void onComplete() {
                Log.d("TAG", "对Complete事件作出响应");
              }
            });

  }

  public void translateFixedTimes(){
    // c. 采用Observable<...>形式 对 网络请求 进行封装
    observable = request.getTranslation("fy","auto","auto",editText.getText().toString());

    observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {

      // 在Function函数中，必须对输入的 Observable<Object>进行处理，此处使用flatMap操作符接收上游的数据
      @Override
      public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {

        // 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
        // 以此决定是否重新订阅 & 发送原来的 Observable，即轮询
        // 此处有2种情况：
        // 1. 若返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable，即轮询结束
        // 2. 若返回其余事件，则重新订阅 & 发送原来的 Observable，即继续轮询
        return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
          @Override
          public ObservableSource<?> apply(Object o) throws Exception {
            // 加入判断条件：当轮询次数 = 5次后，就停止轮询
            if (i > 3) {
              // 此处选择发送onError事件以结束轮询，因为可触发下游观察者的onError（）方法回调
              return Observable.error(new Throwable("轮询结束"));
            }
            // 若轮询次数＜4次，则发送1,Next事件以继续轮询
            // 注：此处加入了delay操作符，作用 = 延迟一段时间发送（此处设置 = 2s），以实现轮询间间隔设置
            return Observable.just(1).delay(2000, TimeUnit.MILLISECONDS);
          }
        });
      }
    }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<TranslateBean>(){

              @Override
              public void onSubscribe(Disposable d) {
                Log.d("TAG", "网络请求开始采用subscribe连接");
              }

              @Override
              public void onNext(TranslateBean translateBean) {
                // e.接收服务器返回的数据
                Log.d("TAG", "收到服务器返回的数据: "+translateBean.getContent().getOut());
                translateBean.show() ;
                showTip.setText(translateBean.getContent().getOut());
                i++;
              }

              @Override
              public void onError(Throwable e) {
                Log.d("TAG", "网络访问出错："+ e.getMessage());
                i = 0;
              }

              @Override
              public void onComplete() {
                Log.d("TAG", "网络访问完成");
              }
            });


  }

  TranslateBean bean;

  public void aa(){


    // 步骤3：采用Observable<...>形式 对 2个网络请求 进行封装
    observable1 = request.getCall();
    observable2 = request.getCall_2();


    observable1.subscribeOn(Schedulers.io())               // （初始被观察者）切换到IO线程进行网络请求1
            .observeOn(AndroidSchedulers.mainThread())  // （新观察者）切换到主线程 处理网络请求1的结果
            .doOnNext(new Consumer<TranslateBean>() {
              @Override
              public void accept(TranslateBean result) throws Exception {
                Log.d("Rxjava", "observable1已经被激活，doOnNext() 网络请求成功: "+result.getContent().getOut());
                bean = result;
                result.show();
                // 对第1次网络请求返回的结果进行操作 = 显示翻译结果
              }
            })
            //（新被观察者，同时也是新观察者）切换到IO线程去发起登录请求
            .observeOn(Schedulers.io())
            // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，
            // 所以通过observeOn切换线程
            // 但对于初始观察者，它则是新的被观察者

            //flatMap() 原理：1. 使用传入的事件对象创建一个 Observable 对象；
            // 2. 并不发送这个 Observable, 而是将它激活，于是它开始发送事件
            .flatMap(new Function<TranslateBean, ObservableSource<TranslateBean>>() { // 作变换，即作嵌套网络请求
              @Override
              public ObservableSource<TranslateBean> apply(TranslateBean result) throws Exception {
                // 将网络请求1转换成网络请求2，即发送网络请求2
                Log.d("Rxjava", "flatMap() 转换被观察者11111："+result.getContent().getOut());
                Log.d("Rxjava", "observable2 将被激活发送事件");
                return observable2;
              }
            })
            .observeOn(Schedulers.io())
            .flatMap(new Function<TranslateBean, ObservableSource<TranslateBean>>() {
              @Override
              public ObservableSource<TranslateBean> apply(TranslateBean translateBean) throws Exception {
                Log.d("Rxjava", "flatMap() 转换被观察者222222："+translateBean.getContent().getOut());
                observable = request.getTranslation("fy","auto","auto",editText.getText().toString());
                Log.d("Rxjava", "observable 将被激活发送事件");
                return observable;
              }
            })

            .observeOn(AndroidSchedulers.mainThread())  // （初始观察者）切换到主线程 处理网络请求2的结果
    .subscribe(new Observer<TranslateBean>() {
      @Override
      public void onSubscribe(Disposable d) {
        if(bean != null){
          Log.d("Rxjava", "onSubscribe: "+bean.getContent().getOut());
        }else {
          Log.d("Rxjava", "onSubscribe");
        }
      }

      @Override
      public void onNext(TranslateBean translateBean) {
        Log.d("Rxjava", "回调给初始的观察者: "+translateBean.getContent().getOut());
      }

      @Override
      public void onError(Throwable e) {
        Log.d("Rxjava", "登录失败");
      }

      @Override
      public void onComplete() {
      }
    });
  }
}
