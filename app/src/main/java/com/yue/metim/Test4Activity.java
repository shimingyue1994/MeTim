package com.yue.metim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yue.libtim.fragment.InputFaceFragment;
import com.yue.libtim.fragment.InputMoreFragment;
import com.yue.libtim.weight.input.InputStatusListener;
import com.yue.metim.databinding.ActivityTest4Binding;
import com.yue.metim.databinding.ItemTestBinding;

import java.util.ArrayList;
import java.util.List;

public class Test4Activity extends AppCompatActivity {

    private ActivityTest4Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test4);
        initRecycler();

        mBinding.inputlayout.init(this);
        mBinding.inputlayout.setInputFaceFragment(new InputFaceFragment());
        mBinding.inputlayout.setInputMoreFragment(new InputMoreFragment());
        mBinding.inputlayout.enableFace(false);
        mBinding.inputlayout.setInputStatusListener(new InputStatusListener() {
            @Override
            public void onInputAreaChanged(boolean open) {
                /*延时，布局改变没那么快*/
                mBinding.recycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.recycler.scrollToPosition(list.size() - 1);
                    }
                }, 200);

            }

            @Override
            public void onVoiceStatusChanged(int status) {
                if (status == RECORD_CANCELING) {
                    mBinding.tvVoiceTip.setVisibility(View.VISIBLE);
                    mBinding.tvVoiceTip.setText("松开取消");
                } else if (status == RECORD_CANCEL) {
                    mBinding.tvVoiceTip.setVisibility(View.GONE);
                    mBinding.tvVoiceTip.setText("");
                    Toast.makeText(Test4Activity.this, "录音取消发送", Toast.LENGTH_SHORT).show();
                } else if (status == RECORD_START) {
                    mBinding.tvVoiceTip.setVisibility(View.VISIBLE);
                    mBinding.tvVoiceTip.setText("录音中");
                } else if (status == RECORD_STOP) {
                    mBinding.tvVoiceTip.setVisibility(View.GONE);
                    mBinding.tvVoiceTip.setText("");
                    Toast.makeText(Test4Activity.this, "录音停止", Toast.LENGTH_SHORT).show();
                } else if (status == RECORD_TOO_SHORT) {
                    mBinding.tvVoiceTip.setVisibility(View.GONE);
                    mBinding.tvVoiceTip.setText("");
                    Toast.makeText(Test4Activity.this, "太短了", Toast.LENGTH_SHORT).show();
                } else if (status == RECORD_FAILED) {
                    mBinding.tvVoiceTip.setVisibility(View.GONE);
                    mBinding.tvVoiceTip.setText("");
                    Toast.makeText(Test4Activity.this, "录音失败", Toast.LENGTH_SHORT).show();
                } else if (status == RECORD_COMPLETE) {
                    mBinding.tvVoiceTip.setVisibility(View.GONE);
                    mBinding.tvVoiceTip.setText("");
                    Toast.makeText(Test4Activity.this, "录音完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private List<String> list;

    private void initRecycler() {
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("标题   " + i);
        }
        MyAdapter adapter = new MyAdapter(list);
        mBinding.recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mBinding.recycler.scrollToPosition(list.size() - 1);

    }

    class MyAdapter extends RecyclerView.Adapter {

        private List<String> list;

        public MyAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTestBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_test, parent, false);
            return new MyHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ItemTestBinding binding = DataBindingUtil.getBinding(holder.itemView);
            binding.tvTest.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            public MyHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
