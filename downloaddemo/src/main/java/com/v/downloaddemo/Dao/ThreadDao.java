package com.v.downloaddemo.Dao;

import com.v.downloaddemo.Bean.ThreadInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 * 数据访问接口
 */

public interface ThreadDao {
    /**
     * 插入线程信息
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * 删除线程
     * @param url
     */
    public void deleteThread(String url);

    /**
     * 更新线程完成进度
     * @param url
     * @param thread_id
     */
    public void updateThread(String url,int thread_id,int finished);

    /**
     * 查询文件的线程信息
     * @param url
     * @return
     */
    public List<ThreadInfo> getThread(String url);

    /**
     * 线程信息是否存在
     * @param url
     * @param thread_id
     * @return
     */
    public  boolean isExists(String url,int thread_id);


}
