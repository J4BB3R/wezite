package ca.wezite.wezite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class InfosLieuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_lieu);

        Intent intent = getIntent();
        String description = intent.getStringExtra("description");
        TextView descTextView = (TextView) findViewById(R.id.description);
        descTextView.setText(description);

        Log.i("description", description);
    }
}
