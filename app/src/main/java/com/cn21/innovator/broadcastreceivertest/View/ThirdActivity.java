package com.cn21.innovator.broadcastreceivertest.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.cn21.innovator.broadcastreceivertest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

public class ThirdActivity extends AppCompatActivity {

  private static final String TAG = "Rxjava";

  @BindView(R.id.name)
  EditText name;

  @BindView(R.id.age)
  EditText age;

  @BindView(R.id.job)
  EditText job;

  @BindView(R.id.list)
  Button list;

  //当文本发生变化的时候，发送事件
  ObservableEmitter nameEmitter;
  Observable nameObservabel = Observable.create(new ObservableOnSubscribe() {
    @Override
    public void subscribe(ObservableEmitter e) throws Exception {
      nameEmitter = e;
    }
  });

  ObservableEmitter ageEmitter;
  Observable ageObservabel = Observable.create(new ObservableOnSubscribe() {
    @Override
    public void subscribe(ObservableEmitter e) throws Exception {
      ageEmitter = e;
    }
  });

  ObservableEmitter jobEmitter;
  Observable jobObservabel = Observable.create(new ObservableOnSubscribe() {
    @Override
    public void subscribe(ObservableEmitter e) throws Exception {
      jobEmitter = e;
    }
  });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_third);
    ButterKnife.bind(this);

    name.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        nameEmitter.onNext(s);
      }
    });

    age.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        ageEmitter.onNext(s);
      }
    });

    job.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        jobEmitter.onNext(s);
      }
    });

    Observable.combineLatest(nameObservabel, ageObservabel, jobObservabel, new Function3<CharSequence, CharSequence, CharSequence,Boolean>() {
      @Override
      public Boolean apply(CharSequence a, CharSequence b, CharSequence c) throws Exception {
        /*
         * 规定表单信息输入不能为空
         **/
        // 1. 姓名信息,除了设置为空，也可设置长度限制
         boolean isUserNameValid = !TextUtils.isEmpty(name.getText()) &&
                 (name.getText().toString().length() > 2 && name.getText().toString().length() < 9);

        // 2. 年龄信息
        boolean isUserAgeValid = !TextUtils.isEmpty(age.getText());
        // 3. 职业信息
        boolean isUserJobValid = !TextUtils.isEmpty(job.getText()) ;

        /*
          * 返回信息 = 联合判断，即3个信息同时已填写，"提交按钮"才可点击
          **/
        return isUserNameValid && isUserAgeValid && isUserJobValid;

      }
    }).subscribe(new Consumer<Boolean>() {
      @Override
      public void accept(Boolean o) throws Exception {
        /*
         * 步骤6：返回结果 & 设置按钮可点击样式
         **/
        Log.e(TAG, "提交按钮是否可点击： "+o);
        list.setEnabled(o);
      }
    });
  }
}
