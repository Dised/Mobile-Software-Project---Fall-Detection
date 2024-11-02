package com.example.oldersafe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oldersafe.MainActivity;
import com.example.oldersafe.R;
import com.example.oldersafe.bean.UserBaseInfo;
import com.example.oldersafe.config.Constants;
import com.example.oldersafe.config.SharedPreferencesUtils;
import com.example.oldersafe.database.DBDao;

import java.util.ArrayList;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText name;
    EditText psw;
    TextView tvData;
    RadioButton rbOld;
    RadioButton rbCon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkPermission_();

        
        name = findViewById(R.id.account);
        psw = findViewById(R.id.psw);
        tvData = findViewById(R.id.data);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        rbOld = findViewById(R.id.type_old);
        rbCon = findViewById(R.id.type_con);
        rbOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbOld.setChecked(true);
                rbCon.setChecked(false);
            }
        });
        rbCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbOld.setChecked(false);
                rbCon.setChecked(true);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(name.getText().toString())){
                    Toast.makeText(LoginActivity.this,"account is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                if("".equals(psw.getText().toString())){
                    Toast.makeText(LoginActivity.this,"password is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                judgeLogin();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        tvData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBDao dao=new DBDao(getApplicationContext());
                dao.open();
                ArrayList<Map<String, Object>> contactData = dao.getAllData("", "2", "2");
                if(contactData != null && contactData.size()>0) {

                }
                dao.close();
            }
        });
    }

    public void judgeLogin(){
        DBDao dao=new DBDao(getApplicationContext());
        dao.open();
        UserBaseInfo userBaseInfo=dao.Login(name.getText().toString(),psw.getText().toString(),"1");
        dao.close();
        if(userBaseInfo == null ||userBaseInfo.getInfoId()==null || userBaseInfo.getInfoId().equals("")){
            Toast.makeText(LoginActivity.this,"login fail",Toast.LENGTH_SHORT).show();
            return;
        }else{
            SharedPreferencesUtils.setParam(LoginActivity.this, Constants.User_ID,userBaseInfo.getInfoId());
            SharedPreferencesUtils.setParam(LoginActivity.this, Constants.User_Name,userBaseInfo.getUserName());
            SharedPreferencesUtils.setParam(LoginActivity.this, Constants.User_Type,userBaseInfo.getUserType());
            SharedPreferencesUtils.setParam(LoginActivity.this, Constants.Phone,userBaseInfo.getPhone());
            SharedPreferencesUtils.setParam(LoginActivity.this, Constants.State,userBaseInfo.getState());
            if(userBaseInfo.getUserType().equals("older")){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }else{
                //if the role is rescue worker, load the corresponding page
                startActivity(new Intent(LoginActivity.this, Main2Activity.class));
            }
            finish();
        }
    }

    private void openDialog() {
        new AlertDialog.Builder(this)
                .setTitle("please choose")
                .setPositiveButton("Elderly", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginActivity.this,RegisterActivity.class).putExtra("type","older"));
                    }
                })
                .setNegativeButton("Rescue Worker", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LoginActivity.this,RegisterActivity.class).putExtra("type","contact"));
                    }
                }).show();
    }

    public void checkPermission_(){
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,1);

        }
    }
}
