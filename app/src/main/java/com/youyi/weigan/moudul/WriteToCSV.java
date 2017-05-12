package com.youyi.weigan.moudul;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.Pulse;
import com.youyi.weigan.beans.UserBean;
import com.youyi.weigan.beans.UserJsonBean;
import com.youyi.weigan.net.RetrofitItfc;
import com.youyi.weigan.service.GATTService;
import com.youyi.weigan.utils.DateUtils;
import com.youyi.weigan.utils.EventUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 2017/4/12.
 */

public class WriteToCSV {

    private UserBean userBean = UserBean.getInstence();
    private Context context;
    private String url;

    public WriteToCSV(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    //和服务器交互：将数据以json的形式传到服务器中
    private void sendToService(final UserJsonBean userJsonBean) {
        String userJson = new Gson().toJson(userJsonBean);
        Log.i("MSL", "writeToServer: " + userJson);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitItfc retrofitItfc = retrofit.create(RetrofitItfc.class);

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userJson);

        Call<ResponseBody> call = retrofitItfc.postUser(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("MSL", "onResponse: OK");
                EventUtil.post("上传成功");

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MSL", "onResponse: Fail" + t);
                EventUtil.post("上传失败");

                //需求：上传失败时，将未上传的数据存为tmp，等待有网络可上传时再次上传
                saveToTmp(userJsonBean);
            }
        });
    }

    private void saveToTmp(UserJsonBean userJsonBean) {
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel";
        File cacheFile = new File(fileDir, userJsonBean.getDeviceId());
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
                Log.i("MSL", "saveToTmp: file not exists and it been created yet ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        UserJsonBean tmpUser = null;
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(cacheFile);
            ois = new ObjectInputStream(fis);
            tmpUser = (UserJsonBean) ois.readObject();

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

        if (tmpUser == null) {
            tmpUser = new UserJsonBean();
            tmpUser.setDeviceId(userJsonBean.getDeviceId());
        }

        //整合两个类,然后再存储
        tmpUser.getAngVArrayList().addAll(userJsonBean.getAngVArrayList());
        tmpUser.getPressureArrayList().addAll(userJsonBean.getPressureArrayList());
        tmpUser.getPulseArrayList().addAll(userJsonBean.getPulseArrayList());
        tmpUser.getGravAArrayList().addAll(userJsonBean.getGravAArrayList());
        tmpUser.getMagArrayList().addAll(userJsonBean.getMagArrayList());


        FileOutputStream fos;
        try {
            fos = new FileOutputStream(cacheFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tmpUser);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("MSL", "saveToTmp:  file write ok");
    }

    public void writeGravA(ArrayList<GravA> gravaList, String name) {
        ArrayList<GravA> list = new ArrayList<>();
        list.addAll(gravaList);
        userBean.getGravAArrayList().clear();

        UserJsonBean userJsonBean = new UserJsonBean(GATTService.DEVICE_ID);
        userJsonBean.setGravAArrayList(list);

        sendToService(userJsonBean);

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//如果不存在SD卡,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
        }
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel/csv";
        File dirFile = new File(fileDir);
//        Log.e("MSL", "writeGravA: dirFile.exists() = " + dirFile.exists());
        if (!dirFile.exists()) {
            boolean iss = dirFile.mkdirs();
//            Log.e("MSL", "writeGravA: filepath not exists,but i creat it :"  + iss);
        }

        File aogFile = new File(fileDir + "/" + name);
//        Log.e("MSL", "writeGravA: aogFile.exists() = " + aogFile.exists());
        if (!aogFile.exists()) {
            try {
                boolean creatResult = aogFile.createNewFile();
                Log.e("MSL", "creat GravA File: " + creatResult);
                addToFileByFileWriter(aogFile.getAbsolutePath(), "time,strengthX,strengthY,strengthZ,deviceId\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String time;
            int x, y, z;
            time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
            if (time.length() != "2017-04-26 11:17:17".length()) {
                Log.e("MSL", "writeAngV: " + time);
                continue;
            }
            x = list.get(i).getVelX();
            y = list.get(i).getVelY();
            z = list.get(i).getVelZ();
            buffer.append(time).append(",").append(x).append(",").append(y).append(",").append(z).append(",").append(GATTService.DEVICE_ID).append("\n");
        }
        addToFileByFileWriter(aogFile.getAbsolutePath(), buffer.toString());
    }

    public void writeAngV(ArrayList<AngV> angVList, String name) {
        ArrayList<AngV> list = new ArrayList<>();
        list.addAll(angVList);
        userBean.getAngVArrayList().clear();

        UserJsonBean userJsonBean = new UserJsonBean(GATTService.DEVICE_ID);
        userJsonBean.setAngVArrayList(list);

        sendToService(userJsonBean);

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//如果不存在SD卡,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
        }
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel/csv";
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) dirFile.mkdirs();

        File csvFile = new File(fileDir + "/" + name);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                addToFileByFileWriter(csvFile.getAbsolutePath(), "time,velX,velY,velZ,deviceId\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String time;
            int x, y, z;
            time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
            if (time.length() != "2017-04-26 11:17:17".length()) {
                Log.e("MSL", "writeAngV: " + time);
                continue;
            }
            x = list.get(i).getVelX();
            y = list.get(i).getVelY();
            z = list.get(i).getVelZ();
            buffer.append(time).append(",").append(x).append(",").append(y).append(",").append(z).append(",").append(GATTService.DEVICE_ID).append("\n");
        }
        addToFileByFileWriter(csvFile.getAbsolutePath(), buffer.toString());
    }

    public void writeMag(ArrayList<Mag> magList, String name) {
        ArrayList<Mag> list = new ArrayList<>();
        list.addAll(magList);
        userBean.getMagArrayList().clear();

        UserJsonBean userJsonBean = new UserJsonBean(GATTService.DEVICE_ID);
        userJsonBean.setMagArrayList(list);

        sendToService(userJsonBean);

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//如果不存在SD卡,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
        }
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel/csv";
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) dirFile.mkdirs();

        File csvFile = new File(fileDir + "/" + name);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                addToFileByFileWriter(csvFile.getAbsolutePath(), "time,strengthX,strengthY,strengthZ,deviceId\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String time;
            int x, y, z;
            time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
            if (time.length() != "2017-04-26 11:17:17".length()) {
                Log.e("MSL", "writeAngV: " + time);
                continue;
            }
            x = list.get(i).getStrengthX();
            y = list.get(i).getStrengthY();
            z = list.get(i).getStrengthZ();
            buffer.append(time).append(",").append(x).append(",").append(y).append(",").append(z).append(",").append(GATTService.DEVICE_ID).append("\n");
        }
        addToFileByFileWriter(csvFile.getAbsolutePath(), buffer.toString());
    }

    public void writePressure(ArrayList<Pressure> PressureList, String name) {
        ArrayList<Pressure> list = new ArrayList<>();
        list.addAll(PressureList);
        userBean.getPressureArrayList().clear();

        UserJsonBean userJsonBean = new UserJsonBean(GATTService.DEVICE_ID);
        userJsonBean.setPressureArrayList(list);

        sendToService(userJsonBean);

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//如果不存在SD卡,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
        }
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel/csv";
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) dirFile.mkdirs();

        File csvFile = new File(fileDir + "/" + name);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                addToFileByFileWriter(csvFile.getAbsolutePath(), "time,intensityOfPressure, deviceId\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String time;
            long pressure;
            time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
            if (time.length() != "2017-04-26 11:17:17".length()) {
                Log.e("MSL", "writeAngV: " + time);
                continue;
            }
            pressure = list.get(i).getIntensityOfPressure();
            buffer.append(time).append(",").append(pressure).append(",").append(GATTService.DEVICE_ID).append("\n");
        }
        addToFileByFileWriter(csvFile.getAbsolutePath(), buffer.toString());
    }

    public void writePulse(ArrayList<Pulse> pulseArrayList, String name) {
        ArrayList<Pulse> list = new ArrayList<>();
        list.addAll(pulseArrayList);
        userBean.getPulseArrayList().clear();

        UserJsonBean userJsonBean = new UserJsonBean(GATTService.DEVICE_ID);
        userJsonBean.setPulseArrayList(list);

        sendToService(userJsonBean);

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//如果不存在SD卡,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
        }
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir += "/vervel/csv";
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) dirFile.mkdirs();

        File csvFile = new File(fileDir + "/" + name);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                addToFileByFileWriter(csvFile.getAbsolutePath(), "time,pulse,trustLevel,deviceId\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String time;
            int pulse;
            int trustLevel;
            time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
            if (time.length() != "2017-04-26 11:17:17".length()) {
                Log.e("MSL", "writeAngV: " + time);
                continue;
            }
            pulse = list.get(i).getPulse();
            trustLevel = list.get(i).getTrustLevel();
            buffer.append(time).append(",").append(pulse).append(",").append(trustLevel).append(",").append(GATTService.DEVICE_ID).append("\n");
        }
        addToFileByFileWriter(csvFile.getAbsolutePath(), buffer.toString());
    }

    //追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
    public void addToFileByOutputStream(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 以追加形式写文件:写文件器，构造函数中的第二个参数为true
    private void addToFileByFileWriter(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
