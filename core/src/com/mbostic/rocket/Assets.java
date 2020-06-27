package com.mbostic.rocket;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.mbostic.preferences.AbstractPreferences;
import com.mbostic.preferences.DesktopPreferences;
import com.mbostic.screens.MenuScreen;

public class Assets implements Disposable {

    public BitmapFont fontGame;
    public BitmapFont font;
    public AtlasRegion[] doodle, slotRockets, slotRocketOutlines;

    private AssetManager assetManager;
    public SKIN skin;
    public AbstractPreferences prefs;

    public AtlasRegion rocket, rocketOutline, fire1, fire2, fire3, clouds,
            background, pauseGame, homeGame, playGame,
            pauseGamePressed, homeGamePressed, playGamePressed,
            pause, home, play, arrowLeft, arrowRight, settings, button,
            pausePressed, homePressed, playPressed, arrowLeftPressed,
            arrowRightPressed, settingsPressed, buttonPressed, pencilLine,
            trace, title, smoke, gamble, gamblePressed, locked, coin, coinPx, unlock,
            unlockPressed, trainingArrow, circle, slotButton, slotButtonPressed, whitePixel;

    public Texture slot;

    public static Music launch, insertCoins;

    public Sound cloudsPassedSound, noise, prizeSelected;

    public static final Assets instance = new Assets();

    public boolean soundOn;

    private Assets (){}

    public void init(){

        if(!RocketMain.desktop)
            prefs = new com.mbostic.preferences.EncryptedPreferences();
        else
            prefs = new DesktopPreferences();

// ASSET MANAGER

        updateSoundOn();

        assetManager = new AssetManager();
        assetManager.load("icons/icons.atlas",
                TextureAtlas.class);
        assetManager.load("sounds/launch.ogg",
                Music.class);
        assetManager.load("sounds/cloudsPassed.ogg",
                Sound.class);
        assetManager.load("sounds/noise.ogg",
                Sound.class);
        assetManager.load("sounds/prizeSelected.ogg",
                Sound.class);
        assetManager.load("sounds/insertCoins.ogg",
                Music.class);
      //  assetManager.load("sounds/slotSound.ogg",
      //          Sound.class);
        assetManager.finishLoading();


        TextureAtlas  iconsAtlas
                = assetManager.get("icons/icons.atlas");

        for (Texture t : iconsAtlas.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        locked = iconsAtlas.findRegion("locked");
        unlock = iconsAtlas.findRegion("unlock");
        unlockPressed = iconsAtlas.findRegion("unlockPressed");
        coin = iconsAtlas.findRegion("coin");
        trace = iconsAtlas.findRegion("trace");
        trainingArrow = iconsAtlas.findRegion("arrow");
        circle = iconsAtlas.findRegion("circle");
        whitePixel = iconsAtlas.findRegion("whitePixel");
        whitePixel.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        title = iconsAtlas.findRegion("title");
        pause = iconsAtlas.findRegion("pause");
        home = iconsAtlas.findRegion("home");
        play = iconsAtlas.findRegion("play");
        arrowLeft = iconsAtlas.findRegion("arrowLeft");
        arrowRight = iconsAtlas.findRegion("arrowRight");
        settings = iconsAtlas.findRegion("settings");
        button = iconsAtlas.findRegion("button");
        gamble = iconsAtlas.findRegion("gamble");
        slotButton = iconsAtlas.findRegion("slotButton");

        pausePressed = iconsAtlas.findRegion("pausePressed");
        homePressed = iconsAtlas.findRegion("homePressed");
        playPressed = iconsAtlas.findRegion("playPressed");
        arrowLeftPressed = iconsAtlas.findRegion("arrowLeftPressed");
        arrowRightPressed = iconsAtlas.findRegion("arrowRightPressed");
        settingsPressed = iconsAtlas.findRegion("settingsPressed");
        buttonPressed = iconsAtlas.findRegion("buttonPressed");
        gamblePressed = iconsAtlas.findRegion("gamblePressed");
        slotButtonPressed = iconsAtlas.findRegion("slotButtonPressed");

// FONT
        font = new BitmapFont(
                Gdx.files.internal("fonts/SWlsop3.fnt"), false);

        font.getRegion().getTexture().setFilter(
                Texture.TextureFilter.
                        Linear, Texture.TextureFilter.Linear);

        if (launch == null)
            launch = assetManager.get("sounds/launch.ogg", Music.class);


        noise = assetManager.get("sounds/noise.ogg", Sound.class);
        prizeSelected = assetManager.get("sounds/prizeSelected.ogg", Sound.class);
     //   slotSound = assetManager.get("sounds/slotSound.ogg", Sound.class);

        insertCoins = assetManager.get("sounds/insertCoins.ogg", Music.class);
        insertCoins.setVolume(0.9f);

        cloudsPassedSound = assetManager.get("sounds/cloudsPassed.ogg", Sound.class);

    }

    public BitmapFont getFont(float size){
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/SWlsop3.fnt"), false);
        font.getData().setScale(size);
        return font;
    }

    public void updateSoundOn(){
        soundOn = instance.prefs.readBoolean("sound");
    }

    public void load(){

        skin = MenuScreen.skin;

        boolean px = skin.isPixelated;
// ATLAS
        assetManager.load(skin.location,
                TextureAtlas.class);

// ICONS
        if(px) {
            assetManager.load("icons/iconsPX.atlas",
                    TextureAtlas.class);
        }

        assetManager.finishLoading();

        TextureAtlas  atlas = assetManager.get(skin.location);

        if (!px) {  //če ni pikslast
            for (Texture t : atlas.getTextures()) {
                t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }

        }

        // TEXTURES
        rocket = atlas.findRegion("rocket");
        rocketOutline = atlas.findRegion("rocketOutline");

        if(MenuScreen.skin != SKIN.PENCIL) {
            smoke = atlas.findRegion("smoke");
            fire1 = atlas.findRegion("fire1");
            fire2 = atlas.findRegion("fire2");
            fire3 = atlas.findRegion("fire3");
            clouds = atlas.findRegion("clouds");
            background = atlas.findRegion("background");
        }
        else{
            pencilLine = atlas.findRegion("line");

            doodle = new AtlasRegion[11];
            for(int i = 1; i<=11; i++){
                doodle[i-1] = atlas.findRegion("doodel"+i);
             //   if(doodle[i-1] == null)Gdx.app.debug("null","doodle=null");
            }

        }

        if(px){
            TextureAtlas  iconsAtlasGame
                    =  assetManager.get("icons/iconsPX.atlas");
            pauseGame = iconsAtlasGame.findRegion("pausePX");
            homeGame = iconsAtlasGame.findRegion("homePX");
            playGame = iconsAtlasGame.findRegion("playPX");
            pauseGamePressed = iconsAtlasGame.findRegion("pausePXPressed");
            homeGamePressed = iconsAtlasGame.findRegion("homePXPressed");
            playGamePressed = iconsAtlasGame.findRegion("playPXPressed");
            coinPx = iconsAtlasGame.findRegion("coin");

            fontGame = new BitmapFont(Gdx.files.internal("fonts/retroPX.fnt"), false);
            fontGame.getData().setScale(1.5f);


        }
        else {
            pauseGame = pause;
            homeGame = home;
            playGame = play;

            pauseGamePressed = pausePressed;
            homeGamePressed = homePressed;
            playGamePressed = playPressed;

            fontGame = font;
        }
    }

    public void loadSlot(){

        assetManager = new AssetManager();
        assetManager.load("icons/slot.png",
                Texture.class);
        assetManager.load("icons/allSkins.atlas",
                TextureAtlas.class);

        assetManager.finishLoading();

        slot = assetManager.get("icons/slot.png");
        slot.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int skinsNum = SKIN.vals.length;//koliko je skinov

        slotRockets = new AtlasRegion[skinsNum];
        slotRocketOutlines = new AtlasRegion[skinsNum];

        TextureAtlas  atlas = assetManager.get("icons/allSkins.atlas");
        for (Texture t : atlas.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        for(int i = 0; i < skinsNum; i++){
            slotRockets[i] = atlas.findRegion("rocket" + (i+1));
            slotRocketOutlines[i]  = atlas.findRegion("rocketOutline" + (i+1));
        }
/*
        TextureAtlas  atlas = assetManager.get("icons/slot.atlas");
        TextureAtlas  rocketsAtlas = assetManager.get("atlases/allSkins.atlas");

        slot = atlas.findRegion("slot");

        slotLever = new AtlasRegion[6];
        rockets = new AtlasRegion[3];
        rocketOutlines = new AtlasRegion[3];

        for(int i = 1; i<=rockets.length; i++){
            rockets[i-1] = rocketsAtlas.findRegion("rocket"+i);
            rocketOutlines[i-1] = rocketsAtlas.findRegion("rocketOutline"+i);
        }

        for(int i = 1; i<=6; i++){
            slotLever[i-1] = atlas.findRegion("rocka"+i);
        }
*/
    }

    public static void stopLaunchSound(){
        if(launch.isPlaying())
            launch.stop();
        if(WorldController.launchPlayed)
            Assets.instance.noise.stop(WorldController.noiseId);

    }

    public static void pauseLaunchSound(){
        if(launch.isPlaying())
            launch.pause();
        else if(WorldController.launchPlayed)
            Assets.instance.noise.pause(WorldController.noiseId);

    }

    public static void startLaunchSound(){

        if(Assets.instance.soundOn) {
            if (!launch.isPlaying() && !WorldController.launchPlayed) {
                launch.play();
                launch.setVolume(0.3f);
            } else
                Assets.instance.noise.resume(WorldController.noiseId);
        }
    }

    public void playCoinsSound(){
        if(Assets.instance.soundOn)
            insertCoins.play();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        font.dispose();
        fontGame.dispose();
        launch.dispose();
        cloudsPassedSound.dispose();
        insertCoins.dispose();

        slot.dispose();
    }

}
