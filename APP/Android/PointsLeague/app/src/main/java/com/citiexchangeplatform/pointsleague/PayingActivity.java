package com.citiexchangeplatform.pointsleague;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PayingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> data_posses_point;
    private List<Integer> business_image;
    private PayingAdapter mAdapter;
    private TextView Text_NeedPoints;
    private ImageView ImageView_Business;
    TextView Choose_Points;


    KeyListener storedKeylistener;
    // 存储勾选框状态的map集合
    private HashMap<Integer, Boolean> map = new HashMap<>();


    //接口实例
    //private RecyclerViewOnItemClickListener onItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_paying);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Text_NeedPoints = (TextView)findViewById(R.id.textView_points_need);
        //通过findViewById拿到RecyclerView实例
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_points);

        ImageView_Business = (ImageView)findViewById(R.id.imageView_business);
        Choose_Points = (TextView) findViewById(R.id.textview_points_choose);


        //初始化数据
        initData();

        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PayingAdapter(data_posses_point,business_image,getApplicationContext());

        mRecyclerView.setAdapter(mAdapter);
        //添加Android自带的分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mAdapter.buttonSetOnclick(new PayingAdapter.ButtonInterface() {
            @Override
            public void onclick(View view, int position) {
                //Toast.makeText(getApplicationContext(), "点击条目上的按钮"+position, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), data_posses_point.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.checkBoxSetOnclick(new PayingAdapter.CheckBoxInterface() {
            @Override
            public void onclick(View view, int position) {
                Choose_Points.setText(String.valueOf(mAdapter.getTotal()));
                if(isSoftShowing()){

                }
            }
        });

        getMscardInfoRequest();

    }


    /*判断是否显示软键盘*/
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    /*展示详情点击事件*/
    public void click_expand(View view){

        Intent intent = new Intent(this, PayingDetailsActivity.class);
        startActivity(intent);
    }

    /*确认抵扣按钮点击事件*/
    public void click_finish(View view){

        Intent intent = new Intent(this, PaymentFinishActivity.class);

        ArrayList<String> Points_Result = new ArrayList<>();
        ArrayList<Integer> Business_Image_Result = new ArrayList<>();

        if(Text_NeedPoints.getText().toString().equals(Choose_Points.getText().toString())){
            map = mAdapter.getMap();
            for (Integer i:map.keySet()){
                //map.keySet()返回的是所有key的值
                if(map.get(i)){
                    Points_Result.add(data_posses_point.get(i));
                    Business_Image_Result.add(business_image.get(i));
                }

            }
            Bundle bundle = new Bundle();


            //intent.putExtra("value", (Serializable)map);

            SerializableHashMap myMap=new SerializableHashMap();
            myMap.setMap(map);//将hashmap数据添加到封装的myMap中

            bundle.putStringArrayList("points_result",Points_Result);
            bundle.putIntegerArrayList("image_resource",Business_Image_Result);
            //bundle.putSerializable("checkbox_map", myMap);
            intent.putExtras(bundle);

            startActivity(intent);
        }
        else{
            int diffPoints = Integer.parseInt(Text_NeedPoints.getText().toString()) - Integer.parseInt(Choose_Points.getText().toString());
            Toast.makeText(getApplicationContext(), "所选积分不足，还需"+diffPoints, Toast.LENGTH_SHORT).show();
        }



    }

    /*获得各项积分数据：商家图标、积分数*/
    protected void initData()
    {

        //postStringRequest();
        String name = MerchantInfo.getInstance(PayingActivity.this).getName();
        String logoURL = MerchantInfo.getInstance(PayingActivity.this).getLogoURL();
        //设置需要的积分数
        Text_NeedPoints.setText("120");

        //ImageView_Business.setImageResource(R.drawable.ic_store_24dp);

        //获得并显示商家logo
        Glide.with(PayingActivity.this)
                .load(logoURL)
                .override(200,200)
                .into(ImageView_Business);


        //设置列表项中的文字（用户拥有的积分数）
        data_posses_point = new ArrayList<String>();
        for (int i = 0; i < 20; i++)
        {
            data_posses_point.add("" + i);
        }

        //设置选择积分栏目中商家logo
        business_image = new ArrayList<Integer>();

        for (int i = 0; i < data_posses_point.size(); i++)
        {
            business_image.add(R.drawable.ic_mall_black_24dp);
        }
    }


    private void postStringRequest() {
        String url="http://193.112.44.141:80/citi/merchant/getInfos";
        RequestQueue queue = MyApplication.getHttpQueues();
        //RequestQueue queue= Volley.newRequestQueue(this);
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("success",s);
                System.out.println(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("start","0");
                map.put("n", "1");
                return map;
            }
        };
        queue.add(request);
    }

    private void getMscardInfoRequest() {
        String url="http://193.112.44.141:80/citi/mscard/infos";
        RequestQueue queue = MyApplication.getHttpQueues();
        //RequestQueue queue=Volley.newRequestQueue(this);
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("success",s);
                System.out.println(s);
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.getJSONObject(i);

                        mAdapter.addData(jobj.getString("generalPoints"),jobj.getString("availablePoints"),jobj.getString("logoURL"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("userId",LogStateInfo.getInstance(PayingActivity.this).getUserID());
                map.put("n", "10");
                return map;
            }
        };
        queue.add(request);
    }

    private void postRequest() {
        //RequestQueue queue = VolleySingleton.getVolleySingleton(this.getApplicationContext()).getRequestQueue();
        String url="http://193.112.44.141:80/citi/merchant/getInfos";
        //RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("json data===" + response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name= jsonObject.getString("name");
                        String merchantID= jsonObject.getString("merchantID");
                        String logoURL= jsonObject.getString("logoURL");
                        String description= jsonObject.getString("description");
                        String address= jsonObject.getString("address");

                        System.out.println(name);
                        MerchantInfo.getInstance(PayingActivity.this).setMerchantID(merchantID)
                                .setName(name)
                                .setLogoURL(logoURL)
                                .setDescription(description)
                                .setAddress(address);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();

                map.put("start","0");
                map.put("n", "1");

                return map;
            }
        };

        //queue.add(request);
        //VolleySingleton.getVolleySingleton(this).addToRequestQueue(request);
        //设置请求标签用于加入全局队列后，方便找到
        request.setTag("postsr");
        //添加到全局的请求队列
        MyApplication.getHttpQueues().add(request);
    }



}

