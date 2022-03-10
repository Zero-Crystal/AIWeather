package com.zero.aiweather.voice.config;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.zero.aiweather.voice.util.Auth;
import com.zero.aiweather.voice.util.IOfflineResourceConst;
import com.zero.base.AIWeatherApplication;
import com.zero.base.util.LogUtils;
import com.zero.base.util.ToastUtils;

public class VoiceConfig implements IOfflineResourceConst {
    private final String TAG = "VoiceDemo";

    private static VoiceConfig instance;
    protected String appId;
    protected String appKey;
    protected String secretKey;
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = DEFAULT_OFFLINE_TTS_MODE;
    protected SpeechSynthesizer mSpeechSynthesizer;
    private boolean isSpeaking = false;

    private VoiceConfig() {
    }

    public static VoiceConfig getInstance() {
        if (instance == null) {
            instance = new VoiceConfig();
        }
        return instance;
    }

    public void initAuth() {
        appId = Auth.getInstance(AIWeatherApplication.getContext()).getAppId();
        appKey = Auth.getInstance(AIWeatherApplication.getContext()).getAppKey();
        secretKey = Auth.getInstance(AIWeatherApplication.getContext()).getSecretKey();
    }

    public boolean initBdVoice() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(AIWeatherApplication.getContext());
        // 2. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {

            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {

            }

            @Override
            public void onSynthesizeFinish(String s) {

            }

            @Override
            public void onSpeechStart(String s) {
                isSpeaking = true;
                LogUtils.d(TAG, "SpeechSynthesizerListener: ---------------> 开始播放");
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
            }

            @Override
            public void onSpeechFinish(String s) {
                isSpeaking = false;
                LogUtils.d(TAG, "SpeechSynthesizerListener: ---------------> 播放结束");
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                isSpeaking = false;
                LogUtils.d(TAG, "SpeechSynthesizerListener: ---------------> 播放错误：" + speechError.code + ", " + speechError.description);
            }
        });
        // 3. 设置appId，appKey.secretKey
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(appKey, secretKey);
        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "1");
        // 设置合成的音量，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        // AuthInfo接口用于测试开发者是否成功申请了在线或者离线授权
        AuthInfo authInfo = mSpeechSynthesizer.auth(ttsMode);
        if (authInfo.isSuccess()) {
            ToastUtils.toastShort("auth success");
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            ToastUtils.toastShort("auth failed errorMsg=" + errorMsg);
        }
        int result = mSpeechSynthesizer.initTts(ttsMode);
        LogUtils.d(TAG, "result code: " + result);
        if (result == 0) {
            return true;
        }
        return false;
    }

    public void speak(String TEXT) {
        if (mSpeechSynthesizer == null) {
            LogUtils.d(TAG, "[ERROR], 初始化失败");
            return;
        }
        int result = mSpeechSynthesizer.speak(TEXT);
    }

    public void stop() {
        LogUtils.d(TAG, "停止合成引擎 按钮已经点击");
        int result = mSpeechSynthesizer.stop();
        isSpeaking = false;
        LogUtils.d(TAG, "SpeechSynthesizerListener: ---------------> 停止播放");
    }

    public void release() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            LogUtils.d(TAG, "释放资源成功");
        }
    }

    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public SpeechSynthesizer getSpeechSynthesizer() {
        return mSpeechSynthesizer;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }
}
