package com.youyi.weigan.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.youyi.weigan.beans.UserBean;
import com.youyi.weigan.beans.UserJsonBean;
import com.youyi.weigan.dbUtils.DataBaseContext;
import com.youyi.weigan.dbUtils.SqliteHelper;
import com.youyi.weigan.eventbean.Comm2WriteService;
import com.youyi.weigan.moudul.WriteToCSV;
import com.youyi.weigan.net.RetrofitItfc;
import com.youyi.weigan.utils.ConstantPool;
import com.youyi.weigan.utils.EventUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 将数据写入本地 (sqlite3 & csv) 的service
 */
public class WriteService extends Service {
    private String url;
    private Context context;
    private boolean isUploading = false;

    public WriteService() {
    }

    private WriteToCSV writeToCSV;
    private UserBean userBean;
    private final int DATA_SIZE = 1;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventUtil.register(this);
        userBean = UserBean.getInstence();
        Log.i("MSL", "onCreate: write service");
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    //存储为sqlite3文件
//                    writeBySqlite3();

//                    存储为本地csv文件,两种存储方式不能同时处理(list.clear()的原因)
                    writeByCsv();
                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String net = intent.getStringExtra("net");
        Log.d("MSL", "onStartCommand: 当前网络：" + net);
        if (net.equals("mobile or null")) {
            url = ConstantPool.URL_DEBUG_WLAN;
        } else if (net.equals("wifi")) {
            if (TextUtils.isEmpty(intent.getStringExtra("wifiName")) || TextUtils.isEmpty(intent.getStringExtra("wifiMac"))) {
                url = ConstantPool.URL_DEBUG_WLAN;
            }
            Log.d("MSL", "onStartCommand: " + intent.getStringExtra("wifiMac") + " ," + intent.getStringExtra("wifiName"));
            String wifiMac = intent.getStringExtra("wifiMac");
            String wifiName = intent.getStringExtra("wifiName");
            Log.d("MSL", "onStartCommand: " + wifiMac.equals(ConstantPool.debugWifiMac));
            if (wifiMac.equals(ConstantPool.debugWifiMac) || wifiMac.equals(ConstantPool.debugWifiMac2)
                    || wifiName.equals(ConstantPool.debugWifiName))
                url = ConstantPool.URL_DEBUG_LAN;
            else {
                url = ConstantPool.URL_DEBUG_WLAN;
            }

            //有网络状况下，重新上传缓存的数据
            if (!isUploading) {
                isUploading = true;

//                upLoadCache(url);

            }
        }
        writeToCSV = new WriteToCSV(this, url);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commWithActivity(Comm2WriteService comm2WriteService) {
        switch (comm2WriteService) {
            case UpLoadCache:
                upLoadCache(url);
                break;
        }
    }


    private void upLoadCache(final String url) {
        //遍历预置的文件夹，如果有文件，就读出来

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel";
        File dir = new File(fileDir);

        final File[] files = dir.listFiles();

        if (files == null) return;


        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) continue;//如果是文件夹，忽略它
            String fileName = files[i].getName();
            final File file = files[i];
            Log.i("MSL", "upLoadCache: " + fileName);

            ObjectInputStream ois = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);

                UserJsonBean tmpUser = (UserJsonBean) ois.readObject();
                String userJson = new Gson().toJson(tmpUser);
//                LogUtil.LogMSL("MSL", userJson);
//              设置timeout最长时间为10分钟
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(600, TimeUnit.SECONDS)
                        .writeTimeout(600, TimeUnit.SECONDS)
                        .readTimeout(600, TimeUnit.SECONDS).build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create()).client(client)
                        .build();

                RetrofitItfc retrofitItfc = retrofit.create(RetrofitItfc.class);

                RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userJson);

                Call<ResponseBody> call = retrofitItfc.postUser(requestBody);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("MSL", "onResponse: OK");
                        EventUtil.post("缓存上传成功");
                        isUploading = false;
                        Log.i("MSL", "onResponse: delete file");
                        boolean deleteResult = file.delete();
                        Log.d("MSL", "onResponse: " + deleteResult);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("MSL", "onResponse: Fail" + t);
                        EventUtil.post("上传失败");
                        isUploading = false;
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (ois != null) try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    public void writeByCsv() {


        if (userBean.getGravAArrayList().size() >= DATA_SIZE) {
            Log.i("MSL", "writeByCsv: " + userBean.getGravAArrayList().size() + "," + userBean.getMagArrayList().size() + "," +
                    userBean.getAngVArrayList().size() + "," + userBean.getPressureArrayList().size() + "," + userBean.getPulseArrayList().size());
            writeToCSV.writeGravA(userBean.getGravAArrayList(), "GravA.csv");
        }
        if (userBean.getMagArrayList().size() >= DATA_SIZE) {
            Log.i("MSL", "writeByCsv: " + userBean.getGravAArrayList().size() + "," + userBean.getMagArrayList().size() + "," +
                    userBean.getAngVArrayList().size() + "," + userBean.getPressureArrayList().size() + "," + userBean.getPulseArrayList().size());
            writeToCSV.writeMag(userBean.getMagArrayList(), "Mag.csv");
        }
        if (userBean.getAngVArrayList().size() >= DATA_SIZE) {
            Log.i("MSL", "writeByCsv: " + userBean.getGravAArrayList().size() + "," + userBean.getMagArrayList().size() + "," +
                    userBean.getAngVArrayList().size() + "," + userBean.getPressureArrayList().size() + "," + userBean.getPulseArrayList().size());
            writeToCSV.writeAngV(userBean.getAngVArrayList(), "AngV.csv");
        }
        if (userBean.getPressureArrayList().size() >= DATA_SIZE) {
            Log.i("MSL", "writeByCsv: " + userBean.getGravAArrayList().size() + "," + userBean.getMagArrayList().size() + "," +
                    userBean.getAngVArrayList().size() + "," + userBean.getPressureArrayList().size() + "," + userBean.getPulseArrayList().size());
            writeToCSV.writePressure(userBean.getPressureArrayList(), "Pressure.csv");
        }
        if (userBean.getPulseArrayList().size() >= DATA_SIZE) {
            Log.i("MSL", "writeByCsv: " + userBean.getGravAArrayList().size() + "," + userBean.getMagArrayList().size() + "," +
                    userBean.getAngVArrayList().size() + "," + userBean.getPressureArrayList().size() + "," + userBean.getPulseArrayList().size());
            writeToCSV.writePulse(userBean.getPulseArrayList(), "Pulse.csv");
        }
    }

    public void writeBySqlite3() {
        if (userBean.getGravAArrayList().size() >= DATA_SIZE
                || userBean.getMagArrayList().size() >= DATA_SIZE
                || userBean.getAngVArrayList().size() >= DATA_SIZE
                || userBean.getPressureArrayList().size() >= DATA_SIZE
                || userBean.getPulseArrayList().size() >= DATA_SIZE
                ) {
            SqliteHelper sqliteHelper = new SqliteHelper(new DataBaseContext(getApplicationContext()));//存在SD卡中
//            SqliteHelper sqliteHelper = new SqliteHelper(getApplicationContext());//存在内存中
            sqliteHelper.insertDataBySw();
        }
    }

}
