package com.hannesdorfmann.hdlib.mosby.retrofit.mock;

import retrofit.Callback;

/**
 * @author Hannes Dorfmann
 */
public interface TestApi {

  public void loadData(Callback<Object> callback);
}
