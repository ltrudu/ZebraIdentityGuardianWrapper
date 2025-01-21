package com.zebra.zebraidentityguardianwrapper;

public interface IIGAuthenticationResultCallback {
    void onSuccess(String result);
    void onError(String message);
    void onDebugStatus(String message);

}
