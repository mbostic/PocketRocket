package com.mbostic.gamble;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class SlotIcon {
    public TextureRegion region;
    public static final float WIDTH = 127.9f, HEIGHT = WIDTH*1.23f;

    public abstract void render(SpriteBatch batch, float x, float y);

    public abstract void getPrize();
}
