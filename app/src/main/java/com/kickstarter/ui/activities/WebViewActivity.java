package com.kickstarter.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

import com.kickstarter.R;
import com.kickstarter.libs.BaseActivity;
import com.kickstarter.libs.qualifiers.RequiresActivityViewModel;
import com.kickstarter.libs.utils.AnimationUtils;
import com.kickstarter.services.KSWebViewClient;
import com.kickstarter.ui.toolbars.KSToolbar;
import com.kickstarter.ui.views.KSWebView;
import com.kickstarter.viewmodels.WebViewViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

import static com.kickstarter.libs.utils.TransitionUtils.slideInFromLeft;

@RequiresActivityViewModel(WebViewViewModel.class)
public final class WebViewActivity extends BaseActivity<WebViewViewModel> implements KSWebViewClient.Delegate {
  protected @Bind(R.id.web_view_toolbar) KSToolbar toolbar;
  protected @Bind(R.id.web_view) KSWebView webView;
  protected @Bind(R.id.loading_indicator_view) View loadingIndicatorView;

  @Override
  protected void onCreate(final @Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.web_view_layout);
    ButterKnife.bind(this);

    webView.client().setDelegate(this);

    viewModel.outputs.toolbarTitle()
      .compose(bindToLifecycle())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(toolbar::setTitle);

    viewModel.outputs.url()
      .compose(bindToLifecycle())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(webView::loadUrl);
  }

  @Override
  public void back() {
    // This logic is sound only for web view activities without RequestHandlers.
    // TODO: Refactor the client to update web history properly for activities with RequestHandlers.
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.back();
    }
  }

  @Override
  public void webViewExternalLinkActivated(final @NonNull KSWebViewClient webViewClient, final @NonNull String url) {}

  @Override
  public void webViewOnPageStarted(final @NonNull KSWebViewClient webViewClient, final @Nullable String url) {
    loadingIndicatorView.startAnimation(AnimationUtils.INSTANCE.appearAnimation());
  }

  @Override
  public void webViewOnPageFinished(final @NonNull KSWebViewClient webViewClient, final @Nullable String url) {
    loadingIndicatorView.startAnimation(AnimationUtils.INSTANCE.disappearAnimation());
  }

  @Override
  public void webViewPageIntercepted(final @NonNull KSWebViewClient webViewClient, final @Nullable String url) {}

  protected @Nullable Pair<Integer, Integer> exitTransition() {
    return slideInFromLeft();
  }
}
