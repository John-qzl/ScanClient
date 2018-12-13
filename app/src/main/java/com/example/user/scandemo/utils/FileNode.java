package com.example.user.scandemo.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileNode {

    public static char[] read(String path) {
        char[] buffer = null;
        try {
            @SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(path));
            int count=reader.read();
            if(count>0){
            	buffer = new char[count];
            	reader.read(buffer, 0, buffer.length);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
    
    public static String readLine(String path) {
        String content = null;
        try {
            @SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(path));
            content=reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    
    
    public static void write(String path,String content) {
    	try {
            BufferedWriter bufWriter = null;
            bufWriter = new BufferedWriter(new FileWriter(path));
            bufWriter.write(content);
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void write(String path,char[] content) {
    	try {
            BufferedWriter bufWriter = null;
            bufWriter = new BufferedWriter(new FileWriter(path));
            bufWriter.write(content);
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
