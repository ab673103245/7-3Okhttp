package qianfeng.a7_3okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();

    }

    public void onClick1(View view) { // 同步网络请求：execute()



        new Thread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder().url("http://www.baidu.com").build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if(response.isSuccessful())
                    {
                        final String string = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(string);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void onClick2(View view) {  // 异步网络请求 enqueue()

        Request request = new Request.Builder().url("http://www.baidu.com").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 注意OkHttp中这个方法，是在子线程中执行的！
                if (response.isSuccessful()) {
                    Log.d("google-my:", "onResponse: --->" + Thread.currentThread());
                    final String string1 = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(string1);
                        }
                    });
                }
            }
        });




    }

    public void onClick3(View view) { // GET请求传递参数(键值对)
        Request request = new Request.Builder().url("http://10.16.153.35:8080/upload?name=张三&age=199&nickname=李四").build();
        //?name=张三&age=12&nickname=李四


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.d("google-my:", "onResponse: " + response.body().string());
                }
            }
        });
    }

    public void onClick4(View view) { // POST请求键值对传递参数

        FormBody formbody = new FormBody.Builder()
                .add("username","李四")
                .add("password","123")
                .build();

        final Request request = new Request.Builder()
                .post(formbody)
                .url("http://10.16.153.35:8080/upload")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.d("google-my:", "onResponse: " + response.body().string());
                }
            }
        });
    }
}
