package com.rizieq.drinkshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_BRITHDATE = "brithdate";
    private static final String is_login = "logginstatus";
    private final String SHARE_NAME = "loginsession";
    private final int MODE_PRIVATE = 0;
    private Context _context;

    public SessionManager(Context context)
    {
        this._context = context;
        sp = _context.getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor = sp.edit();

    }

    public void storeLogin(String phone, String name, String address, String brithdate)
    {
        editor.putBoolean(is_login,true);
        editor.putString(KEY_PHONE,phone);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_ADDRESS,address);
        editor.putString(KEY_BRITHDATE,brithdate);
        editor.commit();

    }

    public HashMap getDataLogin()
    {
        HashMap<String,String> map = new HashMap<>();
        map.put(KEY_PHONE, sp.getString(KEY_PHONE,null));
        map.put(KEY_NAME, sp.getString(KEY_NAME,null));
        map.put(KEY_ADDRESS, sp.getString(KEY_ADDRESS,null));
        map.put(KEY_BRITHDATE, sp.getString(KEY_BRITHDATE,null));
        return map;
    }

    public void checkLogin()
    {
        if (!this.Login())
        {
            Intent login = new Intent(_context, MainActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(login);
        }
    }

    public void logout()
    {
        editor.clear();
        editor.commit();
    }

    private boolean Login()
    {
        return sp.getBoolean(is_login,false);
    }



}
