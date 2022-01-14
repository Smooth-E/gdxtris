package com.simple.tetriscompetitive;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.simple.tetriscompetitive.GameSuper;

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
		configuration.hideStatusBar = false;

		initialize(new GameSuper(), configuration);
	}
}