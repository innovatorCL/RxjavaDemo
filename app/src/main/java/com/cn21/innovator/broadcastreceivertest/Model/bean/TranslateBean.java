package com.cn21.innovator.broadcastreceivertest.Model.bean;

import android.util.Log;

/**
 * 金山词霸的API Bean
 * Created by innovator on 2018/6/12.
 */

public class TranslateBean {

  /**
   * status : 1
   * content : {"from":"en-EU","to":"zh-CN","vendor":"baidu","out":"你好世界<br/>","err_no":0}
   */

  private int status;
  private TranslateContentBean content;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public TranslateContentBean getContent() {
    return content;
  }

  public void setContent(TranslateContentBean content) {
    this.content = content;
  }

  public static class TranslateContentBean {
    /**
     * from : en-EU
     * to : zh-CN
     * vendor : baidu
     * out : 你好世界<br/>
     * err_no : 0
     */

    private String from;
    private String to;
    private String vendor;
    private String out;
    private int err_no;

    public String getFrom() {
      return from;
    }

    public void setFrom(String from) {
      this.from = from;
    }

    public String getTo() {
      return to;
    }

    public void setTo(String to) {
      this.to = to;
    }

    public String getVendor() {
      return vendor;
    }

    public void setVendor(String vendor) {
      this.vendor = vendor;
    }

    public String getOut() {
      return out;
    }

    public void setOut(String out) {
      this.out = out;
    }

    public int getErr_no() {
      return err_no;
    }

    public void setErr_no(int err_no) {
      this.err_no = err_no;
    }
  }

  //定义 输出返回数据 的方法
  public void show() {
    if(content != null && content.out != null){
      Log.d("RxJava", content.out );
    }

  }
}
