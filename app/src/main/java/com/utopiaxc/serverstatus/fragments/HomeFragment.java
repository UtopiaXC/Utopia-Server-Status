package com.utopiaxc.serverstatus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.utopiaxc.serverstatus.databinding.FragmentHomeBinding;

/**
 * 主页Fragment
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:52:18
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    /**
     * 主页Fragment视图渲染
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:52:29
     * @param inflater 参数
     * @param container 参数
     * @param savedInstanceState 参数
     * @return android.view.View
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * 主页Fragment视图销毁
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:52:53

     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}