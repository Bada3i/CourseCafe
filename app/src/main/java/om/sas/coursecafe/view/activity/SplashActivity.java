package om.sas.coursecafe.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import om.sas.coursecafe.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser == null || mFirebaseUser.getEmail() == null || mFirebaseUser.getEmail().isEmpty()) {
            goToSplash();
        } else {


//            Intent i = getIntent();
//            String userId=i.getStringExtra("userid");
//
//            if (userId != null) {
//
//                Intent intentNotification = new Intent(SplashActivity.this, MainActivity.class);
//                intentNotification.putExtra("keyUser", userId);
//                Log.d("Splash2",userId);
//                Toast.makeText(this, "From Splash2 userID: "+userId, Toast.LENGTH_SHORT).show();
//
//                finish();
//
//                startActivity(intentNotification);}
//            else{
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
        //}
        }




    }

    private void goToSplash() {
        ImageView ivLogoSplashCircle = findViewById(R.id.iv_logo_splash_circle);
        Animation animationSplashCircle = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_circle_anim);
        ivLogoSplashCircle.startAnimation(animationSplashCircle);

        ImageView ivLogoSplash = findViewById(R.id.iv_logo_splash);
        Animation animationSplash = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_logo_anim);
        ivLogoSplash.startAnimation(animationSplash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        String userId=intent.getStringExtra("userid");
//        Log.d("Splash1: ",userId);
//        //Bundle extras = intent.getExtras();
//        if (userId != null) {
//            Intent i = new Intent(SplashActivity.this, MainActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("userid", userId);
//            i.putExtras(bundle);
//            startActivity(i);
//        }
//    }
}
