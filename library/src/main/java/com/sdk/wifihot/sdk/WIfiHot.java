package com.sdk.wifihot.sdk;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.sdk.wifihot.BuildConfig;
import com.sdk.wifihot.WIfiSDK;
import com.sdk.wifihot.http.Api;
import com.sdk.wifihot.http.BaseResponse;
import com.sdk.wifihot.http.LoggingInterceptor;
import com.sdk.wifihot.http.Result;
import com.sdk.wifihot.utils.SharedPreferencesUtil;
import com.sdk.wifihot.utils.Sm4ConvertUtil;
import com.thanosfisherman.wifiutils.TypeEnum;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import io.reactivex.*;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class WIfiHot {
    private static Context mContext;
    //是否取消轮询
    private static boolean isStop = false;
    private static Api api1;
    private static Api api3;
    private static Timer timer;
    /**初始化
     * @param mContext
     */
    public static void init(Context mContext){
        WIfiHot.mContext=mContext;
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .retryOnConnectionFailure(true);

        //添加拦截
        if (BuildConfig.DEBUG) {
            client.addInterceptor(new LoggingInterceptor());
        }
        OkHttpClient okHttpClient=client.build();


        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASEURL_AP1)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        Retrofit retrofit3= new Retrofit.Builder()
                .baseUrl(BuildConfig.BASEURL_AP3)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api3=retrofit3.create(Api.class);
        api1=retrofit1.create(Api.class);
    }
    private static void connectAp3(RequestData requestData){
        WifiUtils.withContext(mContext)
                .connectWith(requestData.getSsid(),requestData.getPassword(), TypeEnum.PSK)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("WifiActionImp","ap3热点连接成功");
                        requestAp3(requestData);
                    }
                    @Override
                    public void failed(ConnectionErrorCode errorCode) {
                        Log.e("WifiActionImp","ap3热点连接失败");
                    }
                })
                .start();
    }
    private static void connectAp1(RequestData requestData,Result result){
        WifiUtils.withContext(mContext)
                .connectWith(requestData.getSsid(),requestData.getPassword(), TypeEnum.PSK)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("WifiActionImp","ap1热点连接成功");
                        requestAp1(requestData,result);
                    }
                    @Override
                    public void failed(ConnectionErrorCode errorCode) {
                        Log.e("WifiActionImp","ap1热点连接失败");
                    }
                })
                .start();
    }
    private static void requestAp1(RequestData requestData,Result result){
        LinkedHashMap<String,String> map=new LinkedHashMap<>();
        map.put("code",requestData.getCode());
        map.put("firmware",requestData.getFirmware());
        map.put("identification",requestData.getIdentification());
        map.put("model",requestData.getModel());
        map.put("timestamp",requestData.getTimestamp());
        map.put("version",requestData.getVersion());

        StringBuilder stringBuilder=new StringBuilder();
        for (Map.Entry<String,String> m:map.entrySet()){
            stringBuilder.append(m.getKey()+m.getValue());
        }
        String token=Sm4ConvertUtil.SM4EncForECB(result.getKey(),stringBuilder.toString());
        map.put("token",token);
        api1.req(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseResponse<Result>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }
                    @Override
                    public void onNext(@NonNull BaseResponse<Result> resultBaseResponse) {
                        Log.e("WifiActionImp","ap1请求成功:"+resultBaseResponse.toString());
                        if(resultBaseResponse.getCode()!=200){
                            return;
                        }
                        Result result=resultBaseResponse.getData();
                        if(result==null){
                            return;
                        }
                        SharedPreferencesUtil.putString(mContext,SharedPreferencesUtil.AP1_CODE,result.getCode());
                        SharedPreferencesUtil.putString(mContext,SharedPreferencesUtil.AP1_SSID,result.getSsid());
                        SharedPreferencesUtil.putString(mContext,SharedPreferencesUtil.AP1_PASSWORD,result.getPassword());
                        SharedPreferencesUtil.putString(mContext,SharedPreferencesUtil.AP1_KEY,result.getKey());
                        stop();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("WifiActionImp","ap1请求失败:"+e.getMessage());
                        SharedPreferencesUtil.remove(mContext,SharedPreferencesUtil.AP1_CODE);
                        SharedPreferencesUtil.remove(mContext,SharedPreferencesUtil.AP1_SSID);
                        SharedPreferencesUtil.remove(mContext,SharedPreferencesUtil.AP1_PASSWORD);
                        SharedPreferencesUtil.remove(mContext,SharedPreferencesUtil.AP1_KEY);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    private static void requestAp3(RequestData requestData){
        LinkedHashMap<String,String> map=new LinkedHashMap<>();
        map.put("code",requestData.getCode());
        map.put("firmware",requestData.getFirmware());
        map.put("identification",requestData.getIdentification());
        map.put("model",requestData.getModel());
        map.put("timestamp",requestData.getTimestamp());
        map.put("version",requestData.getVersion());

        StringBuilder stringBuilder=new StringBuilder();
        for (Map.Entry<String,String> m:map.entrySet()){
            stringBuilder.append(m.getKey()+m.getValue());
        }
        String md5Key=Sm4ConvertUtil.MD5("Hsl@router");
        String token=Sm4ConvertUtil.SM4EncForECB(md5Key,stringBuilder.toString());
        map.put("token",token);
        api3.req(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseResponse<Result>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }
                    @Override
                    public void onNext(@NonNull BaseResponse<Result> resultBaseResponse) {
                        Log.e("WifiActionImp","ap3请求成功:"+resultBaseResponse.toString());
                        if(resultBaseResponse.getCode()!=200){
                            return;
                        }
                        Result result=resultBaseResponse.getData();
                        if(result==null){
                            return;
                        }
                        connectAp1(requestData,result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("WifiActionImp","ap3请求失败:"+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**开始连接ap3
     * @param requestData
     */
    public static void start(RequestData requestData){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String ap1SSid=SharedPreferencesUtil.getString(mContext,SharedPreferencesUtil.AP1_SSID,"");
                if(TextUtils.isEmpty(ap1SSid)){
                    //本地没有ap1
                    connectAp3(requestData);
                }else{
                    //本地有ap1
                    String ap1_key=SharedPreferencesUtil.getString(mContext,SharedPreferencesUtil.AP1_KEY,"");
                    String ap1_ssid=SharedPreferencesUtil.getString(mContext,SharedPreferencesUtil.AP1_SSID,"");
                    String ap1_password=SharedPreferencesUtil.getString(mContext,SharedPreferencesUtil.AP1_PASSWORD,"");
                    String ap1_code=SharedPreferencesUtil.getString(mContext,SharedPreferencesUtil.AP1_CODE,"");
                    Result result=new Result(ap1_key,ap1_ssid,ap1_password,ap1_code);
                    connectAp1(requestData,result);
                }
            }
        },10*1000,30*1000);
    }


    /**
     *是否停止轮询
     */
    public static void stop(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
}
