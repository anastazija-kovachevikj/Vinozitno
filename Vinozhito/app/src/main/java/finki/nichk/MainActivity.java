package finki.nichk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton startButton = findViewById(R.id.start_btn);
        startButton.setOnClickListener(view -> {
            // open main menu
            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            finish(); // close this activity
        });
    }
}