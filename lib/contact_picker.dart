import 'dart:async';

import 'package:flutter/material.dart';
import 'package:quick_message/pref_keys.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:contacts_service/contacts_service.dart';
import 'package:permission_handler/permission_handler.dart';

class SelectedContact extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _SelectedContact();
  }
}

class _SelectedContact extends State<SelectedContact> {

  Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  Contact contact;

  void updateContact(String number) async {
    if (number != null && number.isNotEmpty && await Permission.contacts.request().isGranted) {
      print('Updating contact');
      var contacts = await ContactsService.getContactsForPhone(number, withThumbnails: false);
      setState(() {
        contact = contacts.first;
      });
    }
  }

  @override
  void initState() {
    super.initState();
    _prefs.then((prefs) {
      var number = prefs.getString(numberKey);
      updateContact(number);
    });
    print('Contact Picker init completed');
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () => getNewContact(),
      child: Container(
        padding: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
        child: contactPickerWidget(),
      ),
    );
  }

  Widget contactPickerWidget() {
    if (contact != null)
      return buildContactSelectedWidget(contact);
    else
      return buildNoContactWidget();
  }

  Widget buildContactSelectedWidget(Contact contact) {
    return Text(contact.displayName);
  }

  Widget buildNoContactWidget() {
    return Row(
      children: [
        Icon(
            Icons.person_outlined
        ),
        Text("Select a Contact")
      ],
    );
  }

  void getNewContact() async {
    try {
      if (await Permission.contacts.request().isGranted) {
        Contact newContact = await ContactsService.openDeviceContactPicker();
        setState(() {
          _prefs.then((prefs) => prefs.setString(numberKey, newContact.phones.first.value));
          contact = newContact;
        });
      } else {
        print("Failed to get contact");
      }
    } catch(ignored) {
      print("Failed to get contact");
    }
  }
}
