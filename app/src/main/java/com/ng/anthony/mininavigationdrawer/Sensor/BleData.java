package com.ng.anthony.mininavigationdrawer.Sensor;

public class BleData {
    private static double[] locationResult = {0,0};

    public static void setLocationResult(float X, float Y){
        float[] res=DataConstrain.BoxConstrain(X,Y);
        locationResult[0] = res[0];
        locationResult[1] = res[1];
    }

    public static double[] getLocationResult(){
        return locationResult;
    }

    public void reset(){
        for(int i = 0 ; i < locationResult.length ; i++){
            locationResult[i] = 0;
        }
    }
}
