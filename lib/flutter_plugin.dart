import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:flutter_plugin/event_bus.dart';

class FlutterPlugin {
  static MethodChannel _channel =
      const MethodChannel('flutter_plugin')
  ..setMethodCallHandler(_handler);

  static dynamic _eventBus = new EventBus();

  // 注册
  static Future<dynamic> registerPush(String userAccount) async {
    await _channel.invokeMethod('registerPush', <String, dynamic>{"userAccount": userAccount});
  }

  // 设置标签
  static Future<dynamic> setTags(String tags, String operateName) async {
    await _channel.invokeMethod('setTags', <String, dynamic>{"tags": tags, "operateName": operateName});
  }

  // 删除所有标签
  static Future<dynamic> cleanTags(String operateName) async {
    await _channel.invokeMethod('cleanTags', <String, dynamic>{"operateName": operateName});
  }

  // 播放音频文件
  static Future<dynamic> mediaPlay(String sourceUrl) async {
    await _channel.invokeMethod('mediaPlay', <String, dynamic>{"sourceUrl": sourceUrl});
  }

  // 监听异步事件回调结果
  static Future<dynamic> _handler(MethodCall methodCall) {
    _eventBus.emit("listenOpreateRes", <String, dynamic>{"eventType": methodCall.method, "data": methodCall.arguments});
    return Future.value(true);
  }
}
