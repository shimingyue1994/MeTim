package com.yue.metim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yue.libtim.fragment.InputFaceFragment;
import com.yue.libtim.fragment.InputMoreFragment;
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
    }


    private void initRecycler() {
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        List<String> list = new ArrayList<>();
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
