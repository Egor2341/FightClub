package Game.jdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
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
    private float chelWidth;
    private float chelHeight;
    private Vector2 vel;
    private Vector2 pos;
    private static boolean jump;
    private float startY;


    @Override
    public void create() {

        Graphics.DisplayMode dm = Gdx.graphics.getDisplayMode();
        Gdx.graphics.setFullscreenMode(dm);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(w, h);

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);

        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        world = new World(new Vector2(0, -100f), true);
        world.setContactListener(new ListenerClass());

        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 20f);

        Body ground = world.createBody(groundDef);

        PolygonShape borderBox = new PolygonShape();
        borderBox.setAsBox(camera.viewportWidth / 2f, camera.viewportHeight / 20f);

        Fixture groundFixture = ground.createFixture(borderBox, 0.0f);
        groundFixture.setUserData("Ground");

        borderBox.dispose();

        Texture img = new Texture("chel.png");
        float attitude = (float) img.getHeight() / img.getWidth();
        chelWidth = w / 50f;
        chelHeight = w / 50f * attitude;
        BodyDef chelDef = new BodyDef();
        chelDef.type = BodyDef.BodyType.DynamicBody;
        startY = camera.viewportHeight / 10f + chelHeight / 2f;
        chelDef.position.set(camera.viewportWidth / 2f, startY);

        chel = world.createBody(chelDef);


        Sprite sprite = new Sprite(img);

        sprite.setSize(chelWidth, chelHeight);
        chel.setUserData(sprite);
        chel.setFixedRotation(true);


        PolygonShape chelShape = new PolygonShape();
        chelShape.setAsBox(chelWidth / 2f, chelHeight / 2f);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chelShape;
        fixtureDef.friction = 5000f;



        Fixture fixture = chel.createFixture(fixtureDef);
        fixture.setUserData("Chel");

        chelShape.dispose();


        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        debugRenderer.render(world, camera.combined);
        updateChel();
        batch.begin();
        Sprite chelSprite = (Sprite) chel.getUserData();
        batch.draw(chelSprite, chel.getPosition().x - chelSprite.getWidth() / 2f, chel.getPosition().y - chelSprite.getHeight() / 2f,
            chelWidth, chelHeight);
        batch.end();
        doPhysicsStep(dt);
    }


    private void updateChel() {
        vel = chel.getLinearVelocity();
        pos = chel.getPosition();
        if (!jump) {
            if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -Constants.MAX_VELOCITY) {
                chel.applyLinearImpulse(-5000f, 0, pos.x, pos.y, true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < Constants.MAX_VELOCITY) {
                chel.applyLinearImpulse(5000f, 0, pos.x, pos.y, true);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && vel.x < Constants.MAX_VELOCITY) {
                chel.applyLinearImpulse(0, 1000f, pos.x, pos.y, true);
                jump = true;
            }
        }
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

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static void setJump(boolean value){
        jump = value;
    }
}
