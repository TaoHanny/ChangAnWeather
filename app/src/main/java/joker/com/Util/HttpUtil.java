package joker.com.Util;


import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkhttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        httpClient.newCall(request).enqueue(callback);
    }
}
