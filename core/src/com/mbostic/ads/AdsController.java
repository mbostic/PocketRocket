package com.mbostic.ads;

public interface AdsController {

    boolean isWifiConnected();
    void showInterstitialAd (Runnable then);
    void showBannerAd();
    void hideBannerAd();
    String getAccountName();
    int getHeight();
    boolean bannerShown();
}
