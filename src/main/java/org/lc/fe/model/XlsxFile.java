package org.lc.fe.model;

import org.apache.tomcat.util.http.fileupload.FileItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class XlsxFile {

    private FileItem fileItem;

    public XlsxFile(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public InputStream getInputStream() {
        if(fileItem != null){
            try {
                return fileItem.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void write(OutputStream outputStream){
        if(fileItem != null && outputStream != null){
            InputStream inputStream = null;
            try{
                inputStream = fileItem.getInputStream();
                byte[] buffer = new byte[1024];
                while(inputStream.read(buffer) > -1){
                    outputStream.write(buffer);
                    outputStream.flush();
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                delete();
                fileItem = null;
            }
        }
    }

    public void delete(){
        if(fileItem != null){
            fileItem.delete();
        }
    }

}
