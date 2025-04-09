package com.mygdx.goatandrun;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class PowerUp extends GameEntity {

    AssetManager manager;
    Texture currentFrame;

    float animationFrame = 0;


    public PowerUp(int x, int y, AssetManager manager)
    {
        setBounds(x,y,64,64);
        this.manager = manager;

        animationFrame = 0f;
        currentFrame = manager.get("powerup/frame000"+(int)animationFrame+".png", Texture.class);

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        animationFrame += 4f*delta;
        if(animationFrame >= 7.f) animationFrame -= 7.f;

        currentFrame = manager.get("powerup/frame000"+(int)animationFrame+".png", Texture.class);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(currentFrame, getX() - getWidth()*0.5f - map.scrollX, getY() - getHeight()*0.5f, 64, 64, 0, 0, 32, 32, false, true);
    }
}
