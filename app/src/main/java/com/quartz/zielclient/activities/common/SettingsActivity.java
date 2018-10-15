package com.quartz.zielclient.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartz.zielclient.R;
import com.quartz.zielclient.activities.assisted.AssistedHomePageActivity;
import com.quartz.zielclient.activities.carer.CarerHomepageActivity;
import com.quartz.zielclient.user.User;
import com.quartz.zielclient.user.UserController;
import com.quartz.zielclient.user.UserFactory;

public class SettingsActivity extends AppCompatActivity
    implements ValueEventListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  private boolean updating = false;

  // Current user state
  private User user;

  private TextView firstNameEntry;
  private TextView lastNameEntry;
  private Switch roleSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_ACTION_BAR);
    setContentView(R.layout.settings_activity);

    Bundle userBundle = getIntent().getBundleExtra("user");
    user = UserFactory.getUser(userBundle);
    populateUi();

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("Account Settings");
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  /**
   * Populate the UI based on the current user model.
   */
  private void populateUi() {
    firstNameEntry = findViewById(R.id.settingFirstName);
    firstNameEntry.setText(user.getFirstName());

    lastNameEntry = findViewById(R.id.settingLastName);
    lastNameEntry.setText(user.getLastName());

    String roleString = user.isAssisted() ? "Assisted" : "Carer";
    roleSwitch = findViewById(R.id.roleSwitch);
    roleSwitch.setChecked(user.isAssisted());
    roleSwitch.setOnCheckedChangeListener(this);
    roleSwitch.setText(roleString);

    Button confirmButton = findViewById(R.id.settingsConfirm);
    confirmButton.setOnClickListener(this);
  }

  @Override
  public boolean onSupportNavigateUp() {
    if (updating) {
      return false;
    }

    startActivity(goHomeIntent());
    finish();
    return true;
  }

  /**
   * Updates the database with the current UI values.
   */
  private void confirmChanges() {
    updating = true;
    String firstName = firstNameEntry.getText().toString();
    String lastName = lastNameEntry.getText().toString();
    boolean isAssisted = roleSwitch.isChecked();

    User updatedUser = UserFactory.getUser(firstName, lastName, user.getPhoneNumber(), isAssisted);
    UserController.updateSelf(updatedUser, this);
  }

  /**
   * Go back to the appropriate home page.
   */
  private Intent goHomeIntent() {
    Class<? extends AppCompatActivity> homePage = user.isAssisted()
        ? AssistedHomePageActivity.class
        : CarerHomepageActivity.class;
    Intent intent = new Intent(this, homePage);
    intent.putExtra("user", user.toBundle());
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return intent;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.settingsConfirm:
        confirmChanges();
        break;
      default:
        break;
    }
  }

  /**
   * Updates the text next to the role switch.
   *
   * @param buttonView The role switch view.
   * @param isChecked  Whether or not the switch has been checked.
   */
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      buttonView.setText("Assisted");
    } else {
      buttonView.setText("Carer");
    }
  }

  @Override
  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    user = UserFactory.getUser(dataSnapshot);
    updating = false;
    populateUi();
    Toast.makeText(this, "Updated account settings.", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onCancelled(@NonNull DatabaseError databaseError) {
    // TODO
  }

  @Override
  public Intent getSupportParentActivityIntent() {
    return goHomeIntent();
  }

  @Override
  public Intent getParentActivityIntent() {
    return goHomeIntent();
  }
}