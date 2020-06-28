package com.mbostic.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;

public class Background {

    public Vector2 position;
    public float yVelocity;
    public Vector2 size;
    public final float yAcceleration = -2;
    public TextureRegion region;

    public Background(){
        region = Assets.instance.background;
        size = new Vector2(720, 720 * region.getRegionHeight()/region.getRegionWidth());
        position = new Vector2(0, RocketMain.screenCutOffTop);
        yVelocity = -40;

    }

    public void render(SpriteBatch batch) {
        batch.draw(region.getTexture(), position.x, position.y, 0, 0, size.x, size.y,
                1, 1, 0, region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(), false, false);
    }

}
