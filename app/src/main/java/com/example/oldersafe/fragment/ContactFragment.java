package com.example.oldersafe.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.oldersafe.R;
import com.example.oldersafe.activity.AddContactActivity;
import com.example.oldersafe.config.Constants;
import com.example.oldersafe.config.SharedPreferencesUtils;
import com.example.oldersafe.database.DBDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ContactFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ContactFragment() {
        // Required empty public constructor
    }


    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDatas();
    }

    TextView add;
    ListView listView;
    private void initView() {
        add = getActivity().findViewById(R.id.add);
        listView = getActivity().findViewById(R.id.rv);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddContactActivity.class).putExtra("type","add"));
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> itemMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
                String info_id = (String) itemMap.get("info_id");
                String contact = (String) itemMap.get("contact");
                String contact_phone = (String) itemMap.get("contact_phone");
                startActivity(new Intent(getActivity(),AddContactActivity.class)
                        .putExtra("type","edit")
                        .putExtra("info_id",info_id)
                        .putExtra("contact",contact)
                        .putExtra("contact_phone",contact_phone));
            }
        });
    }

    public void getDatas(){
        DBDao dao=new DBDao(getContext());
        dao.open();
        String name = (String) SharedPreferencesUtils.getParam(getContext(), Constants.User_Name, "00");
        String User_Type = (String) SharedPreferencesUtils.getParam(getContext(), Constants.User_Type, "00");
        ArrayList<Map<String, Object>> contactData = dao.getContactData(name, User_Type, "2");
        if(contactData != null && contactData.size()>0) {
            //define the data source
            String[] from = {"contact", "contact_phone"};
            //define layout control ids
            int[] to = {R.id.name, R.id.phone};
            SimpleAdapter listItemAdapter = new SimpleAdapter(getContext(), contactData, R.layout.contact_item, from, to);
            //add and display
            listView.setAdapter(listItemAdapter);
            //close the database
            dao.close();
        }
    }
}
