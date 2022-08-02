package com.zero.aiweather.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.SearchResultAdapter;
import com.zero.aiweather.contract.SearchContract;
import com.zero.aiweather.databinding.FragmentSearchResultBinding;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.view.IBackListener;
import com.zero.aiweather.viewModel.SearchViewModel;
import com.zero.base.baseMvpNet.MvpFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends MvpFragment<SearchContract.SearchPresenter> implements SearchContract.ISearchView {
    private final String TAG = "SearchResultFragment";
    private static SearchFragment fragment;
    private FragmentSearchResultBinding binding;
    private SearchViewModel searchViewModel;
    private List<CitySearchResponse.Location> resultList = new ArrayList<>();
    private SearchResultAdapter searchAdapter;

    private IBackListener backListener;

    public static SearchFragment newInstance(IBackListener backListener) {
        if (fragment == null) {
            fragment = new SearchFragment();
            fragment.backListener = backListener;
        }
        return fragment;
    }

    @Override
    protected SearchContract.SearchPresenter createPresenter() {
        return new SearchContract.SearchPresenter();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        searchViewModel = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);
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
        searchViewModel.getInputLocation().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: >>>>>>>>>>>>>>>>>>>> location: " + s);
                if (!TextUtils.isEmpty(s)) {
                    mPresenter.searchCity(s);
                }
            }
        });
        searchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                searchViewModel.setSearchResult(resultList.get(position));
                backListener.onBack(SearchFragment.this);
            }
        });
    }

    @Override
    public View getLayoutView() {
        binding = FragmentSearchResultBinding.inflate(getInflater(), getContainer(), false);
        return binding.getRoot();
    }

    @Override
    public void showSearchResult(CitySearchResponse citySearchResponse) {
        resultList.clear();
        if (citySearchResponse.getLocation() != null) {
            resultList.addAll(citySearchResponse.getLocation());
            Log.d(TAG, "showSearchResult: <<<<<<<<<<<<<<<<<<<<<<<< result: "
                    + citySearchResponse.getLocation().get(0).getName() + ", " + citySearchResponse.getLocation().get(0).getId());
        }
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message) {
        ToastUtil.showToast(message);
    }
}
