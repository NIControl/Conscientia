package com.me.conscientia.screen;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
	
	OrthographicCamera camera;
	World world = new World(new Vector2(0, -100), true);  
    Box2DDebugRenderer dRenderer;    
    
    private int viewportWidth;
    private int viewportHeight;
    private float BOX_STEP;  
    private int BOX_VELOCITY_ITERATIONS;  
    private int BOX_POSITION_ITERATIONS;  
    private float WORLD_TO_BOX=0.01f;  
    private float BOX_WORLD_TO=100f; 
    
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
				
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  
        dRenderer.render(world, camera.combined);  
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

	}

	@Override
	public void resize(int width, int height) {
		//Debug purpose
		//Gdx.app.log("Resize", "got it!");
	}

	@Override
	public void show() {
		
		//Debug purpose
		//Gdx.app.log("Show", "got it!");
		
		readXml();
		
		camera = new OrthographicCamera();  
		camera.viewportWidth = viewportWidth;  
        camera.viewportHeight = viewportHeight;  
        camera.position.set(camera.viewportWidth * 0.5f, camera.viewportHeight* 0.5f, 0f);  
        camera.update();  
        
        
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0, 10));  
        Body groundBody = world.createBody(groundBodyDef);  
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox((camera.viewportWidth) * 2, 10.0f);  
        groundBody.createFixture(groundBox, 0.0f);  
        
        
        BodyDef bodyDef = new BodyDef();  
        bodyDef.type = BodyType.DynamicBody;  
        bodyDef.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2);  
        Body body = world.createBody(bodyDef);  
        CircleShape dynamicCircle = new CircleShape();  
        dynamicCircle.setRadius(5f);  
        FixtureDef fixtureDef = new FixtureDef();  
        fixtureDef.shape = dynamicCircle;  
        fixtureDef.density = 1.0f;  
        fixtureDef.friction = 0.0f;  
        fixtureDef.restitution = 1;  
        body.createFixture(fixtureDef);  
        dRenderer = new Box2DDebugRenderer(); 

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
