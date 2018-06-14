package com.cn21.innovator.broadcastreceivertest.Model.net;

import com.cn21.innovator.broadcastreceivertest.Model.bean.TranslateBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API
 * Created by innovator on 2018/6/12.
 */

public interface ITranslateRequest {


  // 注解里传入 网络请求 的部分URL地址
  // Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
  // 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
  // 采用Observable<...>接口
  // getTranslation()是接受网络请求数据的方法

  @GET("ajax.php")
  Observable<TranslateBean> getTranslation(@Query("a") String a, @Query("f") String f,
                                           @Query("t") String t, @Query("w") String word);

  // 网络请求1
  @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
  Observable<TranslateBean> getCall();

  // 网络请求2
  @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
  Observable<TranslateBean> getCall_2();


}
