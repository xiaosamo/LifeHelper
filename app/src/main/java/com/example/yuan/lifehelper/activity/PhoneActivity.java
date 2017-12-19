package com.example.yuan.lifehelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuan.lifehelper.R;
import com.example.yuan.lifehelper.bean.PhoneBean;
import com.example.yuan.lifehelper.Api.PhoneApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.yuan.lifehelper.activity.MainActivity.KEY;

public class PhoneActivity extends AppCompatActivity
{
    private static final String TAG = "PhoneActivity";
    private EditText inputPhone;
    private Button sendPhone;
    private TextView showText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("手机号查询");

        inputPhone = (EditText) findViewById(R.id.et_input_phone);
        sendPhone = (Button) findViewById(R.id.btn_queryphone);
        showText = (TextView) findViewById(R.id.show_text);


        sendPhone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String inputText = inputPhone.getText().toString();
                if(isOk(inputText))
                {
                    getData();
                }
                else
                {
                    Toast.makeText(PhoneActivity.this, "手机号码不正确，请检查", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getData()
    {
        String baseUrl = "http://apicloud.mob.com/";
        Retrofit retrofit = new Retrofit.Builder()
                //设置请求网络的url地址
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())  //添加GSON
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //添加RxJava
                .build();

        //创建网络请求接口实例
        PhoneApi request = retrofit.create(PhoneApi.class);

        String phone = inputPhone.getText().toString();

        //RxJava实现请求
        request.getPhone(KEY, phone)
                //数据通过rxjava提交先在io线程里
                .subscribeOn(Schedulers.io())
                //返回到主线程
                .observeOn(AndroidSchedulers.mainThread())
                //通过subscribe实现订阅关系
                .subscribe(new Observer<PhoneBean>()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {

                    }

                    @Override
                    public void onNext(PhoneBean value)
                    {
                        if (!"success".equals(value.getMsg()))
                        {
                            Toast.makeText(PhoneActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "city="+value.getResult().getCity());
                        String builder=
                                        value.getResult().getProvince()+"\n"+
                                        value.getResult().getCity()+"\n"+
                                        "运营商信息："+value.getResult().getOperator()+"\n"+
                                        value.getResult().getCityCode()+"\n"+
                                        value.getResult().getMobileNumber()+"\n"+
                                        "邮编："+value.getResult().getZipCode();

                        showText.setText(builder);
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                    }

                    @Override
                    public void onComplete()
                    {

                    }
                });

    }

    private boolean isOk(String inputText)
    {
        if (!TextUtils.isEmpty(inputText)&&!inputText.contains(" ")&&inputText.length()==11)
        {
            return true;
        }
        return false;
    }
}
