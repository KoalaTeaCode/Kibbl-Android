package com.thehollidayinn.kibbl.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thehollidayinn.kibbl.R;
import com.thehollidayinn.kibbl.MainActivity;
import com.thehollidayinn.kibbl.data.models.Pet;
import com.thehollidayinn.kibbl.data.models.UserLogin;
import com.thehollidayinn.kibbl.data.models.UserResponse;
import com.thehollidayinn.kibbl.data.remote.ApiUtils;
import com.thehollidayinn.kibbl.data.remote.KibblAPIInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

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
    private UserLogin userLogin;
    private ProgressDialog progDialog;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userLogin = UserLogin.getInstance(getActivity());

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Login");

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

        callbackManager = CallbackManager.Factory.create();
        setUpFBLogin(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showLoading() {
        progDialog = ProgressDialog.show(getActivity(), "Loading...", "One moment please...");
    }

    private void hideLoading() {
        progDialog.hide();
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle("Error");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loginUser(String email, String password) {
        showLoading();

        final Map<String, String> userLoginMap = new HashMap<>();
        userLoginMap.put("email", email);
        userLoginMap.put("password", password);

        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());
        mService.login(userLoginMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        // @TODO: How do we do this?
//                        showErrorMessage(e.toString());
                    }

                    @Override
                    public void onNext(UserResponse userResponse) {
                        hideLoading();
                        userLogin.setToken(userResponse.getToken());
                        Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(mainActivityIntent);
                    }
                });
    }

    private void registerUserViaSocial(String network, String accessToken) {
        showLoading();

        Map<String, String> userLoginMap = new HashMap<>();
        userLoginMap.put("network", network);
        userLoginMap.put("accessToken", accessToken);

        KibblAPIInterface mService = ApiUtils.getKibbleService(getActivity());
        mService.registerSocial(userLoginMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserResponse>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        showErrorMessage(e.toString());
                    }

                    @Override
                    public void onNext(UserResponse userResponse) {
                        hideLoading();
                        userLogin.setToken(userResponse.getToken());
                        Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(mainActivityIntent);
                    }
                });
    }

    private void setUpFBLogin(View view) {
        facebookLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        facebookLoginButton.setReadPermissions("email");
        // If using in a fragment
        facebookLoginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
//                String accessToken = loginResult.getAccessToken().toString();
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                registerUserViaSocial("facebook", accessToken.getToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.v("testshit", "test");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("testshit", "ERROR");
                Log.v("testshit", exception.toString());
            }
        });
    }
}
