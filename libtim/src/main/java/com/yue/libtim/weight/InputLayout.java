package com.yue.libtim.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yue.libtim.R;

/**
 * @author shimy
 * @create 2020/6/2 14:47
 * @desc 输入底部布局
 */
public class InputLayout extends FrameLayout {

    private ImageView ivVoice;
    private ImageView ivFace;
    private ImageView ivMore;
    private EditText etInput;
    private Button btnSend;
    private Button btnVoicePress;//按住说话

    public InputLayout(@NonNull Context context) {
        this(context, null);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.layout_input, this);
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
        etInput.setVisibility(GONE);
        btnVoicePress.setVisibility(VISIBLE);
        initView();
        initVoicePress();
    }

    private void initView() {
        /*语音按钮*/
        ivVoice.setOnClickListener(v -> {

        });
        /*表情按钮*/
        ivFace.setOnClickListener(v -> {

        });
        /*更多操作的按钮*/
        ivMore.setOnClickListener(v -> {

        });
        /*发送按钮*/
        btnSend.setOnClickListener(v -> {

        });
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

    public void setInputText(String text) {
        etInput.setText(text);
        btnSend.setVisibility(VISIBLE);
        ivMore.setVisibility(GONE);
    }
}
