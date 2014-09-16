package com.dront.sensors;


import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecordsWriter extends AsyncTask<AccRecord, Integer, String> {

    private static final String DEFAULT_DIRECTORY_NAME = "Sensor data";
    private String directoryName;
    private Toast msg;

    public RecordsWriter(Toast t){
        directoryName = DEFAULT_DIRECTORY_NAME;
        msg = t;
    }

    public RecordsWriter(Toast t, String folderName){
        directoryName = folderName;
        msg = t;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Log.d(Constants.LOG_TAG, "external storage is not available to read and write");
        return false;
    }

    private String generateFilename(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yy HH:mm:ss");
        return sdf.format(c.getTime());
    }

    @Override
    protected String doInBackground(AccRecord... data) {
        if (data.length == 0){
            cancel(true);
            String text = "Data in doInBackground is empty";
            Log.d(Constants.LOG_ASYNC, text);
            return text;
        }

        if (!isExternalStorageWritable()){
            cancel(true);
            String text = "Storage is not available for writing";
            Log.d(Constants.LOG_ASYNC, text);
            return text;
        }

        File dir = Environment.getExternalStorageDirectory();
        dir = new File(dir.getAbsolutePath() + "/" + directoryName);
        if (!(dir.mkdirs() || dir.exists())){
            cancel(true);
            String text = "Directory " + dir.getAbsolutePath() + " was not created";
            Log.d(Constants.LOG_TAG, text);
            return text;
        }

        String filename = generateFilename();
        File file = new File(dir, filename);

        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
            DataOutputStream dos = new DataOutputStream(fos);

            for (int i = 0; i < data.length; i++){
                //dos.writeLong(data[i].time);
                //dos.writeDouble(data[i].meanVal);
                dos.writeChars(Long.toString(data[i].getTime()) + " ");
                dos.writeChars(Double.toString(data[i].getAbs()) + "\n");
                publishProgress(i, data.length);
                if (isCancelled()){
                    dos.flush();
                    dos.close();
                    fos.close();
                    String text = "Writing process was cancelled";
                    Log.d(Constants.LOG_TAG, text);
                    return text;
                }
            }

            dos.flush();
            dos.close();
            fos.close();

            Log.d(Constants.LOG_TAG, "Data successfully written to file " + filename);
        } catch (IOException e) {
            String msg = "IOException: " + e.getMessage();
            Log.d(Constants.LOG_TAG, msg);
            return msg;
        }
        return "Data was written to file " + filename;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String start = "Start";
        Log.d(Constants.LOG_ASYNC, start);
        msg.setText(start);
        msg.show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(Constants.LOG_ASYNC, result);
        msg.setText(result);
        msg.show();
    }

    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
        Log.d(Constants.LOG_ASYNC, result);
        msg.setText(result);
        msg.show();
    }
}
