package com.eastelsoft.util;
import java.io.File;

public class DeleteFileUtil
{
   public static void main(String args[]){
       DeleteFileUtil t = new DeleteFileUtil();
       delFolder("d:/bb",86400000l);
       System.out.println("deleted");
}

//删除文件夹
//param folderPath 文件夹完整绝对路径
   	public static boolean ExistFlag=false;
     public static void delFolder(String folderPath,long time) {
     try {
        delAllFile(folderPath,time); //删除完里面所有内容
     } catch (Exception e) {
       e.printStackTrace(); 
     }
}

//删除指定文件夹下所有文件
//param path 文件夹完整绝对路径
   public static boolean delAllFile(String path,long time) {
       boolean flag = false;
       File file = new File(path);
       if (!file.exists()) {
         return flag;
       }
       if (!file.isDirectory()) {
         return flag;
       }
       String[] tempList = file.list();
       File temp = null;
       for (int i = 0; i < tempList.length; i++){
          if (path.endsWith(File.separator)){
             temp = new File(path + tempList[i]);
          } else {
              temp = new File(path + File.separator + tempList[i]);
          }
          if (temp.isFile()){
        	  long modTime=temp.lastModified();
        	  if((System.currentTimeMillis()-modTime)>time){
        		  temp.delete();
        		  ExistFlag=true;
        	  }
          }
          if (temp.isDirectory()) {
             delAllFile(path + "/" + tempList[i],time);//先删除文件夹里面的文件
             delFolder(path + "/" + tempList[i],time);//再删除空文件夹
             flag = true;
          }
       }
       return flag;
     }
}