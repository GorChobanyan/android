package com.example.wallpapersapplication.network;

import androidx.annotation.MainThread;
import androidx.lifecycle.MutableLiveData;

public abstract class NetworkBoundResource<T> {
    private final MutableLiveData<Resource<T>> result = new MutableLiveData<>();

    @MainThread
    public NetworkBoundResource() {
        result.setValue(Resource.loading(null));
        createCall();
    }

    @MainThread
    protected abstract void createCall();

    public void setResultValue(Resource<T> value) {
        result.setValue(value);
    }

    public final MutableLiveData<Resource<T>> getAsMutableLiveData() {
        return result;
    }
}
