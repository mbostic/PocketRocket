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

public class RocketTools extends AbstractTools {

    public static float[][] smokePositions;
    int smokePosIndex, currSmokePosIndex;
    float timer;
    boolean retroRocket;

    TextureRegion regionSmoke;

    public RocketTools(){

        smokePositions = new float[30][3];//x,y,size
        smokePosIndex = 0;
        regionSmoke = Assets.instance.smoke;
        retroRocket = MenuScreen.skin == SKIN.RETRO_ROCKET_1;

    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        if(!(WorldController.gameOver || GameScreen.paused)) {

            timer += deltaTime;

            if(timer > 0.08f) {//vsake 0.3s naredi nov dim
                timer = 0;
                smokePositions[smokePosIndex][0] = (Rocket.position.x + 36);//x pozicija
                smokePositions[smokePosIndex][1] = (Rocket.position.y + (retroRocket ? 20 : 0));//y
                smokePositions[smokePosIndex][2] = retroRocket ? 70 : 50;
                smokePosIndex++;
                if (smokePosIndex >= smokePositions.length)
                    smokePosIndex = 0;
            }
        }
        for (currSmokePosIndex = 0; currSmokePosIndex < smokePositions.length; currSmokePosIndex++) {

            if(!(WorldController.gameOver || GameScreen.paused))
                 batch.setColor(1,1,1,2500/(smokePositions[currSmokePosIndex][2]*smokePositions[currSmokePosIndex][2]));
            else
                batch.setColor(0.5f,0.5f,0.5f,2500/(smokePositions[currSmokePosIndex][2]*smokePositions[currSmokePosIndex][2]));

            batch.draw(regionSmoke.getTexture(),
                    smokePositions[currSmokePosIndex][0] - (smokePositions[currSmokePosIndex][2] - 50.0f)/2.0f,
                    smokePositions[currSmokePosIndex][1], 0, 0,
                    smokePositions[currSmokePosIndex][2], smokePositions[currSmokePosIndex][2],
                    1, 1, 0, regionSmoke.getRegionX(), regionSmoke.getRegionY(),
                    regionSmoke.getRegionWidth(), regionSmoke.getRegionHeight(), false, false);

            if(!(WorldController.gameOver || GameScreen.paused)){
                smokePositions[currSmokePosIndex][1] += (float) Clouds.yVelocity * deltaTime;
                smokePositions[currSmokePosIndex][2] += (float) 50 * deltaTime;//velikost oblakov se veca 50px/s
            }

            if(!(WorldController.gameOver || GameScreen.paused))
                batch.setColor(1,1,1,1);
            else
                batch.setColor(0.5f,0.5f,0.5f,1);
        }
    }
}
