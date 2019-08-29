package com.example.nao_control;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class calendar {
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";
    private static TelephonyManager mTm;


    public JSONArray getcalendar(Context context, String d1, String d2, String t1, String t2) {
        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";

        String[] s_d = d1.split("-");
        int s_y = Integer.parseInt(s_d[0]);
        int s_m = Integer.parseInt(s_d[1]);
        int s_day = Integer.parseInt(s_d[2]);

        String[] e_d = d2.split("-");
        int e_y = Integer.parseInt(e_d[0]);
        int e_m = Integer.parseInt(e_d[1]);
        int e_day = Integer.parseInt(e_d[2]);

        String[] s_t = t1.split(":");
        int s_h = Integer.parseInt(s_t[0]);
        int s_min = Integer.parseInt(s_t[1]);

        String[] e_t = t2.split(":");
        int e_h = Integer.parseInt(e_t[0]);
        int e_min = Integer.parseInt(e_t[1]);

        Calendar start_Time = Calendar.getInstance();
        start_Time.set(s_y, s_m-1, s_day, s_h, s_min);
        Calendar end_Time = Calendar.getInstance();
        end_Time.set(e_y, e_m-1, e_day, e_h, e_min);
        long start = start_Time.getTimeInMillis();
        long end = end_Time.getTimeInMillis();

        JSONArray arr = new JSONArray();
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        while (eventCursor.moveToNext()) {
            JSONObject json = new JSONObject();
            eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
            description = eventCursor.getString(eventCursor.getColumnIndex("description"));
            location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
            long unix_start = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart")));
            long unix_end = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend")));
            startTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart"))));
            endTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend"))));
            if (start <= unix_start && end >= unix_end) {
                try {
                    json.put("eventTitle", eventTitle);
                    json.put("description", description);
                    json.put("location", location);
                    json.put("startTime", startTime);
                    json.put("endTime", endTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr.put(json);
            }
        }
        return arr;

    }
    public JSONArray getcalendar_intent(Context context, String target_title, String target_description){
        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";


        JSONArray arr=new JSONArray();
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        while (eventCursor.moveToNext()){
            JSONObject json=new JSONObject();
            eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
            description = eventCursor.getString(eventCursor.getColumnIndex("description"));
            location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
            long unix_start = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart")));
            long unix_end = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend")));
            startTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart"))));
            endTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend"))));
            if (eventTitle.contains(target_title) || description.contains(target_description) ){
            try {
                json.put("eventTitle", eventTitle);
                json.put("description", description);
                json.put("location", location);
                json.put("startTime", startTime);
                json.put("endTime", endTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            arr.put(json);}
        }
        return arr;
    }

    public JSONArray getcalendar_both(Context context, String target_title, String target_description, long input_start, long input_end){
        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";


        JSONArray arr=new JSONArray();
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        while (eventCursor.moveToNext()){
            JSONObject json=new JSONObject();
            eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
            description = eventCursor.getString(eventCursor.getColumnIndex("description"));
            location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
            long unix_start = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart")));
            long unix_end = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend")));
            startTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart"))));
            endTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend"))));
            if (eventTitle.contains(target_title) || description.contains(target_description) && input_start >= unix_start && input_end <=unix_end){
                try {
                    json.put("eventTitle", eventTitle);
                    json.put("description", description);
                    json.put("location", location);
                    json.put("startTime", startTime);
                    json.put("endTime", endTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr.put(json);}
        }
        return arr;
    }
    /**
     * 时间戳转换为字符串
     * @param time:时间戳
     * @return
     */

    private static String timeStamp2Date(long time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

}
