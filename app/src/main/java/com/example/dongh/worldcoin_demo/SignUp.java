package com.example.dongh.worldcoin_demo;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    private EditText signup_email, signup_pw, signup_pw2;
    private TextView email_check, pw_check, pw_check2;
    private Button signup_phone_btn;
    private ImageView signup_btn;
    private FirebaseAuth mAuth;// firebase 공유를 위한 객체 인스턴스
    private boolean id_same_check = false; // 아이디 중복 체크
    private boolean pw_same_check = false; // 비밀번호 확인 체크
    private boolean pw2_same_check = false; // 비밀번호 확인 체크
    private String email, pwd, pwd_same;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup_email = findViewById(R.id.signup_email);
        email_check = (TextView) findViewById(R.id.signup_email_check);
        signup_pw = findViewById(R.id.signup_email_pw);
        pw_check = (TextView) findViewById(R.id.signup_pw_check);
        signup_pw2 = findViewById(R.id.signup_email_pw2);
        pw_check2 = (TextView) findViewById(R.id.signup_pw_check2);
        signup_btn = findViewById(R.id.signup_btn);


        mAuth = FirebaseAuth.getInstance();


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = signup_email.getText().toString().trim();
                pwd = signup_pw2.getText().toString().trim();
                pwd_same = signup_pw2.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {  // 아이디 널 체크
                    email_check.setText("아이디를 입력해주세요.");
                    id_same_check = false;
                } else {
                    email_check.setText(null);
                    id_same_check = true;
                }

                if (TextUtils.isEmpty(pwd)) { // 비밀번호 널 체크
                    pw_check.setText("비밀번호 입력해주세요.");
                    pw_same_check = false;
                }

                if (signup_pw.getText().toString().length() < 8) { // 비밀번호 8자 이상 체크
                    pw_check.setText("8자 이상 입력해주세요.");
                    signup_pw.setText(null);
                    pw_same_check = false;
                } else {
                    pw_check.setText(null);
                    pw_same_check = true;
                    if (TextUtils.isEmpty(pwd_same))
                        pw_check2.setText("비밀번호를 확인해주세요");
                }

                if (!signup_pw2.getText().toString().equals(signup_pw.getText().toString())) { // 비밀번호 같음 유무
                    pw_check2.setText("비밀번호가 다릅니다");
                    pw_check2.setTextColor(Color.parseColor("#D0021B"));
                    signup_pw2.setText(null);
                    pw_same_check = false;
                } else {
                    pw_check2.setText(null);
                    pw_same_check = true;
                }

                if (!TextUtils.isEmpty(pwd) && TextUtils.isEmpty(pwd_same)) { // 비밀번호 확인 널 체크
                    pw_check.setText(null);
                    pw_check2.setText("비밀번호를 확인해주세요");
                    pw_same_check = false;
                } else if (!TextUtils.isEmpty(pwd_same) && TextUtils.isEmpty(pwd)) {
                    pw2_same_check = true;
                    pw_same_check = false;
                    pw_check2.setText(null);
                } else if (!TextUtils.isEmpty(pwd_same) && !TextUtils.isEmpty(pwd))
                    pw2_same_check = true;


                if (id_same_check && pw_same_check && pw2_same_check) {
                    mAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mDatabase = FirebaseDatabase.getInstance().getReference();
                                        String current_user_id = mAuth.getCurrentUser().getUid();
                                        User user = new User(email, 100,"000"); // 회원가입 후 페이 100원 지급
                                        mDatabase.child(current_user_id).setValue(user);

                                        Toast.makeText(SignUp.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUp.this, "아이디가 중복이거나 잘못된 이메일 형식입니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
