package com.mbostic.rocket;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.mbostic.ads.*;
import com.mbostic.screens.MenuScreen;

public class RocketMain extends Game {

	public final static int WIDTH = 720;
	public final static int HEIGHT = 1280;
	public static float screenCutOff;
	public static float fontScale = 1.0f;
	public static boolean menu, desktop;//ce je v menuju

	public static AdsController adsController;

	public RocketMain(AdsController adsController){

		desktop = adsController == null;


		if (!desktop) {
			this.adsController = adsController;
		} else {
			this.adsController = new com.mbostic.ads.DummyAdsController();
		}
	}

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		if(Math.sqrt(Gdx.graphics.getWidth()*Gdx.graphics.getWidth()
				+ Gdx.graphics.getHeight()*Gdx.graphics.getHeight())/Gdx.graphics.getPpiX() < 3.5)
			fontScale = 1.4f;// ce je majhen ekran
		Assets.instance.init();
		screenCutOff =
				(RocketMain.HEIGHT - (float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth()*RocketMain.WIDTH)/2f;

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		screenCutOff =
				(RocketMain.HEIGHT - (float)height/width*RocketMain.WIDTH)/2.0f;
	}

	@Override
	public void render() {
		super.render();
	}

	public static int gamble(int[] probabilities){
		int probabilitySum = 0;

		for(int probability : probabilities){
			probabilitySum += probability;
		}

		int rand = MathUtils.random(probabilitySum);

		int ret = probabilities.length - 1;

		probabilitySum = 0;


		for(int i = 0; i < probabilities.length; i++){

			probabilitySum += probabilities[i];
			if(rand < probabilitySum) {
				ret = i;
				break;
			}
		}
		return ret;
	}


	public static Color newColor(int r, int g, int b){
		return new Color((float) r / 255.0f,(float) g / 255.0f,(float) b / 255.0f, 1);
	}

	public static Color newColor(int r, int g, int b, float a){
		return new Color((float) r / 255.0f,(float) g / 255.0f,(float) b / 255.0f, a );
	}
}
