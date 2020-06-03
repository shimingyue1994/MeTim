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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.yue.libtim.R;
import com.yue.libtim.fragment.InputMoreFragment;

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
    private Button btnSend;
    private Button btnVoicePress;//按住说话
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
        btnSend = view.findViewById(R.id.btn_send);
        btnVoicePress = view.findViewById(R.id.btn_voice_press);
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
    }

    private void initView() {
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
                hideSoftInput();
                showActionsGroup();
            } else {
                inputState = InputState.INPUT_TEXT;
                showSoftInput();
            }
        });
        /*发送按钮*/
        btnSend.setOnClickListener(v -> {

        });
    }

    InputMoreFragment actionsFragment;

    /**
     * 显示更多的布局
     */
    private void showActionsGroup() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (actionsFragment == null)
            actionsFragment = new InputMoreFragment();
        fragmentManager.beginTransaction().replace(R.id.fl_more, actionsFragment).commitAllowingStateLoss();
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
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etInput, 0);
        postDelayed(new Runnable() {
            @Override
            public void run() {

//                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        }, 200);


    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
        etInput.clearFocus();
    }

    public void setInputText(String text) {
        etInput.setText(text);
        btnSend.setVisibility(VISIBLE);
        ivMore.setVisibility(GONE);
    }
}
