package com.mbostic.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.SKIN;
import com.mbostic.rocket.WorldController;
import com.mbostic.screens.GameScreen;
import com.mbostic.screens.MenuScreen;

public class Clouds {
    public float holeXPos;
    public static int holeWidth = 200;//širina enega doodla
    public final int doodleWidth = 200;
    public Vector2 position;
    float doodlePosX;
    public int xVelocity;

    public static int yVelocity = -200;
    public static float offset = 600;
    public static Vector2 size;
    private TextureRegion region;
    private static float prevHoleXPos;
    TextureAtlas.AtlasRegion[] doodles;

    public Clouds(){
        setXPos();
        size = new Vector2(900 * 0.7f, 200 * 0.7f);

        while(Math.abs(prevHoleXPos - holeXPos) < 170) {
            setXPos();
        }
        prevHoleXPos = holeXPos;

        if(MenuScreen.skin != SKIN.PENCIL){
            region = Assets.instance.clouds;
            position = new Vector2(holeXPos - size.x, RocketMain.HEIGHT / 2 + 100);
        }
        else {
            doodles = new TextureAtlas.AtlasRegion[4];
            for(int i = 0;i < 4 ;i++) {
                doodles[i] = Assets.instance.doodle[MathUtils.random(10)];  //doodle je izbran na random
            }
            position = new Vector2(holeXPos - 2*doodleWidth-50, RocketMain.HEIGHT / 2 + 100);//3 doodli namesto enega oblaka
            doodlePosX = position.x;
        }
    }
    public void render(SpriteBatch batch){
        if(MenuScreen.skin != SKIN.PENCIL){
        batch.draw(region.getTexture(), position.x, position.y, 0, 0, size.x, size.y,//lev oblak
                1, 1, 0, region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(), false, false);

        batch.draw(region.getTexture(), holeXPos + holeWidth, position.y, 0, 0, size.x, size.y,//desen oblak
                1, 1, 0, region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(), false, false);}
        else{//pencil

            if((WorldController.gameOver || GameScreen.paused))
                batch.setColor(0.5f, 0.5f, 0.5f, 1);

            for(int i = 0;i < 4 ;i++) {

                region = doodles[i];

                batch.draw(region.getTexture(), doodlePosX, position.y, 0, 0, doodleWidth, region.getRegionHeight(),//lev oblak
                    1, 1, 0, region.getRegionX(), region.getRegionY(),
                    region.getRegionWidth(), region.getRegionHeight(), false, false);

                doodlePosX += doodleWidth;

                if(i != 1)
                    doodlePosX += 50;
                else
                    doodlePosX += holeWidth;
            }
            doodlePosX = position.x;
        }
    }

    private void setXPos(){
        holeXPos = MathUtils.random(0, RocketMain.WIDTH - holeWidth);
    }

    public void setHoleXPos(int x){
        holeXPos = x;
        position.x = MenuScreen.skin != SKIN.PENCIL ? holeXPos - size.x : holeXPos - 2*doodleWidth-50;
        doodlePosX = position.x;
    }
}
