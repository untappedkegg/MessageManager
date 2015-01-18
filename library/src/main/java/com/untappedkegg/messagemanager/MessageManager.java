package com.untappedkegg.messagemanager;

import android.util.Log;

/**
 * Created by UntappedKegg on 1/17/15.
 */
public class MessageManager {

    /*----- CONSTANTS -----*/
    static final String LOG_TAG = "MessageManager";
    private static final String LOG_INIT_CONFIG = "Initialize MessageManager with configuration";
    private static final String LOG_DESTROY = "Destroy MessageManager";
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "MessageManager configuration can not be initialized with null";
    private static final String WARNING_RE_INIT_CONFIG = "Try to initialize MessageManager which had already been initialized before. " + "To re-init MessageManager with new configuration call MessageManager.destroy() at first.";
    private static final String ERROR_NOT_INIT = "MessageManager must be init with configuration before using";


    public volatile boolean loggingEnabled = true;

    private volatile static MessageManager instance;
    private MessageManagerConfiguration configuration;

    /** Returns singleton class instance */
    public static MessageManager getInstance() {
        if (instance == null) {
            synchronized (MessageManager.class) {
                if (instance == null) {
                    instance = new MessageManager();
                }
            }
        }
        return instance;
    }

    protected MessageManager() {
    }

    /**
     * Initializes MessageManager instance with configuration.<br />
     * If configurations was set before ( {@link #isInited()} == true) then this method does nothing.<br />
     * To force initialization with new configuration you should {@linkplain #destroy() destroy MessageManager} at first.
     *
     * @param configuration {@linkplain MessageManagerConfiguration configuration}
     * @throws IllegalArgumentException if <b>configuration</b> parameter is null
     */
    public synchronized void init(MessageManagerConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (this.configuration == null) {
            Log.d(LOG_TAG, LOG_INIT_CONFIG);
            this.configuration = configuration;
        } else {
            Log.w(LOG_TAG, WARNING_RE_INIT_CONFIG);
        }
    }

    /**
     * Returns <b>true</b> - if MessageManager {@linkplain #init(MessageManagerConfiguration) is initialized with
     * configuration}; <b>false</b> - otherwise
     */
    public boolean isInited() {
        return configuration != null;
    }

    /**
     * Checks if MessageManager's configuration was initialized
     *
     * @throws IllegalStateException if configuration wasn't initialized
     */
    private void checkConfiguration() {
        if (configuration == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
    }

    /**
     * clears current configuration. <br />
     * You can {@linkplain #init(MessageManagerConfiguration) init} MessageManager with new configuration after calling this
     * method.
     */
    public void destroy() {
        if (configuration != null) Log.d(LOG_TAG, LOG_DESTROY);
//        stop();
        configuration = null;
    }

}
