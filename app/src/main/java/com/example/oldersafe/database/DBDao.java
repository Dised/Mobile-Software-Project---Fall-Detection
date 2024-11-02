package com.example.oldersafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.oldersafe.bean.UserBaseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class DBDao {

    private Context context;
    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;

    // Constructor
    public DBDao(Context context) {
        this.context = context;
    }

    // Open the database
    public void open() throws SQLiteException {
        dbHelper = new MyDataBaseHelper(context);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }

    // Close the database
    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }
    //login functionality
    public UserBaseInfo Login(String account, String psw, String flag) {
        Cursor cursor = db.query("userinfo", null, "account=? and psw=? and flag=?", new String[]{account, psw, flag}, null, null, null);
        UserBaseInfo o = new UserBaseInfo();
        while (cursor.moveToNext()) {
            o.infoId = cursor.getString(cursor.getColumnIndex("info_id"));
            o.userName = cursor.getString(cursor.getColumnIndex("name"));
            o.phone = cursor.getString(cursor.getColumnIndex("phone"));
            o.flag = cursor.getString(cursor.getColumnIndex("flag"));
            o.state = cursor.getString(cursor.getColumnIndex("state"));
            o.userType = cursor.getString(cursor.getColumnIndex("usertype"));
        }
        return o;
    }

    //Register Functionality
    public long register(UserBaseInfo o) {
        ContentValues values = new ContentValues();
        //Register Contacts
        values.put("info_id", o.infoId);
        values.put("name", o.userName);
        values.put("account", o.account);
        values.put("psw", o.psw);
        values.put("phone", o.phone);
        values.put("usertype", o.userType);
        values.put("flag", o.flag);
        values.put("state", o.state);
        //insert into the data base
        return db.insert("userinfo", null, values);
    }

    // Search contact list by criteria
    public ArrayList<Map<String, Object>> getContactData(String name,String type,String flag) {
        ArrayList<Map<String, Object>> contactList = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query("userinfo", null, "name=? and usertype=? and flag=?", new String[]{name,type,flag}, null, null,null);

        int resultCounts = cursor.getCount();
        if (resultCounts == 0 ) {
            return null;
        } else {
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("info_id", cursor.getString(cursor.getColumnIndex("info_id")));
                map.put("contact", cursor.getString(cursor.getColumnIndex("contact")));
                map.put("contact_phone", cursor.getString(cursor.getColumnIndex("contact_phone")));

                contactList.add(map);
            }
            return contactList;
        }
    }

    //Retrieve data which meet the criteria
    public ArrayList<Map<String, Object>> getHelpData(String contact,String state,String flag) {
        ArrayList<Map<String, Object>> contactList = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query("userinfo", null, "contact=? and state=? and flag=?", new String[]{contact,state,flag}, null, null,null);
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 ) {
            return null;
        } else {
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", cursor.getString(cursor.getColumnIndex("name")));
                map.put("phone", cursor.getString(cursor.getColumnIndex("phone")));
                map.put("address", cursor.getString(cursor.getColumnIndex("address")));
                map.put("state", cursor.getString(cursor.getColumnIndex("state")));
                map.put("flag", cursor.getString(cursor.getColumnIndex("flag")));
                map.put("contact", cursor.getString(cursor.getColumnIndex("contact")));
                map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                contactList.add(map);
            }
            return contactList;
        }
    }

    //add contact person data(Recue Workers)
    public long addContact(UserBaseInfo o) {
        ContentValues values = new ContentValues();
        values.put("info_id", o.infoId);
        values.put("name", o.userName);
        values.put("account", o.account);
        values.put("phone", o.phone);
        values.put("usertype",o.userType);
        values.put("flag",o.flag);
        values.put("contact",o.contact);
        values.put("contact_phone",o.contactPhone);
        return db.insert("userinfo", null, values);
    }

    public long editContact(UserBaseInfo o) {
        ContentValues values = new ContentValues();
        values.put("contact",o.contact);
        values.put("contact_phone",o.contactPhone);
        return db.update("userinfo", values, "info_id=?",new String[]{o.infoId});
    }

    public int updateAddress(String name,String address,String state,double latitude,double longitude){
        ContentValues value = new ContentValues();
        value.put("address", address);
        value.put("state", state);
        value.put("latitude", latitude);
        value.put("longitude", longitude);
        return db.update("userinfo", value, "name=?", new String[]{name});
    }

    //This function serves to get all data from the elderly
    public ArrayList<Map<String, Object>> getAllData(String contact,String state,String flag) {
        ArrayList<Map<String, Object>> contactList = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query("userinfo", null, null, null, null, null,null);
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 ) {
            return null;
        } else {
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", cursor.getString(cursor.getColumnIndex("name")));
                map.put("phone", cursor.getString(cursor.getColumnIndex("phone")));
                map.put("account", cursor.getString(cursor.getColumnIndex("account")));
                map.put("psw", cursor.getString(cursor.getColumnIndex("psw")));
                map.put("address", cursor.getString(cursor.getColumnIndex("address")));
                map.put("state", cursor.getString(cursor.getColumnIndex("state")));
                map.put("flag", cursor.getString(cursor.getColumnIndex("flag")));
                map.put("contact", cursor.getString(cursor.getColumnIndex("contact")));
                map.put("contact_phone", cursor.getString(cursor.getColumnIndex("contact_phone")));
                map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                contactList.add(map);
            }
            return contactList;
        }
    }
}
