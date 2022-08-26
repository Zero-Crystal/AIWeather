package com.zero.base.util;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 自定义日志类
 */
public final class KLog {
    private static final String DEFAULT_MESSAGE = "execute";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int JSON_INDENT = 4;

    public static final int V = 0x1;
    public static final int D = 0x2;
    public static final int I = 0x3;
    public static final int W = 0x4;
    public static final int E = 0x5;
    public static final int A = 0x6;
    public static final int JSON = 0x7;
    public static int LEVEL = 0;

    public static void v() {
        printLog(V, null, DEFAULT_MESSAGE);
    }

    public static void v(String msg) {
        printLog(V, null, msg);
    }

    public static void v(String tag, String msg) {
        printLog(V, tag, msg);
    }

    public static void d() {
        printLog(D, null, DEFAULT_MESSAGE);
    }

    public static void d(String msg) {
        printLog(D, null, msg);
    }

    public static void d(String tag, String msg) {
        printLog(D, tag, msg);
    }

    public static void i() {
        printLog(I, null, DEFAULT_MESSAGE);
    }

    public static void i(String msg) {
        printLog(I, null, msg);
    }

    public static void i(String tag, String msg) {
        printLog(I, tag, msg);
    }

    public static void w() {
        printLog(W, null, DEFAULT_MESSAGE);
    }

    public static void w(String msg) {
        printLog(W, null, msg);
    }

    public static void w(String tag, String msg) {
        printLog(W, tag, msg);
    }

    public static void e() {
        printLog(E, null, DEFAULT_MESSAGE);
    }

    public static void e(String msg) {
        printLog(E, null, msg);
    }

    public static void e(String tag, String msg) {
        printLog(E, tag, msg);
    }

    public static void a() {
        printLog(A, null, DEFAULT_MESSAGE);
    }

    public static void a(String msg) {
        printLog(A, null, msg);
    }

    public static void a(String tag, String msg) {
        printLog(A, tag, msg);
    }

    public static void json(String jsonFormat) {
        printLog(JSON, null, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(JSON, tag, jsonFormat);
    }

    private static void printLog(int type, String tagStr, String msg) {
        if (LEVEL > type) return;

        int index = 4;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        //TAG
        String tag = (tagStr == null ? className : tagStr);
        //Message
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("KLog [(").append(className).append(":").append(lineNumber).append(")->").append(methodName).append("] ");
        if (msg != null && type != JSON) stringBuilder.append(msg);
        String logMessage = stringBuilder.toString();
        //print log
        switch (type) {
            case V:
                Log.v(tag, logMessage);
                break;
            case D:
                Log.d(tag, logMessage);
                break;
            case I:
                Log.i(tag, logMessage);
                break;
            case W:
                Log.w(tag, logMessage);
                break;
            case E:
                Log.e(tag, logMessage);
                break;
            case A:
                Log.wtf(tag, logMessage);
                break;
            case JSON: {
                if (TextUtils.isEmpty(msg)) {
                    Log.d(tag, "Empty or Null json content");
                    return;
                }
                String message = null;
                try {
                    if (msg.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(msg);
                        message = jsonObject.toString(JSON_INDENT);
                    } else if (msg.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(msg);
                        message = jsonArray.toString(JSON_INDENT);
                    }
                } catch (JSONException e) {
                    e(tag, e.getCause().getMessage() + "\n" + msg);
                    return;
                }

                printLine(tag, true);
                message = logMessage + LINE_SEPARATOR + message;
                String[] lines = message.split(LINE_SEPARATOR);
                StringBuilder jsonContent = new StringBuilder();
                for (String line : lines) {
                    jsonContent.append("║ ").append(line).append(LINE_SEPARATOR);
                }
                Log.d(tag, jsonContent.toString());
                printLine(tag, false);
                break;
            }
            default:
                break;
        }
    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
}
