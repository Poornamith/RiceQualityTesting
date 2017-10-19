package com.poornamith.ricetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    EditText editTextSigma;
    EditText editTextErode;
    EditText editTextThreshVal;
    EditText editTextThreshMax;
    EditText editTextLimitU;
    EditText editTextLimitL;

    private void init() {

        editTextSigma = (EditText) findViewById(R.id.edit_text_g_s_v);
        editTextErode = (EditText) findViewById(R.id.edit_text_e_k_s);
        editTextThreshVal = (EditText) findViewById(R.id.edit_text_t_t_v);
        editTextThreshMax = (EditText) findViewById(R.id.edit_text_t_m_t);
        editTextLimitU = (EditText) findViewById(R.id.edit_text_l_upper);
        editTextLimitL = (EditText) findViewById(R.id.edit_text_l_lower);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {

            // put the String to pass back into an Intent and close this activity
            final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();

            globalVariables.setGaussianSigma(Double.parseDouble(editTextSigma.getText().toString()));
            globalVariables.setErodeKernal(Integer.parseInt(editTextErode.getText().toString()));
            globalVariables.setThresholdVal(Integer.parseInt(editTextThreshVal.getText().toString()));
            globalVariables.setThresholdMax(Integer.parseInt(editTextThreshMax.getText().toString()));
            globalVariables.setLimitUpper(Integer.parseInt(editTextLimitU.getText().toString()));
            globalVariables.setLimitLower(Integer.parseInt(editTextLimitL.getText().toString()));
        }
        catch (Exception ex) {
            Log.d(TAG, ex.getMessage());

        }
    }
}
