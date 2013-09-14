package com.me.conscientia.screen;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Play implements Screen {
	XmlReader xml = new XmlReader();
	
	private OrthographicCamera camera;
	private World world;  
    private Box2DDebugRenderer dRenderer;    
    
    private int viewportWidth;
    private int viewportHeight;
    private float BOX_STEP;  
    private int BOX_VELOCITY_ITERATIONS;  
    private int BOX_POSITION_ITERATIONS;  
    private float WORLD_TO_BOX=0.01f;  
    private float BOX_WORLD_TO=100f; 
    
    private final static float cameraCloseParameter = 30f;
    private final static float DEGTORAD = 0.0174532925199432957f; // Degree to Radians
    
    private Body boxbody;
    private float boxSpeed = 90000;
    private Vector2 movement = new Vector2();
    
    private void readXml(){
    	try {
			Element root = xml.parse(Gdx.files.internal("variables/Box2D.xml"));
			
			viewportWidth = root.getChildByName("Camera").getInt("width");
			viewportHeight = root.getChildByName("Camera").getInt("height");
			
			BOX_STEP = root.getChildByName("World").getFloat("timeStep");
			BOX_VELOCITY_ITERATIONS = root.getChildByName("World").getInt("velocityIterations");
			BOX_POSITION_ITERATIONS = root.getChildByName("World").getInt("positionIterations");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @SuppressWarnings("unused")
    private float convertToBox(float x){
    	return x*WORLD_TO_BOX;
    }
    
    @SuppressWarnings("unused")
    private float convertToWorld(float x){
    	return x*BOX_WORLD_TO;
    }

	@Override
	public void render(float delta) {
		
		//Debug purpose
		//Gdx.app.log("Render", "got it!");
		
		Gdx.gl.glClearColor(0, 0, 0, 1);		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
        boxbody.applyForceToCenter(movement, true);
        
        dRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		//Debug purpose
		//Gdx.app.log("Resize", "got it!");
		
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
		
	}

	@Override
	public void show() {
		
		//Debug purpose
		//Gdx.app.log("Show", "got it!");
		
		world = new World(new Vector2(0, -9.8f),true);
		
		readXml();
		
		dRenderer = new Box2DDebugRenderer(); 
		
		camera = new OrthographicCamera();  
		camera.viewportWidth = viewportWidth;  
        camera.viewportHeight = viewportHeight;
        camera.position.set(camera.viewportWidth / cameraCloseParameter , camera.viewportHeight / cameraCloseParameter, 0f);
        
        //Input controls
        Gdx.input.setInputProcessor(new InputController(){
        	@Override
        	public boolean keyDown(int keycode) {
        		switch(keycode){
        		case Keys.W:
        			movement.y = boxSpeed;
        			break;
        		case Keys.A:
        			movement.x = -boxSpeed;
        			break;
        		case Keys.D:
        			movement.x = boxSpeed;
        			break;
        		case Keys.S:
        			movement.y = -boxSpeed;
        			break;
        		}
				return true;        		
        	}
        	
        	@Override
        	public boolean keyUp(int keycode) {
        		switch(keycode){
        		case Keys.W:
        			movement.y = 0;
        			break;
        		case Keys.A:
        			movement.x = 0;
        			break;
        		case Keys.D:
        			movement.x = 0;
        			break;
        		case Keys.S:
        			movement.y = 0;
        			break;
        		}
				return true;  
        	}

        });
        
        
        //GROUND
        //Ground Body definition
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.StaticBody;
        groundBodyDef.position.set(new Vector2(0, 10));  
        
        //Ground Shape
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox(500f, 10f); 
        
        //Fixture definition
        FixtureDef groundfixtureDef = new FixtureDef();
        groundfixtureDef.shape = groundBox;
        groundfixtureDef.friction = 1.0f;
        groundfixtureDef.restitution = 0;
        
        //Join Ground bodyDef with fixtureDef and place it inside the world
        Body groundBody = world.createBody(groundBodyDef);
        groundBody.createFixture(groundfixtureDef);  
        
        //Dispose Shape after use
        groundBox.dispose();
        
        
        //BALL
        //Ball body definition
        BodyDef ballBodyDef = new BodyDef();
        ballBodyDef.type = BodyType.DynamicBody;  
        ballBodyDef.position.set(new Vector2(10, 100));
        
        //Ball shape  
        CircleShape ballShape = new CircleShape();  
        ballShape.setRadius(10f); 
        
        //Ball Fixture
        FixtureDef ballfixtureDef = new FixtureDef();
        ballfixtureDef.shape = ballShape;  
        ballfixtureDef.density = 3.0f;  
        ballfixtureDef.friction = 0.7f;  
        ballfixtureDef.restitution = 0.5f;
        
        Body body = world.createBody(ballBodyDef);
        body.createFixture(ballfixtureDef);  
        
        //Dispose shape
        ballShape.dispose();
        
        
        //BOX
        //Box body definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyType.DynamicBody;  
        boxBodyDef.position.set(new Vector2(7, 125));
        
        //Box shape  
        PolygonShape boxShape = new PolygonShape();  
        boxShape.setAsBox(10f, 10f);
        
        //Box Fixture
        FixtureDef boxfixtureDef = new FixtureDef();
        boxfixtureDef.shape = boxShape;  
        boxfixtureDef.density = 5.0f;  
        boxfixtureDef.friction = 0.7f;  
        boxfixtureDef.restitution = 0.1f;
        
        boxbody = world.createBody(boxBodyDef);
        boxbody.createFixture(boxfixtureDef);  
        
        
        //Dispose shape
        boxShape.dispose();
        
        
        //Moving platform
        //platform Body definition
        BodyDef platBodyDef =new BodyDef();  
        platBodyDef.type = BodyType.KinematicBody;
        platBodyDef.position.set(new Vector2(20, 115));
        
        //platform Shape
        PolygonShape platBox = new PolygonShape();  
        platBox.setAsBox(10f, 10f); 
        
        //Fixture definition
        FixtureDef platfixtureDef = new FixtureDef();
        platfixtureDef.shape = platBox;
        platfixtureDef.friction = 1.0f;
        platfixtureDef.restitution = 0.2f;
        
        //Join Ground bodyDef with fixtureDef and place it inside the world
        Body mBody = world.createBody(platBodyDef);
        mBody.createFixture(platfixtureDef); 
        
        mBody.setLinearVelocity(1, 0);
        mBody.setAngularVelocity(360*DEGTORAD);
        
        //Dispose Shape after use
        platBox.dispose();
        

	}

	@Override
	public void hide() {
		//Debug purpose
		//Gdx.app.log("Hide", "got it!");
	}

	@Override
	public void pause() {
		//Debug purpose
		//Gdx.app.log("Pause", "got it!");
	}

	@Override
	public void resume() {
		//Debug purpose
		//Gdx.app.log("Resume", "got it!");
	}

	@Override
	public void dispose() {
		//Debug purpose
		//Gdx.app.log("Dispose", "got it!");
	}

}
