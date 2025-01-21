package com.zebra.zebraidentityguardianwrapper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zebra.emdkprofilemanagerhelper.CSPAccessMgrHelper;
import com.zebra.emdkprofilemanagerhelper.IResultCallbacks;

public class RetrieveIGCRTask extends ExecutorTask<Object, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        Uri uri = (Uri) objects[1];
        IGRetrieveTaskCallback idiResultCallbacks = (IGRetrieveTaskCallback) objects[2];
        RetrieveIGCRInfo(context, uri, idiResultCallbacks);
        return true;
    }

    private void RetrieveIGCRInfo(Context context, Uri uri, IGRetrieveTaskCallback callbackInterface) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() < 1) {
            if (callbackInterface != null) {
                callbackInterface.onDebugStatus("App not registered to call Service:" + uri.toString() + "\nRegistering current application using profile manger, this may take a couple of seconds...");
            }
            // Let's register the application
            CSPAccessMgrHelper.executeAccessMgrServiceAccessAction(context, uri.toString(), CSPAccessMgrHelper.EServiceAccessAction.AllowCaller, new IResultCallbacks() {
                @Override
                public void onSuccess(String message, String resultXML) {
                    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                    if (cursor == null || cursor.getCount() < 1) {
                        if(callbackInterface != null)
                        {
                            callbackInterface.onError("App has been registered but content resolver's cursor is null or empty");
                            return;
                        }
                    }
                    else
                    {
                        if(callbackInterface != null)
                        {
                            callbackInterface.onSuccess(cursor);
                        }
                    }
                }

                @Override
                public void onError(String message, String resultXML) {
                    if(callbackInterface != null)
                    {
                        callbackInterface.onError("CSPAccessMgrHelper error: " + message + "\n" + "ResultXML=" + resultXML);
                        return;
                    }
                }

                @Override
                public void onDebugStatus(String message) {
                    if(callbackInterface != null)
                    {
                        callbackInterface.onDebugStatus(message);
                    }
                }
            });
        }
        else
        {
            if(callbackInterface != null)
            {
                callbackInterface.onDebugStatus("App already registered for URI: " + uri);
                callbackInterface.onSuccess(cursor);
            }
        }
    }
}
