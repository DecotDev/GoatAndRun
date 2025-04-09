package com.mygdx.goatandrun;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class MainMenuScreen implements Screen
{
    GoatAndRun game;
    PawLayout mainMenu;
    public MainMenuScreen(GoatAndRun game)
    {
        this.game = game;

        mainMenu = new PawLayout(game.camera, game.manager, game.mediumFont);
        mainMenu.loadFromJson("mainmenu.json");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.textBatch.setProjectionMatrix(game.textCamera.combined);

        game.batch.begin();
        game.batch.draw(game.manager.get("BG.png", Texture.class), 0, 0, 800, 480, 0,0, 1000, 750, false, true);
        game.batch.end();

        game.textBatch.begin();
        game.bigFont.draw(game.textBatch,"SUPER PUIG BROS.", 30, 480 - 60);
        game.smallFont.draw(game.textBatch,"(c) Puig Castellar 2025", 160, 480 - 420);
        game.textBatch.end();

        mainMenu.render(game.batch, game.textBatch);


        // Start the game!
        if(mainMenu.consumeRelease("Start"))
        {
            game.lives = 3;
            game.setScreen(new GameScreen(game));
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
