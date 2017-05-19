package com.example.android.automatictestgrader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PromptActivity extends AppCompatActivity {

    private EditText number;
    private Button done;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference numberOfQuestionsReference = database.getReference("numberOfQuestions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
        number = (EditText) findViewById(R.id.editText);
        done = (Button) findViewById(R.id.done);

        //Click listener for done button
        //Adds number of questions to the database
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numberOfQuestionsInt = Integer.parseInt(number.getText().toString());
                if(numberOfQuestionsInt != 0 && numberOfQuestionsInt < 54){
                numberOfQuestionsReference.setValue(numberOfQuestionsInt);
                Intent intent = new Intent(PromptActivity.this, AnswerKeyActivity.class);
                startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid number of questions", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
