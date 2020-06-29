package com.mbostic.rocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mbostic.objects.Background;
import com.mbostic.objects.Clouds;
import com.mbostic.objects.Icon;
import com.mbostic.objects.Rocket;
import com.mbostic.screens.GameScreen;
import com.mbostic.screens.MenuScreen;

public class WorldController extends InputAdapter implements GestureDetector.GestureListener, Music.OnCompletionListener {

    private RocketMain game;
    InputMultiplexer inputMultiplexer;
    private boolean passedCloud;
    public boolean swiped, newBest, backToMenu,
            replay, troughWall, wasPaused, scoreChanged = true;
    public int score;
    public float swipeCoef;

    public static boolean gameOver, firstGame;
    boolean soundOn;

    public Rocket rocket;
    public Clouds[] cloudsArray;
    public Background background;
    private Vector2 scalePoints;
    public static Vector2 swipeDistance;

    public Icon pause, home, play;

    public WorldController (RocketMain game) {

        this.game = game;

        rocket = new Rocket();
        Clouds.offset = 350 + Rocket.size.y;
        Clouds.yVelocity = -200;
        Clouds.holeWidth = 200;

        switch (MenuScreen.gameMode){
            case TRAINING:{
                Clouds.offset = 450 + Rocket.size.y;
                Clouds.holeWidth = 300;
                troughWall = true;
                break;
            }
            case THROUGH_WALL:
            case ACCELERATING: {
                troughWall = true;
                break;
            }
            case HIGH_SPEED:{
                Clouds.yVelocity = -320;
                troughWall = true;
                break;
            }
            case EASY:{
                troughWall = true;
                Clouds.holeWidth = 250;
            }
        }

        if(MenuScreen.skin != SKIN.PENCIL)
            background = new Background();
        scalePoints = new Vector2();
        swipeDistance = new Vector2();
        if(((float)Gdx.graphics.getHeight())/((float)Gdx.graphics.getWidth()) < 1.8) {

            pause = new Icon(Assets.instance.pauseGame, Assets.instance.pauseGamePressed, RocketMain.WIDTH - 25 - 60,
                    RocketMain.HEIGHT - 25 - 60 - (int) RocketMain.screenCutOffTop, 60, 60);

        } else {
            pause = new Icon(Assets.instance.pauseGame, Assets.instance.pauseGamePressed, RocketMain.WIDTH - 50 - 60,
                    RocketMain.HEIGHT - 25 - 60, 60, 60);
        }

        home = new Icon(Assets.instance.homeGame, Assets.instance.homeGamePressed, RocketMain.WIDTH / 2 - 65, 150, 130, 130);
        if(MenuScreen.skin.isPixelated)
            play = new Icon(Assets.instance.playGame, Assets.instance.playGamePressed, RocketMain.WIDTH / 2 - 60, 400, 120, 200);
        else
            play = new Icon(Assets.instance.playGame, Assets.instance.playGamePressed, RocketMain.WIDTH / 2 - 125, 500, 250, 250);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(new GestureDetector(this));
        Gdx.input.setInputProcessor(inputMultiplexer);

// naredi oblake
        cloudsArray = new Clouds[3];
        for (int i = 0; i<cloudsArray.length; i++){
            cloudsArray[i] = new Clouds();
               if (i == 0) {
                   cloudsArray[i].position.y = Rocket.position.y + Rocket.size.y +
                           Clouds.offset + (firstGame ? 350 : 0);
                   if (firstGame)
                       firstGame = false;
                    continue;
               }
            cloudsArray[i].position.y = cloudsArray[i-1].position.y + Clouds.offset; // nov oblak nastane nad prejšnim
        }

        if(MenuScreen.gameMode == GAME_MODE.TRAINING) {
            cloudsArray[0].setHoleXPos(0);
            cloudsArray[1].setHoleXPos(RocketMain.WIDTH - Clouds.holeWidth);
            cloudsArray[1].position.y = cloudsArray[0].position.y + Clouds.offset + 200;
            cloudsArray[2].position.y = cloudsArray[1].position.y + Clouds.offset + 200;
        }


        swipeCoef = 3 * ((float)Assets.instance.prefs.readInt("sensitivity") * 0.1f + 1);

        soundOn = Assets.instance.prefs.readBoolean("sound");

        launchPlayed = false;

        if (MenuScreen.skin != SKIN.PENCIL){
            Assets.startLaunchSound();
            Assets.launch.setOnCompletionListener(this);
        }
//skrije reklamo
        RocketMain.adsController.hideBannerAd();
    }

    public void update (float deltaTime) {

// PREMAKNE OBJEKTE

        updateObjects(deltaTime);

// NOV OBLAK
        if(cloudsArray[0].position.y + Clouds.size.y < 0)
            newClouds();

// COLLISION TEST
       if(troughWall)
           troughWallCollisionTest();
        else
           collisionTest();

// POVEČA TOČKE
        if(Rocket.position.y > cloudsArray[0].position.y && Rocket.position.y < cloudsArray[0].position.y + 20) {
            if (swiped)
                swiped = false;

            if(!passedCloud){

                cloudsPassed();
            }
        }
    }

    public void collisionTest(){
        if ((Rocket.position.y + Rocket.size.y - 20 > cloudsArray[0].position.y && Rocket.position.y + 170 < cloudsArray[0].position.y + Clouds.size.y &&      //če je raketa med oblakom
                (Rocket.position.x + 20 < cloudsArray[0].holeXPos || Rocket.position.x + Rocket.size.x - 20 > cloudsArray[0].holeXPos + Clouds.holeWidth))
                || Rocket.position.x + 20 < 0 || Rocket.position.x + Rocket.size.x - 20 > RocketMain.WIDTH) {

            gameOver = true;
        }
    }

    public void troughWallCollisionTest(){
        if (Rocket.position.y + Rocket.size.y - 20 > cloudsArray[0].position.y &&
                Rocket.position.y + 170 < cloudsArray[0].position.y + Clouds.size.y &&      //če je raketa med oblakom
                (Rocket.position.x + 20 < cloudsArray[0].holeXPos ||
                        Rocket.position.x + Rocket.size.x - 20 > cloudsArray[0].holeXPos + Clouds.holeWidth))
            gameOver = true;

        else if(Rocket.position.x + Rocket.size.x/2 < 0)
            Rocket.position.x = RocketMain.WIDTH -Rocket.size.x/2;

        else if(Rocket.position.x > RocketMain.WIDTH - Rocket.size.x/2)
            Rocket.position.x = -Rocket.size.x/2;
    }

    public void newClouds(){

        if(MenuScreen.gameMode == GAME_MODE.RANDOM_CLOUDS_OFFSET)
            Clouds.offset = MathUtils.random(500, 800) + Rocket.size.x - 200;

            for (int i = 0; i<cloudsArray.length - 1; i++) {//premakne oblake v arrayu
                cloudsArray[i] = cloudsArray[i+1];
            }
            cloudsArray[cloudsArray.length - 1] = new Clouds(); // nov oblak na zadnjem mestu arraya
            cloudsArray[cloudsArray.length - 1].position.y =
                    cloudsArray[cloudsArray.length - 2].position.y + Clouds.offset;  // nov oblak nastane nad prejšnim

            passedCloud = false;
            wasPaused = false;

        if(MenuScreen.gameMode == GAME_MODE.ACCELERATING)
            Clouds.yVelocity -= 3;
    }

    public void cloudsPassed(){
        score++;
        scoreChanged = true;

        if(soundOn)
            Assets.instance.cloudsPassedSound.play(0.5f);

        if(MenuScreen.gameMode.getBestScore() < score){
            MenuScreen.gameMode.setBestScore(score);
            newBest = true;
        }
        passedCloud = true;

    }

    public void backToMenu(){
        backToMenu = true;
        game.setScreen(new MenuScreen(game));
    }

   public void updateObjects(float deltaTime){
        rocket.velocity.x -= Math.signum(rocket.velocity.x)*0.5*rocket.xAcceleration*deltaTime;
       if(Math.abs(rocket.velocity.x) > 8)
            Rocket.position.x += rocket.velocity.x*deltaTime;

       if(MenuScreen.skin != SKIN.PENCIL) {
           if (background.yVelocity < -4)
               background.yVelocity -= 0.5 * background.yAcceleration * deltaTime;

           background.position.y += background.yVelocity * deltaTime;
       }

       for (Clouds clouds: cloudsArray)
            clouds.position.y += Clouds.yVelocity * deltaTime;
    }

//SWIPE
    private boolean swipeStarted;
    private float startX;
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

              if(!swipeStarted){
            startX = x;     //začetno mesto swipa
            swipeStarted = true;
        }
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        if(!GameScreen.paused)
        swipeDistance.x = x - startX;
        swipeDistance = WorldRenderer.viewport.unproject(swipeDistance);

        swipeStarted = false;
        if(!swiped && Math.abs(swipeDistance.x) > 10){
            rocket.velocity.x = swipeDistance.x * swipeCoef;
            swiped = true;
        }

       return true;
    }

    @Override
    public boolean keyDown(int keycode) {
// NAZAJ V MENU
        if(keycode == Input.Keys.BACK){
            if(GameScreen.paused || gameOver){
                GameScreen.paused = false;
                gameOver = false;
                game.setScreen(new MenuScreen(game));}
            else
                GameScreen.paused = true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        Vector2 input = new Vector2(screenX,screenY);

        if(GameScreen.paused || WorldController.gameOver){
            scalePoints = WorldRenderer.viewportGUI.unproject(input);
            play.press(scalePoints.x,scalePoints.y);
            home.press(scalePoints.x,scalePoints.y);
        }
        else {
            scalePoints = WorldRenderer.viewport.unproject(input);
            pause.press(scalePoints.x, scalePoints.y);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(GameScreen.paused) {
            if (play.pressed) {
                play.pressed = false;
                GameScreen.paused = false;
                Assets.startLaunchSound();
                RocketMain.adsController.hideBannerAd();
            }
            else if (home.pressed){
                home.pressed = false;
                GameScreen.paused = false;
                backToMenu();
            }
        }

        else if(gameOver){

            if (play.pressed){
                play.pressed = false;
               replay = true;
               gameOver = false;
            }
            else if (home.pressed){
                home.pressed = false;
                gameOver = false;
                backToMenu();
            }
        }
        else if (pause.pressed){
            pause.pressed = false;
            GameScreen.paused = true;
            RocketMain.adsController.showBannerAd();
        }

        return false;
    }

    public static long noiseId;
    public static boolean launchPlayed;

    @Override
    public void onCompletion(Music music) {
        noiseId = Assets.instance.noise.loop(0.3f);
        launchPlayed = true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }


}
