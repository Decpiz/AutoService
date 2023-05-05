package com.example.autoservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.autoservice.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_authorization extends AppCompatActivity {

    private Button btnBack, btnAuth;
    private EditText etNumberInput, etPassInput;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        btnBack = (Button) findViewById(R.id.auth_btnBack);
        btnAuth = (Button) findViewById(R.id.auth_btnAuth);
        etNumberInput = (EditText) findViewById(R.id.auth_phone_input);
        etPassInput = (EditText) findViewById(R.id.auth_password_input);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack = new Intent(activity_authorization.this, MainActivity.class);
                startActivity(intentBack);
            }
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AuthUser();
            }
        });
    }

    private void AuthUser()
    {
        String number = etNumberInput.getText().toString();
        String password = etPassInput.getText().toString();

        if (TextUtils.isEmpty(number)||TextUtils.isEmpty(password))
        {
            Toast.makeText(activity_authorization.this, "Введите данные!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(number, password);
        }
    }
    private void ValidateUser(String number, String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Users").child(number).exists())
                {
                    Users usersData = dataSnapshot.child("Users").child(number).getValue(Users.class);

                    if(usersData.getNumber().equals(number))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            loadingBar.dismiss();
                            Toast.makeText(activity_authorization.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(activity_authorization.this, "Введен неверный пароль\nПовторите попытку!", Toast.LENGTH_SHORT).show();

                            Intent intentAuth = new Intent(activity_authorization.this, activity_MainWindow.class);
                            startActivity(intentAuth);
                        }
                    }
                }
                else
                {
                    loadingBar.dismiss();
                    Toast.makeText(activity_authorization.this, "Аккаунт с номером "+number+" не существует", Toast.LENGTH_SHORT).show();
                    Intent intentReg = new Intent(activity_authorization.this, activity_registration.class);
                    startActivity(intentReg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}