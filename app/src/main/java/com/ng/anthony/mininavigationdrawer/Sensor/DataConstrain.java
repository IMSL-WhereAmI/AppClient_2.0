package com.ng.anthony.mininavigationdrawer.Sensor;

public class DataConstrain {
    public static int getMinIndex(float[] arr) {
        int minIndex=0;
        for(int i =0;i<arr.length-1;i++){
            if(arr[minIndex]>arr[i+1]){
                minIndex=i+1;
            }
        }
        return minIndex;
    }

    public static float[] BoxConstrain(float x, float y){
        float[] res = new float[2];
        int flag = 0;
        if(1290>= x && x >= 635 && 378 >= y && y >= 100 ){//1
            float[] arr = {Math.abs(y-100), Math.abs(y-378), Math.abs(x-635), Math.abs(x-1290)};
            flag = getMinIndex(arr);
            if(flag == 0) y=75;
            else if(flag == 1) y=400;
            else if(flag == 2) x=610;
            else if(flag == 3) x=1310;
        }
        else if(580>= x && x >= 430 && 378 >= y && y >= 100 ){//2
            float[] arr = {Math.abs(y-100), Math.abs(y-378),1600, Math.abs(x-580)};
            flag = getMinIndex(arr);
            if(flag == 0) y=75;
            else if(flag == 1) y=400;
            else if(flag == 3) x=1310;
        }
        else if(1555>= x && x >= 1335 && 378 >= y && y >= 100 ){//3
            float[] arr = {Math.abs(y-100), Math.abs(y-378),Math.abs(x-1335), 1600};
            flag = getMinIndex(arr);
            if(flag == 0) y=75;
            else if(flag == 1) y=400;
            else if(flag == 2) x=1310;
        }
        else if(1555>= x && x >= 430 && 47 >= y && y >= 0 ){//4
            y=75;
        }
        else if(1555>= x && x >= 46 && 475 >= y && y >= 423 ){//5
            y=400;
        }
        else if(1600>= x && x >= 1555 && 238 >= y && y >= 0 ){//6
            x=1550;
            y=75;
        }
        else if(1600>= x && x >= 1555 && 475 >= y && y >= 238 ){//7
            x=1550;
            y=400;
        }
        else if(46>= x && x >= 0 && 475 >= y && y >= 238 ){//8
            x=50;
            y=400;
        }
        else if(430>= x && x >= 46 && 378 >= y && y >= 238 ){//9
            y=400;
        }
        else if(430>= x && x >= 0 && 238 >= y && y >= 0 ){//10
            x=450;
            y=75;
        }

        res[0]=x;
        res[1]=y;

        return res;

    }
}
