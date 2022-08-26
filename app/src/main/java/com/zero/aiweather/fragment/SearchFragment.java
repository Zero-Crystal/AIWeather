package com.zero.aiweather.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zero.aiweather.R;
import com.zero.aiweather.adapter.SearchResultAdapter;
import com.zero.aiweather.presenter.SearchPresenter;
import com.zero.aiweather.databinding.FragmentSearchResultBinding;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.contract.SearchContract;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.activity.IBackListener;
import com.zero.aiweather.viewModel.MainViewModel;
import com.zero.base.baseMvp.MvpFragment;
import com.zero.base.util.KLog;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends MvpFragment<SearchPresenter> implements SearchContract.ISearchView {
    private FragmentSearchResultBinding binding;
    private MainViewModel mainViewModel;
    private final List<CitySearchResponse.Location> resultList = new ArrayList<>();
    private SearchResultAdapter searchAdapter;

    private IBackListener backListener;

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter();
    }

    @Override
    public View getLayoutView() {
        binding = FragmentSearchResultBinding.inflate(getInflater());
        return binding.getRoot();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        initList();
        initListener();
    }

    private void initList() {
        searchAdapter = new SearchResultAdapter(R.layout.item_recyclerview_search_result, resultList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.rlvSearchResult.setLayoutManager(manager);
        binding.rlvSearchResult.setAdapter(searchAdapter);
    }

    private void initListener() {
        mainViewModel.getInputLocation().observe(this, s -> {
            KLog.d("onChanged: >>>>>>>>>>>>>>>>>>>> location: " + s);
            if (!TextUtils.isEmpty(s)) {
                mPresenter.searchCity(s);
            }
        });
        searchAdapter.setOnItemClickListener((adapter, view, position) -> {
            mainViewModel.setSearchResult(resultList.get(position));
            backListener.onBack(SearchFragment.this);
        });
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void showSearchResult(CitySearchResponse citySearchResponse) {
        resultList.clear();
        if (citySearchResponse.getLocation() != null) {
            resultList.addAll(citySearchResponse.getLocation());
            KLog.d("showSearchResult: <<<<<<<<<<<<<<<<<<<<<<<< result: "
                    + citySearchResponse.getLocation().get(0).getName() + ", " + citySearchResponse.getLocation().get(0).getId());
        }
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message) {
        ToastUtil.showToast(message);
    }

    public void setBackListener(IBackListener backListener) {
        this.backListener = backListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backListener = null;
    }
}
