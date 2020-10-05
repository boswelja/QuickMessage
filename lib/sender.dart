import 'dart:async';

import 'package:flutter_sms/flutter_sms.dart';
import 'package:quick_message/pref_keys.dart';
import 'package:shared_preferences/shared_preferences.dart';

Future<bool> requestSend() async {
  if (await canSendSMS()) {
    var prefs = await SharedPreferences.getInstance();
    var number = prefs.getString(numberKey);
    var message = prefs.getString(messageKey);
    var result = await sendSMS(message: message, recipients: [number]);
    print(result);
    return true;
  }
  return false;
}
