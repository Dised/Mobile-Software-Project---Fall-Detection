package com.example.oldersafe.activity;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.oldersafe.R;
import java.util.ArrayList;

import android.widget.SimpleAdapter;
import com.example.oldersafe.database.DBDao;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    ListView listView;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView = findViewById(R.id.rv);

        getDatas();
    }


    public void getDatas(){
        DBDao dao = new DBDao(getApplicationContext());
        dao.open();

        // place holder
        String name = "Name";
        String type = "2";
        String flag = "2";

        ArrayList<Map<String, Object>> contactData = dao.getContactData(name, type, flag); // Fetch contact data with parameters
        if (contactData != null && !contactData.isEmpty()) {
            String[] from = {"name", "phone", "address"};
            int[] to = {R.id.name, R.id.phone, R.id.address};

            SimpleAdapter adapter = new SimpleAdapter(this, contactData, R.layout.older_item, from, to);
            listView.setAdapter(adapter);
        }

        dao.close();
    }
}
