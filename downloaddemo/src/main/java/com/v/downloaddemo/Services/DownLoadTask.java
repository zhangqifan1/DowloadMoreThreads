package com.v.downloaddemo.Services;

import android.content.Context;
import android.content.Intent;

import com.v.downloaddemo.Bean.FileInfo;
import com.v.downloaddemo.Dao.ThreadDao;
import com.v.downloaddemo.Dao.ThreadDaoImpl;
import com.v.downloaddemo.Bean.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/11/29.
 * 下载任务
 */

public class DownLoadTask {
    private Context context;
    private FileInfo fileInfo;
    private ThreadDao threadDao;
    private int finished = 0;
    public boolean isPause = false;
    private int ThreadCount = 1;//线程数量 默认为  单线程
    private List<DownLoadThread> downLoadThreads;
    private List<ThreadInfo> threadInfoList;

    public  static ExecutorService sExecutorService= Executors.newCachedThreadPool();
    public DownLoadTask(Context context, FileInfo fileInfo, int threadCount) {
        this.ThreadCount = threadCount;
        this.context = context;
        this.fileInfo = fileInfo;
        threadDao = new ThreadDaoImpl(context);
    }

    public void downLoad() {
        //读取数据库的线程信息
        threadInfoList = threadDao.getThread(fileInfo.getUrl());
        ThreadInfo threadInfo = null;
        if (threadInfoList.size() == 0) {//第一次下载
            //获得每个线程下载进度
            int length = fileInfo.getLength() / ThreadCount;
            for (int i = 0; i < ThreadCount; i++) {
                ThreadInfo threadInfo1 = new ThreadInfo(i, fileInfo.getUrl(), length * i, (i + 1) * length - 1, 0);
                if (i == ThreadCount - 1) {
                    threadInfo1.setEnd(fileInfo.getLength());//防止除不尽
                }
                //添加到线程信息集合中
                threadInfoList.add(threadInfo);
                //插入下载线程信息
                threadDao.insertThread(threadInfo1);
            }

        }
        downLoadThreads = new ArrayList<>();
        //启动多个线程进行下载
        for (ThreadInfo info : threadInfoList) {
            DownLoadThread downLoadThread = new DownLoadThread(info);
            DownLoadTask.sExecutorService.execute(downLoadThread);
            //添加到下载线程集合 中  和上面不一样   这个只是为了方便管理暂停下载
            downLoadThreads.add(downLoadThread);
        }
    }

    /**
     * 下载线程
     */
    class DownLoadThread extends Thread {
        private ThreadInfo threadInfo;
        private boolean isFinish = false;//线程是否下载完毕

        public DownLoadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {

            //设置线程的下载位置
            URL url=null;
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;
            try {
                url = new URL(fileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                int start = threadInfo.getStart() + threadInfo.getFinished();
                connection.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());//下载范围  可以设置下载的字节数 和 结束的字节数
                //找到文件的写入位置
                File file = new File(DownLoadService.DOWNLOAD_PATH, fileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                //在读写的时候跳过设置好的字节数,从下一个字节数开始下载
                raf.seek(start);
                Intent intent = new Intent(DownLoadService.ACTION_UPDATE);
                finished += threadInfo.getFinished();

                //开始下载
                int responseCode = connection.getResponseCode();
                if (responseCode>200 &&responseCode<300) {
                    System.out.println(""+responseCode);
                    //读取数据
                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long l = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        //  累加整个文件完成的进度
                        finished += len;
                        //累加每个线程完成的进度
                        threadInfo.setFinished(threadInfo.getFinished() + len);

                        if (System.currentTimeMillis() - l > 1000) {
                            l = System.currentTimeMillis();
                            //把下载进度发送给广播  还要发送一下文件id
                            intent.putExtra("finished", finished * 100 / fileInfo.getLength());
                            intent.putExtra("id", fileInfo.getId());

                            context.sendBroadcast(intent);
                        }
                        //下载暂停时,保存下载进度
                        if (isPause) {
                            threadDao.updateThread(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
                            return;
                        }
                    }

                    //标识线程下载完毕
                    isFinish = true;

                    //检查是否所有部分都下载完成
                    checkAllThreadFinished();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {

                    if(connection!=null){
                        connection.disconnect();
                    }
                    if(raf!=null){
                        raf.close();
                    }
                    if(inputStream!=null){
                        inputStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    /**
     * 判断是否所有线程都执行完毕
     */
    private synchronized void checkAllThreadFinished() {
        boolean allFinished = true;
        for (DownLoadThread thread : downLoadThreads) {
            if (!thread.isFinish) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            //删除线程信息
            threadDao.deleteThread(fileInfo.getUrl());
            //发送广播通知UI下载任务结束
            Intent intent = new Intent(DownLoadService.ACTION_ALLFINISH);
            intent.putExtra("fileInfo", fileInfo);
            context.sendBroadcast(intent);
        }
    }
}
