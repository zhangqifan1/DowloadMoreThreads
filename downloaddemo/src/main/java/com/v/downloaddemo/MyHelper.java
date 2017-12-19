package com.v.downloaddemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/11/29.
 */

public class MyHelper extends SQLiteOpenHelper {

    private static MyHelper  helper=null;
    private String Sql_Insert="create table thread_info(thread_id integer primary key autoincrement,url text,start integer,_end integer,finished integer )";
    private String Sql_Drop="drop table if exists thread_info";
    private MyHelper(Context context) {
        super(context, "db", null, 1);
    }


    /**
     * 提供一个共有的方法获取对象
     */
    public static MyHelper getInstance(Context context){
        if(helper==null){
            helper=new MyHelper(context);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Sql_Insert);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Sql_Drop);
        db.execSQL(Sql_Insert);
    }

}
