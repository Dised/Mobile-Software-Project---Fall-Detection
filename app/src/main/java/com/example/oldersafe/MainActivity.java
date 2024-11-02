package com.example.oldersafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.oldersafe.fragment.ContactFragment;
import com.example.oldersafe.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FragmentTransaction fragmentTransaction;
    private TextView txt_home;
    private TextView txt_contact;
    private FrameLayout ly_content;
    private FragmentManager fManager;
    HomeFragment managerHomeFragment;
    ContactFragment managerAccountFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        txt_home = findViewById(R.id.home);
        txt_contact =findViewById(R.id.contact);
        ly_content = findViewById(R.id.ly_content);

        txt_home.setOnClickListener(this);
        txt_contact.setOnClickListener(this);

        fManager = getSupportFragmentManager();
        fragmentTransaction = fManager.beginTransaction();
        if(managerHomeFragment==null){
            managerHomeFragment = new HomeFragment();
            fragmentTransaction.add(R.id.ly_content,managerHomeFragment);
        }
        fragmentTransaction.show(managerHomeFragment);
        fragmentTransaction.commit();
    }

    //setSelected
    private void setSelected(){
        txt_home.setSelected(false);
        txt_contact.setSelected(false);
    }

    //hideAllFragment
    private void hideAllFragment(FragmentTransaction transaction){
        if(managerHomeFragment!=null) transaction.hide(managerHomeFragment);
        if(managerAccountFragment!=null) transaction.hide(managerAccountFragment);
    }

    @Override
    public void onClick(View view) {

        fragmentTransaction = fManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (view.getId()){
            case R.id.home:
                setSelected();
                txt_home.setSelected(true);
                if(managerHomeFragment==null){
                    managerHomeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.ly_content,managerHomeFragment);
                }
                else{
                    fragmentTransaction.show(managerHomeFragment);
                }
                txt_home.setBackgroundColor(getResources().getColor(R.color.blue_grey));
                txt_contact.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case  R.id.contact:
                setSelected();
                txt_contact.setSelected(true);
                if(managerAccountFragment==null){
                    managerAccountFragment = new ContactFragment();
                    fragmentTransaction.add(R.id.ly_content,managerAccountFragment);
                }
                else{
                    fragmentTransaction.show(managerAccountFragment);
                }
                txt_home.setBackgroundColor(getResources().getColor(R.color.blue));
                txt_contact.setBackgroundColor(getResources().getColor(R.color.blue_grey));
                break;
        }
        fragmentTransaction.commit();

    }
}
