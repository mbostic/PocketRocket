package com.mbostic.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbostic.objects.Background;
import com.mbostic.objects.Icon;
import com.mbostic.objects.Rocket;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.GAME_MODE;
import com.mbostic.rocket.SKIN;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.WorldController;
import com.mbostic.gamble.Slot;
import com.mbostic.tools.PencilTools;

public class MenuScreen implements Screen, InputProcessor {

    final RocketMain game;
    SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport, viewportBG;
    private Vector2 scalePoints;
    public static GAME_MODE gameMode = GAME_MODE.CLASSIC;
    public static SKIN skin;
    private BitmapFont font;
    GlyphLayout layout;
    Rocket rocket;
    public Background background;
    Slot slot;

    final boolean unlimitedCoins = false;

    private boolean settingsOn, prevSettingsBMoving,
            settingsBMoving, gambleBMoving, prevGambleBMoving,
            playPressed;
    public boolean gambleOn;
    public static boolean unlocked;

    float playSpeed, settingsSpeed, relativeYPosDown, relativeYPosUp, timer;

    public int xCut, arrowWidth, arrowHeight, buttonSize, iconYOffset, settingsYOffset, bannerAdHeight;
    public static int coins;

    Icon play, leftArrowMode, rightArrowMode, leftArrowSkin, rightArrowSkin,
            settings, sound, leftArrowSensitivity, rightArrowSensitivity, vibration,
            gamble, unlockSkin, unlockMode, rightColorArrow, leftColorArrow;

    public MenuScreen(final RocketMain gam){

        RocketMain.menu = true;
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
        game = gam;
        camera = new OrthographicCamera(RocketMain.WIDTH, RocketMain.HEIGHT);
        camera.setToOrtho(false, RocketMain.WIDTH, RocketMain.HEIGHT);
        camera.update();
        scalePoints = new Vector2();

        arrowWidth = (int)(50 * RocketMain.fontScale);
        arrowHeight = (int)(65 * RocketMain.fontScale);
        buttonSize  = (int)(60 * RocketMain.fontScale);
        //zamik zaradi majhnega ekrana(velikosti fonta)
        iconYOffset = RocketMain.fontScale == 1.0f ? 0 : 20;
        settingsYOffset = RocketMain.fontScale == 1.0f ? 0 : 110;
        //zamik zaradi reklam
        bannerAdHeight = RocketMain.adsController.bannerShown() ? 110 : 0;

        //nastavi skin

        if (!Assets.instance.prefs.has("skin")) {
            Assets.instance.prefs.writeString("skin", "Default");

        }
        String s = Assets.instance.prefs.readString("skin");

        for(SKIN sk : SKIN.vals)
            if(sk.name.equals(s))
                skin = sk;

        Assets.instance.load();
        rocket = new Rocket();
        Rocket.position.y -=  RocketMain.screenCutOffTop;

        if(MenuScreen.skin != SKIN.PENCIL)
            background = new Background();
        else
            PencilTools.initPencilLines();

        viewport = new ScreenViewport(camera);

        viewportBG = new FillViewport(RocketMain.WIDTH, RocketMain.HEIGHT, camera);
        layout = new GlyphLayout();

        font = Assets.instance.font;

//nastavi nastavitve
        if (!Assets.instance.prefs.has("sound")){
            Assets.instance.prefs.writeBoolean("sound", true);
            Assets.instance.updateSoundOn();
        }

        if (!Assets.instance.prefs.has("vibration")){
            Assets.instance.prefs.writeBoolean("vibration", true);
        }

        if (!Assets.instance.prefs.has("sensitivity")){
            Assets.instance.prefs.writeInt("sensitivity", 0);
        }

        slot = new Slot(this);
        Assets.instance.loadSlot();
        slot.initIcons();

//IKONE
        play = new Icon(Assets.instance.play, Assets.instance.playPressed,
                RocketMain.WIDTH / 2 - 110, RocketMain.HEIGHT/2 - 200, 220,220);

        leftColorArrow = new Icon(Assets.instance.arrowLeft,
                Assets.instance.arrowLeftPressed,RocketMain.WIDTH / 2 - (RocketMain.fontScale == 1.0f ? 135 : 145), 40 - iconYOffset + bannerAdHeight,arrowWidth, arrowHeight);
        rightColorArrow = new Icon(Assets.instance.arrowRight, Assets.instance.arrowRightPressed,
                RocketMain.WIDTH / 2 + (RocketMain.fontScale == 1.0f ? 95 : 100), 40 - iconYOffset + bannerAdHeight, arrowWidth, arrowHeight);

        leftArrowMode = new Icon(Assets.instance.arrowLeft, Assets.instance.arrowLeftPressed,
                (RocketMain.fontScale == 1.0f ?  90 : 0), RocketMain.HEIGHT - 90 - 65 - iconYOffset, arrowWidth, arrowHeight);
        rightArrowMode = new Icon(Assets.instance.arrowRight, Assets.instance.arrowRightPressed,
                RocketMain.WIDTH - (RocketMain.fontScale == 1.0f ? 90 : 0) - 50, RocketMain.HEIGHT - 90 - 65 - iconYOffset, arrowWidth, arrowHeight);

        leftArrowSkin = new Icon(Assets.instance.arrowLeft,
                Assets.instance.arrowLeftPressed,RocketMain.WIDTH / 2 - (RocketMain.fontScale == 1.0f ? 235 : 245), 40 - iconYOffset + bannerAdHeight,arrowWidth, arrowHeight);
        rightArrowSkin = new Icon(Assets.instance.arrowRight, Assets.instance.arrowRightPressed,
                RocketMain.WIDTH / 2 + (RocketMain.fontScale == 1.0f ? 195 : 200), 40 - iconYOffset + bannerAdHeight, arrowWidth, arrowHeight);

        settings = new Icon(Assets.instance.settings, Assets.instance.settingsPressed,
                RocketMain.WIDTH *3/4 - 70 + 30, 260, 140, 140);
        gamble = new Icon(Assets.instance.gamble, Assets.instance.gamblePressed,
                RocketMain.WIDTH /4 - 70 - 30, 260, 140, 140);

        sound = new Icon(Assets.instance.button, Assets.instance.buttonPressed,
                2*RocketMain.WIDTH - 220 + 2*settingsYOffset,
                RocketMain.HEIGHT - 150  - iconYOffset, buttonSize, buttonSize);
        sound.pressed = Assets.instance.prefs.readBoolean("sound");

        vibration = new Icon(Assets.instance.button, Assets.instance.buttonPressed,
                2*RocketMain.WIDTH - 220 + 2*settingsYOffset,
                RocketMain.HEIGHT - 255  - iconYOffset, buttonSize, buttonSize);
        vibration.pressed = Assets.instance.prefs.readBoolean("vibration");

        leftArrowSensitivity = new Icon(Assets.instance.arrowLeft,
                Assets.instance.arrowLeftPressed, 700 + RocketMain.WIDTH / 2 - 120 + settingsYOffset, RocketMain.HEIGHT - 465 - iconYOffset ,arrowWidth, arrowHeight);
        rightArrowSensitivity = new Icon(Assets.instance.arrowRight,
                Assets.instance.arrowRightPressed, 700 + RocketMain.WIDTH / 2 + 120 - 50 + settingsYOffset, RocketMain.HEIGHT - 465 - iconYOffset ,arrowWidth, arrowHeight);

        unlockSkin = new Icon(Assets.instance.unlock, Assets.instance.unlockPressed,
                RocketMain.WIDTH *3/4 - 70 + 30, 650, 140, 140);
        unlockMode = new Icon(Assets.instance.unlock, Assets.instance.unlockPressed,
                RocketMain.WIDTH /4 - 70 - 30, 650, 140, 140);


//nastavi gamemode
        if (!Assets.instance.prefs.has("gameMode")) {
            Assets.instance.prefs.writeString("gameMode", "Tutorial");
        }
        s = Assets.instance.prefs.readString("gameMode");

        for(GAME_MODE gm : GAME_MODE.vals)
            if(gm.name.equals(s))
                gameMode = gm;

            RocketMain.adsController.showBannerAd();

        updateUnlocked();

//nastavi denar
        if (!Assets.instance.prefs.has("coins")) {
            Assets.instance.prefs.writeInt("coins", 0);
        }

        coins = Assets.instance.prefs.readInt("coins");
    }

    static void updateUnlocked(){
        skin.updateUnlocked();
        gameMode.updateUnlocked();
        unlocked = skin.isUnlocked() && gameMode.isUnlocked();
    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        bannerAdHeight = RocketMain.adsController.bannerShown() ? 110 : 0;
        rightArrowSkin.position.y = leftArrowSkin.position.y = 40 - iconYOffset + bannerAdHeight;


    }

    @Override
    public void render(float deltaTime) {
        if(unlocked)
            Gdx.gl.glClearColor(MenuScreen.skin.bgColor.r,MenuScreen.skin.bgColor.g, MenuScreen.skin.bgColor.b, 1);
        else
            Gdx.gl.glClearColor(MenuScreen.skin.bgColor.r / 2,MenuScreen.skin.bgColor.g / 2, MenuScreen.skin.bgColor.b / 2, 1);
        

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if(!unlocked)
            batch.setColor(0.5f, 0.5f, 0.5f, 1);

            viewportBG.apply();
            batch.setProjectionMatrix(camera.combined);
            //BACKGROUND
        if(MenuScreen.skin != SKIN.PENCIL)
            background.render(batch);
        else
             PencilTools.renderPencilLines(batch);

            batch.flush();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        rocket.render(batch);

        font.getData().setScale(0.8f*RocketMain.fontScale);
        //font.setColor(67f/255f, 185f/255f, 184f/255f, 1); modra
//NASLOV

        if(unlocked) {

            simpleDraw(batch, Assets.instance.title, 140, relativeYPosUp + RocketMain.HEIGHT / 2 + 60, 450, 450 / 1.55f);
            if(gameMode != GAME_MODE.TRAINING) {
                layout.setText(font, "best: " + gameMode.getBestScore());
                font.draw(batch, "best: " + gameMode.getBestScore(), RocketMain.WIDTH / 2 - layout.width / 2,
                        relativeYPosUp + RocketMain.HEIGHT - (RocketMain.fontScale == 1.0f ? 180 : 195));
            }

        }
            else{
            batch.setColor(1, 1, 1, 1);

            drawCoins(batch, layout, font, RocketMain.WIDTH / 2, RocketMain.HEIGHT  - 230, ""+coins);

            if(!skin.isUnlocked()) {
                drawCoins(batch, layout, font, RocketMain.WIDTH *3/4 + 30, 580, ""+skin.price);
            }
            if(!gameMode.isUnlocked()) {
                drawCoins(batch, layout, font, RocketMain.WIDTH /4 - 30, 580, ""+gameMode.price);
            }
        }

        font.draw(batch, "skin", RocketMain.WIDTH / 2 - (RocketMain.fontScale == 1.0f ? 62 : 77), relativeYPosDown + 100 + bannerAdHeight);
        layout.setText(font, gameMode.name);
        font.draw(batch, gameMode.name, RocketMain.WIDTH / 2 - layout.width / 2, relativeYPosUp + RocketMain.HEIGHT - 95);

        font.getData().setScale(0.6f*RocketMain.fontScale);
        if (RocketMain.fontScale == 1.0f)
            font.draw(batch, "game mode:", RocketMain.WIDTH / 2 - 110, relativeYPosUp + RocketMain.HEIGHT - 30);

        if(!skin.isUnlocked()) {
            font.draw(batch, "unlock\n  skin", RocketMain.WIDTH *3/4 - 50 ,910);
        }
        if(!gameMode.isUnlocked()) {
            font.draw(batch, "unlock\n mode", RocketMain.WIDTH /4 - 100 ,910);
        }

        batch.setColor(skin.nextColor);
        rightColorArrow.render(batch);

        batch.setColor(skin.previousColor);
        leftColorArrow.render(batch);

        batch.setColor(67f/255f, 185f/255f, 184f/255f, 1);

        leftArrowMode.render(batch);
        rightArrowMode.render(batch);

        leftArrowSkin.render(batch);
        rightArrowSkin.render(batch);

        if(camera.position.x > 360) {
            rightArrowSensitivity.render(batch);
            leftArrowSensitivity.render(batch);
        }
        batch.setColor(1, 1, 1, 1);

        settings.render(batch);
        gamble.render(batch);
        if(unlocked)
            play.render(batch);
        else {//KLJUCAVNICA
            simpleDraw(batch, Assets.instance.locked, RocketMain.WIDTH / 2 - 225 / 2,
                    RocketMain.HEIGHT / 2 - 225 / 2 * 1.37f, 225, 225 * 1.37f );

            if(!skin.isUnlocked()) {
                unlockSkin.render(batch);
            }
            if(!gameMode.isUnlocked()) {
                unlockMode.render(batch);
            }
        }
        if(camera.position.x > 360){
            renderSettings();
        }

        //SLOT
        else if(camera.position.x < 360 || gambleOn){
            slot.render(deltaTime, batch);
        }

         font.getData().setScale(1);
        batch.end();
// SETTINGS

        if(settingsOn && camera.position.x < 1060 + settingsYOffset + xCut){ //ce so settings on in kamera
            timer += deltaTime;             //še ni prišla na settings meni

            updateSettingsSpeed();

            camera.translate( settingsSpeed *deltaTime ,0);
            if(MenuScreen.skin != SKIN.PENCIL)
                background.position.x += settingsSpeed *deltaTime;

            if(camera.position.x > 790){

                settings.position.x += settingsSpeed *deltaTime;
            }
        }
        else if(!settingsOn && camera.position.x > 360){
            timer += deltaTime;

            updateSettingsSpeed();

            camera.translate( -settingsSpeed *deltaTime ,0);
            if(MenuScreen.skin != SKIN.PENCIL)
                background.position.x -= settingsSpeed *deltaTime;
            if(camera.position.x > 790){
                settingsBMoving = true;
                settings.position.x -= settingsSpeed *deltaTime;
            }
        }

        if(prevSettingsBMoving && !settingsBMoving){//ce se je gumb prej premikal, zdaj pa se ne vec
            //popravi bug, ko se je gumb za nastavitve zamaknil
            settings.position.x = RocketMain.WIDTH *3/4 - 70 + 30;
        }

        prevSettingsBMoving = settingsBMoving;
        settingsBMoving = false;

//GAMBLE

        if(gambleOn && camera.position.x > -447 - settingsYOffset - xCut){
            timer += deltaTime;
            //še ni prišla na settings meni
            updateGambleSpeed();


            camera.translate( -settingsSpeed *deltaTime ,0);
            if(MenuScreen.skin != SKIN.PENCIL)
                background.position.x -= settingsSpeed *deltaTime;

            if(camera.position.x < -70){
                //gambleBMoving = true;
                gamble.position.x -= settingsSpeed *deltaTime;
            }
        }
        else if(!gambleOn && camera.position.x < 358){
            timer += deltaTime;
            updateGambleSpeed();


            camera.translate( settingsSpeed *deltaTime ,0);
            if(MenuScreen.skin != SKIN.PENCIL)
                background.position.x += settingsSpeed *deltaTime;
            if(camera.position.x < -70){
                gambleBMoving = true;
                gamble.position.x += settingsSpeed *deltaTime;
            }
        }

        if(prevGambleBMoving && !gambleBMoving){//ce se je gumb prej premikal, zdaj pa se ne vec
            //popravi bug, ko se je gumb zamaknil
                gamble.position.x = RocketMain.WIDTH / 4 - 70 - 30;
        }
/*
        if(prevCameraGMoving && !cameraGMoving){
            if(camera.position.x > 0){
               // camera.position.x = 360;

            }
            else
                camera.position.x = -450 - settingsYOffset - xCut;
            if(background != null)
                background.position.x = camera.position.x - 360;
        }
        prevCameraGMoving = cameraGMoving;
        cameraGMoving = false;
*/

        prevGambleBMoving = gambleBMoving;
        gambleBMoving = false;

 //PLAY ANIMATION
        if(playPressed){
            playSpeed += 1000*deltaTime;
            relativeYPosDown = play.position.y - 440;
            relativeYPosUp = leftArrowMode.position.y - (RocketMain.HEIGHT - 90 - 65 - iconYOffset);

            leftArrowMode.position.y += playSpeed * deltaTime;
            rightArrowMode.position.y += playSpeed * deltaTime;
            play.position.y -= playSpeed * deltaTime;
            gamble.position.y -= playSpeed * deltaTime;
            settings.position.y -= playSpeed * deltaTime;
            leftArrowSkin.position.y -= playSpeed * deltaTime;
            rightArrowSkin.position.y -= playSpeed * deltaTime;
            leftColorArrow.position.y -= playSpeed * deltaTime;
            rightColorArrow.position.y -= playSpeed * deltaTime;

            playSpeed += 600.0f * deltaTime;

            if(play.position.y < -play.size.y){
                WorldController.firstGame = true;
                game.setScreen(new GameScreen(game));

            }
        }

    }


    void updateSettingsSpeed(){
        settingsSpeed = (float)Math.abs((800 + settingsYOffset + xCut)*0.44f*Math.PI*Math.sin(Math.PI* timer));
    }
    void updateGambleSpeed(){
        settingsSpeed = (float)Math.abs((810 + settingsYOffset + xCut)*0.5f*Math.PI*Math.sin(Math.PI* timer));
    }

/*    boolean leverPulled, leverDescending;
    float leverTimer;
    int leverN;

    private void renderGamble(float deltaTime){
        font.getData().setScale(0.8f*RocketMain.fontScale);

        TextureAtlas.AtlasRegion lever = Assets.instance.slotLever[leverN];

        simpleDraw(batch, lever, -125, 510, 170, 170*1.75f);

        simpleDraw(batch, Assets.instance.slot, -665, 450, 540, 540/1.3f);

        if(leverPulled) {
            if(leverTimer > 0.08f){
                leverTimer = 0;
                if(leverDescending) {

                    leverN++;
                    if (leverN >= 5)
                        leverDescending = false;
                }else {
                    leverN--;
                    if(leverN <= 0)
                        leverPulled = false;
                }
            }
            leverTimer += deltaTime;
        }
    }
*/

    private void renderSettings(){
        font.getData().setScale(0.8f*RocketMain.fontScale);

        sound.render(batch);
        vibration.render(batch);

        font.draw(batch, "sound",  RocketMain.WIDTH + 120 + xCut + settingsYOffset, RocketMain.HEIGHT - 90);
        font.draw(batch, "swipe sensitivity", RocketMain.WIDTH + 70 +
                (RocketMain.fontScale == 1.0f ? 50 : 0) + xCut + settingsYOffset, RocketMain.HEIGHT - 300);
        int sensitivity = Assets.instance.prefs.readInt("sensitivity");
        font.draw(batch, "" + (sensitivity >= 0 ? "+" : "")  + sensitivity,
                700 + RocketMain.WIDTH / 2 - (RocketMain.fontScale == 1.0f ? 40 : 60) + xCut + settingsYOffset, RocketMain.HEIGHT - 410);
        font.draw(batch, "vibration",  RocketMain.WIDTH + 120 + xCut + settingsYOffset, RocketMain.HEIGHT - 200);

        font.getData().setScale(0.6f);
        font.draw(batch, "drawing:",  RocketMain.WIDTH +370+ xCut + settingsYOffset, 260 + bannerAdHeight);
        font.draw(batch, "programming:",  RocketMain.WIDTH +320+ xCut + settingsYOffset, 405 + bannerAdHeight);

        font.getData().setScale(0.65f*RocketMain.fontScale);
        font.draw(batch, "Matjaž Boštic",  RocketMain.WIDTH +305+ xCut + settingsYOffset, 340 + bannerAdHeight);
        font.draw(batch, "Sebastjan Kramar",  RocketMain.WIDTH +252+ xCut + settingsYOffset, 195 + bannerAdHeight);
        font.draw(batch, "LibGDX framework",  RocketMain.WIDTH +245+ xCut + settingsYOffset, 80 + bannerAdHeight);
    }



    @Override
    public void resize(int width, int height) {

        xCut = Math.round((float)width/height*RocketMain.HEIGHT - RocketMain.WIDTH)/2; // screen x cutoff

        slot.resize();

        leftArrowSensitivity.position.x += xCut;
        rightArrowSensitivity.position.x += xCut;

        sound.position.x += xCut;
        vibration.position.x += xCut;

        viewport.update(width, height);

        viewport.setWorldSize(RocketMain.HEIGHT * (float)width/height,RocketMain.HEIGHT);

        viewportBG.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        batch.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {


        scalePoints.set(screenX, screenY);
        scalePoints = viewport.unproject(scalePoints);

        //-125, 510, 170, 170*1.75f

      /*  if(scalePoints.x > -125 && scalePoints.x < -125 + 170 && scalePoints.y > 550
                && scalePoints.y < 550 + 170*1.75f && !leverPulled) {
            leverPulled = true;
            leverDescending = true;
            for(SKIN s : SKIN.values()){
                s.saveNewColor(SKIN.randomColor());
            }

        }*/
        if(unlocked)
            play.press(scalePoints.x, scalePoints.y);
        leftArrowSkin.press(scalePoints.x, scalePoints.y);
        leftArrowMode.press(scalePoints.x, scalePoints.y);
        rightArrowSkin.press(scalePoints.x, scalePoints.y);
        rightArrowMode.press(scalePoints.x, scalePoints.y);
        rightColorArrow.press(scalePoints.x, scalePoints.y);
        leftColorArrow.press(scalePoints.x, scalePoints.y);

        if(coins >= Slot.SPIN_PRICE || unlimitedCoins)
            slot.spin.press(scalePoints.x, scalePoints.y);

        if((!skin.isUnlocked() && coins >= skin.price) || unlimitedCoins)
            unlockSkin.press(scalePoints.x, scalePoints.y);

        if((!gameMode.isUnlocked() && coins >= gameMode.price) || unlimitedCoins)
            unlockMode.press(scalePoints.x, scalePoints.y);

        if(!settingsOn && (slot.stopped || slot.prizeSelected)) {
            gamble.press(scalePoints.x, scalePoints.y);
            if (gambleOn)
                gamble.pressed = true;
        }
        if (!gambleOn) {
            settings.press(scalePoints.x, scalePoints.y);
            if (settingsOn)
                settings.pressed = true;
        }
        sound.press(scalePoints.x, scalePoints.y);
        vibration.press(scalePoints.x, scalePoints.y);

        if(Assets.instance.prefs.readInt("sensitivity") > -5)
            leftArrowSensitivity.press(scalePoints.x, scalePoints.y);

        if(Assets.instance.prefs.readInt("sensitivity") < 5)
            rightArrowSensitivity.press(scalePoints.x, scalePoints.y);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        scalePoints.set(screenX, screenY);
        scalePoints = viewport.unproject(scalePoints);

        if(play.pressed){
            playPressed = true;
            playSpeed = 0;
            }

        else if(slot.spin.pressed){
            slot.spin.pressed = false;
            slot.gamble();
        }
        else if(rightArrowMode.pressed) {
            rightArrowMode.pressed = false;
            gameMode = gameMode.next();
            gameMode.updateUnlocked();
            Assets.instance.prefs.writeString("gameMode", gameMode.name);
            updateUnlocked();
                    }
        else if(leftArrowMode.pressed) {
            leftArrowMode.pressed = false;
            gameMode = gameMode.previous();
            gameMode.updateUnlocked();
            Assets.instance.prefs.writeString("gameMode", gameMode.name);
            updateUnlocked();
        }
        else if(leftColorArrow.pressed) {
            leftColorArrow.pressed = false;
            skin.prevColor();
        }
        else if(rightColorArrow.pressed) {
            rightColorArrow.pressed = false;
            skin.nextColor();
        }
        else if(rightArrowSkin.pressed) {
            rightArrowSkin.pressed = false;
            skin = skin.next();
            skin.updateUnlocked();
            Assets.instance.prefs.writeString("skin", skin.name);
            game.setScreen(new MenuScreen(game));
        }
        else if(leftArrowSkin.pressed) {
            leftArrowSkin.pressed = false;
            skin = skin.previous();
            skin.updateUnlocked();
            Assets.instance.prefs.writeString("skin", skin.name);
            game.setScreen(new MenuScreen(game));
        }
        else if(unlockSkin.pressed) {
            unlockSkin.pressed = false;
            coins -= skin.price;
            Assets.instance.prefs.writeInt("coins", coins);
            skin.unlock();
            Assets.instance.playCoinsSound();
            updateUnlocked();
        }
        else if(unlockMode.pressed) {
            unlockMode.pressed = false;
            coins -= gameMode.price;
            Assets.instance.prefs.writeInt("coins", coins);
            gameMode.unlock();
            Assets.instance.playCoinsSound();
            updateUnlocked();
        }
        else if(rightArrowSensitivity.pressed) {
            rightArrowSensitivity.pressed = false;
            Assets.instance.prefs.writeInt("sensitivity", Assets.instance.prefs.readInt("sensitivity") + 1);//sensitivity se poveca
        }
        else if(leftArrowSensitivity.pressed) {
            leftArrowSensitivity.pressed = false;
            Assets.instance.prefs.writeInt("sensitivity", Assets.instance.prefs.readInt("sensitivity") - 1);
        }
        else if(settings.press(scalePoints.x, scalePoints.y)) {
           if(!settingsOn){
               settingsOn = true;
              // Gdx.input.setCatchBackKey(true);
               timer = 0;
           }
            else{
               settingsOn = false;
               settings.pressed = false;
              // Gdx.input.setCatchBackKey(false);
               timer = 0;
           }
        }
        else if(gamble.press(scalePoints.x, scalePoints.y) && (slot.stopped || slot.prizeSelected)) {
            if(!gambleOn){
                if(Assets.instance.slot == null)
                    Assets.instance.loadSlot();
                gambleOn = true;
                slot.initIcons();
                timer = 0;
                //Gdx.input.setCatchBackKey(true);
            }
            else{
                gambleOn = false;
                gamble.pressed = false;
                timer = 0;
               // Gdx.input.setCatchBackKey(false);
            }
        }
        else if(sound.press(scalePoints.x, scalePoints.y)) {
            boolean soundB = Assets.instance.prefs.readBoolean("sound");
            if(!soundB){
                soundB = true;
            }
            else{
                soundB = false;
                sound.pressed = false;
            }

            Assets.instance.prefs.writeBoolean("sound", soundB);
            Assets.instance.updateSoundOn();
        }

        else if(vibration.press(scalePoints.x, scalePoints.y)) {
            boolean vib = Assets.instance.prefs.readBoolean("vibration");
            if(!vib){
                vib = true;
            }
            else{
                vib = false;
                vibration.pressed = false;
            }

            Assets.instance.prefs.writeBoolean("vibration", vib);
        }

        return false;

    }

    public static void simpleDraw(SpriteBatch batch, TextureRegion region, float posX, float posY, float width, float height){
        batch.draw(region.getTexture(), posX, posY, 0, 0, width, height,
                1, 1, 0, region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(), false, false);
    }

    public static void drawCoins(SpriteBatch batch, GlyphLayout layout, BitmapFont font, float midX, float posY, String numCoins){

        layout.setText(font, numCoins);
        float xStart = midX - (layout.width + 55.0f)/2.0f;

        simpleDraw(batch, Assets.instance.coin, xStart, posY, 50, 50);

        font.draw(batch, numCoins, xStart + 55, posY + layout.height);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.BACK && (slot.stopped || slot.prizeSelected)){
          if(gambleOn)  {
              gambleOn = gamble.pressed = false;
              timer = 0;
          }
            else if(settingsOn){
              settingsOn = settings.pressed = false;
              timer = 0;
          }
           else if(camera.position.x < 370 && camera.position.x > 350) {
              Gdx.app.exit();
          }
            //Gdx.input.setCatchBackKey(false);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
