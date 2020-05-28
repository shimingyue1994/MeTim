package com.yue.metim.weight.msgpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yue.libtim.utils.PopupUtils;
import com.yue.metim.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author shimy
 * @create 2020/5/26 17:06
 * @desc
 */
public class IMPopupView {


    @SuppressLint("WrongConstant")
    public void showPopMsgAction(View anchorView, List<MsgPopAction> list) {
        Context context = anchorView.getContext();
        View view = LayoutInflater.from(context).inflate(
                R.layout.popup_msg_action, null);// 要弹出的pop布局
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);// 将布局添加到popupwindow中
        /*pop不能被输入法上顶*/
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置动画
//        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setTouchable(true); // 设置popupwindow可点击
        popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
        popupWindow.setFocusable(true); // 获取焦点

        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        ActionAdapter listAdapter = new ActionAdapter(list,popupWindow);

        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        listAdapter.notifyDataSetChanged();
        /**
         * 点击其他区域
         */
        int[] windowPos = PopupUtils.calculatePopViewPos(anchorView, view);
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
//            Gravity.TOP | Gravity.START 不能设置成  Gravity.NO_GRAVITY 否则顶不上去
            popupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
//            popupWindow.showAtLocation(mBinding.layoutBingzheng, Gravity.NO_GRAVITY, 0, 0);
        }/**/
    }

    class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.MyHolder> {

        private List<MsgPopAction> mList = new ArrayList<>();
        private PopupWindow popupWindow;

        public ActionAdapter(List<MsgPopAction> list,PopupWindow popupWindow) {
            this.mList = list;
            this.popupWindow = popupWindow;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_item_msg, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            MsgPopAction action = mList.get(position);
            holder.tvAction.setText(action.getActionName());
            holder.tvAction.setOnClickListener(v -> {
                action.getOnMsgActionListener().onActionClick(popupWindow);
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            public TextView tvAction;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                tvAction = itemView.findViewById(R.id.tv_action_name);
            }
        }
    }

}
