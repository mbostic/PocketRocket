package com.mbostic.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Icon {

    private TextureRegion region;
    private TextureRegion regionPressed;

    public Vector2 position;
    public Vector2 size;
    public boolean pressed;

    public Icon(TextureRegion region, TextureRegion regionPressed, int posX, int posY, int sizeX, int sizeY){

        this.regionPressed = regionPressed;
        this.region = region;
        this.size = new Vector2(sizeX, sizeY);
        pressed = false;
        position = new Vector2(posX, posY);

    }

    public boolean press(float x, float y){

        boolean released = false;

        if(x > position.x && x < position.x + size.x && y > position.y && y < position.y + size.y){

            if(pressed)
                   released = true;

            pressed = true;

        }
        return released;
    }

    public void render(SpriteBatch batch){

        TextureRegion reg = pressed ? regionPressed : region;

        batch.draw(reg.getTexture(), position.x, position.y, 0, 0, size.x, size.y,
                1, 1, 0, reg.getRegionX(), reg.getRegionY(),
               reg.getRegionWidth(), reg.getRegionHeight(), false, false);
    }

}
