package joker.com.Util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import joker.com.Gson.City;
import joker.com.Gson.County;
import joker.com.Gson.Province;

/**
 * The type Utility.
 */
public class Utility {

    private static final String TAG="Utility";
    /**
     * 将服务器返回的省份（Province）数据存储在数据库中
     * 数据库框架：LitePal 1.6
     * @param response the response
     * @return the boolean
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject jsonObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
//    public static boolean handleProvinceResponse(String response){
//        Log.d(TAG,"Province");
//        if(!TextUtils.isEmpty(response)){
//            Gson gson = new Gson();
//            List<Province> list = gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
//            for (Province pro: list) {
//                Province province = new Province();
//                province.setProvinceName(pro.getProvinceName());
//                province.setProvinceCode(pro.getProvinceCode());
//                //LitePal框架
//                Log.d(TAG,pro.getProvinceName());
//                province.save();
//            }
//            return true;
//        }
//        return false;
//    }

    /**
     * 将city的数据保存到SQlite数据库
     * @param response 响应的数据
     * @return the boolean
     */
    public static boolean headerCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject jsonObject = allProvinces.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
    public static boolean headerCityResponse(String response,int province){
        Log.d(TAG,"City");
        if(!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            List<City> list = gson.fromJson(response,new TypeToken<List<City>>(){}.getType());
            for (City c:list) {
                City city = new City();
                city.setProvinceId(province);
                city.setCityCode(c.getCityCode());
                city.setCityName(c.getCityName());
                Log.d(TAG,"City");
                city.save();
            }
            return true;
        }
        return false;
    }
    */

    /**
     * 将county数据存入数据库
     * @param response 响应的数据
     * @param cityId     县区所属的市
     * @return the boolean
     */
    public static boolean headerCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject jsonObject = allProvinces.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getInt("id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
    public static boolean headerCountyResponse(String response,int city){
        Log.d(TAG,"County");
        if(!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            List<County> list = gson.fromJson(response,new TypeToken<List<County>>(){}.getType());
            for (County c:list) {
                County county = new County();
                county.setCityId(city);
                county.setCountyName(c.getCountyName());
                county.setWeatherId(c.getWeatherId());
                Log.d(TAG,c.getCountyName());
                county.save();
            }
            return true;
        }
        return false;
    }
    */


}
