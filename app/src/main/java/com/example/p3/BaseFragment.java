package com.example.p3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    private ImageView btnRoom;
    private ImageView btnGlobal;
    private ImageView btnRoulette;
    private ImageView btnFriends;
    private ImageView btnSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "started");
        View view = inflater.inflate(R.layout.activity_main, container, false);
        btnRoom = (ImageView) view.findViewById(R.id.chat_rooms);
        btnGlobal = (ImageView) view.findViewById(R.id.global_chat);
        btnRoulette = (ImageView) view.findViewById(R.id.chat_roulette);
        btnFriends = (ImageView) view.findViewById(R.id.friends);
        btnSettings = (ImageView) view.findViewById(R.id.settings);

        btnGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on Glob");
                ((Main)getActivity()).changeView(1);

            }
        });

        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on Rooms");
                ((Main)getActivity()).changeView(2);
            }
        });

        btnRoulette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on Rou");
                ((Main)getActivity()).changeView(3);
            }
        });
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on Fre");
                ((Main)getActivity()).changeView(4);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked on Set");
                ((Main)getActivity()).changeView(5);
            }
        });


        return view;
    }


}
