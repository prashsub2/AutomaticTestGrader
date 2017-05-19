package com.example.android.automatictestgrader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView cameraView;
    private TextView text;
    private Button grade;
    private CameraSource cameraSource;
    private final int RequestCameraPermissionID = 2002;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference studentsAnswersReference = database.getReference("studentsAnswers");
    private DatabaseReference answerKeyReference = database.getReference("answerKey");
    private String answerKey = "";

    /**
     * Starts surfaceView's view of the camera.
     *
     * @param requestCode - Code will equal RequestCameraPermissionId if successful
     * @param permissions - String array
     * @param grantResults - first element will be determine status of permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestCameraPermissionID:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        text = (TextView) findViewById(R.id.text_view);
        grade = (Button) findViewById(R.id.grade);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not available yet");
        } else {
            //Creates source for back facing camera of device that is being used
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            //Uses cameraSource created above as the surfaceView so that user can see exactly what they are
            //taking a picture of.
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            //How the vision software recognizes the text in the surfaceView (cameraView).
            //Sets the text of the text view to whatever the textRecognizer picks up
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0){
                        text.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();

                                for(int i =0; i < items.size(); ++i){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");

                                }
                                text.setText(stringBuilder.toString());
                            }
                        });
                    }

                }
            });
        }

        answerKeyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answerKey = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Listener for grade button, starts GradeActivity. Sets value of the answers of the student.
        grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentAnswers = text.getText().toString();
                int extraCharacters = 0;

                if( answerKey.length() >= 10){
                    extraCharacters = (answerKey.length() - 10) + 1;
                }
                if(studentAnswers.length() == (5 * answerKey.length()) + extraCharacters) {
                    //Toast.makeText(getApplicationContext(), "Length: " + studentAnswers.charAt(8), Toast.LENGTH_LONG).show();
                    studentsAnswersReference.setValue(studentAnswers);
                    Intent intent = new Intent(MainActivity.this, GradeActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please retake picture, the amount of answers does not " +
                            "match the amount of answers in the answer key.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


}
