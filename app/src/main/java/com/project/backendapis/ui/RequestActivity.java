package com.project.backendapis.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.backendapis.R;
import com.project.backendapis.data.Data;
import com.project.backendapis.databinding.ActivityRequestBinding;
import com.project.backendapis.utilities.Format;
import com.project.backendapis.utilities.HTTPCallback;
import com.project.backendapis.utilities.MyHttpRequestTask;
import com.project.backendapis.utilities.Params;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {

    ProgressDialog loading;
    String url = "";

    String my_url;
    String request_method = "";
    String request_body = "";
    ArrayList<Data> headers = new ArrayList<>();
    private ActivityRequestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        my_url = getIntent().getStringExtra(Params.URL);
        request_method = getIntent().getStringExtra(Params.REQUEST_METHOD);
        request_body = getIntent().getStringExtra(Params.REQUEST_BODY);
        headers = getIntent().getParcelableArrayListExtra(Params.REQUEST_HEADERS);

        setUpComponents();
        startRequest();
    }

    private void setUpComponents() {

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.response));

        binding.showHideHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.showHideHeader.getText().toString().equals("Show")) {
                    binding.showHideHeader.setText(getString(R.string.hide));
                    binding.responseHeaders.setVisibility(View.VISIBLE);
                } else {
                    binding.showHideHeader.setText(R.string.show);
                    binding.responseHeaders.setVisibility(View.GONE);
                }
            }
        });
        binding.response.setMovementMethod(new ScrollingMovementMethod());
        binding.responseHeaders.setMovementMethod(new ScrollingMovementMethod());
    }

    private void startRequest() {

        loading = ProgressDialog.show(RequestActivity.this, null, getString(R.string.please_wait), false, false);
        MyHttpRequestTask requestHttp = new MyHttpRequestTask(my_url, request_method, request_body, headers, new HTTPCallback() {
            @Override
            public void requestFinish(int code, String msg, String responseHeader, String output) {
                loading.dismiss();
                String formatOutput = Format.formatString(output);
                binding.response.setText(formatOutput);
                binding.responseCode.setText(String.valueOf(code));
                binding.responseMsg.setText(msg);
                binding.responseHeaders.setText(responseHeader);

            }

            @Override
            public void requestError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error \n" + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        requestHttp.execute();

    }
}