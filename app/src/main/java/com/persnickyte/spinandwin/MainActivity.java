package com.persnickyte.spinandwin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final int[] sectors = {1, 2, 3, 4, 5 ,6 ,7 ,8 , 9, 10, 11, 12};
    final int[] sectorDegrees = new int[sectors.length];
    int randomSectorIndex = 0;
    ImageView imageView;
    boolean spinning = false;
    int earningsRecord = 0;
    Button button, reset, rules, bonus, demopoints, deposit, withdraw;
    TextView total, scored, top_winners;
    Random random = new Random();
    private SoundPool soundPool;
    private int spin_wheel_sound;
    SharedPreferences sharedPreferences;
    String a, b, c, d, e, records, balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.spin);
        reset = findViewById(R.id.reset);
        rules = findViewById(R.id.rules);
        bonus = findViewById(R.id.bonus);
        demopoints = findViewById(R.id.logout);
        deposit = findViewById(R.id.deposit);
        withdraw = findViewById(R.id.withdraw);
        total = findViewById(R.id.total);
        scored = findViewById(R.id.scored);
        top_winners = findViewById(R.id.top_winners);

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        records = sharedPreferences.getString("Points_Earned", "0");
        /*int depo = Integer.parseInt(records);
        balance = String.valueOf(depo * 1 + Integer.parseInt(records));*/
        total.setText("Total Points: " + records + " Points.");


        Degrees();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
        spin_wheel_sound = soundPool.load(this, R.raw.spin_wheel_sound, 1);

        top_winners.setText("1. \n" +
                "2. \n" +
                "3. \n" +
                "4. \n" +
                "5. ");


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Confidential")
                .document("Active");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot dsnaps = task.getResult();

                String OpenK = dsnaps.getString("Open");
                String HiddenK = dsnaps.getString("Hidden");
                String minim = dsnaps.getString("Least");

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("OpenK", OpenK);
                editor.putString("HiddenK", HiddenK);
                editor.putString("least", minim);
                editor.commit();
            }
        });
        DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("Deposit")
                .document("Winners");
        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot dsnaps = task.getResult();

                a = dsnaps.getString("a");
                b = dsnaps.getString("b");
                c = dsnaps.getString("c");
                d = dsnaps.getString("d");
                e = dsnaps.getString("e");

                top_winners.setText("1. " + a +"\n" +
                        "2. " + b + "\n" +
                        "3. " + c + "\n" +
                        "4. " + d + "\n" +
                        "5. " + e );
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinning){
                    spin();
                    spinning = true;
                    soundPool.play(spin_wheel_sound, 1, 1, 0, 0, 1);
                }else {
                    Toast.makeText(MainActivity.this, "Wait for wheel to stop spinning", Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinning) {
                    imageView.clearAnimation();
                }else {
                    Toast.makeText(MainActivity.this, "Wait for wheel to stop spinning", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.Rules).setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
            }
        });
        demopoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                Integer logged_in = 1;
                editor.putInt("logged", logged_in);
                editor.commit();

                Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, brief.class);
                startActivity(intent);
                finish();
            }
        });
        bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, bonus_ads.class);
                startActivity(intent);
            }
        });
        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, deposit.class);
                startActivity(intent);
            }
        });
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, bank.class);
                startActivity(intent);
            }
        });
    }
    private void Degrees(){

        int sectorDegree = 360/sectors.length;

        for (int i = 0; i < sectors.length; i++){

            sectorDegrees[i] = (i+1) * sectorDegree;
        }
    }
    private void spin(){

        randomSectorIndex = random.nextInt(sectors.length);

        int randomDegree = generateRandomDegreeToSpinTo();


        RotateAnimation rotateAnimation = new RotateAnimation(0, randomDegree, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(4000);
        rotateAnimation.setFillAfter(true);

        rotateAnimation.setInterpolator(new DecelerateInterpolator());


        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                int earnedcoins = sectors[sectors.length - (randomSectorIndex + 1)];

                saveEarnings(earnedcoins);

                spinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(rotateAnimation);

    }
    private int generateRandomDegreeToSpinTo(){

        return (360 * sectors.length) + sectorDegrees[randomSectorIndex];

    }

    private void saveEarnings(int earnedcoins) {

        earningsRecord = Integer.parseInt(records ) + earnedcoins;
        records = String.valueOf(earningsRecord);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Points_Earned", String.valueOf(earningsRecord));
        editor.commit();

        total.setText("Total Points: " + earningsRecord + " Points.");
        scored.setText("You have got: " + earnedcoins);

        Toast.makeText(this, "Yo have got " + earnedcoins, Toast.LENGTH_SHORT).show();
    }
}