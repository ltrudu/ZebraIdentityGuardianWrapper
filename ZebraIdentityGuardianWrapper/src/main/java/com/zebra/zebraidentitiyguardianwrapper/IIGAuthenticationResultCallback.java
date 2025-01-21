package com.zebra.zebraidentitiyguardianwrapper;

import android.database.Cursor;

public interface IIGAuthenticationResultCallback {
    void onSuccess(String result);
    void onError(String message);
    void onDebugStatus(String message);

}
