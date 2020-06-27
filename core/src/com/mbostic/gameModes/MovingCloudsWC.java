package com.mbostic.gameModes;

import com.badlogic.gdx.math.MathUtils;
import com.mbostic.objects.Clouds;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.WorldController;

public class MovingCloudsWC extends WorldController {

    public MovingCloudsWC(RocketMain game) {
        super(game);
        troughWall = true;
    }

    @Override
    public void update(float deltaTime){

        super.update(deltaTime);

        for (Clouds clouds: cloudsArray){
            if(clouds.holeXPos + Clouds.holeWidth > RocketMain.WIDTH - 10 || clouds.holeXPos < 10)
                clouds.xVelocity = -clouds.xVelocity;

            clouds.holeXPos += (float) clouds.xVelocity * deltaTime;
            clouds.position.x += (float) clouds.xVelocity * deltaTime;
        }

    }

    @Override
    public void newClouds(){
        super.newClouds();

        cloudsArray[cloudsArray.length - 1].xVelocity = MathUtils.random(-70, 70);
        }
    }
