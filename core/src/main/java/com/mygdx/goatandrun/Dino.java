package com.mygdx.goatandrun;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Dino extends RunningAnimal
{

    static final float RUN_SPEED = 120f;
    static final float RUN_ACCELERATION = 200f;
    static final float DISCOVER_DISTANCE = 500f;

    AssetManager manager;
    Texture currentFrame;
    Goat goat;

    boolean discovered;

    float animationFrame = 0;

    public Dino(int x, int y, AssetManager manager, Goat goat)
    {
        setBounds(x,y,64, 116);
        this.manager = manager;
        this.goat = goat;
        currentFrame = manager.get("dino/Walk (1).png", Texture.class);
        lookLeft = true;
        discovered = false;
    }

    @Override
    public void act(float delta) {

        if(Math.abs(goat.getX() - getX()) < DISCOVER_DISTANCE) discovered = true;
        if(!discovered) return;

        super.act(delta);

        if(dead)
        {
            animationFrame += delta * 6.f;
            int textureFrame = (int) animationFrame+1;
            if(textureFrame >= 9)
                textureFrame = 8;
            currentFrame = manager.get("dino/Dead ("+textureFrame+").png", Texture.class);

            speed.x = 0f;

            if(animationFrame >= 15)
            {
                this.remove();
            }

        }
        else if(!falling)
        {
            // Walk animation
            animationFrame += delta * 6.f;
            if(animationFrame >= 10.f)
                animationFrame -= 10.f;
            currentFrame = manager.get("dino/Walk ("+(int)(animationFrame+1)+").png", Texture.class);

            if(!lookLeft)
            {
                // Accelerate right
                speed.x += RUN_ACCELERATION * delta;
                if(speed.x > RUN_SPEED)
                {
                    speed.x = RUN_SPEED;
                }

                // Collided with a wall
                if(map.isSolid((int)(getX() + getWidth()/2 + delta * speed.x), (int)(getY() - getHeight()*0.25f)) ||
                    map.isSolid((int)(getX() + getWidth()/2 + delta * speed.x), (int)(getY() + getHeight()*0.25f)) )
                {
                    lookLeft = true;
                }
            }
            else
            {
                // Accelerate left
                speed.x -= RUN_ACCELERATION * delta;
                if(speed.x < -RUN_SPEED)
                {
                    speed.x = -RUN_SPEED;
                }

                // Collided with a wall
                if(map.isSolid((int)(getX() - getWidth()/2 + delta * speed.x), (int)(getY() - getHeight()*0.25f)) ||
                    map.isSolid((int)(getX() - getWidth()/2 + delta * speed.x), (int)(getY() + getHeight()*0.25f)))
                {
                    lookLeft = false;
                }
            }
        }
        else
        {
            // Frame for falling
            currentFrame = manager.get("dino/Walk (1).png", Texture.class);
        }
    }

    @Override
    public void kill() {
        super.kill();
        animationFrame = 0f;
        manager.get("sound/kill.wav", Sound.class).play();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(currentFrame, getX() - getWidth()*0.5f - map.scrollX - (lookLeft ? 44 : 12), getY() - getHeight()*0.5f, 128, 128, 0, 0, 680, 472, lookLeft, true);
    }

    // Draw collision box
    @Override
    public void drawDebug(ShapeRenderer shapes) {
        //super.drawDebug(shapes);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.GREEN);
        shapes.rect(getX() - getWidth()*0.5f - map.scrollX, getY() - getHeight()*0.5f, getWidth(), getHeight());
        shapes.end();
    }

}
