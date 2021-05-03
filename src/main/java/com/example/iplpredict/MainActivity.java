package com.example.iplpredict;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
public class MainActivity extends AppCompatActivity {
    Interpreter tflite;

    TextView outp;
    Button pred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pred= findViewById(R.id.button);
        outp=findViewById(R.id.textView);
        EditText inp1 =  findViewById(R.id.editTextNumberSigned);
        EditText inp2 =  findViewById(R.id.editTextNumberSigned2);
        EditText inp3 =  findViewById(R.id.editTextNumberSigned3);
        EditText inp4 =  findViewById(R.id.editTextNumberSigned4);
        EditText inp5 =  findViewById(R.id.editTextNumberSigned5);


        try {
            tflite = new Interpreter(loadModelFile());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        pred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String v1 =inp1.getText().toString();
                String v2 =inp2.getText().toString();
                String v3 =inp3.getText().toString();
                String v4 =inp4.getText().toString();
                String v5 =inp5.getText().toString();
                if (v1.isEmpty()){
                    inp1.setError("Invalid input ");
                    return;

                }
                if( v2.isEmpty() ){
                    inp2.setError("Invalid input ");
                    return;
                }
                if( v3.isEmpty() ){
                    inp3.setError("Invalid input ");
                    return;
                }
                if( v4.isEmpty() ){
                    inp4.setError("Invalid input ");
                    return;
                }
                if( v5.isEmpty()){
                    inp5.setError("Invalid input ");
                    return;
                }

                hideKeybaord(v);
                float prediction=doInference(v1,v2,v3,v4,v5);
                System.out.println(prediction);
                int f = (int) (prediction-10);
                int f1 = (int) (prediction +5);
                String s ="         Predicted Score :   \n               "+ f +" to "+ f1;
                outp.setText(s);
            }
        });
    }
    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("IPLFINAL.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    private float doInference(String in,String in1,String in2,String in3,String in4) {
        float[] inputVal=new float[5];

        inputVal[0]=Float.parseFloat(in);
        inputVal[1]=Float.parseFloat(in1);
        inputVal[2]=Float.parseFloat(in2);
        inputVal[3]=Float.parseFloat(in3);
        inputVal[4]=Float.parseFloat(in4);


        float[][] output=new float[1][1];
        tflite.run(inputVal,output);
        return output[0][0];
    }
}