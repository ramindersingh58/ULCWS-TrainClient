package com.raminder.ulcws_trainclient;

/**
 * Created by Raminder on 5/16/2017.
 */

public class GlobalApp {
    static String ip1="192.168.1.6";
    static String ip2="192.168.43.97";
    static String socket="8084";
    static String localwifi="http://"+ GlobalApp.ip1 +":"+GlobalApp.socket +"/unmannedlevelcrossing";
    static String localhotspot="http://"+ GlobalApp.ip2 +":"+GlobalApp.socket +"/unmannedlevelcrossing";
    static String cloud="http://ulcws.cloud.cms500.com";
    static String weburl=cloud;
    static boolean isStopped=false;
   // static String locations="";
    static int rate=10;

}
