package com.project.backendapis.utilities;

public interface HTTPCallback {

    void requestFinish(int responseCode, String responseMessage, String responseHeader, String output);

    void requestError(String message);

}
