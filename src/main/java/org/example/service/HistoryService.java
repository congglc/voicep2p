package org.example.service;

import java.io.FileWriter;

public class HistoryService {

    public static void save(String message){

        try{

            FileWriter writer = new FileWriter("history.txt",true);

            writer.write(message+"\n");

            writer.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}