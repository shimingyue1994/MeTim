package com.yue.libtim.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.yue.libtim.R;
import com.yue.libtim.fragment.InputMoreFragment;
import com.yue.libtim.utils.soft.SoftKeyBoardListener;
import com.yue.libtim.utils.soft.SoftKeyBoardUtil;

/**
 * @author shimy
 * @create 2020/6/2 14:47
 * @desc 输入底部布局
 */
public class InputLayout extends FrameLayout {


    private enum InputState {
        INPUT_VOICE,
        INPUT_FACE,
        INPUT_MORE,
        INPUT_TEXT
    }

    /*当前的输入状态*/
    private InputState inputState = InputState.INPUT_TEXT;

    private ImageView ivVoice;
    private ImageView ivFace;
    private ImageView ivMore;
    private EditText etInput;
    private LinearLayout llInput;
    private Button btnSend;
    private Button btnVoicePress;//按住说话
    private FrameLayout flMore;
    private AppCompatActivity activity;

    public InputLayout(@NonNull Context context) {
        this(context, null);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.layout_input2, this);
        ivVoice = view.findViewById(R.id.iv_voice);
        ivFace = view.findViewById(R.id.iv_face);
        ivMore = view.findViewById(R.id.iv_more);
        etInput = view.findViewById(R.id.et_input);
        llInput = view.findViewById(R.id.ll_input_text);
        btnSend = view.findViewById(R.id.btn_send);
        btnVoicePress = view.findViewById(R.id.btn_voice_press);
        flMore = view.findViewById(R.id.fl_more);
        if (!TextUtils.isEmpty(etInput.getText().toString())) {
            btnSend.setVisibility(VISIBLE);
            ivMore.setVisibility(GONE);
        } else {
            btnSend.setVisibility(GONE);
            ivMore.setVisibility(VISIBLE);
        }
//        etInput.setVisibility(GONE);
//        btnVoicePress.setVisibility(VISIBLE);
        initView();
        initVoicePress();

    }

    public void init(AppCompatActivity activity) {
        this.activity = activity;
        /*在登录的时候就应该把高度初始化好*/
        SoftKeyBoardListener.setListener(activity, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (height > 0 && SoftKeyBoardUtil.getSaveHeight() <= 0) {
                    SoftKeyBoardUtil.putHeight(height);
                }
            }

            @Override
            public void keyBoardHide(int height) {
                if (height > 0 && SoftKeyBoardUtil.getSaveHeight() <= 0) {
                    SoftKeyBoardUtil.putHeight(height);
                }
            }
        });
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initView() {


        /**
         * 输入框触摸事件
         */
        etInput.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
        /*语音按钮*/
        ivVoice.setOnClickListener(v -> {
            if (inputState == InputState.INPUT_TEXT) {

            } else if (inputState == InputState.INPUT_VOICE) {

            } else if (inputState == InputState.INPUT_MORE) {

            } else if (inputState == InputState.INPUT_FACE) {

            }
        });
        /*表情按钮*/
        ivFace.setOnClickListener(v -> {

        });
        /*更多操作的按钮*/
        ivMore.setOnClickListener(v -> {
            if (inputState != InputState.INPUT_MORE) {
                inputState = InputState.INPUT_MORE;
                /*nothing 不会因为输入框移动布局,防止下一次弹出突然将更多布局和编辑框一起顶到最顶部*/
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                hideSoftInput();
                /*清除输入框焦点 清不清都不影响*/
                etInput.clearFocus();
                flMore.setVisibility(VISIBLE);
                showMore();
            } else {
                /*获取输入框焦点,使得showSoftInput 弹出有效*/
                etInput.requestFocus();
                inputState = InputState.INPUT_TEXT;
                showSoftInput();
                /*重要!!!!!--->>>> 延迟200毫秒设置为ADJUST_RESIZE模式(可以改变布局,顶起edittext),并隐藏更多布局,
                不加延迟会引起闪烁,因为键盘还没有弹起,但flMore隐藏了,输入框会猛地向下,加个延迟等输入框弹出到一定高度再执行*/
                postDelayed(() -> {
                    /*此时还是ADJUST_NOTHING模式,flMore的gone 按道理来说应该会有个输入框向下隐藏的动作,就是会向下闪烁一下,
                    但因为下一句代码执行太快, 此时输入法已显示完全了,所以会顺着输入框此时的位置再次往上顶起剩余的部分
                    */
                    flMore.setVisibility(GONE);
                    /*改为ADJUST_RESIZE 此时输入框并没有*/
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }, 200);
            }
        });
        /*发送按钮*/
        btnSend.setOnClickListener(v -> {

        });
    }

    InputMoreFragment inputMoreFragment;

    /**
     * 显示更多的布局
     */
    private void showMore() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (inputMoreFragment == null)
            inputMoreFragment = new InputMoreFragment();
        fragmentManager.beginTransaction().replace(R.id.fl_more, inputMoreFragment).commitAllowingStateLoss();
    }

    private void initVoicePress() {
        btnVoicePress.setOnTouchListener((view, motionEvent) -> {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    btnVoicePress.setText("松开结束");
                    break;
                case MotionEvent.ACTION_MOVE:
                    btnVoicePress.setText("松开结束");
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    btnVoicePress.setText("按住说话");
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    /**
     * 显示软键盘布局
     */
    private void showSoftInput() {
        etInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etInput, 0);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    public void setInputText(String text) {
        etInput.setText(text);
        btnSend.setVisibility(VISIBLE);
        ivMore.setVisibility(GONE);
    }
}
