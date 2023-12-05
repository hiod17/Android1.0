package com.example.myappli.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myappli.R;
import com.example.myappli.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(getActivity())
                        .get(HomeViewModel.class);//this)

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //getActivity();//setContentView(R.layout.activity_search);
        startActivity(new Intent(getActivity(), bosszp_jobActivity.class));
        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String s);
    }
}