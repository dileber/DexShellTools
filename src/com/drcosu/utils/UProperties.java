package com.drcosu.utils;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class UProperties {

    private static Map<String,String> config = null;

    public static Map<String,String> readProperties(){
        System.out.println("loding config");
        config = new HashMap<>();
        Properties prop = new Properties();
        try{
            InputStreamReader in = new InputStreamReader(new FileInputStream("config/config.properties"));
            prop.load(in);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key = it.next();
                config.put(key,prop.getProperty(key));
            }
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("end loding config");
        return config;

    }

}
