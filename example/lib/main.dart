import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_plugin/flutter_plugin.dart';
import 'package:flutter_plugin/event_bus.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _registerPush = 'Unknow';
  String _setTags = "unKnow";
  String _cleanTags = "unKnow";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String registerPush;
    String setTags;
    String cleanTags;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      var _eventBus = new EventBus();

      _eventBus.on("listenOpreateRes", (param){
        if(param["eventType"] == "onRegisterResult"){
          print("onRegisterResult=-----1:" + param["data"].toString());
          registerPush = param["data"].toString();

          setState(() {_registerPush = registerPush;});

        }else if(param["eventType"] == "onUnregisterResult") {
          print("onUnregisterResult=-----2:" + param["data"].toString());

        }else if(param["eventType"] == "onSetTagResult") {
          print("onSetTagResult=-----3:" + param["data"].toString());
          setTags = param["data"].toString();
          setState(() {_setTags = setTags;});

        }else if(param["eventType"] == "onDeleteTagResult") {
          print("onDeleteTagResult=-----4:" + param["data"].toString());
          cleanTags = param["data"].toString();
          setState(() {_cleanTags = cleanTags;});

        }else if(param["eventType"] == "onNotifactionShowedResult") {
          print("onNotifactionShowedResult=-----5:" + param["data"].toString());

        }else if(param["eventType"] == "onNotifactionClickedResult") {
          print("onNotifactionClickedResult=-----6:" + param["data"].toString());

        }else if(param["eventType"] == "onTextMessage") {
          print("onTextMessage=-----7:" + param["data"].toString());
        }
      });

      FlutterPlugin.registerPush("userAccount");
      FlutterPlugin.cleanTags("setTags");
      FlutterPlugin.setTags("wuxj", "setTags");

    } on PlatformException {
      registerPush ='Failed to get registerPush test.';
      cleanTags ='Failed to get registerPush test.';
      setTags ='Failed to get registerPush test.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _registerPush = registerPush;
      _cleanTags = cleanTags;
      _setTags = setTags;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              Text('Running on register push: $_registerPush\n'),
              Text('Running on register push: $_cleanTags\n'),
              Text('Running on register push: $_setTags\n'),
              RaisedButton(
                child: Text('参数传递', style: new TextStyle(
                  fontSize: 28.0,
                ),),
                onPressed: () async {
                  },
              )
            ],
          ),
        ),
      ),
    );
  }
}
