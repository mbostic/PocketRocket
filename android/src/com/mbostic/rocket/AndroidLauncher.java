package com.mbostic.rocket;

import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.accounts.Account;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mbostic.ads.AdsController;

public class AndroidLauncher extends AndroidApplication implements AdsController {

	private static final String BANNER_AD_UNIT_ID = "ca-app-pub-9346893337778731/9784742806";
	private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9346893337778731/1563472006";

	static AdView bannerAd;
	InterstitialAd interstitialAd;
	boolean isAdShown;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		// Create a gameView and a bannerAd AdView
		View gameView = initializeForView(new RocketMain(this), config);
		setupAds();

		// Define the layout
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView(bannerAd, params);
		setContentView(layout);

		isAdShown = isWifiConnected();
	}

	public void setupAds() {
		bannerAd = new AdView(this);
		bannerAd.setVisibility(View.INVISIBLE);
		bannerAd.setBackgroundColor(0x00000000); // transparent
		bannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
		bannerAd.setAdSize(AdSize.SMART_BANNER);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);

		AdRequest.Builder builder = new AdRequest.Builder();
		AdRequest ad = builder.build();
		interstitialAd.loadAd(ad);
	}

	@Override
	public boolean isWifiConnected() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo ni1 = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return ((ni != null && ni.isConnected()) || (ni1 != null && ni1.isConnected()));

	}

	@Override
	public void showBannerAd() {
		if(isWifiConnected()){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.VISIBLE);
				AdRequest.Builder builder = new AdRequest.Builder();
				AdRequest ad = builder.build();
				bannerAd.loadAd(ad);
			}
		});
		isAdShown = true;
		}
	}

	@Override
	public void showInterstitialAd(final Runnable then) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (then != null) {
					interstitialAd.setAdListener(new AdListener() {
						@Override
						public void onAdClosed() {
							Gdx.app.postRunnable(then);
							AdRequest.Builder builder = new AdRequest.Builder();
							AdRequest ad = builder.build();
							interstitialAd.loadAd(ad);
						}
					});
				}
				interstitialAd.show();
			}
		});
	}



	@Override
	public void hideBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.INVISIBLE);
			}
		});
		isAdShown = false;
	}

	@Override
	public int getHeight() {
		return bannerAd.getHeight();
	}

	@Override
	public boolean bannerShown() {
		return isAdShown;
	}

	@Override
	public String getAccountName() {

		AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] accounts = manager.getAccountsByType("com.google");

		String name;
		if (accounts.length > 0) {
			name = accounts[0].name;
		} else {
			name ="No account";
		}

		return name;
	}
}
