import 'package:flutter/material.dart';
import 'package:quick_message/pref_keys.dart';
import 'package:shared_preferences/shared_preferences.dart';

class MessageBuilder extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _MessageBuilder();
  }
}

class _MessageBuilder extends State<MessageBuilder> {
  Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  TextEditingController _controller = TextEditingController();
  
  @override
  void initState() {
    super.initState();
    _prefs.then((prefs) =>
      _controller.text = prefs.getString(messageKey)
    );
  }

  @override
  void dispose() {
    super.dispose();
    _controller.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      child: TextField(
        controller: _controller,
        decoration: InputDecoration(
            border: OutlineInputBorder(),
            labelText: 'Message'
        ),
        onSubmitted: (value) {
          _prefs.then((prefs) => prefs.setString(messageKey, value));
        },
        textInputAction: TextInputAction.done,
        autocorrect: true,
      )
    );
  }
}