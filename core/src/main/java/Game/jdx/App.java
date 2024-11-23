package Game.jdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class App extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float accumulator = 0;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Viewport viewport;
    private Body chel;
    private Sprite sprite;


    @Override
    public void create() {

        Graphics.DisplayMode dm = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(dm);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(w, h);

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);

        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        world = new World(new Vector2(0, -10f), true);

        BodyDef borderDef = new BodyDef();
        borderDef.type = BodyDef.BodyType.StaticBody;
        borderDef.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f);

        Body border = world.createBody(borderDef);

        PolygonShape borderBox = new PolygonShape();
        borderBox.setAsBox(camera.viewportWidth / 2f, camera.viewportHeight / 2f);
        border.createFixture(borderBox, 0.0f);

        borderBox.dispose();

        BodyDef chelDef = new BodyDef();
        chelDef.type = BodyDef.BodyType.DynamicBody;
        chelDef.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f);

        chel = world.createBody(chelDef);

        Texture img = new Texture("chel.png");
        sprite = new Sprite(img);
        chel.setUserData(sprite);



        PolygonShape chelShape = new PolygonShape();
        chelShape.setAsBox(img.getWidth() / 2f, img.getHeight() / 2f);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chelShape;
        fixtureDef.density = 0.5f;


        Fixture fixture = chel.createFixture(fixtureDef);

        chelShape.dispose();


        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        debugRenderer.render(world, camera.combined);
        batch.begin();
        Sprite chelSprite = (Sprite) chel.getUserData();
        batch.draw(chelSprite, chel.getPosition().x - chelSprite.getWidth()/2f, chel.getPosition().y - chelSprite.getHeight()/2f);
        batch.end();
//        doPhysicsStep(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }
}
