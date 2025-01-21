package com.zebra.zebraidentityguardianwrapper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.zebra.emdkprofilemanagerhelper.CSPAccessMgrHelper;
import com.zebra.emdkprofilemanagerhelper.IResultCallbacks;

import androidx.annotation.Nullable;

public class AuthenticationStatusObserver {
    public interface IStatusChangeCallback
    {
       void onAuthenticationStatusChanged(String status);
       void onError(String error);
       void onDebugStatus(String message);
    }

    private Context mContext;
    private IStatusChangeCallback mStatusChangeCallback;
    private ContentObserver mContentObserver;

    public AuthenticationStatusObserver(Context context, IStatusChangeCallback callback)
    {
        mContext = context;
        mStatusChangeCallback = callback;
        mContentObserver = new ContentObserver(new Handler(mContext.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {

                if(mStatusChangeCallback != null) {
                    mStatusChangeCallback.onAuthenticationStatusChanged(getStatus());
                }
            }
        };

    }

    public void start()
    {
        CSPAccessMgrHelper.executeAccessMgrServiceAccessAction(mContext, IGContentResolverConstants.AUTHENTICATION_STATUS_URI, CSPAccessMgrHelper.EServiceAccessAction.AllowCaller, new IResultCallbacks() {
            @Override
            public void onSuccess(String message, String resultXML) {
                ContentResolver resolver = mContext.getContentResolver();
                resolver.registerContentObserver(Uri.parse(IGContentResolverConstants.AUTHENTICATION_STATUS_URI),
                        false,
                        mContentObserver);
                if(mStatusChangeCallback != null) {
                    mStatusChangeCallback.onAuthenticationStatusChanged(getStatus());
                }
            }

            @Override
            public void onError(String message, String resultXML) {
                if(mStatusChangeCallback != null)
                {
                    mStatusChangeCallback.onError("Error while trying to authorize app for URI:" + IGContentResolverConstants.AUTHENTICATION_STATUS_URI + "\nMessage: " + message + "\nError:" + resultXML);
                }
            }

            @Override
            public void onDebugStatus(String message) {
                if(mStatusChangeCallback != null)
                {
                    mStatusChangeCallback.onDebugStatus(message);
                }
            }
        });
    }

    public void stop()
    {
        ContentResolver resolver = mContext.getContentResolver();
        resolver.unregisterContentObserver(mContentObserver);
    }

    public String getStatus()
    {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(IGContentResolverConstants.AUTHENTICATION_STATUS_URI), null, null, null);
        String response = "";
        if(cursor != null)
        {
            Bundle bundle = cursor.getExtras();
            if (bundle != null && bundle.containsKey("RESULT")) {
                response = bundle.getString("RESULT");
            }
        }
        return response;
    }

}
