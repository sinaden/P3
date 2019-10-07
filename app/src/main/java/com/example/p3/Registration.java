package com.example.p3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class Registration extends AppCompatActivity {

    private SharedPreferences pref;

    private TextInputLayout mNicknameLayout;

    public static String NICKNAME;

    @Override
    protected void onStart(){
        super.onStart();
        pref = Registration.this.getSharedPreferences("User", Context.MODE_PRIVATE);
        checkIfRegistered(pref);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNicknameLayout = findViewById(R.id.nickname_layout);
    }


    private void checkIfRegistered(SharedPreferences pref){
        if (pref.getString("Nickname", null)!=null){
            NICKNAME = pref.getString("Nickname", null);
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
        }
    }

    public void register(View view){
        String nickname = mNicknameLayout.getEditText().getText().toString();
        if (nickname.isEmpty()){
            mNicknameLayout.setError("Please enter your nickname");
        }
        else if (nickname.length()<3 || nickname.length()>16){
            mNicknameLayout.setError("Nickname has to be 3-16 characters long.");
        }
        else{
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Nickname", nickname);
            editor.apply();
            NICKNAME = nickname;
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
        }
    }
}
