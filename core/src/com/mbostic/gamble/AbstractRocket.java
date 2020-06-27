package com.mbostic.gamble;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.SKIN;

public abstract class AbstractRocket extends SlotIcon {

    int rocketNum, rocketWidth, rocketHeight, rocketFireHeight;
    TextureRegion outline;
    Color color;
    SKIN skin;

    public AbstractRocket(){

        skin = SKIN.gamble();
        init();

    }

    public void init(){

        rocketNum = skin.ordinal();
        rocketFireHeight = skin.fireHeight;

        region = Assets.instance.slotRockets[rocketNum];
        outline = Assets.instance.slotRocketOutlines[rocketNum];


        rocketHeight = (int) HEIGHT - 10;
        rocketWidth = rocketHeight * region.getRegionWidth()/region.getRegionHeight();
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        batch.setColor(color);

        batch.draw(region.getTexture(), x + WIDTH/2 - rocketWidth/2, y + 5 - rocketFireHeight/2, 0, 0, rocketWidth,
                rocketHeight, 1, 1, 0, region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(), false, false);

        batch.setColor(1,1,1,1);

        batch.draw(outline.getTexture(), x + WIDTH/2 - rocketWidth/2, y + 5 - rocketFireHeight/2, 0, 0, rocketWidth,
                rocketHeight, 1, 1, 0, outline.getRegionX(), outline.getRegionY(),
                outline.getRegionWidth(), outline.getRegionHeight(), false, false);
    }

    @Override
    public abstract void getPrize();
}
