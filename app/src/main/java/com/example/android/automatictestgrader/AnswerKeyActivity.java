package com.example.android.automatictestgrader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.widget.GridLayout.VERTICAL;
import static android.widget.ListPopupWindow.WRAP_CONTENT;

public class AnswerKeyActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference numberOfQuestionsReference = database.getReference("numberOfQuestions");
    private DatabaseReference answerKeyReference = database.getReference("answerKey");
    private int numberOfQuestionsInt;
    private List<EditText> editTextList = new ArrayList<EditText>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LinearLayout linearLayout = new LinearLayout(this);
        final TextView directions = new TextView(this);

        directions.setText("Please enter the corresponding answer to each question");
        directions.setTextSize(20f);
        directions.setTextColor(Color.BLACK);

        //Listener for Firebase reference
        numberOfQuestionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfQuestionsInt = dataSnapshot.getValue(Integer.class);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(FILL_PARENT, WRAP_CONTENT);
                linearLayout.setLayoutParams(params);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(directions);
                linearLayout.addView(tableLayout(numberOfQuestionsInt));
                linearLayout.addView(submitButton());
                setContentView(linearLayout);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     * Creates a submit button
     *
     * @return a button that the user clicks on after they have finished entering text
     */
    private Button submitButton() {
        Button button = new Button(this);
        button.setHeight(WRAP_CONTENT);
        button.setText("Submit");
        button.setOnClickListener(submitListener);
        return button;
    }

    /**
     * Access the value of the EditText. Adds answer key to database as a string.
     */

    private View.OnClickListener submitListener = new View.OnClickListener() {
        public void onClick(View view) {
            StringBuilder stringBuilder = new StringBuilder();
            for (EditText editText : editTextList) {
                if(editText.getText().toString().isEmpty() || editText.getText().toString().length()  > 1){
                    Toast.makeText(getApplicationContext(), "At least one answer has an invalid length", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                stringBuilder.append(editText.getText().toString());
                }
            }
            answerKeyReference.setValue(stringBuilder.toString());
            Intent intent = new Intent(AnswerKeyActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };


    /**
     * Using a TableLayout as it provides for a neat ordering structure
     *
     * @param count - number of edit texts needed as it corresponds to the amount of questions needed
     * @return - TableLayout
     */

    private TableLayout tableLayout(int count) {
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setStretchAllColumns(true);
        int noOfRows = count / 5;
        for (int i = 0; i < noOfRows; i++) {
            int rowId = 5 * i;
            tableLayout.addView(createOneFullRow(rowId));
        }
        int individualCells = count % 5;
        tableLayout.addView(createLeftOverCells(individualCells, count));
        return tableLayout;
    }

    /**
     * After the creating of full rows is done, we must create the left over cells as not every test must have
     * an amount of  questions that is a multiple of five.
     *
     * @param individualCells- Amount of leftover cells
     * @param count - Number ofo questions
     * @return - TableRow
     */
    private TableRow createLeftOverCells(int individualCells, int count) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        int rowId = count - individualCells;
        for (int i = 1; i <= individualCells; i++) {
            tableRow.addView(editText(String.valueOf(rowId + i)));
        }
        return tableRow;
    }

    /**
     * Creates one full row for the TableLayout
     *
     * @param rowId - Question number
     * @return Row of EditTexts
     */
    private TableRow createOneFullRow(int rowId) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        for (int i = 1; i <= 5; i++) {
            tableRow.addView(editText(String.valueOf(rowId + i)));
        }
        return tableRow;
    }

    /**
     * Creates edit text when called
     *
     * @param hint - Hint for each EditText
     * @return - EditText
     */
    private EditText editText(String hint) {
        EditText editText = new EditText(this);
        editText.setId(Integer.valueOf(hint));
        editText.setHint(hint);
        editTextList.add(editText);
        return editText;
    }
}

