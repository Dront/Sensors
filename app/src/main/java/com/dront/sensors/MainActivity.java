package com.dront.sensors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends Activity {

    public static final int UI_UPDATE_DELAY = 100;
    public static final int FLIGHT_CHECK_DELAY = 2000;

    private TextView txtViewXAxisVal, txtViewYAxisVal, txtViewZAxisVal, txtViewAbsVal;
    private TextView txtViewFlightCount, txtViewFlightHeight;

    private AccInfo accInfo;
    private RecordsWriter recordsWriter;
    private FlightAnalyzer flightAnalyzer;

    private Handler h;
    private Runnable sensorDataUpdate;
    private Runnable flightUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //magic of the Creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Constants.LOG_TAG, "MainActivity onCreate");

        getInterfaceResources();

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Constants.DEFAULT_SENSOR) == null){
            finish();
            return;
        }
        accInfo = new AccInfo(mSensorManager);

        h = new Handler();
        sensorDataUpdate = new Runnable() {
            @Override
            public void run() {
                DecimalFormat df = new DecimalFormat("#.##");
                txtViewAbsVal.setText(df.format(accInfo.getLastAbs()));
                txtViewXAxisVal.setText(df.format(accInfo.getLastX()));
                txtViewYAxisVal.setText(df.format(accInfo.getLastY()));
                txtViewZAxisVal.setText(df.format(accInfo.getLastZ()));
                txtViewFlightCount.setText(Integer.toString(FlightAnalyzer.getFlightCount()));
                txtViewFlightHeight.setText(df.format(FlightAnalyzer.getLastHeight()));

                h.postDelayed(this, UI_UPDATE_DELAY);
            }
        };

        flightUpdate = new Runnable() {
            @Override
            public void run() {
                if ((flightAnalyzer == null || flightAnalyzer.getStatus() == FlightAnalyzer.Status.FINISHED)
                        && !accInfo.isDataEmpty()){
                    flightAnalyzer = new FlightAnalyzer(accInfo.getData());
                    flightAnalyzer.execute();
                }

                String msg = "Size of AccRecord array = " + accInfo.removeOldRecords();
                Log.d(Constants.LOG_MEMORY, msg);

                h.postDelayed(this, FLIGHT_CHECK_DELAY);
            }
        };
    }

    @Override
    protected void onResume() {
        //guess
        super.onResume();
        DataTransport transport = DataTransport.getInstance();
        transport.clear();

        enableSensor();
        Log.d(Constants.LOG_TAG, "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        //guess
        super.onPause();
        Log.d(Constants.LOG_TAG, "MainActivity onPause");
        disableSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recordsWriter != null){
            recordsWriter.cancel(true);
        }
    }

    public void btnDrawGraph(View v){
        if (accInfo.isDataEmpty()){
            Toast.makeText(getApplicationContext(), "Record data first.", Toast.LENGTH_SHORT).show();
        } else {
            disableSensor();

            DataTransport transport = DataTransport.getInstance();
            transport.clear();
            transport.addAll(accInfo.getData());

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

//    private String flightCheck(){
//        double[] heights = accInfo.getFlights();
//        String msg;
//        if (heights == null || heights.length == 0){
//            msg = "Phone was not thrown";
//            txtViewFlightCount.setText("0");
//            txtViewFlightHeight.setText("");
//        } else {
//            msg = "Count: " + heights.length + "\n";
//            txtViewFlightCount.setText(String.valueOf(heights.length));
//
//            for (int i = 0; i < heights.length; i++){
//                msg += "Flight " + (i + 1) + ". Height: " + heights[i] + " meters.\n";
//            }
//            double height = ArrayOperation.findMaxInArray(heights);
//            DecimalFormat df = new DecimalFormat("#.##");
//            txtViewFlightHeight.setText(df.format(height));
//        }
//        Log.d(Constants.LOG_FLIGHT, msg);
//        return msg;
//    }

    private void disableSensor(){
        accInfo.disable();
        h.removeCallbacks(sensorDataUpdate);
        h.removeCallbacks(flightUpdate);
    }

    private void enableSensor(){
        accInfo.enable();
        h.postDelayed(sensorDataUpdate, UI_UPDATE_DELAY);
        h.postDelayed(flightUpdate, FLIGHT_CHECK_DELAY);
    }

    private void getInterfaceResources(){
        txtViewXAxisVal = (TextView) findViewById(R.id.txtViewXVal);
        txtViewYAxisVal = (TextView) findViewById(R.id.txtViewYVal);
        txtViewZAxisVal = (TextView) findViewById(R.id.txtViewZVal);
        txtViewAbsVal = (TextView) findViewById(R.id.txtViewAbsVal);

        txtViewFlightCount = (TextView) findViewById(R.id.txtViewFlightCountVal);
        txtViewFlightHeight = (TextView) findViewById(R.id.txtViewFlightHeightVal);
    }

}
