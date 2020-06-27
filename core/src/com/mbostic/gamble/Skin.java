package com.mbostic.gamble;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.SKIN;
import com.mbostic.screens.MenuScreen;

public class Skin extends AbstractRocket {

    Color circle;

    public Skin(){
        super();

        while (skin == SKIN.DEFAULT)
           skin = SKIN.gamble();

        init();

        color = new Color(Color.valueOf(Assets.instance.prefs.readString(skin.name + 0)));

        circle = RocketMain.newColor(255, 230, 116);
    }

    @Override
    public void render(SpriteBatch batch, float x, float y){

        batch.setColor(circle);

        MenuScreen.simpleDraw(batch, Assets.instance.circle, x + 5,
                y + (HEIGHT - WIDTH)/2 + 5,WIDTH - 10, WIDTH - 10);

        batch.setColor(Color.WHITE);

        super.render(batch, x, y);
    }

    @Override
    public void getPrize() {
        skin.unlock();
    }
}
