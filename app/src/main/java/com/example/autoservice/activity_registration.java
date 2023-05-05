package com.example.autoservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.PasswordAuthentication;
import java.util.HashMap;

public class activity_registration extends AppCompatActivity {

    private Button btnBack, btnAuth, btnReg;
    private EditText etNumberInput, etPassInput, etPodPassInput;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnBack = (Button) findViewById(R.id.auth_btnBack);
        btnAuth = (Button) findViewById(R.id.reg_btnAuth);
        btnReg = (Button) findViewById(R.id.reg_btnRegister);
        etNumberInput = (EditText) findViewById(R.id.reg_phone_input);
        etPassInput = (EditText) findViewById(R.id.reg_password_input);
        etPodPassInput = (EditText) findViewById(R.id.reg_podPassword_input);
        loadingBar = new ProgressDialog(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack = new Intent(activity_registration.this, MainActivity.class);
                startActivity(intentBack);
            }
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAuth = new Intent(activity_registration.this, activity_authorization.class);
                startActivity(intentAuth);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String number = etNumberInput.getText().toString();
        String password = etPassInput.getText().toString();
        String podPassword = etPodPassInput.getText().toString();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(password) || TextUtils.isEmpty(podPassword))
        {
            Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
        }
        else if(!(password.equals(podPassword))){
            Toast.makeText(this, "Пароли не совпадают\nПовторите ввод!", Toast.LENGTH_SHORT).show();
            etPodPassInput.setText("");
            etPassInput.setText("");
        }
        else
        {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Подождите!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhone(number, password);
        }
    }

    private void ValidatePhone(String number, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(number).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("Number",number);
                    userDataMap.put("Password",password);

                    RootRef.child("Users").child(number).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(activity_registration.this, "Регистраци прошла успешно!\nМожете войти", Toast.LENGTH_SHORT).show();
                                
                                Intent intentAuth = new Intent(activity_registration.this, activity_authorization.class);
                                startActivity(intentAuth);
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(activity_registration.this, "АШИБСЯ ТЫ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(activity_registration.this, "Номер "+ number +" уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intentAuth = new Intent(activity_registration.this, activity_authorization.class);
                    startActivity(intentAuth);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

