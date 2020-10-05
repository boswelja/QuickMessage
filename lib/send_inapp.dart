import 'package:flutter/material.dart';
import 'package:quick_message/sender.dart';

class SendButton extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      child: ElevatedButton(
          onPressed: () => {
            requestSend()
          },
          child: Text('Send'))
    );
  }
}
