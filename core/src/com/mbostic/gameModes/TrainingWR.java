package com.mbostic.gameModes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.WorldController;
import com.mbostic.rocket.WorldRenderer;
import com.mbostic.screens.GameScreen;

public class TrainingWR extends WorldRenderer{

    boolean lowerTransparency;
    final int POS_Y = 400,
            HEIGHT = 100,
            STRETCH_SPEED = 500,
            CIRCLE_SIZE = 70;

    int fullWidth = 150,
            circleXPos = RocketMain.WIDTH/2 + fullWidth /2;

    float width, posX = fullWidth /2, transparency = 0;
    TextureAtlas.AtlasRegion region, circleRegion;

    public TrainingWR(WorldController worldController) {
        super(worldController);
        region = Assets.instance.trainingArrow;
        circleRegion = Assets.instance.circle;
    }

    @Override
    public void additionalRender(float deltaTime){

    if (!(WorldController.gameOver || GameScreen.paused)  && worldController.score < 2) {

        if (!Gdx.input.isTouched()){

            if (worldController.score == 1) {
                fullWidth = 220;
                circleXPos = RocketMain.WIDTH / 2 - fullWidth / 2 - CIRCLE_SIZE;
            }
            if(worldController.scoreChanged){
                renderCircle(deltaTime);
                font.draw(batch, "press",  RocketMain.WIDTH / 2 - 100, POS_Y + 300);
            }
        }
        else {

            scaleInput(viewport);

            boolean firstDrag = worldController.score == 0;

            if (firstDrag ? scalePoints.x > RocketMain.WIDTH/2 - 20 : scalePoints.x < RocketMain.WIDTH/2 + 50) {

                worldController.scoreChanged = false;

                font.draw(batch, "drag", RocketMain.WIDTH / 2 - 100, POS_Y + 300);

                if (firstDrag) {

                    renderArrow(deltaTime, true);
                } else {

                    renderArrow(deltaTime, false);
                }
            }
            else
                font.draw(batch, "release", RocketMain.WIDTH / 2 - 110, POS_Y + 300);
        }

    }
    }

    void renderCircle(float deltaTime){

        batch.setColor(1,1,1,transparency);

        batch.draw(circleRegion.getTexture(), circleXPos, POS_Y, 0, 0, CIRCLE_SIZE, CIRCLE_SIZE,
                1, 1, 0, circleRegion.getRegionX(), circleRegion.getRegionY(),
                circleRegion.getRegionWidth(), circleRegion.getRegionHeight(), false, false);

        if(lowerTransparency) {
            transparency -= deltaTime;
            if(transparency < 0.05)
                lowerTransparency = false;
        }else{
            transparency += deltaTime;
            if(transparency > 0.95)
                lowerTransparency = true;
        }
    }

    void renderArrow(float deltaTime, boolean left){

        if(!left){
            batch.draw(region.getTexture(), RocketMain.WIDTH/2 - fullWidth /2, POS_Y + 100, 0, 0, width, HEIGHT,
                    1, 1, 0, region.getRegionX(), region.getRegionY(),
                    region.getRegionWidth(), region.getRegionHeight(), false, false);
        }else{
            batch.draw(region.getTexture(), RocketMain.WIDTH/2 + posX, POS_Y + 100, 0, 0, width, HEIGHT,
                    1, 1, 0, region.getRegionX(), region.getRegionY(),
                    region.getRegionWidth(), region.getRegionHeight(), true, false);

            if(width < fullWidth)
                posX -= STRETCH_SPEED * deltaTime;
        }

        if(width < fullWidth) {
            width += STRETCH_SPEED * deltaTime;

        }
    }
}
