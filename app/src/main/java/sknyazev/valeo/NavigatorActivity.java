package sknyazev.valeo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class NavigatorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        SharedPreferences sp = getSharedPreferences("valeo", MODE_PRIVATE);
        boolean isAuthenticated = sp.getBoolean("isAuthenticated", false);

        if(isAuthenticated) {
            intent = new Intent(NavigatorActivity.this, MainActivity.class);
        } else {
            intent = new Intent(NavigatorActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
