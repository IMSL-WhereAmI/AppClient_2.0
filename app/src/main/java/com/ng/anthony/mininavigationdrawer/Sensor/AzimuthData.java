package com.ng.anthony.mininavigationdrawer.Sensor;

public class AzimuthData {
    private static float orien_x = 0;
    public static void setAzimuth(float alpha){
        orien_x = alpha;
    }
    public static float getAzimuth(){
        return orien_x;
    }
}
