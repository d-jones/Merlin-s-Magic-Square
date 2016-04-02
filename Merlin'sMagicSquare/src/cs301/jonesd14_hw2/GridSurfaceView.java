package cs301.jonesd14_hw2;

import java.util.Random;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.media.MediaPlayer;

/**
 * 
 * @author Daniel Jones
 * @version 9/17/13
 * 
 * This class is responsible for initializing and manipulation the SurfaceView (the grid of squares).
 */
public class GridSurfaceView extends SurfaceView implements OnTouchListener{

	//instance variables
	protected Rect[] squares;
	protected static Paint[] paints;
	protected Paint onColor;
	private Paint offColor;
	//our RGB values, currently the default is WHITE
	protected int redValue ;
	protected int greenValue;
	protected int blueValue;
	private Random randGen;
	private int xCoord;
	private int yCoord;
	//depending on what the user chooses, either 
	//3x3 or 5x5 (3x3 by default)
	protected static int gridDimensions;
	protected boolean justChanged = false;
	private Context context;
	protected int pressesCount;
	MainActivity activity;


	/**
	 * GridSurfaceView
	 * 
	 * Constructor for the GridSurfaceView class. 
	 * 
	 * @param context
	 *      a reference to the activity this animation is run under
	 */
	public GridSurfaceView(Context context) {
		super(context);
		init(); // perform common initialization
	}// ctor

	/**
	 * GridSurfaceView
	 * 
	 * Another constructor for the GridSurfaceView class.
	 * @param context
	 * 		a reference to the activity this animation is run under.
	 * 
	 * @param attrs
	 * 		the set of attributes associated with this view.
	 */
	public GridSurfaceView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;
		init();
	}//ctor



	/**
	 * init()
	 * 
	 * initializes the grid of squares to a random setting
	 */
	public void init(){
		//set the touch listener for the SurfaceView
		setOnTouchListener(this);
		setWillNotDraw(false);
		activity = new MainActivity();
		
		onColor = new Paint();
		offColor = new Paint();
		redValue = MainActivity.progressRed;
		greenValue = MainActivity.progressGreen;
		blueValue = MainActivity.progressBlue;
		onColor.setColor(Color.rgb(redValue,greenValue,blueValue));
		offColor.setColor(Color.rgb(0,0,0));

		if(gridDimensions == 0){
			gridDimensions = 3;
		}
		//initialize the squares[]
		initSquares(gridDimensions);
	}


	/**
	 * initSquares()
	 * 
	 * Initializes the array of rectangle to be drawn.
	 * 
	 * @param dimensions
	 * 			The dimensions of the grid (3x3, or 5x5).
	 * 
	 * @return void
	 */
	public void initSquares(int dimensions){
		xCoord = 0;
		yCoord = 0;
		//a random generator. This will be used to randomly
		//determine the color of a square
		randGen = new Random();
		

		//initialize the squares[] only if they haven't been already,
		//and we haven't been told to wait
		if(squares == null || !MainActivity.wait){
			//initialize the 3x3 grid
			if(gridDimensions == 3){
				squares = new Rect[9];
				paints = new Paint[9];
				for(int i = 0; i < 9; ++i){
					int randNum = randGen.nextInt(100);
					//if the random number is divisible by 2 with no remainder
					//and we're not looking at the middle square, set the square
					//to black. This is to be random and make sure that we can't start
					//in a winning position
					if((randNum %2 == 0) && (i != 4)){
						paints[i] = offColor;
					}
					else{
						paints[i] = onColor;
					}
					//we've reached the end of our row, so restart x
					//and change the y-coordinate for the next row
					if(xCoord >= 500){
						xCoord = 0;
						yCoord += 170;
					}
					squares[i] = new Rect(xCoord, yCoord, xCoord + 160, yCoord + 160);
					xCoord += 170;
				}
			}
			//initialize the 5x5 grid
			else{
				squares = new Rect[25];
				paints = new Paint[25];
				for(int i = 0; i < 25; ++i){
					int randNum = randGen.nextInt(100);
					if((randNum % 2 == 0) && (i != 12)){
						paints[i] = offColor;
					}
					else{
						paints[i] = onColor;
					}
					//if we've reached the end of a row, reset the 
					//x-coordinate
					if(xCoord >= 500){
						xCoord = 0;
						yCoord += 102;
					}
					squares[i] = new Rect(xCoord, yCoord, xCoord + 92, yCoord + 92);
					xCoord += 102;
				}
			}
			MainActivity.wait = true;
		}
		invalidate();
	}



	@Override
	/**
	 * onDraw()
	 * 
	 * The overridden Android method, which is called whenever something 
	 * needs to be drawn on the GridSurfaceView.
	 */
	public void onDraw(Canvas c){
		int paintIdx = 0;
		/*for now, the color of each square is determined by an
		  arbitrary random int. Except for the middle square, 
		  which is always on to start.*/
		for(Rect square : squares){
			c.drawRect(square, paints[paintIdx]);
			++paintIdx;
		}

		//user wins!!
		if(hasWon()){
			((MainActivity) context).winner();
		}
	}


	/**
	 * switchColor
	 * 
	 * Switches the color of an individual square
	 * 
	 * @param idx
	 * 			The index of the square to have its color switched.
	 * 
	 * @return void
	 */
	public void switchIdvColor(int idx){
		if(paints[idx] == onColor){
			paints[idx] = offColor;
		}
		else{
			paints[idx] = onColor;
		}
	}


	/**
	 * switchAllColors
	 * 
	 * Switches the square that was touched, along with its proper neighbors.
	 * 
	 * @param idx
	 * 			The index of the square to have its color switched.
	 * 
	 * @return void
	 */
	public void switchAllColors(int idx){
		if(gridDimensions == 3){
			switch(idx){
			case 0://square 0,0 was touched
				switchIdvColor(0);
				switchIdvColor(1);
				switchIdvColor(3);
				switchIdvColor(4);
				break;
			case 1: //square 1,0 was touched
				switchIdvColor(0);
				switchIdvColor(1);
				switchIdvColor(2);
				break;
			case 2: //square 2,0 was touched
				switchIdvColor(1);
				switchIdvColor(2);
				switchIdvColor(4);
				switchIdvColor(5);
				break;
			case 3: //square 0,1 was touched
				switchIdvColor(0);
				switchIdvColor(3);
				switchIdvColor(6);
				break;
			case 4: //square 1,1 was touched
				switchIdvColor(1);
				switchIdvColor(3);
				switchIdvColor(4);
				switchIdvColor(5);
				switchIdvColor(7);
				break;
			case 5: //square 2,1 was touched
				switchIdvColor(2);
				switchIdvColor(5);
				switchIdvColor(8);
				break;
			case 6: //square 0,2 was touched
				switchIdvColor(3);
				switchIdvColor(4);
				switchIdvColor(6);
				switchIdvColor(7);
				break;
			case 7: //square 1,2 was touched
				switchIdvColor(6);
				switchIdvColor(7);
				switchIdvColor(8);
				break;
			case 8: //square 2,2 was touched
				switchIdvColor(4);
				switchIdvColor(5);
				switchIdvColor(7);
				switchIdvColor(8);
			default: //shouldn't happen
				break;
			}
		}
		//we've got a 5x5 grid. Get ready for a LONG ASS switch statement...
		else{
			switch(idx){
			case 0: //square 0,0 was touched
				switchIdvColor(0);
				switchIdvColor(1);
				switchIdvColor(5);
				switchIdvColor(6);
				break;
			case 1: //square 1,0 was touched
				switchIdvColor(0);
				switchIdvColor(1);
				switchIdvColor(2);
				switchIdvColor(6);
				break;
			case 2: //square 2,0 was touched
				switchIdvColor(1);
				switchIdvColor(2);
				switchIdvColor(3);
				switchIdvColor(7);
				break;
			case 3: //square 3,0 was touched
				switchIdvColor(2);
				switchIdvColor(3);
				switchIdvColor(4);
				switchIdvColor(8);
				break;
			case 4: //square 4,0 was touched
				switchIdvColor(3);
				switchIdvColor(4);
				switchIdvColor(8);
				switchIdvColor(9);
				break;
			case 5: //square 0,1 was touched
				switchIdvColor(0);
				switchIdvColor(5);
				switchIdvColor(6);
				switchIdvColor(10);
				break;
			case 6: //square 1,1 was touched
				switchIdvColor(1);
				switchIdvColor(5);
				switchIdvColor(6);
				switchIdvColor(7);
				switchIdvColor(11);
				break;
			case 7: //square 2,1 was touched
				switchIdvColor(2);
				switchIdvColor(6);
				switchIdvColor(7);
				switchIdvColor(8);
				switchIdvColor(12);
				break;
			case 8: //square 3,1 was touched
				switchIdvColor(3);
				switchIdvColor(7);
				switchIdvColor(8);
				switchIdvColor(9);
				switchIdvColor(13);
				break;
			case 9: //square 4,1 was touched
				switchIdvColor(4);
				switchIdvColor(8);
				switchIdvColor(9);
				switchIdvColor(14);
				break;
			case 10: //square 0,2 was touched
				switchIdvColor(05);
				switchIdvColor(10);
				switchIdvColor(11);
				switchIdvColor(15);
				break;
			case 11: //square 1,2 was touched
				switchIdvColor(6);
				switchIdvColor(10);
				switchIdvColor(11);
				switchIdvColor(12);
				switchIdvColor(16);
				break;
			case 12: //square 2,2 was touched
				switchIdvColor(7);
				switchIdvColor(11);
				switchIdvColor(12);
				switchIdvColor(13);
				switchIdvColor(17);
				break;
			case 13: //square 3,2 was touched
				switchIdvColor(8);
				switchIdvColor(12);
				switchIdvColor(13);
				switchIdvColor(14);
				switchIdvColor(18);
				break;
			case 14: //square 4,2 was touched
				switchIdvColor(9);
				switchIdvColor(13);
				switchIdvColor(14);
				switchIdvColor(19);
				break;
			case 15: //square 0,3 was touched
				switchIdvColor(10);
				switchIdvColor(15);
				switchIdvColor(16);
				switchIdvColor(20);
				break;
			case 16: //square 1,3 was touched
				switchIdvColor(11);
				switchIdvColor(15);
				switchIdvColor(16);
				switchIdvColor(17);
				switchIdvColor(21);
				break;
			case 17: //square 2,3 was touched
				switchIdvColor(12);
				switchIdvColor(16);
				switchIdvColor(17);
				switchIdvColor(18);
				switchIdvColor(22);
				break;
			case 18: //square 3,3 was touched
				switchIdvColor(13);
				switchIdvColor(17);
				switchIdvColor(18);
				switchIdvColor(19);
				switchIdvColor(23);
				break;
			case 19: //square 4,3 was touched
				switchIdvColor(14);
				switchIdvColor(18);
				switchIdvColor(19);
				switchIdvColor(24);
				break;
			case 20: //square 0,4 was touched
				switchIdvColor(15);
				switchIdvColor(16);
				switchIdvColor(20);
				switchIdvColor(21);
				break;
			case 21: //square 1,4 was touched
				switchIdvColor(16);
				switchIdvColor(20);
				switchIdvColor(21);
				switchIdvColor(22);
				break;
			case 22: //square 2,4 was touched
				switchIdvColor(17);
				switchIdvColor(21);
				switchIdvColor(22);
				switchIdvColor(23);
				break;
			case 23: //square 3,4 was touched
				switchIdvColor(18);
				switchIdvColor(22);
				switchIdvColor(23);
				switchIdvColor(24);
				break;
			case 24: //square 4,4 was touched
				switchIdvColor(18);
				switchIdvColor(19);
				switchIdvColor(23);
				switchIdvColor(24);
				break;
			default: //shouldn't happen
				break;
			}
		}
	}


	/**
	 * hasWon()
	 * 
	 * Determines if the user has won the game.
	 * 
	 * @return boolean
	 */
	public boolean hasWon(){
		int idx = 0;
		boolean winOrLose = false;
		//the number of squares that are "correct"
		//for a winning scenario
		int correctVal = 0;
		for(Rect square : squares){
			if(gridDimensions == 3){
				//we're playing a 3x3 layout
				if((paints[idx] == onColor) && (idx != 4)){
					++correctVal;
				}
				else if((idx == 4) && (paints[idx] == offColor)){
					++correctVal;
				}
				++idx;
			}
			//we're playing a 5x5 layout
			else{
				//if a square is ON and not the middle square...
				if((paints[idx] == onColor) && (idx !=12)){
					++correctVal;
				}
				//if the middle square is OFF...
				else if((idx == 14) && (paints[idx] == offColor)){
					++correctVal;
				}
				++idx;
			}
		}

		//check correctVal
		if((gridDimensions == 3) && (correctVal == 9)){
			winOrLose = true; //winner, winner, chicken dinner!!
		}
		else if((gridDimensions == 5) && (correctVal == 25)){
			winOrLose = true;
		}
		return winOrLose;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int idx  = 0;

		MainActivity.touchPlayer1.start();

		//find the square that was touched
		for(Rect square : squares){
			//if we've found the square that was touched,
			//change its color and its proper neighbors colors
			if(square.contains((int)x,(int)y)){
				switchAllColors(idx);
				break;
			}
			//we haven't found our square yet
			++idx;
		}
		++pressesCount;
		activity.changePressCountDisplay(pressesCount);

		invalidate();
		return false;
	}
}
