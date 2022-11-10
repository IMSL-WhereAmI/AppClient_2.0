package com.ng.anthony.mininavigationdrawer.Sensor;

public class DataConstrain {
    static boolean mode;


    public static void changeConstrainMode(){
        mode = !mode;
    }

    public static boolean getConstrainMode(){
        return mode;
    }

    public static void setConstrainMode(boolean flag){
        mode = flag;
    }


    public static int getMinIndex(float[] arr) {
        int minIndex=0;
        for(int i =0;i<arr.length-1;i++){
            if(arr[minIndex]>arr[i+1]){
                minIndex=i+1;
            }
        }
        return minIndex;
    }

    public static float[] BoxConstrain0(float x, float y){
//        float azimuth = AzimuthData.getAzimuth();
//        if(0 < azimuth && azimuth <= 45){
//            azimuth = 0;
//        }else if(315 < azimuth && azimuth <= 360){
//            azimuth = 0;
//        }else if(45 < azimuth && azimuth <= 135){
//            azimuth = 90;
//        }else if(135 < azimuth && azimuth <= 225){
//            azimuth = 180;
//        }else if(225 < azimuth && azimuth <= 315){
//            azimuth = 270;
//        }

        float[] res = new float[2];

        if (x == 0 && y==0){
            res[0]=0;
            res[1]=0;
            return res;
        }

        int flag = 0;
        if(1295>= x && x >= 623 && 385 >= y && y >= 90 ){//1

            float[] arr = {Math.abs(y-75), Math.abs(y-400), Math.abs(x-610), Math.abs(x-1310)};
            flag = getMinIndex(arr);
            if(flag == 0) y=75;
            else if(flag == 1) y=400;
            else if(flag == 2) x=610;
            else if(flag == 3) x=1310;
        }
        else if(587>= x && x >= 435 && 385 >= y && y >= 90 ){//2

            float[] arr = {Math.abs(y-75), Math.abs(y-400),1600, Math.abs(x-610)};
            flag = getMinIndex(arr);
            if(flag == 0) y=75;
            else if(flag == 1) y=400;
            else if(flag == 3) x=610;
        }
        else if(1550>= x && x >= 1330 && 385 >= y && y >= 90 ){//3

            float[] arr = {Math.abs(y-75), Math.abs(y-400),Math.abs(x-1310), 1600};
            flag = getMinIndex(arr);
            if(flag == 0) y=75;
            else if(flag == 1) y=400;
            else if(flag == 2) x=1310;
        }
        else if(1550>= x && x >= 435 && 57 >= y && y >= 0 ){//4
            y=75;
        }
        else if(1160>= x && x >= 45 && 600 >= y && y >= 415 ){//5
            y=400;
        }
        else if(1600>= x && x >= 1550 && 238 >= y && y >= 0 ){//6
            x=1550;
            y=75;
        }
        else if(1600>= x && x >= 1555 && 600 >= y && y >= 238 ){//7
            x=1550;
            y=400;
        }
        else if(45>= x && x >= 0 && 600 >= y && y >= 238 ){//8
            x=50;
            y=400;
        }
        else if(435>= x && x >= 45 && 380 >= y && y >= 238 ){//9
            y=400;
        }
        else if(435>= x && x >= 0 && 238 >= y && y >= 0 ){//10
            x=450;
            y=75;
        }
        //教师区域
        else if(1190>= x && x >= 1160 && 540 >= y && y >= 460 ){//1
            float[] arr = {Math.abs(x-1206), Math.abs(y-453)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1206;
            else if(flag == 1) y=453;
        }
        else if(1190>= x && x >= 1160 && 600 >= y && y >= 540 ){//2
            x=1206;
            y=530;
        }
        else if(1390>= x && x >= 1190 && 600 >= y && y >= 540 ){//3
            y=530;
        }
        else if(1215>= x && x >= 1180 && 445 >= y && y >= 415 ){//4
            float[] arr = {Math.abs(x-1170), Math.abs(y-400),Math.abs(y-453)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1170;
            else if(flag == 1) y=400;
            else if(flag == 2) y=453;
        }
        else if(1370>= x && x >= 1215 && 445 >= y && y >= 415 ){//5
            float[] arr = {Math.abs(x-1380), Math.abs(y-400)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1380;
            else if(flag == 1) y=400;
        }
        else if(1370>= x && x >= 1215 && 520 >= y && y >= 445 ){//6
            float[] arr = {Math.abs(x-1203), Math.abs(x-1380), Math.abs(y-530)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1203;
            else if(flag == 1) x=1380;
            else if(flag == 2) y=530;
        }
        else if(1440>= x && x >= 1390 && 460 >= y && y >= 415 ){//7
            float[] arr = {Math.abs(x-1380), Math.abs(x-1450), Math.abs(y-400)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1380;
            else if(flag == 1) x=1450;
            else if(flag == 2) y=400;
        }
        else if(1440>= x && x >= 1390 && 540 >= y && y >= 460 ){//8
            x=1380;
        }
        else if(1486>= x && x >= 1440 && 578 >= y && y >= 460 ){//9
            float[] arr = {Math.abs(x-1495), Math.abs(y-453)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1495;
            else if(flag == 1) y=453;
        }
        else if(1550>= x && x >= 1486 && 600 >= y && y >= 578 ){//10
            x=1495;
            y=578;
        }
        else if(1660>= x && x >= 1505 && 578 >= y && y >= 460 ){//11
            float[] arr = {Math.abs(x-1495), Math.abs(y-453)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1495;
            else if(flag == 1) y=453;
        }
        else if(1550>= x && x >= 1460 && 445 >= y && y >= 415 ){//12
            float[] arr = {Math.abs(x-1450), Math.abs(y-453)};
            flag = getMinIndex(arr);
            if(flag == 0) x=1450;
            else if(flag == 1) y=453;
        }
        else if(1486>= x && x >= 1390 && 600 >= y && y >= 540 ){//13
            x=1380;
            y=530;
        }
        else if(1600>= x && x >= 1550 && 600 >= y && y >= 415 ){//14
            x=1550;
            y=453;
        }

        res[0]=x;
        res[1]=y;



        return res;

    }


    public static float[] BoxConstrain(float x, float y){
        float azimuth = AzimuthData.getAzimuth();
        if(0 < azimuth && azimuth <= 45){
            azimuth = 0;
        }else if(315 < azimuth && azimuth <= 360){
            azimuth = 0;
        }else if(45 < azimuth && azimuth <= 135){
            azimuth = 90;
        }else if(135 < azimuth && azimuth <= 225){
            azimuth = 180;
        }else if(225 < azimuth && azimuth <= 315){
            azimuth = 270;
        }

        float[] res = new float[2];

        if (x == 0 && y==0){
            res[0]=0;
            res[1]=0;
            return res;
        }

        int flag = 0;
        if(1295>= x && x >= 623 && 385 >= y && y >= 90 ){//1

            if(azimuth == 90 || azimuth == 270){
                float[] arr = {Math.abs(x-610), Math.abs(x-1310)};
                flag = getMinIndex(arr);
                if(flag == 0) x=610;
                else if(flag == 1) x=1310;
            }else if(azimuth == 0 || azimuth == 180){
                float[] arr = {Math.abs(y-75), Math.abs(y-400)};
                flag = getMinIndex(arr);
                if(flag == 0) y=75;
                else if(flag == 1) y=400;
            }

//            float[] arr = {Math.abs(y-75), Math.abs(y-400), Math.abs(x-610), Math.abs(x-1310)};
//            flag = getMinIndex(arr);
//            if(flag == 0) y=75;
//            else if(flag == 1) y=400;
//            else if(flag == 2) x=610;
//            else if(flag == 3) x=1310;
        }
        else if(587>= x && x >= 435 && 385 >= y && y >= 90 ){//2
            if(azimuth == 90 || azimuth == 270){
                x=610;
            }else if(azimuth == 0 || azimuth == 180){
                float[] arr = {Math.abs(y-75), Math.abs(y-400)};
                flag = getMinIndex(arr);
                if(flag == 0) y=75;
                else if(flag == 1) y=400;
            }

//            float[] arr = {Math.abs(y-75), Math.abs(y-400),1600, Math.abs(x-610)};
//            flag = getMinIndex(arr);
//            if(flag == 0) y=75;
//            else if(flag == 1) y=400;
//            else if(flag == 3) x=610;
        }
        else if(1550>= x && x >= 1330 && 385 >= y && y >= 90 ){//3
            if(azimuth == 90 || azimuth == 270){
                x=1310;
            }else if(azimuth == 0 || azimuth == 180){
                float[] arr = {Math.abs(y-75), Math.abs(y-400)};
                flag = getMinIndex(arr);
                if(flag == 0) y=75;
                else if(flag == 1) y=400;
            }

//            float[] arr = {Math.abs(y-75), Math.abs(y-400),Math.abs(x-1310), 1600};
//            flag = getMinIndex(arr);
//            if(flag == 0) y=75;
//            else if(flag == 1) y=400;
//            else if(flag == 2) x=1310;
        }
        else if(1550>= x && x >= 435 && 57 >= y && y >= 0 ){//4
            y=75;
        }
        else if(1160>= x && x >= 45 && 600 >= y && y >= 415 ){//5
            y=400;
        }
        else if(1600>= x && x >= 1550 && 238 >= y && y >= 0 ){//6
            x=1550;
            y=75;
        }
        else if(1600>= x && x >= 1555 && 600 >= y && y >= 238 ){//7
            x=1550;
            y=400;
        }
        else if(45>= x && x >= 0 && 600 >= y && y >= 238 ){//8
            x=50;
            y=400;
        }
        else if(435>= x && x >= 45 && 380 >= y && y >= 238 ){//9
            y=400;
        }
        else if(435>= x && x >= 0 && 238 >= y && y >= 0 ){//10
            x=450;
            y=75;
        }

        res[0]=x;
        res[1]=y;



        return res;

    }
}
