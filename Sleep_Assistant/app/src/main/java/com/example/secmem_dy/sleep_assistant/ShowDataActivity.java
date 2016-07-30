package com.example.secmem_dy.sleep_assistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by SECMEM-DY on 2016-07-27.
 */
public class ShowDataActivity extends Activity {

    private AsyncHttpClient client;
    private String id;
    private LineChart mChart;

    private String mUrl;
    private Bitmap bmImg;
    private OpenHttpConnection getImage;
    private String[] data;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualize);
        Intent intent=getIntent();
        id= intent.getStringExtra("ID");
        client=HttpClient.getinstance();
        getImage=new OpenHttpConnection();
        /***sendRequest();***/

        //create line chart
        mChart = (LineChart)findViewById(R.id.chart);
        //add to main layout
        //mainLayout.addView(mChart);


        //customize line chart
        mChart.setDescription("");
        mChart.setNoDataTextDescription("No data for the momet");

        //enable alue highlighting
        mChart.setHighlightPerDragEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        //enable pinch zoom to to avoid scaling x and y axis separately
        mChart.setPinchZoom(true);

        //set bg color
        mChart.setBackgroundColor(Color.LTGRAY);

        //work on data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add data to line chart
        mChart.setData(data);

        //get legent object;
        Legend l = mChart.getLegend();

        //customiaze legent
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1=mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1=mChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(120f);
        y1.setDrawGridLines(true);

        YAxis y12 =mChart.getAxisRight();
        y12.setEnabled(false);
    }

    public void sendRequest(){
        StringEntity entity=null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(HttpClient.JSON_ID,id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity=HttpClient.makeStringEntity(jsonParams);

        client.post(getApplicationContext(), HttpClient.getAbsoulteUrl(HttpClient.SHOW_VISUAL_DATA_URL), entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                Log.e("FROM_SERVER","success");
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mUrl=ackData;
                if(ackData!=null && ackData.equals(HttpClient.ACK_FAIL)) {//Login Success
                    Toast.makeText(getApplicationContext(),"이미지 URL 불러오기 실패",Toast.LENGTH_SHORT).show();
                }else{
                    mUrl=ackData;
                    String url=ackData.substring(1,ackData.length()-1);
                    Log.e("FROM_SERVER",url);

                    //getImage.execute(visualImage,url);
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FROM_SERVER","fail");
                Toast.makeText(getApplicationContext(),"이미지 불러오기 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class OpenHttpConnection extends AsyncTask<Object, Void, Bitmap> {
        private ImageView bmImage ;
        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            bmImage = (ImageView)params[0];
            String url = (String)params[1];
            url="http://cfile6.uf.tistory.com/image/21097435544EC2FB1BBFD0";
            Log.e("FROM_SERVER",url);
            InputStream in = null;
            try {
                in = new java.net.URL(url).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
            bmImage.setImageBitmap(bm);
        }
    }
}
