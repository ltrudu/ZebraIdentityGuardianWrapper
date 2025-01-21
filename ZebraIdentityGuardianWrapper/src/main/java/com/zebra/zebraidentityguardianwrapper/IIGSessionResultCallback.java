package com.zebra.zebraidentityguardianwrapper;

import android.database.Cursor;

public interface IIGSessionResultCallback {
    void onSuccess(Cursor result);
    void onError(String message);
    void onDebugStatus(String message);

}
