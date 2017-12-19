package com.v.downloaddemo.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.v.downloaddemo.Bean.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/29.
 */

public class DownLoadService extends Service {
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_ALLFINISH = "ACTION_ALLFINISH";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
    //    下载任务的集合   LinkedHashMap在添加删除时 效率比较高
    private Map<Integer,DownLoadTask> mtasks=new LinkedHashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            DownLoadTask.sExecutorService.execute(new InitThread(fileInfo));
        } else if (ACTION_STOP.equals(intent.getAction())) {
            //暂停下载
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            //从集合中取出下载任务
            DownLoadTask downLoadTask = mtasks.get(fileInfo.getId());
            if(downLoadTask!=null){
                downLoadTask.isPause=true;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InitThread extends Thread {

        private FileInfo fileInfo;


        public InitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {

            HttpURLConnection urlConnection = null;
            RandomAccessFile raf = null;
            try {
                urlConnection = (HttpURLConnection) new URL(fileInfo.getUrl()).openConnection();
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();
                //获得文件长度
                int length = -1;
                if (responseCode == 200) {
                    //获得文件长度
                    length = urlConnection.getContentLength();
                }
                if (length < 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //在本地创建文件
                File file = new File(dir, fileInfo.getFileName());
                //随机访问文件  特殊的输出流 在文件的任意一个位置进行写入操作
                raf = new RandomAccessFile(file, "rwd");
                //设置文件长度
                raf.setLength(length);
                fileInfo.setLength(length);
                handler.obtainMessage(MSG_Init, fileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public static final int MSG_Init = 0;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_Init:
                    FileInfo obj = (FileInfo) msg.obj;
                    //启动下载任务
                    DownLoadTask downLoadTask = new DownLoadTask(DownLoadService.this, obj, obj.getThreadCount());
                    downLoadTask.downLoad();

                    //把下载任务添加到集合中
                    mtasks.put(obj.getId(),downLoadTask);

                    break;
                default:
                    break;
            }
        }
    };
}
