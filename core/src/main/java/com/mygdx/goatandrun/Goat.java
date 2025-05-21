package com.mygdx.goatandrun;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Goat extends com.mygdx.goatandrun.RunningAnimal {
    static final float JUMP_IMPULSE = -580; //-420; //-360f; //-600f;
    static final float RUN_SPEED = 99999f;//300f;
    static final float AIR_RUN_SPEED = 99999f;//240f;
    static final float BRAKE_SPEED = 1500f;
    static final float AIR_BRAKE_SPEED = 420f;
    static final float STOP_SPEED = 50f;
    static final float AIR_STOP_SPEED = 60f;
    static final float RUN_ACCELERATION = 180;//200f;//600f;//1800f;
    static final float AIR_RUN_ACCELERATION = 160f;//150f;//600f;
    static final float INVULNERABILITY_DURATION = 20f;

    private static final float STAND_WIDTH = 42f;
    private static final float STAND_HEIGHT = 86f;

    private static final float CROUCH_WIDTH = 42f;
    private static final float CROUCH_HEIGHT = 48f; // smaller height when crouched
    private float bottomY;

    AssetManager manager;
    PawLayout joypad;

    Texture currentFrame;

    float animationFrame = 0;
    float invulnerability = 0f;

    public Goat(AssetManager manager) {
        //setSize(120, 120);
        setBounds(400, 120, 42, 86);
        this.manager = manager;
        currentFrame = manager.get("goat/Idle (1).png", Texture.class);
        invulnerability = 0.f;
        bottomY = getY() - getHeight() / 2f; // store bottom
    }

    private void accelerate(float delta) {
        if (speed.x < 125) {
            speed.x += (RUN_ACCELERATION * 5) * delta;
        }else if (speed.x < 250) {
            speed.x += (RUN_ACCELERATION * 2f) * delta;
        } else if (speed.x < 400) {
            speed.x += (RUN_ACCELERATION * 0.038f) * delta ;
        } else if (speed.x < 1600000) {
            speed.x += (RUN_ACCELERATION * 0.02f) * delta;

        }
        if (crouched && !falling && speed.x > 250 ) {
            speed.x += (RUN_ACCELERATION *  0.1f) * delta;
            //speed.x += (RUN_ACCELERATION *  -10000f) * delta;
        }
        System.out.println(speed.x);
    }

    /*private void updateBounds() {
        float bottomY = getY() - getHeight() * 0.5f; // preserve current bottom
        float width = crouched ? CROUCH_WIDTH : STAND_WIDTH;
        float height = crouched ? CROUCH_HEIGHT : STAND_HEIGHT;

        // Set bounds with new height, keeping bottomY fixed
        setBounds(getX(), bottomY + height / 2f, width, height);
    }*/
    private void updateBounds() {
        if (crouched) {
            setBounds(getX(), getY() + 18, 42, CROUCH_HEIGHT);
        } else if (previousCrouch) {
            setBounds(getX(), getY() - 18, 42, STAND_HEIGHT);
        } else {
            setBounds(getX(), getY(), 42, STAND_HEIGHT);
        }
    }

    public void setJoypad(PawLayout joypad) {
        this.joypad = joypad;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        previousCrouch = crouched;
        // Constant right move
        accelerate(delta);
        /*speed.x += RUN_ACCELERATION * delta;
        if (speed.x > RUN_SPEED) {
            speed.x = RUN_SPEED;
        }*/

        // Fall too low
        if (getY() > map.height * TileMap.TILE_SIZE) {
            kill();
        }

        // Left bounds of the level
        if (getX() < getWidth() / 2) {
            setX(getWidth() / 2);
        }

        if (dead) {
            // Death animation
            animationFrame += 10.f * delta;
            int frameTexture = (int) animationFrame + 1;
            if (frameTexture > 4) frameTexture = 4;
            currentFrame = manager.get("goat/Hurt (" + frameTexture + ").png", Texture.class);

            speed.x = 0f;
        } else {
            // Reduce remaining invulnerability
            if (invulnerability > 0.f) invulnerability -= delta;

            if (falling) {
                if (speed.y < 0) {
                    // Start jumping
                    float base_impulse = -JUMP_IMPULSE;
                    float current_impulse = -speed.y;
                    animationFrame = 0 + ((base_impulse - current_impulse) / 32);
                    if (animationFrame > 0) animationFrame = 0;
                } else {
                    // Start falling
                    animationFrame = 2 + (speed.y / 64);
                    if (animationFrame > 1) animationFrame = 1;
                }
                currentFrame = manager.get("goat/Jump (" + (int) (animationFrame + 1) + ").png", Texture.class);

                //Air constant movement
                //accelerate(delta);
                /*speed.x += AIR_RUN_ACCELERATION * delta;
                if (speed.x > AIR_RUN_SPEED) {
                    if (speed.x > RUN_SPEED) {
                        speed.x = RUN_SPEED;
                    } else {
                        speed.x = AIR_RUN_SPEED;
                    }
                }*/

                //Air control
                if (joypad.isPressed("Right")) {
                    // Turbo?
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

                } else if (joypad.isPressed("Down")) {
                    //modificate gravity
                    fast_fall = true;

                }

                // If not left or right input, reduce speed.x
                else {
                    //Desactivate fast fall
                    fast_fall = false;
                    // Reduce speed and stop
                    /*if(speed.x < 0f)
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
                    }*/
                }


            } else if ((speed.x < 0.1f && speed.x > -0.1f)) {
                // Idle
                animationFrame += 10 * delta;
                if (animationFrame >= 4.f) animationFrame -= 4.f;
                currentFrame = manager.get("goat/Idle (" + (int) (animationFrame + 1) + ").png", Texture.class);

            } else if (crouched) {
                // Crouch Walk
                animationFrame += 10 * delta;
                if (animationFrame >= 5.f) animationFrame -= 5.f;
                currentFrame = manager.get("goat/Crouch Walk (" + (int) (animationFrame + 1) + ").png", Texture.class);

            } else {
                // Walk
                animationFrame += 10 * delta;
                if (animationFrame >= 6.f) animationFrame -= 6.f;
                currentFrame = manager.get("goat/Run (" + (int) (animationFrame + 1) + ").png", Texture.class);

            }

            if (!falling && joypad.isPressed("Jump")) {
                // Jump
                jump(1.f);
                manager.get("sound/jump.wav", Sound.class).play();
            }

            if (!falling) {
                // On the ground
                if (joypad.isPressed("Right")) {
                    // Accelerate right
                    /*lookLeft = false;
                    speed.x += RUN_ACCELERATION * delta;
                    if (speed.x > RUN_SPEED) {
                        speed.x = RUN_SPEED;
                    }*/
                } else if (joypad.isPressed("Down")) {
                    // Crouch
                    crouched = true;

                } else {
                    // Stop crouching
                    crouched = false;
                    // Reduce speed and stop
                    /*
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
                    }*/
                }
            }
            if (crouched != previousCrouch) {
                updateBounds();
            }
        }
    }

    public void jump(float strength) {
        speed.y = JUMP_IMPULSE * strength;
    }

    @Override
    public void kill() {
        if (!dead) {
            super.kill();
            animationFrame = 0;
        }
    }

    public void getInvulnerability() {
        invulnerability = INVULNERABILITY_DURATION;
    }

    public boolean hasInvulnerability() {
        return invulnerability > 0.f;
    }

    float getAnimationFrame() {
        return animationFrame;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Blink effect when invulnerable
        if (invulnerability > 0.f && (int) (invulnerability / 0.125f) % 2 == 0) return;
        if (crouched) {
            batch.draw(currentFrame, getX() - getWidth() * 0.85f - map.scrollX - (lookLeft ? 26 : 22), getY() - getHeight() * 1.46f, 120, 120, 0, 2, 24, 24, lookLeft, true);

        //} else if (previousCrouch) {
        //    batch.draw(currentFrame, getX() - getWidth() * 0.85f - map.scrollX - (lookLeft ? 26 : 22), getY() - getHeight() * 1.4f, 120, 120, 0, 2, 24, 24, lookLeft, true);

        }
        else {
            batch.draw(currentFrame, getX() - getWidth() * 0.85f - map.scrollX - (lookLeft ? 26 : 22), getY() - getHeight() * 0.6f, 120, 120, 0, 2, 24, 24, lookLeft, true);
        }
            /*float spriteDrawX = getX() - getWidth() * 0.85f - map.scrollX - (lookLeft ? 26 : 22);
        float spriteDrawY = (getY() - getHeight() * 0.5f); // align to bottom of collision shape
        batch.draw(currentFrame, spriteDrawX, spriteDrawY, 120, 120, 0, 2, 24, 24, lookLeft, true);*/

    }

    // Draw collision box
    public void drawDebug(ShapeRenderer shapes) {
        //super.drawDebug(shapes);

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.NAVY);
        shapes.rect(getX() - getWidth() * 0.5f - map.scrollX, getY() - getHeight() * 0.5f, getWidth(), getHeight());
        shapes.end();
    }
}
