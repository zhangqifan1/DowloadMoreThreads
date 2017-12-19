package com.v.downloaddemo.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.v.downloaddemo.MyHelper;
import com.v.downloaddemo.Bean.ThreadInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Administrator on 2017/11/29.
 * 数据访问接口的实现
 */

public class ThreadDaoImpl implements  ThreadDao {
    private String tableName="thread_info";
    private MyHelper helper=null;

    public ThreadDaoImpl(Context context) {
        helper= MyHelper.getInstance(context);
    }

    @Override
    public synchronized void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into "+tableName+" (thread_id,url,start,_end,finished) values(?,?,?,?,?)",
                new Object[]{threadInfo.getId(),threadInfo.getUrl(),threadInfo.getStart(),threadInfo.getEnd(),threadInfo.getFinished()});
        db.close();
    }

    @Override
    public synchronized void deleteThread(String url) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from "+tableName+" where url=?",new Object[]{url });
        db.close();
    }

    @Override
    public synchronized void updateThread(String url, int thread_id, int finished) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update "+tableName+" set finished=? where url =? and thread_id=?",new Object[]{finished,url,thread_id });
        db.close();
    }

    @Override
    public List<ThreadInfo> getThread(String url) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ThreadInfo> list=new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where url=?", new String[]{url});
        while(cursor.moveToNext()){
            ThreadInfo threadInfo=new ThreadInfo();
            threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("_end")));
            threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public  boolean isExists(String url, int thread_id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where url=? and thread_id=?", new String[]{url,thread_id+""});
        boolean b = cursor.moveToNext();
        cursor.close();
        db.close();
        return b;

    }
}
