package Game.jdx;

import com.badlogic.gdx.physics.box2d.*;

public class ListenerClass implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getUserData().equals("Ground") && fixtureB.getUserData().equals("Chel")) {
            App.setJump(false);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }


};
