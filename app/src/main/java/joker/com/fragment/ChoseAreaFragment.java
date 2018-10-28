package joker.com.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import joker.com.Gson.City;
import joker.com.Gson.County;
import joker.com.Gson.Province;
import joker.com.Util.HttpUtil;
import joker.com.Util.Utility;
import joker.com.activity.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChoseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private static final String TAG = "QUERY_SQL";
    //适配器
    private  ArrayAdapter<String> adapter;
    //进度条
    ProgressDialog progressDialog ;
    //控件定义
    private Button back_button;
    private ListView listView;
    private TextView title_text;
    //省份
    private  Province province;
    private  List<Province> provinceList;
    //城市
    private  City city;
    private List<City> cityList;
    //县区
    private County county;
    private List<County> countyList;
    //当前的等级
    public static int currentLevel;
    //任意一组数据
    private  List<String> list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chose_area,container,false);
        back_button = view.findViewById(R.id.back_button);
        title_text = view.findViewById(R.id.title_text);
        listView = view.findViewById(R.id.main_list_view);
        Log.d("onCreateView","onCreateView");
         adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_expandable_list_item_1,list);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("onActivityCreated","onActivityCreated");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    province = provinceList.get(i);
                    QueryCities();
                    Log.d("onAtivityCreated","City");
                }else if(currentLevel==LEVEL_CITY) {
                    city = cityList.get(i);
                    QueryCounties();
                    Log.d("onActivityCreated", "County");
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTY){
                    QueryCities();
                }else if(currentLevel==LEVEL_CITY){
                    QueryProvinces();
                }
            }
        });
        QueryProvinces();
        Log.d("OnActivityCreated","Province");
    }

    /**
     * Query cities.
     * 查询城市数据，优先查询数据库，若为空则访问服务器
     */
    public  void QueryCities(){
        title_text.setText(province.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport
                .where("provinceId = ?",String.valueOf(province.getId()))
                .find(City.class);
        if(cityList.size()>0){
            list.clear();
            for (City c:cityList) {
                list.add(c.getCityName());
                Log.d(TAG,c.getCityName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
            listView.setSelection(0);
        }else{
            int code = province.getProvinceCode();
            String addess = "http://guolin.tech/api/china/"+code;
            QueryFromServer(addess,"city");
        }
    }

    /**
     * Query counties.
     * 查询县区的数据信息，优先查询数据库，若为空则访问服务器
     */
    public  void QueryCounties(){
        title_text.setText(city.getCityName());
        back_button.setVisibility(View.VISIBLE);
        countyList = DataSupport
                .where("CityId = ?",String.valueOf(city.getId()))
                .find(County.class);
        if(countyList.size()>0){
            list.clear();
            for (County c:countyList) {
                list.add(c.getCountyName());
                //log
            //    Log.d(TAG,c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
            listView.setSelection(0);
        }else {
            int provinceCode = city.getProvinceId();
            int cityCode = city.getCityCode();
            String addess = "http://guolin.tech/api/china/"+provinceCode + "/" + cityCode;
            QueryFromServer(addess,"county");
        }
    }

    /**
     * 查询省份信息，若数据库里面没有则查询网络数据
     */
    public void QueryProvinces(){
        title_text.setText("中国");
        back_button.setVisibility(View.GONE);
        //LitePal查找语句
        provinceList = DataSupport.findAll(Province.class);
        Log.d(TAG,"QUERY_PROVINCES");
        if(provinceList.size()>0){
            list.clear();
            for (Province pro:provinceList) {
                list.add(pro.getProvinceName());
                //LOG
             Log.d(TAG,pro.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
            listView.setSelection(0);
        }else{
            String addess = "http://guolin.tech/api/china";
            QueryFromServer(addess,"province");
        }
    }

    /**
     * Query from server list.
     * 访问服务器，并将响应的数据保存在SQLite
     * @param addess the addess
     * @param type   the type
     */
    public void QueryFromServer(String addess,final String type){
        //显示进度条
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(addess, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialg();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.headerCityResponse(responseText,province.getId());
                }else{
                    result = Utility.headerCountyResponse(responseText,city.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialg();
                            if ("province".equals(type)){
                                QueryProvinces();
                            }else if("city".equals(type)){
                                QueryCities();
                            }else{
                                QueryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog==null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialg(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
