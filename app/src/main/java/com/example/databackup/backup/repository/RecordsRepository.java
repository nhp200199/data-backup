package com.example.databackup.backup.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.databackup.backup.model.BackUpData;
import com.example.databackup.backup.model.CallLogModel;
import com.example.databackup.backup.model.Contact;
import com.example.databackup.backup.model.SmsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class RecordsRepository {
    private static final String TAG = RecordsRepository.class.getSimpleName();
    private static final String ROOT = "data/";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private StorageReference rootStorageRef;
    private BehaviorSubject<List<Long>> recordsSubject = BehaviorSubject.createDefault(new ArrayList<Long>());

    public RecordsRepository() {
        rootStorageRef = storage.getReference(ROOT);
    }

    public void fetchAll(String email) {
        StorageReference listRef = rootStorageRef.child(email);
        List<Long> records = new ArrayList<>();

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            try {
                                String timeStampFromJsonFile = item.getName().substring(0, item.getName().indexOf('.'));
                                records.add(Long.parseLong(timeStampFromJsonFile));
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "Error when parsing timestamps from json file's name");
                                recordsSubject.onError(e);
                            }
                        }
                        recordsSubject.onNext(records);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error when fetching records from Firebase " + e.toString());
                        recordsSubject.onError(e);
                    }
                });
    }

    private Observable<BackUpData> retrieveBackUpData(Context context) {
        return Observable.zip(getContacts(context), getSms(context), getCallLogs(context), (contacts, smsModels, callLogModels) -> {
            BackUpData a = new BackUpData(new Date().getTime(), contacts, smsModels, callLogModels);
//            Log.i(TAG, "JSON representation: " + a.toJson());
            return a;
        });
    }

    public Observable<BackUpData> backUpData(Context context) {
        long backUpDate = new Date().getTime();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
           return retrieveBackUpData(context).flatMap(backUpData -> {
               // Create file metadata including the content type
               StorageMetadata metadata = new StorageMetadata.Builder()
                       .setContentType("application/json")
                       .build();

                backUpData.setBackUpDate(backUpDate);
                String email = currentUser.getEmail();
                StorageReference jsonDataRef = rootStorageRef.child(email + "/" + backUpDate + ".json");
                return Observable.create(emitter -> {
                    UploadTask uploadTask = jsonDataRef.putBytes(backUpData.toJson().getBytes(StandardCharsets.UTF_8), metadata);
                    uploadTask.addOnSuccessListener(task -> {
                        emitter.onNext(backUpData);
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error when back up data to Firebase. " + e.toString());
                        e.printStackTrace();
                        emitter.onError(e);
                    });
                });
            });
        } else {
            Log.i(TAG, "There is no user logged in");
            return Observable.error(Exception::new);
        }
    }

    private Observable<List<Contact>> getContacts(Context context) {
        return Observable.create(emitter -> {
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
                emitter.onNext(contacts);
                emitter.onComplete();
            }
            catch (Exception e) {
                Log.e(TAG, "Error when getting contacts. " + e);
                e.printStackTrace();
                emitter.onError(e);
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }
        });
    }

    private Observable<List<CallLogModel>> getCallLogs(Context context) {
        return Observable.create(emitter -> {
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
                emitter.onNext(callLogs);
                emitter.onComplete();
            }
            catch (Exception e) {
                Log.e(TAG, "Error when getting call logs. " + e);
                e.printStackTrace();
                emitter.onError(e);
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }
        });

    }

    private Observable<List<SmsModel>> getSms(Context context) {
        return Observable.create(emitter -> {
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
                emitter.onNext(smsList);
                emitter.onComplete();
            }
            catch (Exception e) {
                Log.e(TAG, "Error when getting SMS. " + e);
                e.printStackTrace();
                emitter.onError(e);
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }
        });
    }

    public void putRecord(long record) {
        List<Long> currentRecords = recordsSubject.getValue();
        currentRecords.add(0, record);
        recordsSubject.onNext(currentRecords);
    }

    public Observable<List<Long>> getRecordsObservable() {
        return recordsSubject.hide();
    }
}
