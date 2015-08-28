package com.hannesdorfmann.hdlib.mosby.retrofit.mock;

import java.io.IOException;
import java.util.ArrayList;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * @author Hannes Dorfmann
 */
public class MockTestApi implements TestApi {

  public enum FailReason {
    UNKNOWN,
    NETWORK,
    STATUS_CODE;

    public static int httpcode =403;
  }

  private FailReason reason;
  private boolean shouldFail;
  private Object data;

  @Override public void loadData(Callback<Object> callback) {

    if (!shouldFail) {
      callback.success(data, null);
    } else {
      RetrofitError error = null;
      switch (reason) {
        case NETWORK:
          error = RetrofitError.networkError("mock url", new IOException("Mock Network Exception"));
          break;
        case STATUS_CODE:
          error = RetrofitError.httpError("mock url",
              new Response("mock url", FailReason.httpcode, "Forbidden", new ArrayList<Header>(), null), null, null);
          break;

        case UNKNOWN:
        default:
          error = RetrofitError.unexpectedError("mock url", new Exception("Mock Exception"));
          break;
      }
      callback.failure(error);
    }
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public FailReason getReason() {
    return reason;
  }

  public void setReason(FailReason reason) {
    this.reason = reason;
  }

  public boolean isShouldFail() {
    return shouldFail;
  }

  public void setShouldFail(boolean shouldFail) {
    this.shouldFail = shouldFail;
  }
}
