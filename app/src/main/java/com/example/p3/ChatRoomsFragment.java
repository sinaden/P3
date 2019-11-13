package com.example.p3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatRoomsFragment extends Fragment {
    private static final String TAG = "ChatRoomsFragment";
    private Button btnSend;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatrooms_fragment, container, false);
        Log.d(TAG, "started");
        btnSend = (Button) view.findViewById(R.id.sendButton);
        editText = (EditText) view.findViewById(R.id.plain_text_input);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                //(Main)reportOnFragments
                ((Main)getActivity()).reportOnFragments();
            }
        });

        return view;


    }
    @Override
    public void onStart() {
        Log.e(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onResume() {
        //((Main)getActivity()).tearDownChecker(0);
        Log.e(TAG, "onResume: " );
        super.onResume();
    }

    @Override
    public void onPause() {
        //((Main)getActivity()).tearDownChecker(3);
        Log.e(TAG, "onPause: " );

        //    onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        super.onDestroy();
    }
}
