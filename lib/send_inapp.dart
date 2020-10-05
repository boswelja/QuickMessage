import 'package:flutter/material.dart';
import 'package:quick_message/sender.dart';

class SendButton extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      child: ElevatedButton(
          onPressed: () => trySendMessage(context),
          child: Text('Send'))
    );
  }

  void trySendMessage(BuildContext context) async {
    var result = await requestSend();
    if (!result)
      showFailedSnackBar(context);
  }

  void showFailedSnackBar(BuildContext context) {
    final snackBar = SnackBar(content: Text('Failed to send message'));
    Scaffold.of(context).showSnackBar(snackBar);
  }
}
