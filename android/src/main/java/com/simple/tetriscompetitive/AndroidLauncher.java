package com.simple.tetriscompetitive;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();

		configuration.useAccelerometer = false;
		configuration.useCompass = false;
		configuration.useGyroscope = false;
		configuration.useImmersiveMode = true;
		configuration.useRotationVectorSensor = false;

		initialize(new GameSuper(), configuration);
	}

}
