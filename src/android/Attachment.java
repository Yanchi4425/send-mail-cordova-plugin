package com.autentia.plugin.sendmail;

public class Attachment{
    private String fileName = "";
    private String base64Sourse = "";

    public Attachment(String fileName, String base64Source){
        this.fileName = fileName;
        this.base64Sourse = base64Source;
    }

    public String getFileName(){
        return this.fileName;
    }

    public String getBase64Source(){
        return this.base64Sourse;
    }
}