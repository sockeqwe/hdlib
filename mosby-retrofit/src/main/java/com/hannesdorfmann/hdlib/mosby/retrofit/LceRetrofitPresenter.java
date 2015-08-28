/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.hdlib.mosby.retrofit;

import com.hannesdorfmann.hdlib.mosby.retrofit.exception.NetworkException;
import com.hannesdorfmann.hdlib.mosby.retrofit.exception.UnexpectedStatusCodeException;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A Presenter that can be used with {@link
 * MvpLceView} and Retrofit. It provides {@link LceCallback} that can be used as {@link Callback}
 * for retrofit requests.
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class LceRetrofitPresenter<V extends MvpLceView<M>, M> extends MvpBasePresenter<V> {

  /**
   * An out of the box {@link Callback} implementation for Retrofit requests that are associated
   * with this presenter. It automatically calls {@link MvpLceView#setData(Object)}, {@link
   * MvpLceView#showContent()} or {@link MvpLceView#showError(Throwable, boolean)} if the view is
   * still attached to the presenter. It also calls {@link MvpLceView#showLoading(boolean)} for you!
   *
   * @author Hannes Dorfmann
   * @since 1.0.0
   */
  protected class LceCallback implements Callback<M> {

    private final boolean pullToRefresh;

    public LceCallback(boolean pullToRefresh) {
      this.pullToRefresh = pullToRefresh;
      if (isViewAttached()) {
        getView().showLoading(pullToRefresh);
      }
    }

    @Override public void success(M m, Response response) {
      if (isViewAttached()) {
        getView().setData(m);
        getView().showContent();
      }
    }

    @Override public void failure(RetrofitError error) {
      if (isViewAttached()) {
        Throwable t;
        if (error.getKind() == RetrofitError.Kind.HTTP && error.getResponse() != null) {
          t = new UnexpectedStatusCodeException(error.getResponse().getStatus());
        } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
          t = new NetworkException(error.getCause());
        } else {
          t = error.getCause();
        }

        getView().showError(t, pullToRefresh);
      }
    }
  }
}
