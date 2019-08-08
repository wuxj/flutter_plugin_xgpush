package com.qipeiren.flutter_plugin;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.qipeiren.flutter_plugin.xgpush.MessageReceiver;
import com.qipeiren.flutter_plugin.xgpush.NotificationService;
import com.qipeiren.flutter_plugin.xgpush.XGNotification;
import com.tencent.android.tpush.XGCustomPushNotificationBuilder;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPush4Msdk;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.tencent.android.tpush.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** FlutterPlugin */
public class FlutterPlugin implements MethodCallHandler {
    static String plugin_name = "flutter_plugin";
    static String plugin_event_name = "flutter_plugin_event";

    public static MethodChannel CHANEL;

    private FlutterPlugin(Registrar registrar, MethodChannel channel){
        // 初始化事件
        EventChannel eventChannel = new EventChannel(registrar.messenger(), plugin_event_name);
    }

    /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), plugin_name);
    channel.setMethodCallHandler(new FlutterPlugin(registrar, channel));
    CHANEL = channel;
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if(call.method.equals("registerPush")){
        String userAccount = call.argument("userAccount");

        XGPushManager.registerPush(XGPushManager.getContext(), userAccount, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {

                // 返回JSON
                JSONObject res = new JSONObject();
                try {
                    res.put("code", flag);
                    res.put("data", data.toString());
                    res.put("message", "注册成功");
                }catch (JSONException e){

                }
                result.success(res.toString());
                Log.d("wuxj:registerPush", res.toString());
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {

                JSONObject res = new JSONObject();
                try {
                    res.put("code", errCode);
                    res.put("data", data.toString());
                    res.put("message", "注册失败:" + msg);
                }catch (JSONException e){

                }
                result.success(res.toString());

                Log.d("wuxj:registerPush", res.toString());
            }
        });
    }
    else if(call.method.equals("unregisterPush")){
        XGPushManager.unregisterPush(XGPushManager.getContext(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                // 返回JSON
                JSONObject res = new JSONObject();
                try {
                    res.put("code", i);
                    res.put("data", o.toString());
                    res.put("message", "注册成功");
                }catch (JSONException e){

                }
                result.success(res.toString());
            }

            @Override
            public void onFail(Object o, int i, String s) {
                JSONObject res = new JSONObject();
                try {
                    res.put("code", i);
                    res.put("data", o.toString());
                    res.put("message", "注册失败:" + s);
                }catch (JSONException e){

                }
                result.success(res.toString());
            }
        });
    }
    else if(call.method.equals("setTags")){
        String tags = call.argument("tags");
        String operateName = call.argument("operateName");
        String[] tagArray = tags.split(",");
        Set<String> tagsSet = new HashSet<>(Arrays.asList(tagArray));
        XGPushManager.setTags(XGPushManager.getContext(), operateName, tagsSet);
    }

    else if(call.method.equals("cleanTags")){
        String operateName = call.argument("operateName");
        XGPushManager.cleanTags(XGPushManager.getContext(), operateName);
    }
    else if(call.method.equals("mediaPlay")){
        String sourceUrl = call.argument("sourceUrl");
        MessageReceiver.mMediaPlayer(XGPushManager.getContext(), sourceUrl);
    }
    else {
      result.notImplemented();
    }
  }
}


