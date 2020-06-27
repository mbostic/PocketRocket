package com.mbostic.tools;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mbostic.objects.Clouds;
import com.mbostic.objects.Rocket;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.SKIN;
import com.mbostic.rocket.WorldController;
import com.mbostic.screens.GameScreen;
import com.mbostic.screens.MenuScreen;

public class PencilTools extends AbstractTools {

    float dx, dy, l, alpha;
    public static float[][] pencilTracePositions;
    int pencilTracePosIndex, currTracePosIndex, prevTracePosIndex;

    static TextureRegion regionPencilLine;
    public static float[] lineYPositions;

    public PencilTools(){
        pencilTracePositions = new float[170][2];
        pencilTracePosIndex = 0;
        initPencilLines();
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {

        renderPencilLines(batch);

        if(!(WorldController.gameOver || GameScreen.paused))
             updatePencilLines(deltaTime);

        renderPencilTrace(batch, deltaTime);
    }

    public void renderPencilTrace(SpriteBatch batch, float deltaTime){

        if(!(WorldController.gameOver || GameScreen.paused)) {

            pencilTracePositions[pencilTracePosIndex][0] = (Rocket.position.x + Rocket.size.x / 2.0f - 7);//x pozicija
            pencilTracePositions[pencilTracePosIndex][1] = (Rocket.position.y + Rocket.size.y - 10);//y
            batch.setColor(MenuScreen.skin.currColor);
        }
        else
            batch.setColor(MenuScreen.skin.currColor.r / 2,
                    MenuScreen.skin.currColor.g / 2, MenuScreen.skin.currColor.b / 2, 1);


        for (currTracePosIndex = 0; currTracePosIndex < pencilTracePositions.length; currTracePosIndex++) {

            prevTracePosIndex = currTracePosIndex != 0 ? currTracePosIndex - 1 : pencilTracePositions.length - 1;

            dx = pencilTracePositions[currTracePosIndex][0] - pencilTracePositions[prevTracePosIndex][0];

            if (Math.abs(dx) > 60)//ce gre crta iz ene strani na drugo
                 dx = 0;

            dy = Clouds.yVelocity * deltaTime;
            alpha = (float) Math.atan(dy / dx);
            l = dx / (float) Math.cos(alpha);

            batch.draw(Assets.instance.trace.getTexture(),
                    pencilTracePositions[dx > 0 ? prevTracePosIndex : currTracePosIndex][0],
                    pencilTracePositions[currTracePosIndex][1], 4, 4, 8+Math.abs(l), 8,
                    1, 1, -(float) Math.toDegrees(alpha), Assets.instance.trace.getRegionX(), Assets.instance.trace.getRegionY(),
                    Assets.instance.trace.getRegionWidth(), Assets.instance.trace.getRegionHeight(), false, false);

            if(!(WorldController.gameOver || GameScreen.paused))
                pencilTracePositions[currTracePosIndex][1] += (float) Clouds.yVelocity * deltaTime;

        }
        batch.setColor(1, 1, 1, 1);
        if(!(WorldController.gameOver || GameScreen.paused)) {

            pencilTracePosIndex++;

            if (pencilTracePosIndex >= pencilTracePositions.length)
                pencilTracePosIndex = 0;
        }
    }

    public static void initPencilLines(){
        regionPencilLine = Assets.instance.pencilLine;
        lineYPositions = new float[20];
        for (int i = 0; i < lineYPositions.length; i ++){
            lineYPositions[i] = i*64;
        }
    }

    public static void renderPencilLines(SpriteBatch batch){

        for(float lineYPosition : lineYPositions) {

            batch.draw(regionPencilLine.getTexture(), -RocketMain.WIDTH - 250, lineYPosition, 0, 0, 3* RocketMain.WIDTH + 400, 2,
                    1, 1, 0, regionPencilLine.getRegionX(), regionPencilLine.getRegionY(),
                    regionPencilLine.getRegionWidth(), regionPencilLine.getRegionHeight(), false, false);
        }
    }

    void updatePencilLines(float deltaTime){
        for(int i = 0; i < lineYPositions.length; i ++) {

           lineYPositions[i] += deltaTime * Clouds.yVelocity;

            if (lineYPositions[i] < 0)
                lineYPositions[i] = RocketMain.HEIGHT;
        }
    }


}
