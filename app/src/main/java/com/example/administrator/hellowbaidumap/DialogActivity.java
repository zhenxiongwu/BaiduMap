package com.example.administrator.hellowbaidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dailog);

        Intent intent = getIntent();
        final boolean ifAddFriend = intent.getBooleanExtra("setFriendsMode",true);//判断添加朋友还是敌人

        TextView textView_title = (TextView) findViewById(R.id.dialogTitle);
        textView_title.setTextColor(0xffff0000);

        String dialogTitle = "添加敌人";
        if(ifAddFriend){
            textView_title.setTextColor(0xff00ff00);
            dialogTitle = "添加好友";
        }
        ((TextView)findViewById(R.id.dialogTitle)).setText(dialogTitle);

        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);

        ImageButton closeDialogButton = (ImageButton) findViewById(R.id.closeDialogButton);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button makeSureButton = (Button) findViewById(R.id.makeSureButton);
        makeSureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editName.getText().toString();
                String phoneNum = editPhoneNumber.getText().toString();
                if ((!name.equals("")) && (!phoneNum.equals("")) ) {
                    boolean ifFNumExist = false;
                    for(SomeBody someBody:SomeBody.friends){
                        if(someBody.getPhoneNumber().equals(phoneNum)){//检查新填写的号码是否已存在
                            ifFNumExist = true;
                            break;
                        }
                    }
                    boolean ifENumExist = false;
                    if(!ifFNumExist) {
                        for (SomeBody someBody : SomeBody.enemies) {
                            if(someBody.getPhoneNumber().equals(phoneNum)) {
                                ifENumExist = true;
                                break;
                            }
                        }
                    }
                    if(ifFNumExist||ifENumExist){

                        if(ifFNumExist){
                            Toast.makeText(DialogActivity.this,"该号码已在好友列表",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(DialogActivity.this,"该号码已在敌人列表",Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        SomeBody someBody = new SomeBody(name, phoneNum,null);
                        if(ifAddFriend) {
                            SomeBody.friends.add(someBody);
                            /*添加后立刻保存数据*/
                            MainActivity.saveObject(DialogActivity.this, MainActivity.friendsDataFile);
                        } else {
                            SomeBody.enemies.add(someBody);
                            MainActivity.saveObject(DialogActivity.this, MainActivity.enemiesDatafile);
                        }
                        finish();
                    }
                }
                else{
                    Toast.makeText(DialogActivity.this,"请完整填写信息", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }
}
