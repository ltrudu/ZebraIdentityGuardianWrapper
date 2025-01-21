package com.zebra.zebraidentitiyguardianwrapper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;

import com.zebra.emdkprofilemanagerhelper.CSPAccessMgrHelper;
import com.zebra.emdkprofilemanagerhelper.IResultCallbacks;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;
import java.io.StringWriter;


public class IGCRHelper {
    public enum EAuthenticationScheme
    {
        authenticationScheme1("authenticationScheme1"),
        authenticationScheme2("authenticationScheme2"),
        authenticationScheme3("authenticationScheme3"),
        authenticationScheme4("authenticationScheme4");

        private String name;
        EAuthenticationScheme(String name)
        {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    public enum EAuthenticationFlag
    {
        blocking("blocking"),
        unblocking("unblocking");

        private String name;
        EAuthenticationFlag(String name)
        {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }

    public static void sendAuthenticationRequest(Context context, EAuthenticationScheme scheme, EAuthenticationFlag flag, IIGAuthenticationResultCallback callback)
    {
        CSPAccessMgrHelper.executeAccessMgrServiceAccessAction(context, IGContentResolverConstants.START_AUTHENTICATION_URI, CSPAccessMgrHelper.EServiceAccessAction.AllowCaller, new IResultCallbacks() {
            @Override
            public void onSuccess(String message, String resultXML) {
                if(callback != null)
                {
                    callback.onDebugStatus("Authorization to request authentication granted with success.");
                }
                Bundle data = new Bundle();
                data.putString(IGContentResolverConstants.AUTHENTICATION_SCHEME_KEY, scheme.toString());
                data.putString(IGContentResolverConstants.AUTHENTICATION_FLAG_KEY, flag.toString());

                ContentResolver resolver = context.getContentResolver();
                Bundle response = resolver.call(Uri.parse(IGContentResolverConstants.BASE_URI),
                        IGContentResolverConstants.LOCKSCREEN_ACTION,
                        IGContentResolverConstants.START_AUTHENTICATION_METHOD,
                        data);

                if (response == null || !response.containsKey("RESULT") || response.getString("RESULT").equals("Caller is unauthorized")) {
                    if(callback != null)
                    {
                        callback.onError("App has not been authorized to send authentication requests");
                    }
                    return;
                } else if (response.containsKey("RESULT") && response.getString("RESULT").equals("SUCCESS")) {
                    if(callback != null)
                    {
                        callback.onError("Session already in use, please logout first before doing an authentication request.");
                    }
               } else if (response.containsKey("RESULT") && response.getString("RESULT").equals("Error:Cannot initiate as lock type is Device lock")) {
                    if(callback != null)
                    {
                        callback.onError("Can not request a new authentication, please logout first.");
                    }
                } else {
                    if(response == null || response.containsKey("RESULT") == false)
                    {
                        if(callback != null)
                        {
                            callback.onError("Error while trying to send an authentication request, response is empty.");
                        }
                        return;
                    }
                    if(callback != null)
                    {
                        callback.onSuccess(response.getString("RESULT"));
                    }
                }
            }

            @Override
            public void onError(String message, String resultXML) {
                if(callback != null)
                {
                    callback.onError(message);
                }
            }

            @Override
            public void onDebugStatus(String message) {
                if(callback != null)
                {
                    callback.onDebugStatus(message);
                }
            }
        });
    }

    public static void logout(Context context, IIGAuthenticationResultCallback callback) {
        CSPAccessMgrHelper.executeAccessMgrServiceAccessAction(context, IGContentResolverConstants.LOGOUT_URI, CSPAccessMgrHelper.EServiceAccessAction.AllowCaller, new IResultCallbacks() {
            @Override
            public void onSuccess(String message, String resultXML) {
                Bundle result = context.getContentResolver().call(Uri.parse(IGContentResolverConstants.BASE_URI), IGContentResolverConstants.LOCKSCREEN_ACTION, IGContentResolverConstants.LOGOUT_METHOD, null);
                if(result != null)
                {
                    if(callback != null)
                    {
                        callback.onSuccess(result.getString("RESULT"));
                    }
                }
            }

            @Override
            public void onError(String message, String resultXML) {
                if(callback != null)
                {
                    callback.onError(message);
                }
            }

            @Override
            public void onDebugStatus(String message) {
                if(callback != null)
                {
                    callback.onDebugStatus(message);
                }
            }
        });
    }

    public static void getCurrentSession(Context context, IIGSessionResultCallback callback) {
        new RetrieveIGCRTask().doInBackground(context, Uri.parse(IGContentResolverConstants.CURRENT_SESSION_URI), new IGRetrieveTaskCallback() {
            @Override
            public void onSuccess(Cursor cursor) {
                if(callback != null) {
                    callback.onSuccess(cursor);
                }
            }

            @Override
            public void onError(String message) {
                if(callback != null) {
                    callback.onError(message);
                }
            }

            @Override
            public void onDebugStatus(String message) {
                if(callback != null) {
                    callback.onDebugStatus(message);
                }
            }

        });
    }

    public static void getPreviousSession(Context context, IIGSessionResultCallback callback) {
        new RetrieveIGCRTask().doInBackground(context, Uri.parse(IGContentResolverConstants.PREVIOUS_SESSION_URI), new IGRetrieveTaskCallback() {
            @Override
            public void onSuccess(Cursor cursor) {
                if(callback != null) {
                    callback.onSuccess(cursor);
                }
            }

            @Override
            public void onError(String message) {
                if(callback != null) {
                    callback.onError(message);
                }
            }

            @Override
            public void onDebugStatus(String message) {
                if(callback != null) {
                    callback.onDebugStatus(message);
                }
            }
        });
    }

    public static String cursorToString(Cursor cursor)
    {
        String returnString = "";
        try {
            while(cursor.moveToNext()) {
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    returnString += cursor.getColumnName(i) + ": " + cursor.getString(i) + "\n";
                }
            }
        }
        catch(Exception e) {
        }
        finally {
            cursor.close();
        }
        return returnString;
    }

    public static Map<String, String> cursorToMap(Cursor cursor)
    {
        HashMap<String, String> returnMap = new HashMap<>();
        try {
            while(cursor.moveToNext()) {
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    returnMap.put(cursor.getColumnName(i),cursor.getString(i));
                }
            }
        }
        catch(Exception e) {
        }
        finally {
            cursor.close();
        }
        return returnMap;
    }

    public static JSONObject cursorToJson(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    String columnValue = cursor.getString(i);
                    jsonObject.put(columnName, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return jsonObject;
    }

    public static String cursorToXml(Cursor cursor) {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag("", "CursorData");

            while (cursor.moveToNext()) {
                xmlSerializer.startTag("", "Row");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    String columnValue = cursor.getString(i);

                    xmlSerializer.startTag("", columnName);
                    xmlSerializer.text(columnValue);
                    xmlSerializer.endTag("", columnName);
                }
                xmlSerializer.endTag("", "Row");
            }

            xmlSerializer.endTag("", "CursorData");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return writer.toString();
    }


}
