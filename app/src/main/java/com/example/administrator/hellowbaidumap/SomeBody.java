package com.example.administrator.hellowbaidumap;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/7.
 */
public class SomeBody implements Serializable {
    public static ArrayList<SomeBody> friends = null;
    public static ArrayList<SomeBody> enemies = null;

    private String name;            //姓名
    private String phoneNumber;      //号码
    private MyLatLng myLatLng = null;//位置，经纬度

    public SomeBody(String name, String phoneNumber, LatLng ll) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        if (ll != null)
            myLatLng = new MyLatLng(ll);
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LatLng getLatLng() {
        if (myLatLng != null)
            return new LatLng(myLatLng.latitude, myLatLng.longitude);
        else
            return null;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setLatLng(LatLng latLng){
        if(latLng != null)
            myLatLng = new MyLatLng(latLng);
    }

    class MyLatLng implements Serializable {
        double latitude, longitude;

        public MyLatLng(LatLng latLng) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }
    }

}
