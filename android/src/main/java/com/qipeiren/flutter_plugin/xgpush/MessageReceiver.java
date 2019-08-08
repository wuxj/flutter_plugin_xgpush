package com.qipeiren.flutter_plugin.xgpush;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.qipeiren.flutter_plugin.FlutterPlugin;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;


public class MessageReceiver extends XGPushBaseReceiver {
    private Intent intent = new Intent("com.qipeiren.flutter_plugin.xgpush.activity.UPDATE_LISTVIEW");
    public static final String LogTag = "TPushReceiver";;

    // 展示消息
    public void show(Context context, String text) {
        // Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    // 播放音频
    public static void mMediaPlayer(Context context, String sourceUrl) {
        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(sourceUrl);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp = null;
                }
            });
        } catch (IOException e) {
            Log.d(LogTag,"error:" + e.toString());
            e.printStackTrace();
        }
    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context,  XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        // mMediaPlayer(context, "notification-sound/new-order.mp3");

        XGNotification notific = new XGNotification();
        notific.setMsg_id(notifiShowedRlt.getMsgId());
        notific.setTitle(notifiShowedRlt.getTitle());
        notific.setContent(notifiShowedRlt.getContent());
        // notificationActionType==1为Activity，2为url，3为intent
        notific.setNotificationActionType(notifiShowedRlt
                .getNotificationActionType());
        // Activity,url,intent都可以通过getActivity()获得
        notific.setActivity(notifiShowedRlt.getActivity());
        notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
        NotificationService.getInstance(context).save(notific);
        context.sendBroadcast(intent);
        String text = "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString();

        Log.d(LogTag, text);
        show(context, text);


        JSONObject obj = new JSONObject();
        try {
            obj.put("rlt", notifiShowedRlt.toString());
        }catch (JSONException e){

        }
        FlutterPlugin.CHANEL.invokeMethod("onNotifactionShowedResult", obj.toString());
    }

    // 反注册
    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

        JSONObject obj = new JSONObject();
        try {
            obj.put("code", errorCode);
            obj.put("message", errorCode == XGPushBaseReceiver.SUCCESS ? "反注册成功" : "反注册失败");
        }catch (JSONException e){

        }
        FlutterPlugin.CHANEL.invokeMethod("onUnregisterResult", obj.toString());
    }

    // 设置标签
    @Override
    public void  onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

        JSONObject obj = new JSONObject();
        try {
            obj.put("code", errorCode);
            obj.put("tagName", tagName.toString());
            obj.put("message", errorCode == XGPushBaseReceiver.SUCCESS ? "设置标签成功" : "设置标签失败");
        }catch (JSONException e){

        }
        FlutterPlugin.CHANEL.invokeMethod("onSetTagResult", obj.toString());
    }

    // 删除标签
    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

        JSONObject obj = new JSONObject();
        try {
            obj.put("code", errorCode);
            obj.put("tagName", tagName.toString());
            obj.put("message", errorCode == XGPushBaseReceiver.SUCCESS ? "删除标签成功" : "删除标签失败");
        }catch (JSONException e){

        }
        FlutterPlugin.CHANEL.invokeMethod("onDeleteTagResult", obj.toString());
    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }

        // 获取自定义key-value
        String customContent = message.getCustomContent();
        JSONObject obj = new JSONObject();

        try {
            obj.put("actionType", message.getActionType());
            obj.put("message", message.toString());
        } catch (JSONException e){

        }

        if (customContent != null && customContent.length() != 0) {
            try {
                obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Log.d(LogTag, text);
        show(context, text);

        FlutterPlugin.CHANEL.invokeMethod("onNotifactionClickedResult", obj.toString());
    }

    // 注册
    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
            // 在这里拿token
            String token = message.getToken();
        } else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        show(context, text);

        JSONObject obj = new JSONObject();
        try {
            obj.put("code", errorCode);
            obj.put("token", errorCode == XGPushBaseReceiver.SUCCESS ? message.getToken().toString() : "");
            obj.put("message", errorCode == XGPushBaseReceiver.SUCCESS ? "注册成功" : "注册失败");
        }catch (JSONException e){

        }
        FlutterPlugin.CHANEL.invokeMethod("onRegisterResult", obj.toString());
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // TODO Auto-generated method stub
        String text = "收到消息:" + message.toString();
        // show(context, "您有1条新消息, " + "应用内消息被展示 ， " + text.toString());
        // mMediaPlayer(context, "notification-sound/new-order.mp3");

        // 获取自定义key-value
        String customContent = message.getCustomContent();
        JSONObject obj = new JSONObject();
        try {
            obj.put("message", message.toString());
        } catch (JSONException e){

        }
        if (customContent != null && customContent.length() != 0) {
            try {
                obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理消息的过程...
        Log.d(LogTag, text);
        show(context, text);

        FlutterPlugin.CHANEL.invokeMethod("onTextMessage", obj.toString());
    }
}
