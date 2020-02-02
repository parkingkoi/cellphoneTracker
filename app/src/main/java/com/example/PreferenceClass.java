package com.example;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceClass {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public PreferenceClass(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("myPeref",Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

   public void saveNotificationOn(boolean on){
        if(on){
            editor.putBoolean("notiOn",true);
        }else {
            editor.putBoolean("notiOn",false);
        }
        editor.commit();
    }

   public boolean getNotificationOn(){
        return preferences.getBoolean("notiOn",false);
    }
   public void saveRadious(int radious){
        editor.putInt("radious",radious);
        editor.commit();
    }
   public int getRadious(){
        return preferences.getInt("radious",0);
    }
}
