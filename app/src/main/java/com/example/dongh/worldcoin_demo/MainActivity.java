package com.example.dongh.worldcoin_demo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    ImageView loginbtn;
    EditText emailtext,pwtext;
    TextView email_equal,pw_equal,sign_up;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    boolean id_check=false;
    boolean pwd_check=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginbtn = findViewById(R.id.login_btn);
        emailtext = findViewById(R.id.login_id); pwtext=findViewById(R.id.login_pw);
        email_equal = findViewById(R.id.id_equal); pw_equal=findViewById(R.id.pw_equal);
        sign_up = findViewById(R.id.sign_up_txt);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailtext.getText().toString().trim();
                String pwd = pwtext.getText().toString().trim();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(pwd) ) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    id_check=false;
                }
                if(!TextUtils.isEmpty(email) && TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    pwd_check=false;
                }

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
                    id_check=true;
                    pwd_check=true;
                }



                if(id_check && pwd_check) {
                    mAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(MainActivity.this, QRgenerate.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this, "존재하지 않는 아이디이거나 비밀번호 오류입니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,SignUp.class);
                startActivity(it);
            }
        });

    }


}
