import 'package:flutter/material.dart';
import 'package:quick_message/message_builder.dart';
import 'package:quick_message/send_inapp.dart';
import 'package:quick_message/sender.dart';

import 'contact_picker.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Quick Message',
      theme: ThemeData(
        primarySwatch: Colors.pink,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: SetupPage(),
    );
  }
}

class SetupPage extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Quick Message"),
      ),
      body: ListView(
        padding: EdgeInsets.symmetric(vertical: 16),
        children: [
          SelectedContact(),
          MessageBuilder(),
          SendButton()
        ],
      ),
    );
  }
}
