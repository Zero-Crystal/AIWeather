package com.zero.aiweather.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.zero.aiweather.R;
import com.zero.aiweather.contract.CityViewModel;
import com.zero.aiweather.databinding.ActivityMainBinding;
import com.zero.aiweather.ui.fragment.QueryFragment;
import com.zero.aiweather.ui.fragment.WeatherFragment;
import com.zero.aiweather.voice.ui.activity.VoiceDemoActivity;
import com.zero.base.base.BaseActivity;
import com.zero.base.controller.ActivityController;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;
import com.zero.base.util.ToastUtils;

import java.lang.reflect.Method;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private SearchView.SearchAutoComplete searchComplete;
    private SearchView mSearchView;
    //viewModel
    private CityViewModel viewModel;
    private long exitTime = 0;
    private boolean isFirstFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CityViewModel.class);
        replaceFragment(new WeatherFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_main_search);
        //获得SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        //设置Toolbar折叠图标
        ImageView queryBtn = mSearchView.findViewById(R.id.search_button);
        queryBtn.setImageResource(R.drawable.icon_query);
        //通过id得到输入框控件
        searchComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        searchViewListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            LogUtils.d(TAG, "MainActivity: ---------> toolbar home键点击事件!");
            if (searchComplete.isShown()) {
                //点击叉折叠搜索框
                clickBack();
            } else {
                binding.dlActivityMain.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && searchComplete.isShown()) {
            clickBack();
        } else {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initToolbar() {
        setSupportActionBar(binding.tbTitle);
        //这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
        ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_functions);
        }
        binding.nvHome.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_voice_demo:
                        Intent intent = new Intent(MainActivity.this, VoiceDemoActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    private void searchViewListener() {
        //监听文本变化
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard(searchComplete);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //通知SearchFragment更新搜索结果
                viewModel.setInputName(newText);
                LogUtils.d(QueryFragment.TAG, "MainActivity: -------------------> onQueryTextChange: " + newText);
                return false;
            }
        });
        //监听输入框焦点变化
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    replaceFragment(new QueryFragment());
                }
            }
        });
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 3 * 1000) {
            ToastUtils.toastShort("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            ActivityController.finishAll();
        }
    }

    /**
     * 点击返回（包括手机物理按键）
     */
    public void clickBack() {
        try {
            //折叠搜索框
            searchComplete.setText("");
            Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
            method.setAccessible(true);
            method.invoke(mSearchView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_content, fragment);
        if (!isFirstFragment) {
            transaction.addToBackStack(null);
        } else {
            isFirstFragment = false;
        }
        transaction.commit();
    }

    /**
     * 隐藏软键盘
     * */
    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public static void startMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}