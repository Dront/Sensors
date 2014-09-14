package com.dront.sensors;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;


public class GraphActivity extends Activity {

    public final static int MAX_GRAPH_DATA_SIZE = 200;
    DataTransport data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Log.d(Constants.LOG_TAG, "Graph activity onCreate");

        data = DataTransport.getInstance();
        if (data.size() == 0){
            String msg = "No data was trasported";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            Log.d(Constants.LOG_SINGLETON, msg);
            finish();
        }

        GraphView.GraphViewData[] gvData;
        gvData = getGVData();

        int lineColor = getResources().getColor(R.color.yellow);
        GraphViewSeries.GraphViewSeriesStyle gvStyle = new GraphViewSeries.GraphViewSeriesStyle(lineColor, 1);
        GraphViewSeries series = new GraphViewSeries("lol", gvStyle, gvData);
        GraphView gv = new BarGraphView(getApplicationContext(), "Absolute values");
        gv.addSeries(series);

        gv.getGraphViewStyle().setHorizontalLabelsColor(lineColor);
        gv.getGraphViewStyle().setVerticalLabelsColor(lineColor);
        gv.getGraphViewStyle().setTextSize(25);

        //gv.setViewPort(0, 15);
        //gv.setScrollable(true);
        //gv.setScalable(true);

        int backgroundColor = getResources().getColor(R.color.blue);
        gv.setBackgroundColor(backgroundColor);
        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
        linLayout.addView(gv);
    }

    private GraphView.GraphViewData[] getGVData(){
        int size = data.size();
        Long minTime = data.get(0).getTime();
        GraphView.GraphViewData[] gvData;

        /*option 1*/
//        gvData = new GraphView.GraphViewData[size];
//        for (int i = 0; i < size; i++){
//            AccRecord tmp = data.get(i);
//            gvData[i] = new GraphView.GraphViewData((tmp.time - minTime) / 1000, tmp.meanVal);
//        }
//        return gvData;


        /*option 2*/
        if (size >= MAX_GRAPH_DATA_SIZE){
            gvData = new GraphView.GraphViewData[MAX_GRAPH_DATA_SIZE];
            int interval = size / MAX_GRAPH_DATA_SIZE;
            for (int i = 0; i < MAX_GRAPH_DATA_SIZE; i++){
                double sum = 0;
                int counter = 0;

                int begin = i * interval;
                int end = (i + 1) * interval;
                for (int j = begin; j < end; j++){
                    AccRecord tmp = data.get(j);
                    sum += tmp.getAbs();
                    counter++;
                }
                long time = (data.get(i * interval).getTime() - minTime) / 1000;
                double value = sum / counter;
                gvData[i] = new GraphView.GraphViewData(time, value);
            }

        } else {
            gvData = new GraphView.GraphViewData[size];
            for (int i = 0; i < size; i++){
                AccRecord tmp = data.get(i);
                gvData[i] = new GraphView.GraphViewData((tmp.getTime() - minTime) / 1000, tmp.getAbs());
            }
        }
        return gvData;
    }

}
