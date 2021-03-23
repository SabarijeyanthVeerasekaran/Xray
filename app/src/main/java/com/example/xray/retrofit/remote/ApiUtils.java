package com.example.xray.retrofit.remote;

public class ApiUtils {
    private ApiUtils() {
    }
    public static final String Url="https://192.168.0.5:5001";

    public static FileService getFileServices()
    {
        return RetrofitClient.getClient(Url).create(FileService.class);
    }
}
