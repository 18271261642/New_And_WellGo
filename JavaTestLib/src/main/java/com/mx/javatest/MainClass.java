package com.mx.javatest;

import java.util.Calendar;

public class MainClass {



    public static void main(String[] args) {


        Calendar instance = Calendar.getInstance();
        instance.set(2000,0,1,0,0,0);
        instance.add(Calendar.SECOND,612956941);
        System.out.println(DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS,instance));


    }


}

