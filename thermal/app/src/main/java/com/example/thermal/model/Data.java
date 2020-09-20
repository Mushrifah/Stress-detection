package com.example.thermal.model;

public class Data {
   private  String id;
   private int tdry;
   private int twet;
   private int tcanopy;
   private int timeDay;

   public Data(int tdry, int twet, int tcanopy, int timeDay) {
       this.tdry= tdry;
       this.twet=twet;
       this.tcanopy= tcanopy;
       this.timeDay = timeDay;
   }

   public String getId() {
       return id;
   }

    }


