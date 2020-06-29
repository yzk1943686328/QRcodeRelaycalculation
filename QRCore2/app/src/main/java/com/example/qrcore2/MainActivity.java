package com.example.qrcore2;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission(Manifest.permission.CAMERA)
    public void ApplySuccess(){
        scanqrcode();
    }


    @OnPermissionDenied(Manifest.permission.CAMERA)
    public void permissiondeny(){

        AlertDialog.Builder adb=new AlertDialog.Builder(this);
        adb.setTitle("提醒");
        adb.setMessage("您拒绝开启摄像头权限，这将导致您无法扫描二维码!");
        adb.show();

   }
    EditText inputbox;
    Button createbutton;
    ImageView qrimage;

    Button scanbutton;
    TextView scanresult;

    int REQUEST_CODE=1;

    //将字符串加密
    public String Encryption(String str){

        byte b[]=str.getBytes();

        for(int i=0;i<b.length;i++){
            b[i]-=11;
        }

        return new String(b);

    }

    //将字符串解密
    public String Decryption(String str){

        byte b[]=str.getBytes();

        for(int i=0;i<b.length;i++){
            b[i]+=11;
        }

        return new String(b);

    }

    //初始化各变量
    public void initviews(){
        inputbox=findViewById(R.id.inputbox);
        createbutton=findViewById(R.id.createbutton);
        qrimage=findViewById(R.id.qrimage);
        scanbutton=findViewById(R.id.scanbutton);
        scanresult=findViewById(R.id.scanresult);
    }


    //扫描二维码的方法
    public void scanqrcode(){
        Intent intent=new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE);
}

    //接收二维码识别的结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);

                //将字符串解密
                String destring=Decryption(content);

                scanresult.setText("扫描结果为：" + destring);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initviews();

        //生成二维码
        createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qrstring =inputbox.getText().toString();

                //首先将字符串加密然后再生成字符串
                String enstring=Encryption(qrstring);

                Bitmap bitmap= CodeCreator.createQRCode(enstring,400,400,null);
                qrimage.setImageBitmap(bitmap);

                Toast.makeText(getApplicationContext(),"二维码生成成功",Toast.LENGTH_SHORT).show();

                //二维码生成之后清空输入框和扫描别人的二维码得到的结果，防止被别人看到
                inputbox.setText("");
                scanresult.setText("");

            }
        });


        //扫描二维码
        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断Android版本，如果版本小于6.0直接调用权限，如果版本大于或等于6.0需要动态申请权限
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
                    scanqrcode();
                }else{
                    MainActivityPermissionsDispatcher.ApplySuccessWithCheck(MainActivity.this);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}