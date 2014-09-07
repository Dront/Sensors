package com.dront.sensors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import util.ArrayOperation;


public class MainActivity extends Activity implements SensorEventListener {

    private final static String BIG_TICK_PREF = "big tick";
    private final static int DEFAULT_SENSOR = Sensor.TYPE_ACCELEROMETER;

    //some fields
    private TextView txtViewXAxisVal, txtViewYAxisVal, txtViewZAxisVal, txtViewAbsVal;
    private TextView txtViewFlightCount, txtViewFlightHeight;
    private Button btnStartStop;

    private SensorManager mSensorManager;
    private Sensor accSensor;
    private AccInfo accInfo;
    private RecordsWriter recordsWriter;

    private Handler h;
    private Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //magic of the Creation
        super.onCreate(savedInstanceState);

        Log.d(Constants.LOG_TAG, "MainActivity onCreate");

        setContentView(R.layout.activity_main);
        getInterfaceResources();

        h = new Handler();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(DEFAULT_SENSOR) == null){
            finish();
            return;
        }

        accSensor = mSensorManager.getDefaultSensor(DEFAULT_SENSOR);
        accInfo = AccInfo.getInstance(accSensor);
        mSensorManager.unregisterListener(this);

        loadSettings();

        r = new Runnable() {
            @Override
            public void run() {
                accInfo.bigTick();

                DecimalFormat df = new DecimalFormat("#.##");
                txtViewAbsVal.setText(df.format(accInfo.getMeanAbs()));
                txtViewXAxisVal.setText(df.format(accInfo.getMeanX()));
                txtViewYAxisVal.setText(df.format(accInfo.getMeanY()));
                txtViewZAxisVal.setText(df.format(accInfo.getMeanZ()));

                h.postDelayed(this, accInfo.getBigTickDelay());
            }
        };
    }

    @Override
    protected void onResume() {
        //guess
        super.onResume();
        Log.d(Constants.LOG_TAG, "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        //guess
        super.onPause();
        Log.d(Constants.LOG_TAG, "MainActivity onPause");
        saveSettings();
        disableSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recordsWriter != null){
            recordsWriter.cancel(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //stuff
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double[] newVal = new double[3];
        for (int  i = 0; i < 3; i++){
            newVal[i] = (double) event.values[i];
        }
        accInfo.smallTick(newVal);
    }

    public void btnStartStop(View v){
        //btnStartStop onClick
        if (!accInfo.getEnabled()){
            enableSensor();
        } else {
            flightCheck();
            disableSensor();
        }
    }

    public void btnDrawGraph(View v){
        if (accInfo.isDataEmpty()){
            Toast.makeText(getApplicationContext(), "Record data first.", Toast.LENGTH_SHORT).show();
        } else {
            disableSensor();
            //StorageUser.writeRecordList(accInfo.getData());

            Intent intent = new Intent(this, GraphActivity.class);
            startActivity(intent);
        }
    }

    public void btnSaveData(View v){

        if (recordsWriter == null || recordsWriter.getStatus() == RecordsWriter.Status.FINISHED){
            @SuppressLint("ShowToast")
            Toast t = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            recordsWriter = new RecordsWriter(t);
        }

        if (recordsWriter.getStatus() == RecordsWriter.Status.RUNNING){
            String msg = "Records writer is already running";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<AccRecord> data = accInfo.getData();
        if (data.size() == 0){
            String msg = "Nothing to record";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        AccRecord[] records = new AccRecord[data.size()];
        accInfo.getData().toArray(records);

        recordsWriter.execute(records);
    }

    public void btnShowInfo(View v){
        String info = accInfo.getInfo();
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
    }

    private String flightCheck(){
        double[] heights = accInfo.getFlights();
        String msg;
        if (heights == null){
            msg = "Phone was not thrown";
            txtViewFlightCount.setText("0");
            txtViewFlightHeight.setText("");
        } else {
            msg = "Count: " + heights.length + "\n";
            txtViewFlightCount.setText(String.valueOf(heights.length));

            for (int i = 0; i < heights.length; i++){
                msg += "Flight " + (i + 1) + ". Height: " + heights[i] + " meters.\n";
            }
            double height = ArrayOperation.findMaxInArray(heights);
            DecimalFormat df = new DecimalFormat("#.##");
            txtViewFlightHeight.setText(df.format(height));
        }
        Log.d(Constants.LOG_FLIGHT, msg);
        return msg;
    }

    private void disableSensor(){
        btnStartStop.setText(R.string.btnStart);
        accInfo.disable();
        mSensorManager.unregisterListener(this);
        h.removeCallbacks(r);
    }

    private void enableSensor(){
        btnStartStop.setText(R.string.btnPause);
        accInfo.enable();
        mSensorManager.registerListener(this, accSensor, accInfo.getDelay());
        h.postDelayed(r, accInfo.getBigTickDelay());
    }

    private void getInterfaceResources(){
        txtViewXAxisVal = (TextView) findViewById(R.id.txtViewXVal);
        txtViewYAxisVal = (TextView) findViewById(R.id.txtViewYVal);
        txtViewZAxisVal = (TextView) findViewById(R.id.txtViewZVal);
        txtViewAbsVal = (TextView) findViewById(R.id.txtViewAbsVal);

        txtViewFlightCount = (TextView) findViewById(R.id.txtViewFlightCountVal);
        txtViewFlightHeight = (TextView) findViewById(R.id.txtViewFlightHeightVal);

        btnStartStop = (Button) findViewById(R.id.btnStartStop);
    }

    private void loadSettings(){
        Context context = getApplicationContext();
        String filename = getString(R.string.settingsPref);
        SharedPreferences settings = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        int bigTickTime = settings.getInt(BIG_TICK_PREF, AccInfo.DEFAULT_BIG_TIC_DELAY);
        accInfo.setBigTickDelay(bigTickTime);
    }

    private void saveSettings(){
        Context context = getApplicationContext();
        String filename = getString(R.string.settingsPref);
        SharedPreferences settings = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor optionsEditor = settings.edit();
        optionsEditor.putInt(BIG_TICK_PREF, accInfo.getBigTickDelay());
        optionsEditor.apply();
    }

}
