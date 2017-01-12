package com.android.thehollidayinn.kibbl.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.android.thehollidayinn.kibbl.R;
import com.android.thehollidayinn.kibbl.data.models.Pet;
import com.android.thehollidayinn.kibbl.data.models.UserResponse;
import com.android.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.android.thehollidayinn.kibbl.data.remote.KibblAPIInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by krh12 on 1/10/2017.
 */

public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        loginButton = (Button) view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(email, password);
            }
        });

        return view;
    }

    private void loginUser(String email, String password) {
        Map<String, String> userLoginMap = new HashMap<>();
        userLoginMap.put("email", email);
        userLoginMap.put("password", password);

        KibblAPIInterface mService = ApiUtils.getKibbleService();
        mService.login(userLoginMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.v("test", e.toString());
                    }

                    @Override
                    public void onNext(UserResponse userResponse) {
                        Log.v("test", userResponse.getToken());
                    }
                });
    }
}
