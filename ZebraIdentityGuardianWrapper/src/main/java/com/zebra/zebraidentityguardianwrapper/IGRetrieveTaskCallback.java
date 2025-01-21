package com.zebra.zebraidentityguardianwrapper;

import android.database.Cursor;

public interface IGRetrieveTaskCallback {
    void onSuccess(Cursor cursor);
    void onError(String message);
    void onDebugStatus(String message);
}
