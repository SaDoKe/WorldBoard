package org.sadoke.worldboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class CreateMessageActivity extends AppCompatActivity {
    ActionBar actionBar;
    FloatingActionButton floatingActionButton;
    EditText editText;
    RESTApi restApi = RESTApi.init(this);
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);
        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create Message");

        editText = (EditText) findViewById(R.id.mew_message);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(view -> {
            String message = String.valueOf(editText.getText());
            restApi.createMessage(message,result -> {
                editText.setText("Succes");
            },getIntent().getFloatExtra("LNG",-1),getIntent().getFloatExtra("LAT",-1));
        });
    }
}