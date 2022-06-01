package com.project.backendapis.ui;

import static com.project.backendapis.utilities.Format.parseUrl;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.project.backendapis.R;
import com.project.backendapis.adapter.BodyAdapter;
import com.project.backendapis.adapter.HeaderAdapter;
import com.project.backendapis.adapter.ParamsAdapter;
import com.project.backendapis.data.Data;
import com.project.backendapis.databinding.ActivityMainBinding;
import com.project.backendapis.databinding.DailogNewHeaderBinding;
import com.project.backendapis.utilities.CheckNetwork;
import com.project.backendapis.utilities.Format;
import com.project.backendapis.utilities.Params;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HeaderAdapter.Callbacks, BodyAdapter.Callbacks, ParamsAdapter.Callbacks {

    private ActivityMainBinding binding;
    private Dialog dialog;
    private DailogNewHeaderBinding newHeaderBinding;
    HeaderAdapter headeradapter;
    BodyAdapter bodyAdapter;
    ParamsAdapter paramAdapter;
    Boolean add_header = true;
    Boolean add_body = true;
    Boolean add_param = true;
    String type = Params.HEADERS ; // header : 1 , body : 2 , param = 3



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Log.i("APIResponse" , "onCreate");

        setUpComponents();
        setHeaderDialogComponents();


       /* try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
       } catch (IOException e) {
            Log.i("APIResponse", "HTTP response cache installation failed:" + e);
        }*/
    }

    /*protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }*/

    private void setHeaderDialogComponents() {
        newHeaderBinding = DailogNewHeaderBinding.inflate(getLayoutInflater());
        dialog = new Dialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT
        ));
        dialog.setContentView(newHeaderBinding.getRoot());
        dialog.setCancelable(false);
        newHeaderBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        newHeaderBinding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = "";
                String value = "";
                if (newHeaderBinding.keyText.getText() != null || newHeaderBinding.valueText.getText() != null) {

                    key = newHeaderBinding.keyText.getText().toString();
                    value = newHeaderBinding.valueText.getText().toString();

                    if (key.trim().isEmpty()) {
                        newHeaderBinding.keyText.setError("Empty key");
                    } else if (value.trim().isEmpty()) {
                        newHeaderBinding.valueText.setError("Empty value");
                    } else {

                        Data data = new Data(key, value);
                        if (type.equals(Params.HEADERS)) {
                            if (add_header)
                                headeradapter.insertNewHeader(data);
                            else
                                headeradapter.editHeader(data);
                        } else if (type.equals(Params.BODY)) {
                            if (add_body)
                                bodyAdapter.insertNewBody(data);
                            else
                                bodyAdapter.editBody(data);
                        } else {
                            if (add_param)
                                paramAdapter.insertNewParam(data);
                            else
                                paramAdapter.editParam(data);
                        }
                        dialog.cancel();
                    }
                }
            }
        });
    }

    private void setUpComponents() {

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Test Your APIs");

        binding.urlText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paramAdapter.addParams(parseUrl(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        headeradapter = new HeaderAdapter(getApplicationContext());
        headeradapter.setCallbacks(this);
        binding.headersRecycleView.setAdapter(headeradapter);
        binding.headersRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        bodyAdapter = new BodyAdapter(getApplicationContext());
        bodyAdapter.setCallbacks(this);
        binding.bodyRecycleView.setAdapter(bodyAdapter);
        binding.bodyRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        paramAdapter = new ParamsAdapter(getApplicationContext());
        paramAdapter.setCallbacks(this);
        binding.parameterRecycleView.setAdapter(paramAdapter);
        binding.parameterRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        binding.row.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.addBody.setVisibility(View.GONE);
                    binding.bodyRecycleView.setVisibility(View.GONE);

                    binding.body.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.keyValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.addBody.setVisibility(View.VISIBLE);
                    binding.bodyRecycleView.setVisibility(View.VISIBLE);

                    binding.body.setVisibility(View.GONE);
                }
            }
        });

        binding.addHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_header = true;
                type = Params.HEADERS;

                newHeaderBinding.add.setText(getString(R.string.add));
                newHeaderBinding.title.setText(getString(R.string.add_new_header));

                dialog.show();
            }
        });

        binding.addBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_body = true;
                type = Params.BODY;

                newHeaderBinding.add.setText(getString(R.string.add));
                newHeaderBinding.title.setText(getString(R.string.add_new_data));


                dialog.show();
            }
        });

        binding.addParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_param = true;
                type = Params.PARAMS;

                newHeaderBinding.add.setText(getString(R.string.add));
                newHeaderBinding.title.setText(getString(R.string.add_new_param));


                dialog.show();
            }
        });

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isNetworkAvailable(getApplicationContext())) {
                    sendRequest();
                } else {
                    Snackbar.make(binding.getRoot(), getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        binding.requestsTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    binding.bodyLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.bodyLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sendRequest() {


        String my_url = binding.urlText.getText().toString();
        String request_method = binding.requestsTypeSpinner.getSelectedItem().toString();
        String request_body = "";
        ArrayList<Data> headers = headeradapter.getHeaders();

        if (binding.keyValue.isChecked()) {
            try {
                request_body = Format.getPostDataString(bodyAdapter.getBody());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            request_body = binding.body.getText().toString();
        }
        if (!my_url.trim().isEmpty()) {

            Intent intent = new Intent(MainActivity.this, RequestActivity.class);
            intent.putExtra(Params.URL, my_url);
            intent.putExtra(Params.REQUEST_METHOD, request_method);
            intent.putExtra(Params.REQUEST_BODY, request_body);
            intent.putParcelableArrayListExtra(Params.REQUEST_HEADERS, headers);
            startActivity(intent);

        } else {
            binding.urlText.setError(getString(R.string.empty));
        }
    }


    @Override
    public void editHeader(Data data) {
        add_header = false;
        type = Params.HEADERS;
        newHeaderBinding.title.setText(getString(R.string.add_new_header));
        showDialog(data);
    }

    @Override
    public void editBody(Data data) {
        add_body = false;
        type = Params.BODY;
        newHeaderBinding.title.setText(getString(R.string.add_new_data));

        showDialog(data);
    }

    private void showDialog(Data data) {
        newHeaderBinding.keyText.setText(data.getKey());
        newHeaderBinding.valueText.setText(data.getValue());
        newHeaderBinding.add.setText(getString(R.string.edit));
        dialog.show();
    }

    @Override
    public void editParam(Data data) {
        add_param = false;
        type = Params.PARAMS;
        newHeaderBinding.title.setText(getString(R.string.add_new_param));

        showDialog(data);
    }

    @Override
    public void updateUrl(String param) {
        String url = binding.url.getEditText().getText().toString().split("\\?")[0];

        if (!param.isEmpty())
            binding.url.getEditText().setText(url + "?" + param);
        else {
            // only url
            binding.url.getEditText().setText(url);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("APIResponse" , "onSaveInstanceState");
        outState.putString(Params.URL , binding.url.getEditText().getText().toString());
        outState.putParcelableArrayList(Params.BODY , bodyAdapter.getBody());
        outState.putParcelableArrayList(Params.HEADERS , headeradapter.getHeaders());
        outState.putParcelableArrayList(Params.PARAMS , paramAdapter.getData());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i("APIResponse" , "onRestoreInstanceState");
        String url = savedInstanceState.getString(Params.URL);
        ArrayList<Data> bodyData = savedInstanceState.getParcelableArrayList(Params.BODY);
        ArrayList<Data> headers =savedInstanceState.getParcelableArrayList(Params.HEADERS);
        ArrayList<Data> params =savedInstanceState.getParcelableArrayList(Params.PARAMS);


        binding.url.getEditText().setText(url);
        paramAdapter.addParams(params);
        bodyAdapter.addBodyData(bodyData);
        headeradapter.addHeaders(headers);
    }
}
