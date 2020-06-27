package com.mbostic.ads;

public class DummyAdsController implements AdsController {
    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showInterstitialAd(Runnable then) {

    }

    @Override
    public void showBannerAd() {

    }

    @Override
    public void hideBannerAd() {

    }

    @Override
    public String getAccountName() {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean bannerShown() {
        return false;
    }
}
