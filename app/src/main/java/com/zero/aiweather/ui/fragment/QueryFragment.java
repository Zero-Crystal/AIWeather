package com.zero.aiweather.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zero.aiweather.adapter.LocationAdapter;
import com.zero.aiweather.contract.CityViewModel;
import com.zero.aiweather.contract.LocationContract;
import com.zero.aiweather.contract.LocationPresenter;
import com.zero.aiweather.databinding.FragmentCityQueryBinding;
import com.zero.aiweather.model.bean.LocationBean;
import com.zero.aiweather.model.bean.SearchCityBean;
import com.zero.aiweather.model.response.CityResponse;
import com.zero.aiweather.ui.activity.MainActivity;
import com.zero.base.base.BaseFragment;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;
import com.zero.base.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class QueryFragment extends BaseFragment implements LocationContract.IView {
    public static final String TAG = "QueryFragment";

    private FragmentCityQueryBinding binding;
    private List<LocationBean> locationList = new ArrayList<>();
    private LocationAdapter adapter;
    private LocationPresenter presenter;
    private CityViewModel cityModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCityQueryBinding.inflate(inflater, container,false);
        presenter = new LocationPresenter(this, getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        initListView();
    }

    private void initListView() {
        adapter = new LocationAdapter(locationList, getContext());
        binding.lvSearchResult.setAdapter(adapter);
        binding.lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LogUtils.d(TAG, "SearchFragment: ------------------> initListView: 更新搜索结果，id=" + locationList.get(position).getId()
                        + "，name=" + locationList.get(position).getName());
                //折叠输入框
                MainActivity activity = (MainActivity) getActivity();
                activity.clickBack();
                //通知WeatherFragment更新页面
                SPUtils.saveStartModel(Constant.SP_START_MODEL, true);
                cityModel.setCityBean(new SearchCityBean(locationList.get(position).getId(), locationList.get(position).getName()));
                //退出当前Fragment
                getFragmentManager().popBackStack();
            }
        });
    }

    private void initViewModel() {
        cityModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(CityViewModel.class);
        cityModel.getInputName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String input) {
                presenter.getLocationId(input);
                adapter.setInputText(input);
                LogUtils.d(TAG, "SearchFragment: ------------------> initViewModel: 更新输入内容=" + input);
            }
        });
    }

    @Override
    public void getCityNameList(CityResponse response) {
        LogUtils.d(TAG, "SearchFragment: ------------------> getCityNameList: 搜索结果, city size=" + response.getLocation().size());
        locationList.clear();
        locationList.addAll(response.getLocation());
        adapter.notifyDataSetChanged();
    }
}
