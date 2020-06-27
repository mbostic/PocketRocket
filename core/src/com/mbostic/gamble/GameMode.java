package com.mbostic.gamble;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.GAME_MODE;
import com.mbostic.rocket.RocketMain;
import com.mbostic.rocket.SKIN;
import com.mbostic.screens.MenuScreen;

public class GameMode extends SlotIcon{

    BitmapFont font;

    GAME_MODE mode;
    Color circle;

    String firstLetter, secondLetter;
    boolean twoWords;

    public GameMode(BitmapFont font){

        this.font = font;

        mode = GAME_MODE.gamble();

        String[] words = mode.name.split(" ");

        firstLetter = "" + words[0].charAt(0);

        twoWords = words.length == 2;

        if(twoWords)
            secondLetter = "" + words[1].charAt(0);

        circle = RocketMain.newColor(100, 255, 255);
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {

        batch.setColor(circle);

        MenuScreen.simpleDraw(batch, Assets.instance.circle, x + 5,
                y + (HEIGHT - WIDTH)/2 + 5,WIDTH - 10, WIDTH - 10);

        batch.setColor(Color.WHITE);

        font.getData().setScale(0.9f);

        font.draw(batch, firstLetter, x + WIDTH/2 - 27, y +(twoWords ? HEIGHT*3/4 : HEIGHT/2) + 30);

        if(twoWords)
            font.draw(batch, secondLetter, x + WIDTH/2 - 27, y + HEIGHT/4 + 30);

        font.getData().setScale(0.7f);
    }

    @Override
    public void getPrize() {
        mode.unlock();
    }
}
