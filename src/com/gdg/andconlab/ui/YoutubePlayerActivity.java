package com.gdg.andconlab.ui;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeFinishedListener;
import com.actionbarsherlock.ActionBarSherlock.OnActionModeStartedListener;
import com.actionbarsherlock.ActionBarSherlock.OnCreatePanelMenuListener;
import com.actionbarsherlock.ActionBarSherlock.OnMenuItemSelectedListener;
import com.actionbarsherlock.ActionBarSherlock.OnPreparePanelListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gdg.andconlab.R;
import com.gdg.andconlab.youtube.DeveloperKey;
import com.gdg.andconlab.youtube.YouTubeFailureRecoveryActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p>
 * This is the preferred way of handling fullscreen because the default fullscreen implementation
 * will cause re-buffering of the video.
 */
@SuppressLint("NewApi")
public class YoutubePlayerActivity extends YouTubeFailureRecoveryActivity implements
    View.OnClickListener,
    CompoundButton.OnCheckedChangeListener,
    YouTubePlayer.OnFullscreenListener, 
    OnCreatePanelMenuListener, OnPreparePanelListener, OnMenuItemSelectedListener, OnActionModeStartedListener, OnActionModeFinishedListener{

  private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
      ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

  public static final String LECTURE_YOUTUBE_ASSET_ID = "youtube_asset_id";

  private YouTubePlayerView playerView;
  private YouTubePlayer player;

  private boolean fullscreen;
  private ActionBarSherlock mSherlock;

  protected final ActionBarSherlock getSherlock() {
      if (mSherlock == null) {
          mSherlock = ActionBarSherlock.wrap(this, ActionBarSherlock.FLAG_DELEGATE);
      }
      return mSherlock;
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setHomeButtonEnabled(true);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    setContentView(R.layout.single_item_player_activity);
    playerView = (YouTubePlayerView) findViewById(R.id.player);

    playerView.initialize(DeveloperKey.DEVELOPER_KEY, this);

    doLayout();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
        case android.R.id.home:
           // This ID represents the Home or Up button. In the case of this
           // activity, the Up button is shown. Use NavUtils to allow users
           // to navigate up one level in the application structure. For
           // more details, see the Navigation pattern on Android Design:
           //
           // http://developer.android.com/design/patterns/navigation.html#up-vs-back
           //
           NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
           return true;
     }
     return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
      boolean wasRestored) {
    this.player = player;
    // Specify that we want to handle fullscreen behavior ourselves.
    player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
    player.setOnFullscreenListener(this);
    if (!wasRestored) {
      player.cueVideo(this.getIntent().getExtras().getString("CURRENT_ASSET_ID"));//"rgQToPTVD9I");
    }
  }

  @Override
  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    return playerView;
  }

  @Override
  public void onClick(View v) {
    player.setFullscreen(!fullscreen);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    int controlFlags = player.getFullscreenControlFlags();
    if (isChecked) {
      // If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE, your activity's normal UI
      // should never be laid out in landscape mode (since the video will be fullscreen whenever the
      // activity is in landscape orientation). Therefore you should set the activity's requested
      // orientation to portrait. Typically you would do this in your AndroidManifest.xml, we do it
      // programmatically here since this activity demos fullscreen behavior both with and without
      // this flag).
      setRequestedOrientation(PORTRAIT_ORIENTATION);
      controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
      controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
    }
    player.setFullscreenControlFlags(controlFlags);
  }

  private void doLayout() {
    LinearLayout.LayoutParams playerParams =
        (LinearLayout.LayoutParams) playerView.getLayoutParams();
    if (fullscreen) {
      // When in fullscreen, the visibility of all other views than the player should be set to
      // GONE and the player should be laid out across the whole screen.
      playerParams.width = LayoutParams.MATCH_PARENT;
      playerParams.height = LayoutParams.MATCH_PARENT;

    }
  }

  @Override
  public void onFullscreen(boolean isFullscreen) {
    fullscreen = isFullscreen;
    doLayout();
  }

 /* @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    doLayout();
  }*/

  public ActionBar getSupportActionBar() {
      return getSherlock().getActionBar();
  }

  public ActionMode startActionMode(ActionMode.Callback callback) {
      return getSherlock().startActionMode(callback);
  }

  @Override
  public void onActionModeStarted(ActionMode mode) {}

  @Override
  public void onActionModeFinished(ActionMode mode) {}


  ///////////////////////////////////////////////////////////////////////////
  // General lifecycle/callback dispatching
  ///////////////////////////////////////////////////////////////////////////

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      getSherlock().dispatchConfigurationChanged(newConfig);
      doLayout();
  }

  @Override
  protected void onPostResume() {
      super.onPostResume();
      getSherlock().dispatchPostResume();
  }

  @Override
  protected void onPause() {
      getSherlock().dispatchPause();
      super.onPause();
  }

  @Override
  protected void onStop() {
      getSherlock().dispatchStop();
      super.onStop();
  }

  @Override
  protected void onDestroy() {
      getSherlock().dispatchDestroy();
      super.onDestroy();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
      getSherlock().dispatchPostCreate(savedInstanceState);
      super.onPostCreate(savedInstanceState);
  }

  @Override
  protected void onTitleChanged(CharSequence title, int color) {
      getSherlock().dispatchTitleChanged(title, color);
      super.onTitleChanged(title, color);
  }

  @Override
  public final boolean onMenuOpened(int featureId, android.view.Menu menu) {
      if (getSherlock().dispatchMenuOpened(featureId, menu)) {
          return true;
      }
      return super.onMenuOpened(featureId, menu);
  }

  @Override
  public void onPanelClosed(int featureId, android.view.Menu menu) {
      getSherlock().dispatchPanelClosed(featureId, menu);
      super.onPanelClosed(featureId, menu);
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
      if (getSherlock().dispatchKeyEvent(event)) {
          return true;
      }
      return super.dispatchKeyEvent(event);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Native menu handling
  ///////////////////////////////////////////////////////////////////////////

  public MenuInflater getSupportMenuInflater() {
      return getSherlock().getMenuInflater();
  }

  public void invalidateOptionsMenu() {
      getSherlock().dispatchInvalidateOptionsMenu();
  }

  public void supportInvalidateOptionsMenu() {
      invalidateOptionsMenu();
  }

  @Override
  public final boolean onCreateOptionsMenu(android.view.Menu menu) {
      return getSherlock().dispatchCreateOptionsMenu(menu);
  }

  @Override
  public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
      return getSherlock().dispatchPrepareOptionsMenu(menu);
  }

  @Override
  public final boolean onOptionsItemSelected(android.view.MenuItem item) {
      return getSherlock().dispatchOptionsItemSelected(item);
  }

  @Override
  public void openOptionsMenu() {
      if (!getSherlock().dispatchOpenOptionsMenu()) {
          super.openOptionsMenu();
      }
  }

  @Override
  public void closeOptionsMenu() {
      if (!getSherlock().dispatchCloseOptionsMenu()) {
          super.closeOptionsMenu();
      }
  }


  ///////////////////////////////////////////////////////////////////////////
  // Sherlock menu handling
  ///////////////////////////////////////////////////////////////////////////

  @Override
  public boolean onCreatePanelMenu(int featureId, Menu menu) {
      if (featureId == Window.FEATURE_OPTIONS_PANEL) {
          return onCreateOptionsMenu(menu);
      }
      return false;
  }

  public boolean onCreateOptionsMenu(Menu menu) {
      return true;
  }

  @Override
  public boolean onPreparePanel(int featureId, View view, Menu menu) {
      if (featureId == Window.FEATURE_OPTIONS_PANEL) {
          return onPrepareOptionsMenu(menu);
      }
      return false;
  }

  public boolean onPrepareOptionsMenu(Menu menu) {
      return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
      if (featureId == Window.FEATURE_OPTIONS_PANEL) {
          return onOptionsItemSelected(item);
      }
      return false;
  }


  ///////////////////////////////////////////////////////////////////////////
  // Content
  ///////////////////////////////////////////////////////////////////////////

  @Override
  public void addContentView(View view, LayoutParams params) {
      getSherlock().addContentView(view, params);
  }

  @Override
  public void setContentView(int layoutResId) {
      getSherlock().setContentView(layoutResId);
  }

  @Override
  public void setContentView(View view, LayoutParams params) {
      getSherlock().setContentView(view, params);
  }

  @Override
  public void setContentView(View view) {
      getSherlock().setContentView(view);
  }

  public void requestWindowFeature(long featureId) {
      getSherlock().requestFeature((int)featureId);
  }


  ///////////////////////////////////////////////////////////////////////////
  // Progress Indication
  ///////////////////////////////////////////////////////////////////////////

  public void setSupportProgress(int progress) {
      getSherlock().setProgress(progress);
  }

  public void setSupportProgressBarIndeterminate(boolean indeterminate) {
      getSherlock().setProgressBarIndeterminate(indeterminate);
  }

  public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
      getSherlock().setProgressBarIndeterminateVisibility(visible);
  }

  public void setSupportProgressBarVisibility(boolean visible) {
      getSherlock().setProgressBarVisibility(visible);
  }

  public void setSupportSecondaryProgress(int secondaryProgress) {
      getSherlock().setSecondaryProgress(secondaryProgress);
  }
}
