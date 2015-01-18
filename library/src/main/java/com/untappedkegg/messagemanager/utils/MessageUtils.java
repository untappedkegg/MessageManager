package com.untappedkegg.messagemanager.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.untappedkegg.messagemanager.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by UntappedKegg on 1/7/15.
 */
public final class MessageUtils {
    private static final String LOG_TAG = MessageUtils.class.getSimpleName();

    /**
     * Tests {@code str} for a null or "".
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


    /**
     * Returns true if the address is an email address
     *
     * @param address the input address to be tested
     * @return true if address is an email address
     */
    public final static boolean isEmailAddress(String address) {
            /*
             * The '@' char isn't a valid char in phone numbers. However, in SMS
             * messages sent by carrier, the originating-address can contain
             * non-dialable alphanumeric chars. For the purpose of thread id
             * grouping, we don't care about those. We only care about the
             * legitmate/dialable phone numbers (which we use the special phone
             * number comparison) and email addresses (which we do straight up
             * string comparison).
             */
        return (!isNullOrEmpty(address)) && (address.indexOf('@') != -1);
    }


    // An alias (or commonly called "nickname") is:
    // Nickname must begin with a letter.
    // Only letters a-z, numbers 0-9, or . are allowed in Nickname field.
//    public final static boolean isAlias(String string) {
//        if (!MmsConfig.isAliasEnabled()) {
//            return false;
//        }
//
//        int len = string == null ? 0 : string.length();
//
//        if (len < MmsConfig.getAliasMinChars() || len > MmsConfig.getAliasMaxChars()) {
//            return false;
//        }
//
//        if (!Character.isLetter(string.charAt(0))) {    // Nickname begins with a letter
//            return false;
//        }
//        for (int i = 1; i < len; i++) {
//            char c = string.charAt(i);
//            if (!(Character.isLetterOrDigit(c) || c == '.')) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    public static void printColumnsToLog(Cursor c, boolean closeCursor) {
        if(c.moveToFirst()) {
            final String[] colNames = c.getColumnNames();
            final int count = colNames.length;
            Log.e("Index", "Column_Name");
            for(int i = 0; i < count; i++) {
                Log.e(String.valueOf(i), String.valueOf(colNames[i]));
            }
        } else {
            Log.e("Error", "Cursor is null or empty");
        }

        if(closeCursor)
            c.close();

    }

    public static void printMessagesToLog(Cursor c, boolean closeCursor) {
        if(c.moveToFirst()) {
            final String[] colNames = c.getColumnNames();
            final int colCount = c.getColumnCount();
            final int count = c.getCount();
            Log.e("Index", "Column_Name");
            for (int j = 0; j < count; j++) {
                Log.e("========== BLEND", "New Message: ==============");
                c.moveToPosition(j);
                for(int i = 0; i < colCount; i++) {
                    Log.e(String.valueOf(i) + " " + String.valueOf(colNames[i]), c.getString(i) + "");
                }

            }


        } else {
            Log.e("Error", "Cursor is null or empty");
        }

        if(closeCursor)
            c.close();

    }


    public InputStream openPhoto(ContentResolver resolver, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = resolver.query(photoUri,
                new String[]{Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static final String fetchContactIdFromPhoneNumber(ContentResolver resolver, String phoneNumber) {

        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cFetch = resolver.query(uri,
                new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                null, null, null);

        String contactId = "";


        if (cFetch.moveToFirst()) {

            cFetch.moveToFirst();

            contactId = cFetch.getString(cFetch
                    .getColumnIndex(PhoneLookup._ID));

        }

//        Log.e(LOG_TAG, "Contact_ID = " + contactId);

        return contactId;

    }

    public static final Uri getPhotoUri(ContentResolver resolver, String contactId) {
        try {
            return getPhotoUri(resolver, Long.parseLong(contactId));
        } catch (Exception e) {
            return null;
        }
    }


    public static final Uri getPhotoUri(ContentResolver resolver, long contactId) {

        try {
            Cursor cursor = resolver
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + contactId
                                    + " AND "

                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(
                Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person,
                Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static Uri getPhotoFromNumber(ContentResolver resolver, String phoneNumber) {
        try {
            final Uri returnUri = getPhotoUri(resolver, Long.valueOf(fetchContactIdFromPhoneNumber(resolver, phoneNumber)));
//            Log.e("Blend", String.valueOf(returnUri));
            return returnUri;
        } catch (Exception e) {
            return null;
        }

    }

    public static Drawable getDrawableFromNumber(ContentResolver resolver, String phoneNumber) {


        try {
            final Uri returnUri = getPhotoUri(resolver, Long.valueOf(fetchContactIdFromPhoneNumber(resolver, phoneNumber)));
            InputStream inputStream = resolver.openInputStream(returnUri);
            return Drawable.createFromStream(inputStream, returnUri.toString() );
        } catch (Exception e) {
//            return AppState.getApplication().getResources().getDrawable(R.drawable.ic_launcher);
            return null;
        }

    }

    /**
     * *
     * @param phoneNum Phone Number for the contact whose name you seek
     * @return The Name if found, otherwise the phone number
     */
    public static final String getContactName(ContentResolver resolver, String phoneNum) {
        if (isNullOrEmpty(phoneNum))
            return "";

        Cursor cursor = null;

        try {
            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNum));

            cursor = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);

            cursor.moveToFirst();
            final String name = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
//            Log.w(LOG_TAG, name);
            return name;
        } catch (Exception e) {
//            Log.e(LOG_TAG, phoneNum);
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return phoneNum;
        } finally {
            if (cursor != null && !cursor.isClosed())
            cursor.close();
        }

    }

    public static final String[] getContactInfo() {
        // define the columns I want the query to return
        String[] projection = new String[]{
                PhoneLookup.DISPLAY_NAME,
                PhoneLookup._ID};

    return null;

    }

    public InputStream openDisplayPhoto(ContentResolver resolver, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd = resolver.openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
//            return AppState.getApplication().getDrawable(R.drawable.ic_launcher);
            return null;
        }
    }


}
