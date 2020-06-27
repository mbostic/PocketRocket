package com.mbostic.gamble;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mbostic.rocket.SKIN;

public class SkinColor extends AbstractRocket {


    public SkinColor(){

        super();
        color = SKIN.randomColor();

    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {

        super.render(batch, x, y);


    }

    @Override
    public void getPrize() {
        SKIN.values()[rocketNum].saveNewColor(color);
    }
}
