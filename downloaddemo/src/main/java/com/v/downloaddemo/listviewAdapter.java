package com.v.downloaddemo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.v.downloaddemo.Bean.FileInfo;
import com.v.downloaddemo.Services.DownLoadService;

import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */

public class listviewAdapter extends BaseAdapter {
    private Context context;
    private List<FileInfo> list;

    public listviewAdapter(Context context, List<FileInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FileInfo fileInfo = list.get(position);
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=View.inflate(context,R.layout.list_item,null);
            holder.butStart=convertView.findViewById(R.id.butStart);
            holder.butStop=convertView.findViewById(R.id.butStop);
            holder.progressBar2=convertView.findViewById(R.id.progressBar2);
            holder.textView2=convertView.findViewById(R.id.textView2);
            holder.progressBar2.setMax(100);
            holder.textView2.setText(fileInfo.getFileName());
            holder.butStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, DownLoadService.class);
                    intent.setAction(DownLoadService.ACTION_START);
                    intent.putExtra("fileInfo",fileInfo);
                    context.startService(intent);
                }
            });
            holder.butStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, DownLoadService.class);
                    intent.setAction(DownLoadService.ACTION_STOP);
                    intent.putExtra("fileInfo",fileInfo);
                    context.startService(intent);
                }
            });
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }


        holder.progressBar2.setProgress(fileInfo.getFinished());
        return convertView;
    }

    /**
     * 更新列表项的进度条
     * @param id
     * @param progress
     */
    public void updateProgress(int id,int progress){
        FileInfo fileInfo = list.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }
    /**
     * 删除
     * @param id
     */
    public void removeItem(int id){
        list.remove(id);
        notifyDataSetChanged();
    }


    static  class  ViewHolder{
         TextView textView2;
         ProgressBar progressBar2;
         Button butStart;
         Button butStop;
    }
}
