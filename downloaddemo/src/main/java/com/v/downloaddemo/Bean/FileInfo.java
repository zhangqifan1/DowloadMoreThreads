package com.v.downloaddemo.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/29.
 */

public class FileInfo implements Serializable{
    private int id;
    private String url;
    private String fileName;
    private int length;
    private int finished;
    private int ThreadCount;

    public FileInfo(int id, String url, String fileName, int length, int finished, int threadCount) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
        ThreadCount = threadCount;
    }

    public FileInfo() {
    }

    public void setThreadCount(int threadCount) {
        ThreadCount = threadCount;
    }

    public int getThreadCount() {
        return ThreadCount;
    }

    public FileInfo(int id, String url, String fileName, int length, int finished) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                '}';
    }
}
