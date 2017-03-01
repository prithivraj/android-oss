package com.kickstarter.libs;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.models.Project;

import java.math.RoundingMode;

import auto.parcel.AutoParcel;

public final class KSCurrency {
  private final CurrentConfigType currentConfig;

  public KSCurrency(final @NonNull CurrentConfigType currentConfig) {
    this.currentConfig = currentConfig;
  }

  /**
   * Returns a currency string appropriate to the user's locale and location relative to a project.
   *
   * @param initialValue Value to display, local to the project's currency.
   * @param project      The project to use to look up currency information.
   */
  public @NonNull String format(final float initialValue, final @NonNull Project project) {
    return format(initialValue, project, false, false, RoundingMode.DOWN);
  }

  /**
   * Returns a currency string appropriate to the user's locale and location relative to a project.
   *
   * @param initialValue        Value to display, local to the project's currency.
   * @param project             The project to use to look up currency information.
   * @param excludeCurrencyCode If true, hide the currency code, even if that makes the returned value ambiguous.
   *                            This is used when space is constrained and the currency code can be determined elsewhere.
   */
  public @NonNull String format(final float initialValue, final @NonNull Project project,
    final boolean excludeCurrencyCode) {

    return format(initialValue, project, excludeCurrencyCode, false, RoundingMode.DOWN);
  }

  /**
   * Returns a currency string appropriate to the user's locale and location relative to a project.
   *
   * @param initialValue        Value to display, local to the project's currency.
   * @param project             The project to use to look up currency information.
   * @param excludeCurrencyCode If true, hide the currency code, even if that makes the returned value ambiguous.
   *                            This is used when space is constrained and the currency code can be determined elsewhere.
   * @param preferUSD           Attempt to convert a project from it's local currency to USD, if the user is located in
   *                            the US.
   */
  public @NonNull String format(final float initialValue, final @NonNull Project project,
    final boolean excludeCurrencyCode, final boolean preferUSD, final @NonNull RoundingMode roundingMode) {

    final CurrencyOptions currencyOptions = currencyOptions(initialValue, project, preferUSD);

    final boolean showCurrencyCode = showCurrencyCode(currencyOptions, excludeCurrencyCode);

    final NumberOptions numberOptions = NumberOptions.builder()
      .currencyCode(showCurrencyCode ? currencyOptions.currencyCode() : "")
      .currencySymbol(currencyOptions.currencySymbol())
      .roundingMode(roundingMode)
      .build();

    return NumberUtils.format(currencyOptions.value(), numberOptions);
  }

  /**
   * Build {@link CurrencyOptions} based on the project and whether we would prefer to show USD. Even if USD is preferred,
   * we only show USD if the user is in the US.
   */
  private @NonNull CurrencyOptions currencyOptions(final float value, final @NonNull Project project,
    final boolean preferUSD) {

    final Config config = currentConfig.getConfig();
    final Float staticUsdRate = project.staticUsdRate();
    if (preferUSD && config.countryCode().equals("US") && staticUsdRate != null) {
      return CurrencyOptions.builder()
        .country("US")
        .currencySymbol("$")
        .currencyCode("")
        .value(value * staticUsdRate)
        .build();
    } else {
      return CurrencyOptions.builder()
        .country(project.country())
        .currencyCode(project.currency())
        .currencySymbol(project.currencySymbol())
        .value(value)
        .build();
    }
  }

  /**
   * Determines whether the currency code should be shown. If the currency is ambiguous (e.g. CAD and USD both use `$`),
   * we show the currency code if the user is not in the US, or the project is not in the US.
   */
  private boolean showCurrencyCode(final @NonNull CurrencyOptions currencyOptions, final boolean excludeCurrencyCode) {
    if (excludeCurrencyCode) {
      return false;
    }

    final Config config = currentConfig.getConfig();
    final boolean currencyIsDupe = config.currencyNeedsCode(currencyOptions.currencySymbol());
    final boolean userIsUS = config.countryCode().equals("US");
    final boolean projectIsUS = currencyOptions.country().equals("US");

    return (currencyIsDupe && !userIsUS) || (currencyIsDupe && !projectIsUS);
  }

  @AutoParcel
  public abstract static class CurrencyOptions implements Parcelable {
    public abstract String country();
    public abstract String currencyCode();
    public abstract String currencySymbol();
    public abstract float value();

    @AutoParcel.Builder
    public abstract static class Builder {
      public abstract Builder country(String __);
      public abstract Builder currencyCode(String __);
      public abstract Builder currencySymbol(String __);
      public abstract Builder value(float __);
      public abstract CurrencyOptions build();
    }

    public static Builder builder() {
      return new AutoParcel_KSCurrency_CurrencyOptions.Builder();
    }

    public abstract Builder toBuilder();
  }
}
