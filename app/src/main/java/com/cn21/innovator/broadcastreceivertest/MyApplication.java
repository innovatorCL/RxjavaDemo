package com.cn21.innovator.broadcastreceivertest;

import android.app.Application;
import android.util.Log;

import com.cn21.innovator.broadcastreceivertest.Util.RomCheckUtil;
import com.llew.huawei.verifier.LoadedApkHuaWei;

/**
 * Created by innovator on 2018/3/22.
 */

public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    if(RomCheckUtil.isEmui()){
      Log.e("Rom","华为系统");
      LoadedApkHuaWei.hookHuaWeiVerifier(this);
    }else{
      Log.e("Rom","不是华为系统");
    }


  }
}
