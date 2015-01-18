package com.untappedkegg.messagemanager.adapter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import com.untappedkegg.messagemanager.utils.MessageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by UntappedKegg on 1/17/15.
 */
public class TelephonyCursorAdapter {

    final static String LOG_TAG = TelephonyCursorAdapter.class.getSimpleName();



//    public static final int adapter = MmsSms.CONTENT_CONVERSATIONS_URI;

    public static final String QUEUERY_INBOX = "content://sms/inbox/";
    public static final String QUEUERY_FAILED = "content://sms/failed/";
    public static final String QUEUERY_QUEUED = "content://sms/queued/";
    public static final String QUEUERY_SENT = "content://sms/sent/";
    public static final String QUEUERY_DRAFT = "content://sms/draft/";
    public static final String QUEUERY_OUTBOX = "content://sms/outbox/";
    public static final String QUEUERY_UNDELIVERED = "content://sms/undelivered/";
    public static final String QUEUERY_SMS_ALL = "content://sms/all/";

    /*---- SMS + MMS ----*/

    // Telephony.MmsSms.CONTENT_URI                 = content://mms-sms/
    // Telephony.MmsSms.CONTENT_CONVERSATIONS_URI   = content://mms-sms/conversations
    // Telephony.MmsSms.CONTENT_DRAFT_URI           = content://mms-sms/draft
    // Telephony.MmsSms.CONTENT_LOCKED_URI          = content://mms-sms/locked
    // Telephony.MmsSms.CONTENT_FILTER_BYPHONE_URI  = content://mms-sms/messages/byphone
    // Telephony.MmsSms.SEARCH_URI                  = content://mms-sms/search
    // Telephony.MmsSms.CONTENT_UNDELIVERED_URI     = content://mms-sms/undelivered

    public static class TextMessage {
        /*----- VARIABLES -----*/
        public String name;
        public String phoneNumber;
        public String snippet;
        public String threadId;
        public Uri photoUri;

        public TextMessage(String name, String phoneNumber, String snippet, String threadId, Uri photoUri) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.snippet = snippet;
            this.threadId = threadId;
            this.photoUri = photoUri;
        }
    }

    public static class Conversation {
        public String name;
        public String read;
        public String threadId;
        public String snippet;
        public String date;
        public Drawable photo;

        public Conversation(String name, String read, String threadId, String snippet, String date, Drawable photo)  {
            this.name = name;
            this.read = read;
            this.threadId = threadId;
            this.snippet = snippet;
            this.date = date;
            this.photo = photo;

        }

    }


    public static final Cursor readAllMessages(ContentResolver resolver, String where) {
       return resolver.query(Telephony.MmsSms.SEARCH_URI, null, where, null, "date DESC");
    }

    public static final Cursor readThreadMessages(ContentResolver resolver, String contactId) {
        return resolver.query(Telephony.MmsSms.CONTENT_CONVERSATIONS_URI, null, String.format("%s = %s", Telephony.TextBasedSmsColumns.THREAD_ID, contactId), null, "date DESC");
    }

    public static ArrayList<Conversation> conversationsToArrayList(ContentResolver resolver) {


        final Cursor c = resolver.query(Telephony.MmsSms.CONTENT_CONVERSATIONS_URI, null, null, null, "date DESC");

        if(c.moveToFirst()) {
            final int count = c.getCount();

            final int threadIdCol = c.getColumnIndex(Telephony.TextBasedSmsColumns.THREAD_ID);
            final int snippetCol = c.getColumnIndex(Telephony.TextBasedSmsColumns.BODY);
            final int phoneNumCol = c.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS);
            final int dateCol = c.getColumnIndex(Telephony.TextBasedSmsColumns.DATE);
            final int readCol = c.getColumnIndex(Telephony.TextBasedSmsColumns.READ);
            ArrayList<Conversation> mList = new ArrayList<>();

            for (int j = 0; j < count; j++) {
                c.moveToPosition(j);
                final String phoneNumber = c.getString(phoneNumCol);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(c.getLong(dateCol));
//                Log.e(LOG_TAG, String.format("name = %s, Thread_ID = %s, Snippet = %s, date = %s, photo = %s", MessageUtils.getContactName(phoneNumber) , MessageUtils.fetchContactIdFromPhoneNumber(phoneNumber), c.getString(26), formatter.format(calendar.getTime()), MessageUtils.getDrawableFromNumber(phoneNumber).toString()));
                mList.add(new Conversation(MessageUtils.getContactName(resolver, phoneNumber) ,c.getString(readCol), c.getString(threadIdCol), c.getString(snippetCol), formatter.format(calendar.getTime()), MessageUtils.getDrawableFromNumber(resolver, phoneNumber)));

            }
            c.close();
            return mList;

        } else {
            c.close();
            Log.e("Error", "Cursor is null or empty");
        }
        return null;

    }

}
