package com.kickstarter.viewmodels;

import android.support.annotation.NonNull;

import com.kickstarter.libs.ActivityViewModel;
import com.kickstarter.libs.CurrentConfigType;
import com.kickstarter.libs.CurrentUserType;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.rx.transformers.Transformers;
import com.kickstarter.libs.utils.I18nUtils;
import com.kickstarter.libs.utils.StringUtils;
import com.kickstarter.services.ApiClientType;
import com.kickstarter.services.apiresponses.AccessTokenEnvelope;
import com.kickstarter.services.apiresponses.ErrorEnvelope;
import com.kickstarter.ui.activities.SignupActivity;
import com.kickstarter.viewmodels.errors.SignupViewModelErrors;
import com.kickstarter.viewmodels.inputs.SignupViewModelInputs;
import com.kickstarter.viewmodels.outputs.SignupViewModelOutputs;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public final class SignupViewModel extends ActivityViewModel<SignupActivity> implements SignupViewModelInputs, SignupViewModelOutputs,
  SignupViewModelErrors {
  private final ApiClientType client;
  private final CurrentUserType currentUser;
  private final CurrentConfigType currentConfig;

  protected final static class SignupData {
    final @NonNull String fullName;
    final @NonNull String email;
    final @NonNull String password;
    final boolean sendNewsletters;

    protected SignupData(final @NonNull String fullName, final @NonNull String email, final @NonNull String password,
      final boolean sendNewsletters) {
      this.fullName = fullName;
      this.email = email;
      this.password = password;
      this.sendNewsletters = sendNewsletters;
    }

    protected boolean isValid() {
      return fullName.length() > 0 && StringUtils.isEmail(email) && password.length() >= 6;
    }
  }

  // INPUTS
  private final PublishSubject<String> fullName = PublishSubject.create();
  public void fullName(final @NonNull String s) {
    fullName.onNext(s);
  }
  private final PublishSubject<String> email = PublishSubject.create();
  public void email(final @NonNull String s) {
    email.onNext(s);
  }
  private final PublishSubject<String> password = PublishSubject.create();
  public void password(final @NonNull String s) {
    password.onNext(s);
  }
  private final PublishSubject<Boolean> sendNewslettersClick = PublishSubject.create();
  public void sendNewslettersClick(final boolean b) {
    sendNewslettersClick.onNext(b);
  }
  private final PublishSubject<Void> signupClick = PublishSubject.create();
  public void signupClick() {
    signupClick.onNext(null);
  }

  // OUTPUTS
  private final PublishSubject<Void> signupSuccess = PublishSubject.create();
  public Observable<Void> signupSuccess() {
    return signupSuccess.asObservable();
  }
  private final PublishSubject<Boolean> formSubmitting = PublishSubject.create();
  public Observable<Boolean> formSubmitting() {
    return formSubmitting.asObservable();
  }
  private final PublishSubject<Boolean> formIsValid = PublishSubject.create();
  public Observable<Boolean> formIsValid() {
    return formIsValid.asObservable();
  }
  private final BehaviorSubject<Boolean> sendNewslettersIsChecked = BehaviorSubject.create();
  public Observable<Boolean> sendNewslettersIsChecked() {
    return sendNewslettersIsChecked;
  }

  // ERRORS
  private final PublishSubject<ErrorEnvelope> signupError = PublishSubject.create();
  public Observable<String> signupError() {
    return signupError
      .takeUntil(signupSuccess)
      .map(ErrorEnvelope::errorMessage);
  }

  public final SignupViewModelInputs inputs = this;
  public final SignupViewModelOutputs outputs = this;
  public final SignupViewModelErrors errors = this;

  public SignupViewModel(final @NonNull Environment environment) {
    super(environment);

    client = environment.apiClient();
    currentConfig = environment.currentConfig();
    currentUser = environment.currentUser();

    final Observable<SignupData> signupData = Observable.combineLatest(
      fullName, email, password, sendNewslettersIsChecked,
      SignupData::new);


    sendNewslettersClick
      .compose(bindToLifecycle())
      .subscribe(sendNewslettersIsChecked::onNext);

    signupData
      .map(SignupData::isValid)
      .compose(bindToLifecycle())
      .subscribe(formIsValid);

    signupData
      .compose(Transformers.takeWhen(signupClick))
      .flatMap(this::submit)
      .compose(bindToLifecycle())
      .subscribe(this::success);

    currentConfig.observable()
      .take(1)
      .map(config -> I18nUtils.isCountryUS(config.countryCode()))
      .compose(bindToLifecycle())
      .subscribe(sendNewslettersIsChecked::onNext);

    signupError
      .compose(bindToLifecycle())
      .subscribe(__ -> koala.trackRegisterError());

    sendNewslettersClick
      .compose(bindToLifecycle())
      .subscribe(koala::trackSignupNewsletterToggle);

    signupSuccess
      .compose(bindToLifecycle())
      .subscribe(__ -> {
        koala.trackLoginSuccess();
        koala.trackRegisterSuccess();
      });

    koala.trackRegisterFormView();
  }

  private Observable<AccessTokenEnvelope> submit(final @NonNull SignupData data) {
    return client.signup(data.fullName, data.email, data.password, data.password, data.sendNewsletters)
      .compose(Transformers.pipeApiErrorsTo(signupError))
      .compose(Transformers.neverError())
      .doOnSubscribe(() -> formSubmitting.onNext(true))
      .doAfterTerminate(() -> formSubmitting.onNext(false));
  }

  private void success(final @NonNull AccessTokenEnvelope envelope) {
    currentUser.login(envelope.user(), envelope.accessToken());
    signupSuccess.onNext(null);
  }
}
