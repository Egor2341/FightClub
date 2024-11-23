package Game.jdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class App extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float accumulator = 0;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Texture imageKozrev;
    private Sprite spriteKozrev;
    private Body personKozrev;

    @Override
    public void create() {
        batch = new SpriteBatch();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(30, 30 * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        imageKozrev = new Texture("Kozrev.png");
        spriteKozrev = new Sprite(imageKozrev);
        spriteKozrev.setSize(imageKozrev.getWidth(),imageKozrev.getHeight());


        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(10f, 15f); // Начальная позиция
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        personKozrev = world.createBody(bodyDef);
        createPerson(personKozrev);
        createGround();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        doPhysicsStep(dt);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        updatePerson(personKozrev);
        spriteKozrev.setPosition(personKozrev.getPosition().x - 0.5f, personKozrev.getPosition().y - 0.5f);
        batch.begin();
        debugRenderer.render(world, camera.combined);
        spriteKozrev.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        imageKozrev.dispose();
        spriteKozrev.getTexture().dispose();
        world.dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = 30f;
        camera.viewportHeight = 30f * height / width;
        camera.update();
    }

    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            accumulator -= Constants.TIME_STEP;
        }
    }

    private void updatePerson(Body person) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            person.applyForceToCenter(new Vector2(-200, 0), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            person.applyForceToCenter(new Vector2(200, 0), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            person.applyForceToCenter(new Vector2(0, 500), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            person.applyForceToCenter(new Vector2(0, -100), true);
        }

    }

    private void createPerson(Body person) {
//        CircleShape playerShape = new CircleShape();
//        playerShape.setRadius(100 / 40f);
//        person.createFixture(playerShape,1f);
//        playerShape.dispose();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((float) imageKozrev.getWidth() /10, (float) imageKozrev.getHeight() /10); // Размеры персонажа
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 0.5f;
        person.createFixture(fixtureDef);
        shape.dispose();
    }
    private void createGround(){
        BodyDef wallBodyDef = new BodyDef();
        wallBodyDef.type = BodyDef.BodyType.StaticBody;
        wallBodyDef.position.set(0, 0);
        Body wallBody = world.createBody(wallBodyDef);

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(25f, 1.0f);
        FixtureDef wallFixtureDef = new FixtureDef();
        wallFixtureDef.shape = wallShape;
        wallFixtureDef.density = 0;
        wallFixtureDef.friction = 0f;

        wallBody.createFixture(wallFixtureDef);
        wallShape.dispose();
    }
}
