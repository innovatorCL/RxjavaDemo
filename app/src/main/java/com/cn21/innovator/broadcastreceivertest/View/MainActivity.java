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

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "Rxjava";

  @BindView(R.id.edit)
  EditText editText;

  @BindView(R.id.translate_button)
  Button translateButton;

  @BindView(R.id.show_text)
  TextView showTip;

  @BindView(R.id.go_button)
  Button go;

  Observable<TranslateBean> observable;
  ITranslateRequest request;
  Retrofit retrofit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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
        translateInterval();
      }
    });

    go.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(MainActivity.this,SecondActivity.class);
        startActivity(i);
      }
    });
  }

  public void translateInterval(){

    //先调用 onSubscribe()，延迟 2s 后，调用doOnNext()，然后发送事件到onNext()， 2s 一个周期轮询一次
    Observable.intervalRange(2, 3,2,10,TimeUnit.SECONDS)
            /*
             * 步骤2：每次发送数字前发送1次网络请求（doOnNext（）在执行Next事件前调用）
             * 即每隔1秒产生1个数字前，就发送1次网络请求，从而实现轮询需求
             **/
            .doOnNext(new Consumer<Long>() {
              @Override
              public void accept(Long aLong) throws Exception {
                Log.d("TAG", "轮询任务的doOnNext");
                Log.d("TAG", "doOnNext，第 " + aLong + " 次轮询" );

                // c. 采用Observable<...>形式 对 网络请求 进行封装
                observable = request.getTranslation("fy","auto","auto",editText.getText().toString());

                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TranslateBean>(){

                          @Override
                          public void onSubscribe(Disposable d) {
                            Log.d("TAG", "网络请求开始采用subscribe连接");
                          }

                          @Override
                          public void onNext(TranslateBean translateBean) {
                            // e.接收服务器返回的数据
                            Log.d("TAG", "收到服务器返回的数据");
                            translateBean.show() ;
                            showTip.setText(translateBean.getContent().getOut());
                          }

                          @Override
                          public void onError(Throwable e) {
                            Log.d("TAG", "网络访问出错："+ e.getMessage());
                          }

                          @Override
                          public void onComplete() {
                            Log.d("TAG", "网络访问完成");
                          }
                        });

              }
            })
            .subscribe(new Observer<Long>() {
              @Override
              public void onSubscribe(Disposable d) {
                Log.d("TAG", "轮询任务开始采用subscribe连接");
              }

              @Override
              public void onNext(Long aLong) {
                Log.d("TAG", "轮询任务接收到了事件，事件value："+ aLong);
              }

              @Override
              public void onError(Throwable e) {
                Log.d("TAG", "轮询任务接对Error事件作出响应");
              }

              @Override
              public void onComplete() {
                Log.d("TAG", "轮询任务接对Complete事件作出响应");
              }
            });
  }

  public void useIntervalRange(){

    //先调用 onSubscribe()，延迟 5s 后，发送事件，之后 2s 为一个周期发送一个事件（88开始递增的数字,只发送10个）
    Observable.intervalRange(88,10,5, 2,TimeUnit.SECONDS)
            .subscribe(new Observer<Long>() {
              @Override
              public void onSubscribe(Disposable d) {
                Log.d("TAG", "开始采用subscribe连接");
              }

              @Override
              public void onNext(Long aLong) {
                Log.d("TAG", "接收到了事件，事件value："+ aLong);
              }

              @Override
              public void onError(Throwable e) {
                Log.d("TAG", "对Error事件作出响应");
              }

              @Override
              public void onComplete() {
                Log.d("TAG", "对Complete事件作出响应");
              }
            });
  }
}
