package com.utopiaxc.serverstatus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.utopiaxc.serverstatus.databinding.FragmentServerListBinding;

/**
 * 服务器列表Fragment
 * <p>用于显示服务器列表
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:50:32
 */
public class ServerListFragment extends Fragment {

    private FragmentServerListBinding binding;

    /**
     * 服务器列表Fragment视图创建
     *
     * @param inflater           参数
     * @param container          参数
     * @param savedInstanceState 参数
     * @return android.view.View
     * @author UtopiaXC
     * @since 2022-05-22 22:51:05
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServerListBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    /**
     * 服务器列表Fragment视图销毁
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:51:50
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}