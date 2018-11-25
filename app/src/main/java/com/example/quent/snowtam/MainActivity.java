package com.example.quent.snowtam;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button button = findViewById(R.id.buttonSearch);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goActivityResult();
            }
        });
    }

    private void goActivityResult() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, Main2Activity.class);
        EditText code1 = (EditText)findViewById(R.id.code1);
        EditText code2 = (EditText)findViewById(R.id.code2);
        EditText code3 = (EditText)findViewById(R.id.code3);
        EditText code4 = (EditText)findViewById(R.id.code4);

        intent.putExtra("code1", code1.getText().toString());
        intent.putExtra("code2", code2.getText().toString());
        intent.putExtra("code3", code3.getText().toString());
        intent.putExtra("code4", code4.getText().toString());
        startActivity(intent);
    }
}
