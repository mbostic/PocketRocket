package com.mbostic.gamble;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mbostic.rocket.Assets;
import com.mbostic.rocket.RocketMain;
import com.mbostic.screens.MenuScreen;

public class Coins extends SlotIcon{

    int numCoins;
    BitmapFont font;
    final int COIN_SIZE = 70;
    GlyphLayout layout;

    public Coins(BitmapFont font){

        numCoins = COINS.gamble().coinsNum;

        layout = new GlyphLayout();
        font.getData().setScale(0.7f);
        layout.setText(font, ""+numCoins);

        this.font = font;

        region = Assets.instance.coin;

    }

    enum COINS{

        PRIZE1(400, 4),
        PRIZE2(300, 7),
        PRIZE3(200, 15),
        PRIZE4(150, 20),
        PRIZE6(100, 20),
        PRIZE7(70, 20),
        PRIZE8(50, 15),
        PRIZE9(20, 10);

        final int coinsNum,
                probability;

        COINS(int coinsNum, int probability){
            this.coinsNum = coinsNum;
            this.probability = probability;
        }
        public static COINS gamble(){

            int[] arr = new int[values().length];
            for(int i = 0; i < arr.length; i++){
                arr[i] = values()[i].probability;
            }

            return values()[RocketMain.gamble(arr)];
        }
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {

        batch.draw(region.getTexture(), x + WIDTH/2 - COIN_SIZE/2, y + HEIGHT - COIN_SIZE - 10, 0, 0, COIN_SIZE, COIN_SIZE,
                1, 1, 0, region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(), false, false);

        font.getData().setScale(0.7f);
        font.draw(batch,""+numCoins, x + WIDTH/2-layout.width/2, y + 55);

    }

    @Override
    public void getPrize() {

        MenuScreen.coins += numCoins;

        Assets.instance.prefs.writeInt("coins", MenuScreen.coins);
    }
}
