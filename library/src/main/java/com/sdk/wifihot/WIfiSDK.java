package com.sdk.wifihot;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import com.sdk.wifihot.http.*;
import com.sdk.wifihot.utils.Sm4ConvertUtil;
import com.thanosfisherman.wifiutils.TypeEnum;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionScanResultsListener;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WIfiSDK
{
    public static String identification;
    public static String model;
    public static String version;
    public static String code;
    public static String firmware;
    public static String timestamp;
    public static String ssid;
    public static String password;
    private static Handler handler;
    private static Api api1;
    private static Api api3;
    private static Context mContext;
    private WIfiSDK(){}
    public static WIfiSDK instance(Context mContext,String ssid,String password,String identification,String model,String version,
                                   String code,String timestamp,String firmware) {
        WIfiSDK.mContext=mContext;
        WIfiSDK.ssid=ssid;
        WIfiSDK.password=password;
        WIfiSDK.identification=identification;
        WIfiSDK.model=model;
        WIfiSDK.code=code;
        WIfiSDK.version=version;
        WIfiSDK.firmware=firmware;
        WIfiSDK.timestamp=timestamp;

        handler=new Handler();

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

//        IntentFilter filter2 = new IntentFilter();
//        filter2.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        filter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        mContext.registerReceiver(new WifiScanBroadcastReciver(mContext), filter2);

        connect();


        return SingleTonHolder.instance;
    }

    public class ApiRunnable implements Runnable{
        @Override
        public void run() {

        }
    }

    public static void connect(){


        Log.e("WifiActionImp","WIfiSDK.ssid："+WIfiSDK.ssid+",WIfiSDK.password:"+WIfiSDK.password);
        WifiUtils.withContext(mContext)
                .connectWith(WIfiSDK.ssid,WIfiSDK.password, TypeEnum.PSK)
//                .connectWithScanResult(WIfiSDK.password, new ConnectionScanResultsListener() {
//                    @Override
//                    public ScanResult onConnectWithScanResult(List<ScanResult> scanResults) {
//                        for (ScanResult scanResult:scanResults){
//                            if(scanResult.SSID.equalsIgnoreCase(ssid)){
//                                Log.e("WifiActionImp","扫描完成->已找到指定ssid");
//                                return scanResult;
//                            }
//                        }
//                        Log.e("WifiActionImp","扫描完成->未找到指定ssid");
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                connect();
//                            }
//                        },10*1000);
//
//                        return null;
//                    }
//                })
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("WifiActionImp","ap3热点连接成功");
                        requestAp3();
                    }

                    @Override
                    public void failed(ConnectionErrorCode errorCode) {
                        Log.e("WifiActionImp","ap3热点连接失败:"+errorCode.toString());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connect();
                            }
                        },10*1000);
                    }
                })
                .start();
    }
    public static void connect2(String ssid1,String password,String key){
        WifiUtils.withContext(mContext)
                .connectWith(ssid1,password,TypeEnum.PSK)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("WifiActionImp","ap1热点连接成功");

                        LinkedHashMap<String,String> map=new LinkedHashMap<>();
                        map.put("code",WIfiSDK.code);
                        map.put("firmware",WIfiSDK.firmware);
                        map.put("identification",WIfiSDK.identification);
                        map.put("model",WIfiSDK.model);
                        map.put("timestamp",WIfiSDK.timestamp);
                        map.put("version",WIfiSDK.version);

                        StringBuilder stringBuilder=new StringBuilder();
                        for (Map.Entry<String,String> m:map.entrySet()){
                            stringBuilder.append(m.getKey()+m.getValue());
                        }
//                        String md5Key=Sm4ConvertUtil.MD5("Hsl@router");
                        String token=Sm4ConvertUtil.SM4EncForECB(key,stringBuilder.toString());
                        map.put("token",token);
                        requestAp1(map);
                    }

                    @Override
                    public void failed(ConnectionErrorCode errorCode) {
                        Log.e("WifiActionImp","ap1热点连接失败:"+errorCode.toString());
                    }
                })
                .start();
    }
    private static class SingleTonHolder {
        private static final WIfiSDK instance=new WIfiSDK();
    }


    public static void requestAp3(){
        LinkedHashMap<String,String> map=new LinkedHashMap<>();
        map.put("code",WIfiSDK.code);
        map.put("firmware",WIfiSDK.firmware);
        map.put("identification",WIfiSDK.identification);
        map.put("model",WIfiSDK.model);
        map.put("timestamp",WIfiSDK.timestamp);
        map.put("version",WIfiSDK.version);

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
                        if(resultBaseResponse.getCode()!=200)
                            return;
                        Result result=resultBaseResponse.getData();
                        if(result==null)return;
                        connect2(result.getSsid(),result.getPassword(),result.getKey());
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

    public static void requestAp1(LinkedHashMap<String,String> map){

        api1.req(map)
                .retry(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseResponse<Result>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseResponse<Result> resultBaseResponse) {
                        Log.e("WifiActionImp","ap1请求成功:"+resultBaseResponse.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("WifiActionImp","ap1请求失败:"+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }




//    public static void request(){
//        Observable.just(new PostForm("","","","","","",""))
//                .flatMap(new Function<PostForm, Observable<BaseResponse<Result>>>() {
//                    @Override
//                    public Observable<BaseResponse<Result>> apply(PostForm form) throws Exception {
//                        return api3.req(form);
//                    }
//                })
//                .flatMap(new Function<BaseResponse<Result>, Observable<BaseResponse<Result>>>() {
//                    @Override
//                    public Observable<BaseResponse<Result>> apply(BaseResponse<Result> response) throws Exception {
//                        Result result=response.getData();
//                        PostForm postForm=new PostForm("","","","","","","");
//                        return api1.req(postForm);
//                    }
//                })
//                .subscribe(new Observer<BaseResponse<Result>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("WifiActionImp","onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(BaseResponse<Result> response) {
//                        Log.e("WifiActionImp","请求成功:"+response.toString());
//
//                        Result result=response.getData();
//                        connect2(result.getSsid(),result.getPassword());
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("WifiActionImp","请求失败:"+e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("WifiActionImp","onComplete");
//                    }
//                });
//
//    }





}
