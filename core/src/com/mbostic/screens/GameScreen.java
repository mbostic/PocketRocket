package com.mbostic.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mbostic.gameModes.MovingCloudsWC;
import com.mbostic.gameModes.TrainingWR;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.WorldController;
import com.mbostic.rocket.WorldRenderer;


public class GameScreen implements Screen {

    public static boolean paused;
    final RocketMain game;

    private WorldController worldController;
    private WorldRenderer worldRenderer;

    public GameScreen(final RocketMain gam){

        RocketMain.menu = false;

        paused = false;
        game = gam;

    }

    @Override
    public void show() {
        Gdx.input.setCatchBackKey(true);

        worldController = new WorldController(game);
        worldRenderer = new WorldRenderer(worldController);

        switch (MenuScreen.gameMode){

            case TRAINING:{
                worldRenderer = new TrainingWR(worldController);
                break;
            }
            case CLASSIC: break;
            case ACCELERATING: break;
            case MOVING_CLOUDS:{
                worldController = new MovingCloudsWC(game);
                worldRenderer = new WorldRenderer(worldController);
                break;
            }
            case THROUGH_WALL: break;
            case HIGH_SPEED: break;
            case RANDOM_CLOUDS_OFFSET:  break;
        }
    }

    @Override
    public void render(float deltaTime) {

        if(worldController.replay){

            game.setScreen(new GameScreen(game));
        }

        worldRenderer.render(deltaTime);

        if (!paused && !WorldController.gameOver && !worldController.replay) {
            worldController.update(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.resize(width, height);

    }

    @Override
    public void pause() {
        if(!WorldController.gameOver){

            paused = true;
        }
    }

    @Override
    public void resume() {
        Assets.instance.init();
        Assets.instance.load();
    }

    @Override
    public void hide() {
        Assets.stopLaunchSound();
        worldRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void dispose() {
        Assets.instance.dispose();
    }

}
