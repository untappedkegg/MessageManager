package com.untappedkegg.messagemanager;

import android.content.ContentResolver;
import android.content.Context;

/**
 * Created by UntappedKegg on 1/17/15.
 */
public class MessageManagerConfiguration {
    final Context context;
    final ContentResolver resolver;
    final boolean enableLogging;
    final boolean returnCursors;
    final boolean multiThread;

    private MessageManagerConfiguration(final Builder builder) {
        context = builder.context;
        resolver = builder.context.getContentResolver();

        enableLogging = builder.enableLogging;
        returnCursors = builder.returnCursors;
        multiThread = builder.multiThread;

    }

    public static MessageManagerConfiguration createDefault(Context context) {
        return new Builder(context).build();

    }

    public static MessageManagerConfiguration creadeDefaultDebug(Context context) {
        return new Builder(context).enableLogging(true).build();

    }


    public static class Builder {

        private Context context;
        private boolean returnCursors = false;
        private boolean enableLogging = false;
        private boolean multiThread = true;


        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder returnCursors(boolean returnCursors) {
            this.returnCursors = returnCursors;
            return this;

        }

        public Builder enableLogging(boolean enableLogging) {
            this.enableLogging = enableLogging;
            return this;
        }

        public Builder multiThread(boolean multiThread) {
            this.multiThread = multiThread;
            return this;
        }

        public MessageManagerConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new MessageManagerConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {


        }

    }
}
