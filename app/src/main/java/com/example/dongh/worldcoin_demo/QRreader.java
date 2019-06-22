package com.example.dongh.worldcoin_demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.HashMap;
import java.util.Map;

public class QRreader extends AppCompatActivity {

    IntentIntegrator qrScan;
    Button sign_out,coin_check;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    long mycoin_check; // 이 액티비티에서의 나의 코인 제어 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String current_user_id = mAuth.getCurrentUser().getUid();
        mDatabase.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().toString().equals("coin"))
                        mycoin_check = (long)snapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sign_out=findViewById(R.id.sign_out_btn);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        coin_check=findViewById(R.id.coin_check_btn);
        coin_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"현재 코인 : "+mycoin_check+" coin",Toast.LENGTH_SHORT).show();
            }

        });

        // 위험 권한 부여 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "SMS 전송 권한 있음.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "SMS 전송 권한 없음.", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "SMS 권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1);
            }
        }


        findViewById(R.id.btn_start_qrcode_reader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mycoin_check - 35 > 0)
                    startQRCode();
                else
                    Toast.makeText(getApplicationContext(),"코드 부족",Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void startQRCode() {
        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("QR코드 또는 바코드를 사각형 안에 비춰주세요.");
        qrScan.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {  // QR코드 스캔 성공
                mDatabase.getDatabase().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String current_user_id = mAuth.getCurrentUser().getUid();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.getKey().equals(current_user_id)){
                                Map<String, Object> coinupdate = new HashMap<String, Object>();
                                mycoin_check = (long) snapshot.child("coin").getValue()-35;
                                coinupdate.put("coin", mycoin_check); // 코인 소비
                                snapshot.getRef().updateChildren(coinupdate);
                                break;
                            }
                        }
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("code").getValue().equals(result.getContents())) {
                                Map<String, Object> coinsend = new HashMap<String, Object>();
                                coinsend.put("coin", (long) snapshot.child("coin").getValue() + 35); // 코인 발행
                                snapshot.getRef().updateChildren(coinsend);
                                break;
                            }
                        }

                        /*국외번호 국내번호로 변경 후 문자 보내기*/
                        /*Object data = dataSnapshot.getValue();
                        SmsManager smsManager = SmsManager.getDefault();

                        String user_phone = "01"+phone.substring(4);

                        smsManager.sendTextMessage(user_phone,null, result.getContents(),null,null);*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS 권한을 사용자가 승인함.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "SMS 권한 거부됨.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
