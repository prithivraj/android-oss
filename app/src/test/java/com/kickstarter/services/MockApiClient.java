package com.kickstarter.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.kickstarter.factories.ActivityEnvelopeFactory;
import com.kickstarter.factories.ActivityFactory;
import com.kickstarter.factories.BackingFactory;
import com.kickstarter.factories.CategoryFactory;
import com.kickstarter.factories.CommentFactory;
import com.kickstarter.factories.LocationFactory;
import com.kickstarter.factories.ProjectFactory;
import com.kickstarter.factories.UpdateFactory;
import com.kickstarter.factories.UserFactory;
import com.kickstarter.libs.Config;
import com.kickstarter.models.Backing;
import com.kickstarter.models.Category;
import com.kickstarter.models.Comment;
import com.kickstarter.models.Empty;
import com.kickstarter.models.Location;
import com.kickstarter.models.Project;
import com.kickstarter.models.ProjectNotification;
import com.kickstarter.models.Update;
import com.kickstarter.models.User;
import com.kickstarter.services.apiresponses.AccessTokenEnvelope;
import com.kickstarter.services.apiresponses.ActivityEnvelope;
import com.kickstarter.services.apiresponses.CommentsEnvelope;
import com.kickstarter.services.apiresponses.DiscoverEnvelope;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MockApiClient implements ApiClientType {
  private final PublishSubject<Pair<String, Map<String, Object>>> observable = PublishSubject.create();

  /**
   * Emits when endpoints on the client are called. The key in the pair is the underscore-separated
   * name of the method, and the value is a map of argument names/values.
   */
  public @NonNull Observable<Pair<String, Map<String, Object>>> observable() {
    return observable;
  }

  @Override
  public @NonNull Observable<Config> config() {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<ActivityEnvelope> fetchActivities() {
    return Observable.just(
      ActivityEnvelopeFactory.activityEnvelope(Collections.singletonList(ActivityFactory.activity()))
    );
  }

  @Override
  public @NonNull Observable<ActivityEnvelope> fetchActivities(final @Nullable Integer count) {
    return fetchActivities().take(count);
  }

  @Override
  public @NonNull Observable<ActivityEnvelope> fetchActivitiesWithPaginationPath(final @NonNull String paginationPath) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<List<Category>> fetchCategories() {
    return Observable.just(
      Arrays.asList(
        CategoryFactory.artCategory(),
        CategoryFactory.bluesCategory(),
        CategoryFactory.ceramicsCategory(),
        CategoryFactory.gamesCategory(),
        CategoryFactory.musicCategory(),
        CategoryFactory.photographyCategory(),
        CategoryFactory.tabletopGamesCategory(),
        CategoryFactory.textilesCategory(),
        CategoryFactory.worldMusicCategory()
      )
    );
  }

  @Override
  public @NonNull Observable<List<ProjectNotification>> fetchProjectNotifications() {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<Project> fetchProject(final @NonNull String param) {
    return Observable.just(
      ProjectFactory.project()
        .toBuilder()
        .slug(param)
        .build()
    );
  }

  @Override
  public @NonNull Observable<Project> fetchProject(final @NonNull Project project) {
    return Observable.just(project);
  }

  @Override
  public @NonNull Observable<DiscoverEnvelope> fetchProjects(final @NonNull DiscoveryParams params) {
    return Observable.just(
      DiscoverEnvelope
        .builder()
        .projects(
          Arrays.asList(
            ProjectFactory.project(),
            ProjectFactory.allTheWayProject(),
            ProjectFactory.successfulProject()
          )
        )
        .urls(
          DiscoverEnvelope.UrlsEnvelope
            .builder()
            .api(
              DiscoverEnvelope.UrlsEnvelope.ApiEnvelope
                .builder()
                .moreProjects("http://more.projects.please")
                .build()
            )
            .build()
        )
        .build()
    );
  }

  @Override
  public @NonNull Observable<DiscoverEnvelope> fetchProjects(final @NonNull String paginationUrl) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<CommentsEnvelope> fetchComments(final @NonNull Project project) {
    return Observable.just(
      CommentsEnvelope.builder()
        .urls(CommentsEnvelope.UrlsEnvelope.builder()
          .api(CommentsEnvelope.UrlsEnvelope.ApiEnvelope.builder()
            .moreComments("more comments please")
            .newerComments("newer comments please")
            .build())
          .build())
        .comments(Collections.singletonList(CommentFactory.comment()))
        .build()
    );
  }

  @Override
  public @NonNull Observable<CommentsEnvelope> fetchComments(final @NonNull Update update) {
    return Observable.just(
      CommentsEnvelope.builder()
        .urls(CommentsEnvelope.UrlsEnvelope.builder()
          .api(CommentsEnvelope.UrlsEnvelope.ApiEnvelope.builder()
            .moreComments("more comments please")
            .newerComments("newer comments please")
            .build())
          .build())
        .comments(Collections.singletonList(CommentFactory.comment()))
        .build()
    );
  }

  @Override
  public @NonNull Observable<CommentsEnvelope> fetchComments(final @NonNull String paginationPath) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<Update> fetchUpdate(final @NonNull String projectParam, final @NonNull String updateParam) {
    return Observable.just(UpdateFactory.update());
  }

  @Override
  public @NonNull Observable<Update> fetchUpdate(final @NonNull Update update) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<AccessTokenEnvelope> loginWithFacebook(final @NonNull String accessToken) {

    return Observable.just(
      AccessTokenEnvelope.builder()
        .user(UserFactory.user()
            .toBuilder()
            .build()
        )
        .accessToken("deadbeef")
        .build()
    );
  }

  @Override
  public @NonNull Observable<AccessTokenEnvelope> loginWithFacebook(final @NonNull String fbAccessToken,
    final @NonNull String code) {
    return Observable.just(
      AccessTokenEnvelope.builder()
        .user(UserFactory.user()
            .toBuilder()
            .build()
        )
        .accessToken("deadbeef")
        .build()
    );
  }

  @Override
  public @NonNull Observable<AccessTokenEnvelope> registerWithFacebook(final @NonNull String fbAccessToken,
    final boolean sendNewsletters) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<Backing> fetchProjectBacking(final @NonNull Project project, final @NonNull User user) {

    return Observable.just(BackingFactory.backing(project, user));
  }

  @Override
  public @NonNull Observable<Category> fetchCategory(final @NonNull String param) {
    return Observable.just(CategoryFactory.musicCategory());
  }

  @Override
  public @NonNull Observable<Category> fetchCategory(final @NonNull Category category) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<User> fetchCurrentUser() {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<Location> fetchLocation(final @NonNull String param) {
    return Observable.just(LocationFactory.sydney());
  }

  @Override
  public @NonNull Observable<AccessTokenEnvelope> login(final @NonNull String email, final @NonNull String password) {
    return Observable.just(
      AccessTokenEnvelope.builder()
        .user(UserFactory.user()
            .toBuilder()
            .build()
        )
        .accessToken("deadbeef")
        .build()
    );
  }

  @Override
  public @NonNull Observable<AccessTokenEnvelope> login(final @NonNull String email, final @NonNull String password,
    final @NonNull String code) {
    return Observable.just(
      AccessTokenEnvelope.builder()
        .user(UserFactory.user()
            .toBuilder()
            .build()
        )
        .accessToken("deadbeef")
        .build()
    );
  }

  @Override
  public @NonNull Observable<Comment> postComment(final @NonNull Project project, final @NonNull String body) {
    return Observable.just(CommentFactory.comment().toBuilder().body(body).build());
  }

  @Override
  public @NonNull Observable<Comment> postComment(final @NonNull Update update, final @NonNull String body) {
    return Observable.just(CommentFactory.comment().toBuilder().body(body).build());
  }


  @Override
  public @NonNull Observable<Empty> registerPushToken(final @NonNull String token) {
    return Observable.empty();
  }

  @Override
  public @NonNull Observable<User> resetPassword(final @NonNull String email) {
    return Observable.just(UserFactory.user());
  }

  @Override
  public @NonNull Observable<AccessTokenEnvelope> signup(final @NonNull String name, final @NonNull String email,
    final @NonNull String password, final @NonNull String passwordConfirmation, final boolean sendNewsletters) {

    return Observable.just(
      AccessTokenEnvelope.builder()
        .user(UserFactory.user()
          .toBuilder()
          .name(name)
          .build()
        )
      .accessToken("deadbeef")
      .build()
    );
  }

  @Override
  public @NonNull Observable<Project> starProject(final @NonNull Project project) {
    return Observable.just(project.toBuilder().isStarred(true).build());
  }

  @Override
  public @NonNull Observable<Project> toggleProjectStar(final @NonNull Project project) {
    return Observable.just(project.toBuilder().isStarred(!project.isStarred()).build());
  }

  @Override
  public @NonNull Observable<ProjectNotification> updateProjectNotifications(final @NonNull ProjectNotification projectNotification, final boolean checked) {
    return Observable.just(projectNotification.toBuilder().email(checked).mobile(checked).build());
  }

  @Override
  public @NonNull Observable<User> updateUserSettings(final @NonNull User user) {
    observable.onNext(
      Pair.create("update_user_settings", new HashMap<String, Object>() {
        {
          put("user", user);
        }
      })
    );
    return Observable.just(user);
  }
}
