package com.example.p3;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

public class GlobalChatFragment extends Fragment{
    private static final String TAG = "GlobalChatFragment";
    private Button btnSend;
    private EditText editText;
    private LinearLayout linearLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.global_chat_fragment, container, false);
        Log.d(TAG, "started");
        btnSend = (Button) view.findViewById(R.id.sendButton);
        editText = (EditText) view.findViewById(R.id.plain_text_input);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll_global_chat);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                //(Main)reportOnFragments
                String message = String.valueOf(editText.getText());
                // Add textview 1
                AppCompatTextView textView1;

                textView1 = new AppCompatTextView(getActivity());
                textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textView1.setText("programmatically created TextView1");

                textView1.setBackgroundResource(R.drawable.box2); // hex color 0xAARRGGBB
                textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
                //textView1.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView1.setGravity(10);
                textView1.setWidth(120);
                textView1.forceLayout();


                linearLayout.addView(textView1);

                Log.e(TAG, "Said " + message );
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
        ((Main)getActivity()).tearDownChecker(3);
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
