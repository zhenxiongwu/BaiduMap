package com.example.administrator.hellowbaidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsActivity extends Activity {
    //控件相关
    RelativeLayout F_E_title_layout;
    ListView friendsListView;
    Button addButton;
    Button editButton;
    Button backMapButton;
    Button F_E_Button;

    MyArrayAdapter myArrayAdapter;

    boolean setFriendsMode;//用来判断显示朋友列表还是敌人列表

    boolean showDeleteButton = false;//ListView中每个Item中的删除图标默认为不显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Intent intent = getIntent();
        /*有intent的携带信息判断是显示朋友列表还是敌人列表*/
        setFriendsMode = intent.getBooleanExtra("setFriendsMode", true);
        F_E_title_layout = (RelativeLayout) findViewById(R.id.layout_F_E_title);

        addButton = (Button) findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, DialogActivity.class);
                intent.putExtra("setFriendsMode", setFriendsMode);//告诉DialogActivity添加朋友还是添加敌人
                startActivity(intent);
            }
        });

        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*进入非编辑状态进入编辑状态*/
                if (!showDeleteButton) {
                    if(setFriendsMode&&SomeBody.friends.size()!=0){//显示朋友并且列表不为空
                        editButton.setBackgroundResource(R.drawable.button_edit_friends_down);
                        addButton.setVisibility(View.GONE);//隐藏添加按钮，使无法添加朋友
                        showDeleteButton = true;
                    }
                    else if(!setFriendsMode&&SomeBody.enemies.size()!=0) {//显示敌人并且列表不为空
                        editButton.setBackgroundResource(R.drawable.button_edit_enemies_down);
                        addButton.setVisibility(View.GONE);
                        showDeleteButton = true;
                    }
                }
                    /*由退出编辑状态*/
                else {
                    if(setFriendsMode)
                        editButton.setBackgroundResource(R.drawable.button_edit_friends_up);
                    else
                        editButton.setBackgroundResource(R.drawable.button_edit_enemies_up);
                    addButton.setVisibility(View.VISIBLE);//使添加按钮可见，允许添加朋友或敌人
                    showDeleteButton = false;
                }
                myArrayAdapter.notifyDataSetChanged();
            }
        });

        backMapButton = (Button) findViewById(R.id.backMapButton);
        backMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        F_E_Button = (Button) findViewById(R.id.button_F_E);
        F_E_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFriendsMode=!setFriendsMode;
                initView();
            }
        });

        friendsListView = (ListView) findViewById(R.id.friendsListView);

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SomeBody someBody;
                if(setFriendsMode)
                    someBody = SomeBody.friends.get(position);
                else
                    someBody = SomeBody.enemies.get(position);
                String friendInfo = "\t\t姓名：\t\t\t" + someBody.getName() + "\n\n"
                        + "\t\t号码：\t\t\t" + someBody.getPhoneNumber() + "\n\n";

                if(someBody.getLatLng()!=null){
                    friendInfo+= "\t\t纬度：\t\t\t" + someBody.getLatLng().latitude + "\n\n"
                                    + "\t\t经度：\t\t\t" + someBody.getLatLng().longitude + "\n";
                }else{
                    friendInfo+="\t\t纬度：\t\t\t未知\n\n"
                            + "\t\t经度：\t\t\t未知\n";
                }
                String title;
                if(setFriendsMode)title = "好友详情";
                else title = "敌人详情";
                new AlertDialog.Builder(FriendsActivity.this)
                        .setTitle(title)
                        .setMessage(friendInfo)
                        .show();
            }
        });

        initView();

    }

    /*判断显示朋友还是敌人来初始化界面*/
    protected void initView(){
        addButton.setVisibility(View.VISIBLE);
        showDeleteButton = false;
        if(setFriendsMode){
            F_E_title_layout.setBackgroundResource(R.drawable.background_friends_list_title);
            addButton.setBackgroundResource(R.drawable.selector_add_friends);
            editButton.setBackgroundResource(R.drawable.button_edit_friends_up);
            F_E_Button.setBackgroundResource(R.drawable.selecor_button_enemies);
            myArrayAdapter = new MyArrayAdapter(SomeBody.friends);
        }else{
            F_E_title_layout.setBackgroundResource(R.drawable.background_enemies_list_title);
            addButton.setBackgroundResource(R.drawable.selector_add_enemies);
            editButton.setBackgroundResource(R.drawable.button_edit_enemies_up);
            F_E_Button.setBackgroundResource(R.drawable.selector_button_friends);
            myArrayAdapter = new MyArrayAdapter(SomeBody.enemies);
        }

        friendsListView.setAdapter(myArrayAdapter);
    }

    class MyArrayAdapter extends BaseAdapter {

        ArrayList<SomeBody> everyone;

        MyArrayAdapter(ArrayList<SomeBody> everyone){
            this.everyone = everyone;
        }

        @Override
        public int getCount() {
            return everyone.size();
        }

        @Override
        public Object getItem(int position) {
            return everyone.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(FriendsActivity.this, R.layout.list_item, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.listItemTextView);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_friends_icon);
            if(setFriendsMode) {
                textView.setTextColor(0xff00ff00);
                imageView.setImageResource(R.drawable.icon_friends);//ListView的每个Item左边的朋友图标
            }
            else{
                textView.setTextColor(0xffff0000);
                imageView.setImageResource(R.drawable.icon_enemies);//敌人图标
            }
            SomeBody someBody = everyone.get(position);
            String text = someBody.getName() + "(" + someBody.getPhoneNumber() + ")";
            textView.setText(text);
            Button delete = (Button) (convertView.findViewById(R.id.deleteItemButton));
            if (showDeleteButton) {
                delete.setVisibility(View.VISIBLE);//使删除按钮可见
                imageView.setVisibility(View.GONE);//隐藏图标
                textView.setPadding(40,0,0,0);//减少左边的留空，使看上去有左移的效果
            } else {
                delete.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                textView.setPadding(120,0,0,0);
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    everyone.remove(position);
                    myArrayAdapter.notifyDataSetChanged();
                    if(setFriendsMode)//删除朋友，删除后立刻保存数据
                        MainActivity.saveObject(FriendsActivity.this, MainActivity.friendsDataFile);
                    else//删除敌人
                        MainActivity.saveObject(FriendsActivity.this, MainActivity.enemiesDatafile);
                    if(everyone.size()==0){//删除后，列表为空
                        initView();//初始化界面
                    }
                }
            });
            return convertView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
