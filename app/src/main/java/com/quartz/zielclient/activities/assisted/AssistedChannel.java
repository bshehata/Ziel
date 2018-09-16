package com.quartz.zielclient.activities.assisted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quartz.zielclient.R;
import com.quartz.zielclient.activities.common.MapsActivity;
import com.quartz.zielclient.channel.ChannelController;
import com.quartz.zielclient.channel.ChannelData;
import com.quartz.zielclient.channel.ChannelListener;


/**
 * Assisted Session maintains and provides a session to the user from the Assisteds Perspective
 * This activity allows the user to ask for assistance from a carer and share personal data
 * with the carer to allow them to provide assistance
 *
 * @author Bilal Shehata
 */
public class AssistedChannel extends AppCompatActivity implements ChannelListener, View.OnClickListener {

  private TextView status;
  private Button waveButton;
  private Button mapsActivityButton;

  private ChannelData channelData;

  private String assistedId;
  private String carerId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_assisted_session);

    assistedId = getIntent().getStringExtra("assisted");
    carerId = getIntent().getStringExtra("carer");

    // allocate the graphical button to a functional button
    waveButton = findViewById(R.id.waveButton);
    mapsActivityButton = findViewById(R.id.toMapsActivity);

    // button should not be visible until a session is established.
    mapsActivityButton.setVisibility(View.INVISIBLE);
    waveButton.setVisibility(View.INVISIBLE);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    status = findViewById(R.id.channelStatus);
    channelData = ChannelController.createChannel(this);
  }

  @Override
  public void dataChanged() {
    // sometimes the listener can misfire so avoid crash by checking an object was collected.
    if (channelData.getCarerStatus()) {
      // the Carer set their status to true they are active
      status.setText("Session is active");
      waveButton.setVisibility(View.VISIBLE);
      mapsActivityButton.setVisibility(View.VISIBLE);
      waveButton.setOnClickListener(this);
    } else {
      // the Carer has set or has not unset their status and therefore they are inactive.
      status.setText("Session is Inactive");
    }
  }

  @Override
  public void onBackPressed() {
    // assisted has exited the session so set status to false (inactive)
    channelData.setAssistedStatus(false);
    super.onBackPressed();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.waveButton:
        channelData.setPing(true);
        break;
      case R.id.toMapsActivity:
        Intent intentToMaps = new Intent(AssistedChannel.this, MapsActivity.class);
        intentToMaps.putExtra("channelKey", channelData.getChannelKey());
        startActivity(intentToMaps);
        break;

    }

  }

  @Override
  public String getAssistedId() {
    return assistedId;
  }

  @Override
  public String getCarerId() {
    return carerId;
  }
}