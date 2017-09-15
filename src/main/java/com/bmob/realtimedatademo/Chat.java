package com.bmob.realtimedatademo;

import cn.bmob.v3.BmobObject;

public class Chat extends BmobObject {
    private String name;
    private String content;

    public Chat(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
