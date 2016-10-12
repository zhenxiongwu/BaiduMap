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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsActivity extends Activity {
    //控件相关
    RelativeLayout F_E_title_layout;
    TextView F_E_title_textView;
    ListView friendsListView;
    ImageButton addFriendsButton;
    ImageButton editButton;
    Button backMapButton;
    Button F_E_Button;

    MyArrayAdapter myArrayAdapter;

    boolean setFriendsMode;

    boolean showDeleteButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Intent intent = getIntent();
        setFriendsMode = intent.getBooleanExtra("setFriendsMode", true);
        F_E_title_layout = (RelativeLayout) findViewById(R.id.layout_F_E_title);
        F_E_title_textView = (TextView) findViewById(R.id.textView_F_E_title);

        addFriendsButton = (ImageButton) findViewById(R.id.addFriendButton);
        addFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, DialogActivity.class);
                intent.putExtra("setFriendsMode", setFriendsMode);
                startActivity(intent);
            }
        });

        editButton = (ImageButton) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showDeleteButton) {
                    if((setFriendsMode&&SomeBody.friends.size()!=0)||
                            (!setFriendsMode&&SomeBody.enemies.size()!=0)) {
                        editButton.setImageResource(android.R.drawable.ic_menu_revert);
                        addFriendsButton.setEnabled(false);
                        showDeleteButton = true;
                    }
                } else {
                    editButton.setImageResource(android.R.drawable.ic_menu_edit);
                    addFriendsButton.setEnabled(true);
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
                editButton.setImageResource(android.R.drawable.ic_menu_edit);
                addFriendsButton.setEnabled(true);
                showDeleteButton = false;
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

  /*      arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item,R.id.listItemTextView,arrayList);
        friendsListView.setAdapter(arrayAdapter);*/
    }

    protected void initView(){
        if(setFriendsMode){
            F_E_Button.setText(R.string.enemies_list_title);
            F_E_Button.setTextColor(0xffff0000);
            F_E_title_textView.setText(R.string.friends_list_title);
            F_E_title_textView.setTextColor(0xff00ff00);
            myArrayAdapter = new MyArrayAdapter(SomeBody.friends);
        }else{
//            F_E_title_layout.setBackgroundResource(R.color.colorRBtnUp);
            F_E_Button.setText(R.string.friends_list_title);
            F_E_Button.setTextColor(0xff00ff00);
            F_E_title_textView.setText(R.string.enemies_list_title);
            F_E_title_textView.setTextColor(0xffff0000);
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
            if(setFriendsMode) {/*
                textView.setBackgroundResource(R.drawable.popup_left);*/
                textView.setTextColor(0xff00ff00);
                imageView.setImageResource(R.drawable.icon_friend);
            }
            else{/*
                textView.setBackgroundResource(R.drawable.enemies_left);*/
                textView.setTextColor(0xffff0000);
                imageView.setImageResource(R.drawable.icon_gcoding);
            }
            SomeBody someBody = everyone.get(position);
            String text = someBody.getName() + "(" + someBody.getPhoneNumber() + ")";
            textView.setText(text);
            Button delete = (Button) (convertView.findViewById(R.id.deleteItemButton));
            if (showDeleteButton) {
                delete.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                textView.setPadding(40,0,0,0);
            } else {
                delete.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                textView.setPadding(100,0,0,0);
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    everyone.remove(position);
                    myArrayAdapter.notifyDataSetChanged();
                    if(setFriendsMode)
                        MainActivity.saveObject(FriendsActivity.this, MainActivity.friendsDataFile);
                    else
                        MainActivity.saveObject(FriendsActivity.this, MainActivity.enemiesDatafile);
                    if(everyone.size()==0){
                        editButton.setImageResource(android.R.drawable.ic_menu_edit);
                        addFriendsButton.setEnabled(true);
                        showDeleteButton = false;
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
