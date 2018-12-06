package com.example.quent.snowtam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        EditText code1 = (EditText)findViewById(R.id.code1);
        if(code1.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.show();

        } else {
            Context context = getApplicationContext();
            Intent intent = new Intent(context, Main2Activity.class);
            intent.putExtra("code1", code1.getText().toString().toUpperCase());
            startActivity(intent);
        }
    }
}
