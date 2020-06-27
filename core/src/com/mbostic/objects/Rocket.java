package com.mbostic.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.WorldController;
import com.mbostic.screens.GameScreen;
import com.mbostic.screens.MenuScreen;

public class Rocket {

    public static Vector2 position;
    public Vector2 velocity;
    public static Vector2 size;
    private float stateTime;
    private int regionN;
    public final int xAcceleration = 1000;
    TextureRegion regionRocket;
    TextureRegion regionFire;

    public Rocket(){
        regionRocket = Assets.instance.rocket;
        size = new Vector2(120, 120 * regionRocket.getRegionHeight()/regionRocket.getRegionWidth());
        position = new Vector2(RocketMain.WIDTH/2 - size.x/2, 260 - size.y/2 + RocketMain.screenCutOff);
        velocity = new Vector2(0, 0);
        regionN = 1;

    }

    public void render(SpriteBatch batch) {

        if (!GameScreen.paused && !WorldController.gameOver && Assets.instance.fire1 != null) {
            stateTime += Gdx.graphics.getDeltaTime();
//spremeni slikco
            if (stateTime >= 0.05f) {
                regionN++;
                stateTime = 0;
                if (regionN > 3) regionN = 1;
            }
        }
        if (regionN == 1) regionFire = Assets.instance.fire1;
        else if (regionN == 2) regionFire = Assets.instance.fire2;
        else if (regionN == 3) regionFire = Assets.instance.fire3;

        if (Assets.instance.fire1 != null && !RocketMain.menu)
            batch.draw(regionFire.getTexture(), position.x, position.y, 0, 0, size.x, size.x *
                            regionFire.getRegionHeight() / regionFire.getRegionWidth(),
                    1, 1, 0, regionFire.getRegionX(), regionFire.getRegionY(),
                    regionFire.getRegionWidth(), regionFire.getRegionHeight(), false, false);

        if (!(WorldController.gameOver || GameScreen.paused || !MenuScreen.unlocked))
            batch.setColor(MenuScreen.skin.currColor);
        else
            batch.setColor(MenuScreen.skin.currColor.r/2, MenuScreen.skin.currColor.g/2, MenuScreen.skin.currColor.b/2, MenuScreen.skin.currColor.a);

        batch.draw(regionRocket.getTexture(), position.x, position.y, 0, 0, size.x, size.y,
                1, 1, 0, regionRocket.getRegionX(), regionRocket.getRegionY(),
                regionRocket.getRegionWidth(), regionRocket.getRegionHeight(), false, false);
        if (!(WorldController.gameOver || GameScreen.paused || !MenuScreen.unlocked))
            batch.setColor(1,1,1,1);
        else
            batch.setColor(0.5f,0.5f,0.5f,1);

            batch.draw(Assets.instance.rocketOutline.getTexture(), position.x, position.y, 0, 0, size.x, size.y,
                    1, 1, 0, Assets.instance.rocketOutline.getRegionX(), Assets.instance.rocketOutline.getRegionY(),
                    Assets.instance.rocketOutline.getRegionWidth(), Assets.instance.rocketOutline.getRegionHeight(), false, false);

        batch.setColor(1,1,1,1);
    }
}
