package com.v.downloaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.v.downloaddemo.Bean.FileInfo;
import com.v.downloaddemo.Services.Const;
import com.v.downloaddemo.Services.DownLoadService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private com.v.downloaddemo.listviewAdapter listviewAdapter;
    private List<FileInfo> list;
    private int a;
    private FileInfo fileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int CPUnum = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU数量:" + CPUnum);
        initView();
        //注册广播接收器

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(DownLoadService.ACTION_UPDATE);
        intentFilter.addAction(DownLoadService.ACTION_ALLFINISH);
//        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
//        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
//        intentFilter.addAction("android.intent.action.PACKAGE_INSTALL");
        registerReceiver(receiver, intentFilter);


        //创建文件信息对象
        FileInfo fileInfo = new FileInfo(0, Const.WXUrl, "Music.apk", 0, 0, 1);
        FileInfo fileInfo1 = new FileInfo(1, Const.WXUrl, "Music1.apk", 0, 0, 2);
        FileInfo fileInfo2 = new FileInfo(2, Const.WXUrl, "Music2.apk", 0, 0, 3);
        FileInfo fileInfo3 = new FileInfo(3, Const.WXUrl, "Music3.apk", 0, 0, 3);

        list = new ArrayList<>();
        list.add(fileInfo);
        list.add(fileInfo1);
        list.add(fileInfo2);
        list.add(fileInfo3);

        listviewAdapter = new listviewAdapter(this, list);
        lv.setAdapter(listviewAdapter);
    }


    /**
     * 帮助我们更新Ui  的一个广播接收器
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownLoadService.ACTION_UPDATE.equals(intent.getAction())) {
                int finished = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id", 0);
                listviewAdapter.updateProgress(id, finished);
            } else if (DownLoadService.ACTION_ALLFINISH.equals(intent.getAction())) {
                //更新进度为0
                fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                listviewAdapter.updateProgress(fileInfo.getId(), 0);
                Toast.makeText(MainActivity.this, list.get(fileInfo.getId()).getFileName() + "下载完毕", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.addCategory(Intent.CATEGORY_DEFAULT);
                intent2.setDataAndType(Uri.parse("file://" + DownLoadService.DOWNLOAD_PATH + fileInfo.getFileName()), "application/vnd.android.package-archive");
                a = fileInfo.getId();
                startActivityForResult(intent2,a);
                //安装成功了 移除子条目
                listviewAdapter.removeItem(a);
            }
//            else if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
//                Toast.makeText(context, "有应用被添加", Toast.LENGTH_LONG).show();
//            } else if (Intent.ACTION_PACKAGE_INSTALL.equals(intent.getAction())) {
//                Toast.makeText(context, "有应用被安装", Toast.LENGTH_LONG).show();
//                //安装成功了 移除子条目
//                listviewAdapter.removeItem(a);
//            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == a) {
            System.out.println(resultCode);
            if (resultCode == RESULT_CANCELED) {
                //没有安装
                lv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(MainActivity.this, "取消安装", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.addCategory(Intent.CATEGORY_DEFAULT);
                        intent2.setDataAndType(Uri.parse("file://" + DownLoadService.DOWNLOAD_PATH + fileInfo.getFileName()), "application/vnd.android.package-archive");
                        a = fileInfo.getId();
                        startActivityForResult(intent2,a);
                        return false;
                    }
                });

            }else{
                //安装成功了 移除子条目
                listviewAdapter.removeItem(a);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
    }
}
