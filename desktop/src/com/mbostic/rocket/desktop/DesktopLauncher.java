package com.mbostic.rocket.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mbostic.rocket.RocketMain;

public class DesktopLauncher {

	final static boolean is43 = false;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = RocketMain.WIDTH*2 /3;

        config.height =(int) (RocketMain.WIDTH*(is43 ? 4.0f/3.0f : 16.0f/9.0f)*2.0f /3.0f) ;

		new LwjglApplication(new RocketMain(null), config);

	}
}
