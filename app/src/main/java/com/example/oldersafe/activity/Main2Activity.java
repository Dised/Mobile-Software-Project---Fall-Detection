package com.example.oldersafe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.oldersafe.R;
import com.example.oldersafe.config.Constants;
import com.example.oldersafe.config.SharedPreferencesUtils;
import com.example.oldersafe.database.DBDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView = findViewById(R.id.rv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> itemMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
                phone = (String) itemMap.get("phone");
                latitude = (String) itemMap.get("latitude");
                longitude = (String) itemMap.get("longitude");
                openDialog();
            }
        });
        getDatas();
    }
    String phone;
    String path;
    String latitude;
    String longitude;
    private void openDialog() {
        new AlertDialog.Builder(this)
                .setTitle("please choose")
                .setPositiveButton("call phone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phone));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("open map", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        path = "google.streetview:cbll="+latitude+","+longitude;
                        Uri gmmIntentUri = Uri.parse(path);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                }).show();
    }
    public void getDatas(){
        DBDao dao=new DBDao(getApplicationContext());
        dao.open();
        String name = (String) SharedPreferencesUtils.getParam(Main2Activity.this, Constants.User_Name, "00");
        ArrayList<Map<String, Object>> contactData = dao.getHelpData(name, "2", "2");
        if(contactData != null && contactData.size()>0) {
            //define the data source
            String[] from = {"name", "phone","address"};
            //define layout control IDs
            int[] to = {R.id.name, R.id.phone,R.id.address};
            SimpleAdapter listItemAdapter = new SimpleAdapter(Main2Activity.this, contactData, R.layout.older_item, from, to);
            //add and view
            listView.setAdapter(listItemAdapter);
            //close the data
            dao.close();
        }
    }
}