package com.persnickyte.spinandwin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class bank_edit extends AppCompatActivity {

    EditText ed_rout, ed_accou, ed_conf;
    Button ed_but;
    SharedPreferences sharedPreferences;
    String routing, account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_edit);

        ed_rout = findViewById(R.id.ed_rout);
        ed_accou = findViewById(R.id.ed_accou);
        ed_conf = findViewById(R.id.ed_conf);
        ed_but = findViewById(R.id.ed_but);

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        routing = sharedPreferences.getString("routing", "");
        account = sharedPreferences.getString("account", "");

        ed_rout.setText(routing);
        ed_accou.setText(account);

        ed_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ban_routn = ed_rout.getText().toString();
                String ban_accn = ed_accou.getText().toString();
                String ban_confn = ed_conf.getText().toString();

                if (ban_routn.length() != 9){
                    Toast.makeText(bank_edit.this, "Invalid routing number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ban_accn.length() < 5){
                    Toast.makeText(bank_edit.this, "Invalid account number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ban_accn.length() > 20){
                    Toast.makeText(bank_edit.this, "Invalid account number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ban_confn.equals(ban_accn)){
                    Toast.makeText(bank_edit.this, "Please confirm your account number", Toast.LENGTH_SHORT).show();
                    return;
                }


                ProgressDialog progressDialog = new ProgressDialog(bank_edit.this);
                progressDialog.setMessage("Saving...");
                progressDialog.setProgressStyle(0);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("routing", ban_routn);
                            editor.putString("account", ban_accn);
                            editor.commit();

                            Thread.sleep(5000);
                            onBackPressed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });
    }
}