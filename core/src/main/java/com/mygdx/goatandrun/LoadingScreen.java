package com.mygdx.goatandrun;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class LoadingScreen implements Screen {

    GoatAndRun game;
    float loadProgress;

    LoadingScreen(GoatAndRun game)
    {
        this.game = game;
        AssetManager  manager = game.manager;

        // Add assets for loading

        // Tiles
        for(int i = 1; i < 22; i++)
            manager.load("tiles/"+i+".png", Texture.class);

        // Background image
        manager.load("BG.png", Texture.class);

        //GUI
        manager.load("gui/Button-off.png", Texture.class);
        manager.load("gui/Button-on.png", Texture.class);
        manager.load("gui/Down-off.png", Texture.class);
        manager.load("gui/Down-on.png", Texture.class);
        manager.load("gui/Right-off.png", Texture.class);
        manager.load("gui/Right-on.png", Texture.class);
        manager.load("gui/Jump-off.png", Texture.class);
        manager.load("gui/Jump-on.png", Texture.class);
        manager.load("gui/Pause-off.png", Texture.class);
        manager.load("gui/Pause-on.png", Texture.class);


        // Goat
        for (int i = 0; i < 4; i++)
        {
            manager.load("goat/Idle (" +(i+1)+").png", Texture.class);
        }
        for (int i = 0; i < 6; i++)
        {
            manager.load("goat/Run (" +(i+1)+").png", Texture.class);
        }
        for (int i = 0; i < 2; i++)
        {
            manager.load("goat/Jump (" +(i+1)+").png", Texture.class);
        }
        for (int i = 0; i < 4; i++)
        {
            manager.load("goat/Hurt (" +(i+1)+").png", Texture.class);
        }
        for (int i = 0; i < 5; i++)
        {
            manager.load("goat/Crouch Walk (" +(i+1)+").png", Texture.class);
        }

        //Dino
        for (int i = 0; i < 10; i++)
        {
            manager.load("dino/Walk (" +(i+1)+").png", Texture.class);
        }
        for (int i = 0; i < 8; i++)
        {
            manager.load("dino/Dead (" +(i+1)+").png", Texture.class);
        }

        //PowerUp
        for (int i = 0; i < 7; i++)
        {
            manager.load("powerup/frame000" +i+".png", Texture.class);
        }

        // Sounds
        manager.load("sound/music.mp3", Music.class);
        manager.load("sound/loselife.wav", Sound.class);
        manager.load("sound/kill.wav", Sound.class);
        manager.load("sound/jump.mp3", Sound.class);
        manager.load("sound/powerup.wav", Sound.class);
        manager.load("sound/levelcomplete.mp3", Sound.class);

        loadProgress = 0f;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // Render step =============================================
        float currentLoadProgress = game.manager.getProgress();
        if(currentLoadProgress > loadProgress || loadProgress == 0f)
        {
            loadProgress = currentLoadProgress;

            game.camera.update();
            game.batch.setProjectionMatrix(game.camera.combined);
            game.textBatch.setProjectionMatrix(game.textCamera.combined);
            game.shapeRenderer.setProjectionMatrix(game.camera.combined);

            ScreenUtils.clear(Color.BLACK);

            // Progress bar
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(Color.GRAY);
            game.shapeRenderer.rect(90, 290, 620, 100);
            game.shapeRenderer.setColor(Color.BLACK);
            game.shapeRenderer.rect(100, 300, 600, 80);
            game.shapeRenderer.setColor(Color.RED);
            game.shapeRenderer.rect(110, 310, 580 * loadProgress, 60);
            game.shapeRenderer.end();

            game.textBatch.begin();
            game.bigFont.draw(game.textBatch, "LOADING GOAT...", 40, 340);
            game.mediumFont.draw(game.textBatch, (int) (loadProgress * 100.f) + "%", 360, 160);
            game.textBatch.end();

        }

        // Update step ====================================
        if(game.manager.update())
        {
            game.setScreen(new MainMenuScreen(game));
            this.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
