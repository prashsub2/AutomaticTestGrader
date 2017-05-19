package com.example.android.automatictestgrader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GradeActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference studentsAnswersReference = database.getReference("studentsAnswers");
    private DatabaseReference answerKeyReference = database.getReference("answerKey");
    private String studentAnswers= "";
    private String answerKey = "";
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        textView = (TextView) findViewById(R.id.the_end);

        //Assigns studentsAnswers in database to a string.
        studentsAnswersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                studentAnswers = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Assigns studentsAnswers in database to a string. Calls grade.
        answerKeyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answerKey = dataSnapshot.getValue(String.class);
                grade(answerKey, studentAnswers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Grades the student's answers against the answer key.
     *
     * @param answerKeyString - Answer Key
     * @param studentAnswersString - Student's answers.
     */
    public void grade(String answerKeyString, String studentAnswersString){
        char[] answersCharArray = answerKeyString.toCharArray();
        Toast.makeText(getApplicationContext(), studentAnswersString, Toast.LENGTH_LONG).show();
        char[] superfluosStudent = studentAnswersString.toCharArray();
        ArrayList<Character> actualStudentList = new ArrayList<>();

        if( answerKeyString.length() >= 10){
            for ( int i =0; i < 45; i ++){
                if( i == 3 || (i - 3) % 5 == 0 ){
                    actualStudentList.add(superfluosStudent[i]);
                }
            }

            for(int i = 45; i < superfluosStudent.length; i ++){
                if ( i ==49 || (i - 49) % 6 ==0){
                    actualStudentList.add(superfluosStudent[i]);
                }
            }
        }
        else {
            for (int i = 0; i < superfluosStudent.length; i++) {
                if (i == 3 || (i - 3) % 5 == 0) {
                    actualStudentList.add(superfluosStudent[i]);
                }
            }
        }
        StringBuilder studentStringBuilder = new StringBuilder();

        for(Character character: actualStudentList){
            studentStringBuilder.append(character);
        }
        char[] studentAnswersChar = studentStringBuilder.toString().toCharArray();

        double correctCnt =0;
        for( int i =0; i < answersCharArray.length; i ++){
            if(studentAnswersChar[i] == answersCharArray[i]){
                correctCnt++;
            }
        }
        double grade = (correctCnt/answersCharArray.length) * 100;
        textView.setText("This student's grade was a " + grade + " percent");
    }
}
