package com.kickstarter.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.kickstarter.libs.utils.DiscoveryUtils;
import com.kickstarter.models.Category;
import com.kickstarter.services.DiscoveryParams;
import com.kickstarter.ui.ArgumentsKey;
import com.kickstarter.ui.fragments.DiscoveryFragment;

import java.util.List;

import rx.Observable;

public final class DiscoveryPagerAdapter extends FragmentPagerAdapter {
  private final Delegate delegate;
  private final FragmentManager fragmentManager;
  private List<String> pageTitles;

  public interface Delegate {
    void discoveryPagerAdapterSetPrimaryPage(DiscoveryPagerAdapter adapter, int position);
  }

  public DiscoveryPagerAdapter(final @NonNull FragmentManager fragmentManager, final @NonNull List<String> pageTitles,
    final Delegate delegate) {
    super(fragmentManager);
    this.delegate = delegate;
    this.fragmentManager = fragmentManager;
    this.pageTitles = pageTitles;
  }

  @Override
  public void setPrimaryItem(final @NonNull ViewGroup container, final int position, final @NonNull Object object) {
    super.setPrimaryItem(container, position, object);
    delegate.discoveryPagerAdapterSetPrimaryPage(this, position);
  }

  @Override
  public @NonNull Fragment getItem(final int position) {
    return DiscoveryFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    return DiscoveryParams.Sort.values().length;
  }

  @Override
  public CharSequence getPageTitle(final int position) {
    return pageTitles.get(position);
  }

  /**
   * Passes along root categories to its fragment position to help fetch appropriate projects.
   */
  public void takeCategoriesForPosition(final @NonNull List<Category> categories, final int position) {
    Observable.from(fragmentManager.getFragments())
      .ofType(DiscoveryFragment.class)
      .filter(frag -> {
        final int fragmentPosition = frag.getArguments().getInt(ArgumentsKey.DISCOVERY_SORT_POSITION);
        return fragmentPosition == position;
      })
      .subscribe(frag -> frag.takeCategories(categories));
  }

  /**
   * Take current params from activity and pass to the appropriate fragment.
   */
  public void takeParams(final @NonNull DiscoveryParams params) {
    Observable.from(fragmentManager.getFragments())
      .ofType(DiscoveryFragment.class)
      .filter(frag -> {
        final int fragmentPosition = frag.getArguments().getInt(ArgumentsKey.DISCOVERY_SORT_POSITION);
        return DiscoveryUtils.positionFromSort(params.sort()) == fragmentPosition;
      })
      .subscribe(frag -> frag.updateParams(params));
  }

  /**
   * Call when the view model tells us to clear specific pages.
   */
  public void clearPages(final @NonNull List<Integer> pages) {
    Observable.from(fragmentManager.getFragments())
      .ofType(DiscoveryFragment.class)
      .filter(frag -> {
        final int fragmentPosition = frag.getArguments().getInt(ArgumentsKey.DISCOVERY_SORT_POSITION);
        return pages.contains(fragmentPosition);
      })
      .subscribe(DiscoveryFragment::clearPage);
  }
}
