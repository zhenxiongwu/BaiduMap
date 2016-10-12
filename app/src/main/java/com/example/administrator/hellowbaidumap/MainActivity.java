package com.example.administrator.hellowbaidumap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static double myLatitude;
    public static double myLongitude;

    // 定位相关
    LocationClient mLocClient;//定位服务的客户端,定位SDK的核心类
    public MyLocationListenner myListener = new MyLocationListenner();//自定义的位置事件监听类

    boolean isFirstLoc = true; // 是否首次定位

/*    //覆盖物相关
    BitmapDescriptor bd_friends , bd_enemies;//位图描述*/

    //控件相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    Button button_enemies;
    Button button_friends;
    Button button_refresh;
    Button button_get_friends_location;
    Button button_get_enemies_location;

    Object object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        object = getObject(this, friendsDataFile);//尝试读取好友数据
        if (object == null) {
            SomeBody.friends = new ArrayList<>();
        } else {
            SomeBody.friends = (ArrayList<SomeBody>) object;
        }

        object = getObject(this, enemiesDatafile);//尝试读取敌人数据
        if (object == null) {
            SomeBody.enemies = new ArrayList<>();
        } else {
            SomeBody.enemies = (ArrayList<SomeBody>) object;
        }


        button_refresh = (Button) findViewById(R.id.button_refresh);
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendsLocation();
            }
        });

        button_get_friends_location = (Button) findViewById(R.id.button_get_friends_location);
        button_get_friends_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                for (SomeBody someBody : SomeBody.friends) {
                    smsManager.sendTextMessage(someBody.getPhoneNumber(), null,
                            "where are you", null, null);
                }
            }
        });

        button_get_enemies_location = (Button) findViewById(R.id.button_get_enemies_location);
        button_get_enemies_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                for (SomeBody someBody : SomeBody.enemies) {
                    smsManager.sendTextMessage(someBody.getPhoneNumber(), null,
                            "where are you", null, null);
                }
            }
        });

        button_friends = (Button) findViewById(R.id.button_friends);
        button_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                intent.putExtra("setFriendsMode", true);
                startActivity(intent);
            }
        });

        button_enemies = (Button) findViewById(R.id.button_enemies);
        button_enemies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                intent.putExtra("setFriendsMode", false);
                startActivity(intent);
            }
        });

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);//注册位置事件监听器
        LocationClientOption option = new LocationClientOption();//LocationClientOption类，该类用来设置定位SDK的定位方式
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型,默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocClient.setLocOption(option);//绑定包含各种定位参数的LocationClientOption实例
        mLocClient.start();//启动定位SDK

        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));


    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            // map view 销毁后不在处理新接收的位置
            if (mMapView == null) {
                return;
            }

            //构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    public void showFriendsLocation() {
        mBaiduMap.clear();//清除标志物
        if (SomeBody.friends.size() != 0)
            setMarks(ShowWhat.friends);
        if (SomeBody.enemies.size() != 0)
            setMarks(ShowWhat.enemies);
        /*if(SomeBody.friends.size()!=0){
            for(SomeBody someBody:SomeBody.friends){
                LatLng ll = someBody.getLatLng();
                if (ll != null) {
                    OverlayOptions oo = new MarkerOptions().position(ll).icon(bd_friends)
                            .zIndex(18).draggable(true);
                    mBaiduMap.addOverlay(oo);

                    //构建文字Option对象，用于在地图上添加文字
                    OverlayOptions textOption = new TextOptions()
                            .zIndex(19)
                            .bgColor(0xAA000000)
                            .fontSize(40)
                            .fontColor(0xFF00FF00)
                            .text(someBody.getName())
                            .position(ll);
                    //在地图上添加该文字对象并显示
                    mBaiduMap.addOverlay(textOption);
                }
            }
        }
        if(SomeBody.enemies.size()!=0){
            for(SomeBody someBody:SomeBody.enemies){
                LatLng ll = someBody.getLatLng();
                if(ll!=null){
                    OverlayOptions oo = new MarkerOptions().position(ll).icon(bd_enemies)
                            .zIndex(18).draggable(true);
                    mBaiduMap.addOverlay(oo);

                    //构建文字Option对象，用于在地图上添加文字
                    OverlayOptions textOption = new TextOptions()
                            .zIndex(19)
                            .bgColor(0xAA000000)
                            .fontSize(40)
                            .fontColor(0xFFFF0000)
                            .text(someBody.getName())
                            .position(ll);
                    //在地图上添加该文字对象并显示
                    mBaiduMap.addOverlay(textOption);
                }
            }
        }*/
    }

    protected enum ShowWhat {friends, enemies}

    protected void setMarks(ShowWhat showWhat) {
        ArrayList<SomeBody> arrayList;
        BitmapDescriptor bd;
        int color;
/*        bd_friends = BitmapDescriptorFactory.fromResource(R.drawable.icon_friend);
        bd_enemies = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);//创建位图描述对象*/
        if (showWhat == ShowWhat.friends) {
            arrayList = SomeBody.friends;
            bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_friend);
            color = 0xff00ff00;
        } else {
            arrayList = SomeBody.enemies;
            bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
            color = 0xffff0000;
        }
        if (arrayList.size() != 0) {
            LatLng myLatLng = new LatLng(myLatitude, myLongitude);
            for (SomeBody someBody : arrayList) {
                LatLng ll = someBody.getLatLng();
                if (ll != null) {
                    OverlayOptions oo = new MarkerOptions().position(ll).icon(bd)
                            .zIndex(18).draggable(true);
                    mBaiduMap.addOverlay(oo);

                    //构建文字Option对象，用于在地图上添加文字
                    OverlayOptions textOption = new TextOptions()
                            .zIndex(19)
                            .bgColor(0xAA000000)
                            .fontSize(40)
                            .fontColor(color)
                            .text(someBody.getName())
                            .position(ll);
                    //在地图上添加该文字对象并显示
                    mBaiduMap.addOverlay(textOption);

                    List<LatLng> points = new ArrayList<>();
                    points.add(myLatLng);
                    points.add(ll);

                    //构建分段颜色索引数组
                    List<Integer> colors = new ArrayList<>();
                    colors.add(color);
                    OverlayOptions ooPolyline = new PolylineOptions().width(5)
                            .colorsValues(colors).points(points);
                    mBaiduMap.addOverlay(ooPolyline);

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
//        saveObject(this,friendsDataFile);
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        //释放bitmap资源
        /*bd_friends.recycle();
        bd_enemies.recycle();*/
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        showFriendsLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        mBaiduMap.clear();//清除标志物
    }


    public static final String friendsDataFile = "friendsData.dat";
    public static final String enemiesDatafile = "enemiesData.dat";

    public static void saveObject(Context context, String name) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            if (name.equals(friendsDataFile))
                oos.writeObject(SomeBody.friends);//保存好友数据
            else
                oos.writeObject(SomeBody.enemies);//保存敌人数据
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object getObject(Context context, String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }
}
