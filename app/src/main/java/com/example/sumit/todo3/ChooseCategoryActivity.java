package com.example.sumit.todo3;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class ChooseCategoryActivity extends AppCompatActivity {



    String [] Categories = {
            CategoryConstants.CATEGORY_BIRTHDAY
            ,CategoryConstants.CATEGORY_MEETING
            ,CategoryConstants.CATEGORY_SHOPPING
            ,CategoryConstants.CATEGORY_STUDY
            ,CategoryConstants.CATEGORY_GYM
            ,CategoryConstants.CATEGORY_OTHERS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.CircleMenu);
        circleMenu.setMainMenu(Color.parseColor("#e0b582"), R.mipmap.add,R.mipmap.remove_icon).
                addSubMenu(Color.parseColor("#fcf510"),R.mipmap.birthday_icon)
                .addSubMenu(Color.parseColor("#e12e08"),R.mipmap.meeting_menuicon)
                .addSubMenu(Color.parseColor("#43d603"),R. mipmap.shoping_icon)
                .addSubMenu(Color.parseColor("#f347e1"),R.mipmap.study_icon)
                .addSubMenu(Color.parseColor("#041efb"),R.mipmap.gym_icon)
                .addSubMenu(Color.parseColor("#9710e3"),R.mipmap.others_icon)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        //Toast.makeText(ChooseCategoryActivity.this,"Category" + Categories[i],Toast.LENGTH_SHORT).show();
                        Intent i = getIntent();
                        Intent inew = new Intent(ChooseCategoryActivity.this,ToDoAppEditPageActivity.class);
                        inew.putExtra(IntentConstants.ENTRY_CATEGORY,Categories[index]);
                        startActivityForResult(inew,MainActivity.NEW_ENTRY);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivity.NEW_ENTRY)
        {
            if(resultCode == RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
