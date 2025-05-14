package com.mygdx.goatandrun;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Goat extends com.mygdx.goatandrun.RunningAnimal {
    static final float JUMP_IMPULSE = -580; //-420; //-360f; //-600f;
    static final float RUN_SPEED = 300f;
    static final float AIR_RUN_SPEED = 240f;
    static final float BRAKE_SPEED = 1500f;
    static final float AIR_BRAKE_SPEED = 420f;
    static final float STOP_SPEED = 50f;
    static final float AIR_STOP_SPEED = 60f;
    static final float RUN_ACCELERATION = 1800f;
    static final float AIR_RUN_ACCELERATION = 600f;
    static final float INVULNERABILITY_DURATION = 20f;

    AssetManager manager;
    PawLayout joypad;

    Texture currentFrame;

    float animationFrame = 0;
    float invulnerability = 0f;

    public Goat(AssetManager manager)
    {
        //setSize(120, 120);
        setBounds(400,160,42, 86);
        this.manager = manager;
        currentFrame = manager.get("goat/Idle (1).png", Texture.class);
        invulnerability = 0.f;
    }

    public void setJoypad(PawLayout joypad) {
        this.joypad = joypad;
    }
    @Override
    public void act(float delta) {
        super.act(delta);

        // COnstant right move
        speed.x += RUN_ACCELERATION * delta;
        if (speed.x > RUN_SPEED) {
            speed.x = RUN_SPEED;
        }

        // Fall too low
        if(getY() > map.height * TileMap.TILE_SIZE)
        {
            kill();
        }

        // Left bounds of the level
        if(getX() < getWidth() / 2)
        {
            setX(getWidth() / 2);
        }

        if(dead)
        {
            // Death animation
            animationFrame += 10.f*delta;
            int frameTexture = (int)animationFrame+1;
            if(frameTexture > 4)
                frameTexture = 4;
            currentFrame = manager.get("goat/Hurt ("+frameTexture+").png", Texture.class);

            speed.x = 0f;
        }
        else
        {
            // Reduce remaining invulnerability
            if(invulnerability > 0.f)
                invulnerability -= delta;

            if(falling)
            {
                if(speed.y < 0)
                {
                    // Start jumping
                    float base_impulse = -JUMP_IMPULSE;
                    float current_impulse = -speed.y;
                    animationFrame = 0 + ((base_impulse - current_impulse) / 32);
                    if (animationFrame > 0) animationFrame = 0;
                }
                else
                {
                    // Start falling
                    animationFrame = 2 + (speed.y / 64);
                    if (animationFrame > 1) animationFrame = 1;
                }
                currentFrame = manager.get("goat/Jump ("+(int)(animationFrame+1)+").png", Texture.class);

                //Air control
                if (joypad.isPressed("Right"))
                {
                    // Accelerate right
                    /*
                    lookLeft = false;
                    speed.x += AIR_RUN_ACCELERATION * delta;
                    if (speed.x > AIR_RUN_SPEED) {
                        if (speed.x > RUN_SPEED) {
                            speed.x = RUN_SPEED;
                        } else {
                            speed.x = AIR_RUN_SPEED;
                        }
                    }
                    */
                }
                else if (joypad.isPressed("Left"))
                {
                    // Accelerate left
                    lookLeft = true;
                    speed.x -= AIR_RUN_ACCELERATION * delta;
                    if (speed.x < -AIR_RUN_SPEED) {
                        if (speed.x < -RUN_SPEED) {
                            speed.x = -RUN_SPEED;
                        } else {
                            speed.x = -AIR_RUN_SPEED;
                        }
                    }
                }

                // If not left or right input, reduce speed.x
                else {
                    // Reduce speed and stop
                    if(speed.x < 0f)
                    {
                        if(speed.x < -AIR_STOP_SPEED) {
                            speed.x += delta * AIR_BRAKE_SPEED;
                        }
                        else
                        {
                            speed.x = 0f;
                        }
                    }
                    else if (speed.x > 0f)
                    {
                        if(speed.x > AIR_STOP_SPEED) {
                            speed.x -= delta * AIR_BRAKE_SPEED;
                        }
                        else
                        {
                            speed.x = 0f;
                        }
                    }
                }


            }
            else if((speed.x < 0.1f && speed.x > -0.1f))
            {
                // Idle
                animationFrame += 10 * delta;
                if (animationFrame >= 4.f) animationFrame -= 4.f;
                currentFrame = manager.get("goat/Idle ("+(int)(animationFrame+1)+").png", Texture.class);

            }
            else if(crouched)
            {
                // Crouch Walk
                animationFrame += 10 * delta;
                if (animationFrame >= 5.f) animationFrame -= 5.f;
                currentFrame = manager.get("goat/Crouch Walk ("+(int)(animationFrame+1)+").png", Texture.class);

            }
            else
            {
                // Walk
                animationFrame += 10 * delta;
                if (animationFrame >= 6.f) animationFrame -= 6.f;
                currentFrame = manager.get("goat/Run ("+(int)(animationFrame+1)+").png", Texture.class);

            }

            if(!falling && joypad.consumePush("Jump"))
            {
                // Jump
                jump(1.f);
                manager.get("sound/jump.wav", Sound.class).play();
            }

            if(!falling)
            {
                // On the ground
                if (joypad.isPressed("Right"))
                {
                    // Accelerate right
                    lookLeft = false;
                    speed.x += RUN_ACCELERATION * delta;
                    if (speed.x > RUN_SPEED) {
                        speed.x = RUN_SPEED;
                    }
                }
                else if (joypad.isPressed("Down"))
                {
                    // Accelerate left

                    //lookLeft = true;
                    crouched = true;
                    //speed.x -= RUN_ACCELERATION * delta;
                    if (speed.x < -RUN_SPEED) {
                        speed.x = -RUN_SPEED;
                    }
                }
                else
                {
                    // Stop crouching
                    crouched = false;
                    // Reduce speed and stop
                    if(speed.x < 0f)
                    {
                        if(speed.x < -STOP_SPEED) {
                            speed.x += delta * BRAKE_SPEED;
                        }
                        else
                        {
                            speed.x = 0f;
                        }
                    }
                    else if (speed.x > 0f)
                    {
                        if(speed.x > STOP_SPEED) {
                            speed.x -= delta * BRAKE_SPEED;
                        }
                        else
                        {
                            speed.x = 0f;
                        }
                    }
                }
            }
        }
    }

    public void jump(float strength)
    {
        speed.y = JUMP_IMPULSE * strength;

        /*if(joypad.isPressed("Right"))
        {
            speed.x = RUN_SPEED * 05f;
        }
        else if (joypad.isPressed("Left"))
        {
            speed.x = -RUN_SPEED * 05f;
        }*/
    }

    @Override
    public void kill()
    {
        if(!dead) {
            super.kill();
            animationFrame = 0;
        }
    }

    public void getInvulnerability()
    {
        invulnerability = INVULNERABILITY_DURATION;
    }

    public boolean hasInvulnerability()
    {
        return invulnerability > 0.f;
    }

    float getAnimationFrame()
    {
        return animationFrame;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Blink effect when invulnerable
        if(invulnerability > 0.f && (int)(invulnerability/0.125f)%2 == 0)
            return;

        batch.draw(currentFrame, getX() - getWidth()*0.85f - map.scrollX - (lookLeft ? 26 : 22), getY() - getHeight()*0.6f, 120, 120, 0, 2, 24, 24, lookLeft, true);
    }

    // Draw collision box
    public void drawDebug(ShapeRenderer shapes) {
        //super.drawDebug(shapes);

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.NAVY);
        shapes.rect(getX() - getWidth()*0.5f - map.scrollX, getY() - getHeight()*0.5f, getWidth(), getHeight());
        shapes.end();
    }
}
