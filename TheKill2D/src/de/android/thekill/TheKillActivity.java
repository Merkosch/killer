package de.android.thekill;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.AccelerometerSensorOptions;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.MathUtils;

import android.text.BoringLayout;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class TheKillActivity extends BaseGameActivity{

	 // ===========================================================
    // Constants
    // ===========================================================
    
	// Die Gesamtgröße der Umgebung
    private static final int RESOLUTION_HEIGHT = 240;
    private int resolution_h;
    private int resolution_w;
//    private static final int RESOLUTION_WIDTH = 400;
//    private static final int RESOLUTION_HEIGHT = 480;
//    private static final int RESOLUTION_WIDTH = 800;

    // Der sichtbare Ausschnitt
    private static final int CAMERA_HEIGHT = 240;
    private int camera_h;
    private int camera_w;
//    private static final int CAMERA_WIDTH = 400;
//    private static final int CAMERA_HEIGHT = 480;
//    private static final int CAMERA_WIDTH = 800;
//    private static final int CAMERA_HEIGHT = 192;
//    private static final int CAMERA_WIDTH = 320;
    
    private static final int LAYER_RACETRACK = 0;
    private static final int LAYER_BORDERS = LAYER_RACETRACK + 1;
    private static final int LAYER_CARS = LAYER_BORDERS + 1;
    private static final int LAYER_OBSTACLES = LAYER_CARS + 1;

    // ===========================================================
    // Fields
    // ===========================================================
    
    // Steuerkreuz
    private Texture mOnScreenControlTexture;
    private TextureRegion mOnScreenControlBaseTextureRegion;
    private TextureRegion mOnScreenControlKnobTextureRegion;
    
    // Feuerknopf
    private Texture mOnScreenFireTexture; 
    private TextureRegion mOnScreenFireTextureRegion; 
    private TextureRegion mOnScreenLeerTextureRegion; 
    
    private Camera 			mCamera;
    private PhysicsWorld 	mPhysicsWorld;
    
    private TheKillLevel 	level;
    private CharacterLoader characterLoader;

    @Override
    public Engine onLoadEngine() {
    	
    	Log.d("***************Startup*****************", 
      		  "Starte The Kill 2D");
    	
    	// Die Auflösung muss abhängig vom Gerät bestimmt werden. 
    	// Dabei ist auch das Seitenverhältnis zu beachten. Der Plan ist folgender:  
    	// Die Auflösung ist fix 240*X wobei X aus dem Seitenverhältnis des Geräts
    	// bestimmt wird. Ist dieAuflösung des Geräts kleiner als 240*X, wird einfach 
    	// die Auflösung des Geräts genommen. 
    	int dh = this.getWindowManager().getDefaultDisplay().getHeight();
    	int dw = this.getWindowManager().getDefaultDisplay().getWidth();
    	
    	// Die Auflösung ist anhängig von der aktuellen Lage des Geräts.
    	float ratio_faktor = 0;
    	if (dh > dw)
    		ratio_faktor = (float)dh / (float)dw;
    	else
    		ratio_faktor = (float)dw / (float)dh;
    	
    	Log.d("TheKill2D", "Auflösung des Geräts: " + dh + " x " + dw);
    	Log.d("TheKill2D", "Ratio: " + ratio_faktor);

    	// Auflösung wird entsprechend der Ratio des Geräts eingepasst
    	this.camera_h 		= (int)((float)CAMERA_HEIGHT * ratio_faktor);
    	this.camera_w  		= CAMERA_HEIGHT; 
    	this.resolution_h	= (int)((float)CAMERA_HEIGHT * ratio_faktor);
    	this.resolution_w  	= CAMERA_HEIGHT; 

    	// Falls die Auflösung kleiner ist als 240*X wird die Auflösung des Geräts genutzt
    	if (dh < CAMERA_HEIGHT){
    		camera_h 		= dh;
    		camera_w  		= dw;
    		resolution_h 	= dh;
    		resolution_w  	= dw;
    	}
    	
    	Log.d("TheKill2D", "Auflösung: " + camera_h + " x " + camera_w);
    	 
    	this.mCamera = new Camera(0, 0, camera_h, camera_w);
        this.level = new LevelFirstStrike();
        this.characterLoader = new CharacterLoader(this.mCamera);
        
        final Engine engine = new Engine(new EngineOptions(true, 
         									ScreenOrientation.LANDSCAPE, 
         									new RatioResolutionPolicy(	resolution_h, 
         																resolution_w ), 
           									this.mCamera));
        
        try {
        	if (MultiTouch.isSupported(this))
        		engine.setTouchController(new MultiTouchController());
		} catch (MultiTouchException e) {
			Log.d("TheKill2D", e.getMessage());
		}
		
        return engine;
    }
    
    @Override
    public void onLoadResources() {
    	
    	TextureRegionFactory.setAssetBasePath("gfx/");
    	
    	// Steuerungstexturen laden
    	this.mOnScreenControlTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mOnScreenControlBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
        this.mOnScreenControlKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
    	this.mEngine.getTextureManager().loadTexture(mOnScreenControlTexture);
        
    	this.mOnScreenFireTexture = new Texture(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    	this.mOnScreenFireTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenFireTexture, this, "feuerknopf.png", 0, 0);
    	this.mOnScreenLeerTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenFireTexture, this, "leer.png", 128, 0);
    	this.mEngine.getTextureManager().loadTexture(mOnScreenFireTexture);
    	
    	
    	// Umgebungstexturen Laden
    	List<Texture> textureList = this.level.loadRessources(this);
    	for (Texture texture : textureList) {
    		this.mEngine.getTextureManager().loadTexture(texture);			
		}
    	
    	// Charaktertexturen Laden
    	List<Texture> textureList2 = this.characterLoader.loadRessources(this);
    	for (Texture texture : textureList2) {
    		this.mEngine.getTextureManager().loadTexture(texture);			
		}
    }

    @Override
    public Scene onLoadScene() {
            this.mEngine.registerUpdateHandler(new FPSLogger());

            final Scene scene = new Scene(4);
            scene.setBackground(new ColorBackground(0, 0, 0));

            this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
            scene.registerUpdateHandler(this.mPhysicsWorld);

            this.level.initScene(scene, mPhysicsWorld);
            this.characterLoader.initCharacter(2, mPhysicsWorld, scene);
            this.initOnScreenControls(scene);
            
            // Die Kamera folgt dem Character
            this.characterLoader.getCharacter().registerUpdateHandler(mCamera);
            mCamera.onUpdate(0.1f);
            mCamera.setChaseEntity(this.characterLoader.getCharacter());
            
            mEngine.registerUpdateHandler(new IUpdateHandler() {
				
				@Override
				public void reset() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onUpdate(float pSecondsElapsed) {
					for (int i = 0 ; i < scene.getChildCount() ; i++) {
						if (scene.getChild(i) instanceof Bullet){
							Bullet bullet = (Bullet)scene.getChild(i);
							IEntity borders = scene.getChild(LAYER_BORDERS);
							for (int j = 0 ; j < borders.getChildCount(); j++){
								if (bullet.collidesWith((IShape) borders.getChild(j))){
									scene.detachChild(bullet);
								}
							}
						}
					}
				}
			});
            
            
            return scene;
    }

    @Override
    public void onLoadComplete() {

    }
    
    private void initOnScreenControls(final Scene pScene) {
    	
    	 	// Steuerkreuz
            final AnalogOnScreenControl analogOnScreenControl = 
            	    new AnalogOnScreenControl(	0, 
            	    							CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), 
            	    							this.mCamera, 
            	    							this.mOnScreenControlBaseTextureRegion, 
            	    							this.mOnScreenControlKnobTextureRegion, 
            	    							0.1f, 200, 
            	    							new IAnalogOnScreenControlListener() {
	                    @Override
	                    public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {                        
	                         
	    					final Body carBody = characterLoader.getmCharacterBody();
	    					    					
	    					// Steuerung des Charakters Vor und Zurück laufen 
	    					final Vector2 velocity = Vector2Pool.obtain(pValueX * 5, pValueY * 5);
	    					carBody.setLinearVelocity(velocity);	    						
	    					Vector2Pool.recycle(velocity);
	    					
	    					// Steuerung des Charakters Links Rechts drehen 
	    					final float rotationInRad = (float)Math.atan2(-pValueX, pValueY);
	    					if (rotationInRad != 0.0f) {
	    						carBody.setTransform(carBody.getWorldCenter(), rotationInRad);          
//	    						Log.d("TheKill2D", " - " + rotationInRad);	    
	    						characterLoader.setControlValues(pValueX,pValueY);
	    						characterLoader.getCharacter().setRotation(MathUtils.radToDeg(rotationInRad));
	    					}
	
	    					// Animiere das Sprite
	    					if (velocity.x + velocity.y != 0.0f && !(characterLoader.getCharacter().isAnimationRunning()))
	    						characterLoader.getCharacter().animate( 100 );
	    					if (velocity.x + velocity.y == 0.0f) 
	    						characterLoader.getCharacter().stopAnimation();
	                    }
	
	                    @Override
	                    public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
	                            // Mache Nix! 
	                    		// Log.d("TheKill2D", "ControlClick...");  
	                            // level.addSprite(pScene, (int)characterLoader.getCharacter().getX()+20, (int)characterLoader.getCharacter().getY()+20);
	                    }                  
            	    							});                  
                                   
            analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            analogOnScreenControl.getControlBase().setAlpha(0.5f);
            analogOnScreenControl.refreshControlKnobPosition();           
            pScene.setChildScene(analogOnScreenControl);
            
            // Feuerknopf
//            FireControllListener fireControlListener = new FireControllListener();
            final AnalogOnScreenControl fireControl = 
            		new AnalogOnScreenControl(	camera_h-128,
					            				0,
					            				this.mCamera, 
					            				this.mOnScreenFireTextureRegion, 
					            				this.mOnScreenLeerTextureRegion, 
					            				0.1f, 200, 
					            				new IAnalogOnScreenControlListener(){

		    			private float lastX;
		    			private float lastY;
		    			
		    			@Override
		    			public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
		    				
		    				// Nur Feuern, wenn der Controller betätigt ist
		    				if ((pValueX + pValueY) > 0){			
		    					// Durch die leider nur halbe Multitouchvariante kann es passieren,dass 
		    					// durch die Steuerung der Feuerknopf immer noch auf Feuern steht
		    					// Wenn die Koord. gleich bleiben ist das der Fall. Darum dann hier nicht feuern.
		    					if ((pValueX + pValueY) != (lastX + lastY)){
//		    						Log.d("TheKill2D", "+++++FIRE+++++" + pValueX + " - " + pValueY);			
//		    						level.addSprite(pScene, (int)characterLoader.getCharacter().getX()+20, (int)characterLoader.getCharacter().getY()+20);								            				
		    						characterLoader.fireBullet(pScene);
		    						lastX = pValueX; 
		    						lastY = pValueY;				
		    					}			
		    				}
		    			}
		
		    			@Override
		    			public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
		    				// TODO Auto-generated method stub
//		    				Log.d("TheKill2D", "+++++Klick+++++");
//		    				level.addSprite(pScene, (int)characterLoader.getCharacter().getX()+20, (int)characterLoader.getCharacter().getY()+20);								            				
		    			}            			
            									});
            fireControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            fireControl.getControlBase().setAlpha(0.5f);
            fireControl.refreshControlKnobPosition();           
            analogOnScreenControl.setChildScene(fireControl);           
            
    }

}