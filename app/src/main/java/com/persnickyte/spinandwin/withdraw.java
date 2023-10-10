package com.persnickyte.spinandwin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class withdraw extends AppCompatActivity {

    TextView with_statement, with_history;
    Button withdrawal, withd_ed;
    EditText with_amount;
    String points, phone, with_prog, least;
    int amount, deposit;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        with_statement = findViewById(R.id.with_statement);
        with_history = findViewById(R.id.with_history);
        withdrawal = findViewById(R.id.withdrawal);
        with_amount = findViewById(R.id.with_amount);
        withd_ed = findViewById(R.id.withd_ed);

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        deposit= sharedPreferences.getInt("Deposit", 0);
        points = sharedPreferences.getString("Points_Earned", "0");
        with_prog = sharedPreferences.getString("Amount_wit", "");
        least = sharedPreferences.getString("least", "150");
        //int depo = sharedPreferences.getInt("Deposit", 0);
        //balance = String.valueOf(depo * 1 + Integer.parseInt(points));
        phone = sharedPreferences.getString("phone_number", "");

        amount = (int) (Integer.parseInt(points) * 0.01);


        if (!with_prog.isEmpty()) {
            with_history.setText("1. Withdrawal of $" + with_prog + " -In Progress");
        }
        withd_ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ints = new Intent(withdraw.this, bank_edit.class);
                startActivity(ints);
            }
        });
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int depocheck = deposit;
                String with_am = with_amount.getText().toString();

                if (with_am.isEmpty()){
                    Toast.makeText(withdraw.this, "Enter amount to withdraw", Toast.LENGTH_SHORT).show();
                    with_amount.setError("Enter amount");
                    return;
                }
                int wit_amount = Integer.parseInt(with_am);
                if (wit_amount < 10){
                    Toast.makeText(withdraw.this, "Amount should be atleast $10", Toast.LENGTH_SHORT).show();
                    with_amount.setError("Should be atleast $10");
                    return;
                }
                if (wit_amount > amount){
                    Toast.makeText(withdraw.this, "You cannot withdraw more than your balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (depocheck != 1){

                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(withdraw.this);
                    dialog.setTitle("Account not active");
                    dialog.setMessage("To make your first withdrawal, you must first of all make a deposit of at least $"+least+" " +
                            "to your account. We are asking for this as an authentication attempt to avoid abuse of our systems by " +
                            "false users(bots).");
                    dialog.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(withdraw.this, deposit.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                    return;

                }
                else {

                    int remain = Integer.parseInt(points) - wit_amount * 100;
                    String bal = String.valueOf(remain);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Points_Earned", bal);
                    editor.putString("Amount_wit", with_am);
                    editor.commit();

                    Toast.makeText(withdraw.this, "Withdrawal Successful", Toast.LENGTH_SHORT).show();

                    Intent inted = new Intent(withdraw.this, MainActivity.class);
                    startActivity(inted);
                    finish();
                }

            }
        });
        with_statement.setText("Welcome to our withdrawals page. You have got "+ points +" points which is equal to $"+ amount + "" +
                ". You can withdraw your balance anytime and it will be sent to the bank account you provided us. " +
                "If you want to change your bank account, you can click on the button below.\n\n" +
                "NB: The amount you are withdrawing should be less than your balance and more than $10\n\n" +
                "Enter the amount you want to withdraw below.");
    }
}