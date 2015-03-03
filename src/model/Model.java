package model;

import entities.*;
import levelGeneration.Level;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Model
{
	// region Constants
	private final float PLAYER_GRAVITY_ACCELERATION_SCALE = 5.0f;
	private final float DEFAULT_DENSITY = 1.0f;
	// endregion

	// region Fields
	private World world;
	private Body mainBody;
	private ViewEntity mainEntity;
	private List<ViewEntity> dynamicEntities;
	private List<ViewEntity> staticEntities;
	// TODO: LIST OF DYNAMIC BODIES UNDER PLAYER GRAVITY
	private Vec2 currentGravity;
	// endregion

	// region Getters
	public World getWorld()
	{
		return world;
	}


	public ViewEntity getMainEntity()
	{
		return mainEntity;
	}


	public List<ViewEntity> getDynamicEntities()
	{
		return dynamicEntities;
	}


	public List<ViewEntity> getStaticEntities()
	{
		return staticEntities;
	}
	// endregion

	/**
	 *
	 */
	public Model()
	{
		world = null;
		mainBody = null;
		mainEntity = null;
		dynamicEntities = new ArrayList<ViewEntity>();
		staticEntities = new ArrayList<ViewEntity>();
		currentGravity = new Vec2(0.0f, 0.0f);
	}


	/**
	 * 
	 * @param level
	 *
	 * @return	the success of level creation.
	 */
	public boolean buildLevel(Level level)
	{
		// Construct physics world + load physics bodies
		World w = new WorldBuilder().buildEnvironment(level);

		if (w == null || w.getBodyCount() <= 0) return false;

		// Get references to first body in list, which must be the Main Body
		mainBody = w.getBodyList();
		world = w;
		return true;
	}


	/**
	 *
	 * @param x
	 * @param y
	 * @param mag
	 */
	public void applyPlayerGravity(int x, int y, int mag)
	{
		mag *= PLAYER_GRAVITY_ACCELERATION_SCALE;
		Vec2 gravity = new Vec2(x * mag, y * mag);
		if (currentGravity == gravity) return;
		currentGravity = gravity;
	}


	/**
	 *
	 */
	public void stepWorld(float timeStep, int velocityIt, int positionIt)
	{
		mainBody.applyForceToCenter(currentGravity);
		world.step(timeStep, velocityIt, positionIt);
	}


	/**
	 *
	 */
	private class WorldBuilder
	{
		/**
		 *
		 * @param level
		 *
		 * Post-condition: 	The main entity is the first body in the World instance.
		 * 					All other entities added to the World instance.
		 */
		public World buildEnvironment(Level level)
		{
			World world = new World(level.getGravityVector());

			// TODO: STANDARD LEVEL BOUNDING BOX

			Entity e = level.getMainEntity();
			BodyDef bodyDef = constructBodyDef(e);
			Body body = world.createBody(bodyDef);
			FixtureDef fixtureDef = constructFixtureDef(e);
			body.m_mass = e.getMass();
			body.m_invMass = 1 / e.getMass();
			body.createFixture(fixtureDef);
			mainBody = body;
			e.setPhysicsBody(mainBody);
			mainEntity = new ViewEntity(e);

			for (int i = 0; i < level.getEntities().length; i++)
			{
				e = level.getEntities()[i];
				bodyDef = constructBodyDef(e);
				body = world.createBody(bodyDef);
				fixtureDef = constructFixtureDef(e);
				body.createFixture(fixtureDef);
				body.m_mass = e.getMass();
				body.m_invMass = 1 / e.getMass();
				e.setPhysicsBody(body);
				ViewEntity v = new ViewEntity(e);

				if (body.getType() == BodyType.DYNAMIC)
					dynamicEntities.add(v);
				else if (body.getType() == BodyType.STATIC)
					staticEntities.add(v);
			}

			return world;
		}


		/**
		 *
		 * @param entity
		 */
		private BodyDef constructBodyDef(Entity entity)
		{
			assert entity != null;
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(entity.getxModel(), entity.getyModel());
			bodyDef.type = entity.isPositionLocked() ? BodyType.STATIC : BodyType.DYNAMIC;
			bodyDef.angle = entity.getRotation();
			bodyDef.gravityScale = entity.getGravityScale();
			bodyDef.angularDamping = entity.getAngularDamping();
			bodyDef.linearDamping = entity.getLinearDamping();

			return bodyDef;
		}


		private FixtureDef constructFixtureDef(Entity entity)
		{
			assert entity != null;
			FixtureDef fixtureDef = new FixtureDef();

			// TODO: HANDLE CIRCLES + OTHER SHAPES
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.setAsBox(entity.getWidth(), entity.getHeight());

			fixtureDef.shape = polygonShape;
			fixtureDef.friction = entity.getFrictionCoeff();
			fixtureDef.restitution = entity.getRestitutionCoeff();
			fixtureDef.density = DEFAULT_DENSITY;

			return fixtureDef;
		}
	}
}