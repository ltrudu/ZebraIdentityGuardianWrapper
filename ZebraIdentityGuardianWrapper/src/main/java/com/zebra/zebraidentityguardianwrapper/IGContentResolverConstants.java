package com.zebra.zebraidentityguardianwrapper;

public class IGContentResolverConstants {

    public final static String BASE_URI = "content://com.zebra.mdna.els.provider/";

    public final static String CURRENT_SESSION_URI = "content://com.zebra.mdna.els.provider/currentsession";
    public final static String PREVIOUS_SESSION_URI = "content://com.zebra.mdna.els.provider/previoussession";
    public final static String START_AUTHENTICATION_URI = "content://com.zebra.mdna.els.provider/lockscreenaction/startauthentication";
    public final static String AUTHENTICATION_STATUS_URI = "content://com.zebra.mdna.els.provider/lockscreenaction/authenticationstatus";
    public final static String LOGOUT_URI = "content://com.zebra.mdna.els.provider/lockscreenaction/logout";
    public final static String STATUS_URI = "content://com.zebra.mdna.els.provider/lockscreenstatus/state";

    public final static String LOCKSCREEN_ACTION = "lockscreenaction";
    public final static String LOCKSCREEN_STATUS_ACTION = "lockscreenstatus";
    public final static String AUTHENTICATION_SCHEME_KEY = "user_verification";
    public final static String AUTHENTICATION_FLAG_KEY = "launchflag";

    public final static String START_AUTHENTICATION_METHOD = "startauthentication";
    public final static String LOGOUT_METHOD = "logout";
    public final static String LOGOUT_RESULT = "RESULT";
    public final static String LOCKSCREEN_STATUS_STATE_METHOD = "state";
}
