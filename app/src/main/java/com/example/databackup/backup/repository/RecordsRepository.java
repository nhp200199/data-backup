package com.example.databackup.backup.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import com.example.databackup.R;
import com.example.databackup.auth.repository.AuthRepository;
import com.example.databackup.backup.model.CallLogModel;
import com.example.databackup.backup.model.Contact;
import com.example.databackup.backup.model.SmsModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RecordsRepository {
    private static final String TAG = RecordsRepository.class.getSimpleName();
    private static RecordsRepository instance;

    private RecordsRepository(Context context) {}

    public static RecordsRepository getInstance(Context context)
    {
        if (instance == null) {
            instance = new RecordsRepository(context);
        }
        return instance;
    }

    public void backUpData(Context context) {
        getContacts(context);
        getCallLogs(context);
        getSms(context);
    }

    private void getContacts(Context context) {
        Cursor cursor = null;
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            String name, number;
            while (cursor.moveToNext()) {
                name = cursor.getString(nameIndex);
                number = cursor.getString(numberIndex);
                number = number.replace(" ", "");
                contacts.add(new Contact(name, number));
            }
            Log.i(TAG, "contact's size: " + contacts.size());
            if (contacts.size() > 0) {
                Log.i(TAG, "First contact information: " + contacts.get(0).toString());
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Error when getting contacts. " + e);
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void getCallLogs(Context context) {
        Cursor cursor = null;
        List<CallLogModel> callLogs = new ArrayList<CallLogModel>();
        try {
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            final int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            final int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            final int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            final int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

            long date, duration;
            String type, number;
            while (cursor.moveToNext()) {
                type = cursor.getString(typeIndex);
                number = cursor.getString(numberIndex);
                date = cursor.getLong(dateIndex);
                duration = cursor.getLong(durationIndex);

                number = number.replace(" ", "");
                callLogs.add(new CallLogModel(date, duration, type, number));
            }
            Log.i(TAG, "call log's size: " + callLogs.size());
            if (callLogs.size() > 0) {
                Log.i(TAG, "First call log's information: " + callLogs.get(0).toString());
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Error when getting call logs. " + e);
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void getSms(Context context) {
        Cursor cursor = null;
        List<SmsModel> smsList = new ArrayList<SmsModel>();
        try {
            cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
            final int typeIndex = cursor.getColumnIndex(Telephony.Sms.TYPE);
            final int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
            final int dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE);

            long date;
            int type;
            String body;
            while (cursor.moveToNext()) {
                type = cursor.getInt(typeIndex);
                body = cursor.getString(bodyIndex);
                date = cursor.getLong(dateIndex);

                smsList.add(new SmsModel(body, date, type));
            }
            Log.i(TAG, "SMS list's size: " + smsList.size());
            if (smsList.size() > 0) {
                Log.i(TAG, "First sms's information: " + smsList.get(0).toString());
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Error when getting SMS. " + e);
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }
}
