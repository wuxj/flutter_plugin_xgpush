import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_plugin/flutter_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_plugin');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('registerPush', () async {
    expect(FlutterPlugin.registerPush, {"code": 200, "data":"3233-4456-5033-234a-0e4c", "message":"success!!!"});
  });
}
