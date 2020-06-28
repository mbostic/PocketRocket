package com.mbostic.rocket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbostic.objects.Clouds;
import com.mbostic.screens.GameScreen;
import com.mbostic.screens.MenuScreen;
import com.mbostic.tools.PencilTools;
import com.mbostic.tools.RocketTools;
import com.mbostic.tools.AbstractTools;


public class WorldRenderer implements Disposable{

    private OrthographicCamera camera;
    public static Viewport viewport, viewportGUI;
    public Vector2 scalePoints;
    AbstractTools tools;
    GlyphLayout layout;

    public WorldController worldController;
    public SpriteBatch batch;
    public BitmapFont font;
    boolean vibratedAndLaunchSoundStopped, interstitialAdShown;
    static float adTimer;
    int coinsEarned;

    public WorldRenderer (WorldController worldController) {

        this.worldController = worldController;
        font =  Assets.instance.fontGame;
        scalePoints = new Vector2();

        camera = new OrthographicCamera(RocketMain.WIDTH, RocketMain.HEIGHT);
        camera.setToOrtho(false, RocketMain.WIDTH, RocketMain.HEIGHT);
        camera.update();

        viewport = new FillViewport(RocketMain.WIDTH, RocketMain.HEIGHT, camera);
        viewportGUI = new FitViewport(RocketMain.WIDTH, RocketMain.HEIGHT, camera);

        batch = new SpriteBatch();

        layout = new GlyphLayout();

        switch (MenuScreen.skin) {

            case DEFAULT:
            case RETRO_ROCKET_1: {tools = new RocketTools(); break;}
            case PENCIL: {tools = new PencilTools(); break;}//orodja za narisat dim, crto,...

            default: break;
        }
    }

    public void render(float deltaTime) {

        adTimer += deltaTime;

// GAME OVER
        if (WorldController.gameOver || GameScreen.paused) {
            Gdx.gl.glClearColor(MenuScreen.skin.bgColor.r / 2,MenuScreen.skin.bgColor.g / 2, MenuScreen.skin.bgColor.b / 2, 1);
        } else
            Gdx.gl.glClearColor(MenuScreen.skin.bgColor.r,MenuScreen.skin.bgColor.g, MenuScreen.skin.bgColor.b, 1);

// Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!worldController.replay && !worldController.backToMenu) {

            batch.begin();

            if(!(WorldController.gameOver || GameScreen.paused))
                batch.setColor(1, 1, 1, 1);

// nariše ostalo
            viewport.apply();
            batch.setProjectionMatrix(camera.combined);

            if (MenuScreen.skin != SKIN.PENCIL && worldController.background.position.y > -worldController.background.size.y)
                worldController.background.render(batch);

            if(MenuScreen.skin == SKIN.PENCIL)
                tools.render(batch, deltaTime);

            worldController.rocket.render(batch);

            if(MenuScreen.skin == SKIN.DEFAULT || MenuScreen.skin == SKIN.RETRO_ROCKET_1)
                tools.render(batch, deltaTime);

            for (Clouds clouds : worldController.cloudsArray)
                clouds.render(batch);

// nariše GUI
            float scale = 0.8f;
            if(!WorldController.gameOver) {
                if(MenuScreen.skin.isPixelated)
                    font.getData().setScale(1.5f);
                else
                    font.getData().setScale(1);


                if(((float)Gdx.graphics.getHeight())/((float)Gdx.graphics.getWidth()) < 1.8) {
                    font.draw(batch, "" + worldController.score, 25, RocketMain.HEIGHT - 25 - RocketMain.screenCutOffTop);
                } else {
                    font.draw(batch, "" + worldController.score, 50, RocketMain.HEIGHT - 25);
                }

                scaleInput(viewport);

              if(!GameScreen.paused)
                  worldController.pause.render(batch);
            }

            additionalRender(deltaTime);

// PAUSED

            if (GameScreen.paused) {

                Assets.pauseLaunchSound();

                batch.flush();
                viewportGUI.apply();
                batch.setProjectionMatrix(camera.combined);
                batch.setColor(1, 1, 1, 1);

                if(MenuScreen.skin.isPixelated){
                    font.getData().setScale(2);
                    font.draw(batch, "Paused", 110, RocketMain.HEIGHT - 180);
                    font.getData().setScale(1.5f);
                }
                else{
                    font.getData().setScale(1.8f * scale);
                    font.draw(batch, "Paused", 160, RocketMain.HEIGHT - 200);
                    font.getData().setScale(scale);
                }

                worldController.play.render(batch);
                worldController.home.render(batch);

                batch.setColor(0.5f, 0.5f, 0.5f, 1);

                batch.flush();
            }
// GAME OVER

            if (WorldController.gameOver) {

                //TODO showInterstitial();

                batch.flush();
                viewportGUI.apply();
                batch.setProjectionMatrix(camera.combined);
                batch.setColor(1, 1, 1, 1);

                if(!vibratedAndLaunchSoundStopped){

                    RocketMain.adsController.showBannerAd();

                    //doda denar
                    if(MenuScreen.gameMode != GAME_MODE.TRAINING) {

                        coinsEarned = Math.round(worldController.score * MenuScreen.gameMode.coinsFactor);

                        Assets.instance.prefs.writeInt("coins", Assets.instance.prefs.readInt("coins")
                                + coinsEarned);

                    }
                   Assets.stopLaunchSound();

                    if (Assets.instance.prefs.readBoolean("vibration")) {
                        Gdx.input.vibrate(100);
                    }
                    vibratedAndLaunchSoundStopped = true;
                }

                if(MenuScreen.skin.isPixelated){
                    font.getData().setScale(3);
                    font.draw(batch, ((worldController.newBest && MenuScreen.gameMode != GAME_MODE.TRAINING) ? "New\nbest!" : "Game\nover"), 110, RocketMain.HEIGHT - 80);
                    font.getData().setScale(1.5f);
                    font.draw(batch, "SCORE:" + worldController.score, 140, RocketMain.HEIGHT - 420);
                }
                else{
                    font.getData().setScale(2 * scale);
                    if(worldController.newBest && MenuScreen.gameMode != GAME_MODE.TRAINING)
                        font.draw(batch, "New best!", 65, RocketMain.HEIGHT - 90);
                    else
                        font.draw(batch, "Game over", 48, RocketMain.HEIGHT - 90);
                    font.getData().setScale(scale);
                    font.draw(batch, "SCORE: " + worldController.score, 225, RocketMain.HEIGHT - 280);
                }
//COINS
                if(MenuScreen.gameMode != GAME_MODE.TRAINING) {
                    if (MenuScreen.skin.isPixelated) {

                        int posY = RocketMain.HEIGHT - 600;

                        layout.setText(font, "+" + coinsEarned);
                        float xStart = RocketMain.WIDTH / 2 - (layout.width + 75.0f)/2.0f;

                        MenuScreen.simpleDraw(batch, Assets.instance.coinPx, xStart - 10, posY - 10, 70, 70);

                        font.draw(batch, "+" + coinsEarned, xStart + 75, posY + layout.height);

                      //  MenuScreen.drawCoins(batch, layout, font, RocketMain.WIDTH / 2, RocketMain.HEIGHT - 600,
                       //         "+" + (int) (worldController.score * MenuScreen.gameMode.coinsFactor));

                    } else {
                        MenuScreen.drawCoins(batch, layout, font, RocketMain.WIDTH / 2, RocketMain.HEIGHT - 450,
                                "+" + coinsEarned);
                    }
                }
                worldController.play.render(batch);
                worldController.home.render(batch);

                batch.setColor(0.5f, 0.5f, 0.5f, 1);

                batch.flush();
            }



           if(!worldController.backToMenu)
               batch.end();

        }
      //  Gdx.app.debug("height ","" +(float)RocketMain.adsController.getHeigth() * RocketMain.HEIGHT/Gdx.graphics.getHeight());
    }

  /* private void drawFPS() {
       font.getData().setScale(MenuScreen.skin.isPixelated ? 0.8f : 0.5f);
       font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),20, 40 + RocketMain.screenCutOff);
       font.getData().setScale(1);
   }*/

    public void additionalRender(float deltaTime){
    }

    public void showInterstitial(){
        if (RocketMain.adsController.isWifiConnected() && !interstitialAdShown && adTimer > 70.0f) {//pokaže reklamo vsake 70s
            RocketMain.adsController.showInterstitialAd(new Runnable() {
                @Override
                public void run() {
                    interstitialAdShown = true;
                    adTimer = 0; //reset ad timer
                }
            });
        }
    }

    public void scaleInput(Viewport viewport){ //prilagodi točke dotika viewportu
        scalePoints.set(Gdx.input.getX(),Gdx.input.getY());
        scalePoints = viewport.unproject(scalePoints);
    }

    public void resize(int width, int height){

        viewport.update(width, height);
        viewportGUI.update(width, height);
    }


    @Override
    public void dispose() {
        batch.dispose();
    }

}
