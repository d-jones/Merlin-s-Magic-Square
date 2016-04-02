package cs301.jonesd14_hw2;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.AlertDialog;
import android.media.MediaPlayer;

/**
 * @author Daniel Jones
 * @version 9/17/13
 * 
 * The main class which is currently responsible for everything outside of the
 * SurfaceView (the grid of squares).
 */
public class MainActivity extends Activity implements OnClickListener{

	//Instance variables (see methods for implementations)
	private Button resetButton;
	private Button soundButton;
	private Button grid3x3Button;
	private Button grid5x5Button;
	private static boolean isMuted = false;
	private boolean confirmChange = false;
	protected static boolean justWon = false;
	protected static boolean wait = false;
	private int orientation;
	private int seekTime;
	private MediaPlayer player;
	protected static MediaPlayer touchPlayer1;
	protected static MediaPlayer touchPlayer2;
	private Display display;
	private SeekBar seekBarRed;
	private SeekBar seekBarGreen;
	private SeekBar seekBarBlue;
	private SeekBarHandler seekHandler1;
	private SeekBarHandler seekHandler2;
	private SeekBarHandler seekHandler3;
	private GridSurfaceView gridSurfaceView;
	private static TextView numPressesView;
	private static String displayText;
	protected static int progressRed = 255;
	protected static int progressGreen = 255;
	protected static int progressBlue = 255;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//used to get the rotation of the device
		display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		orientation = display.getRotation();

		//create our media players
		player = MediaPlayer.create(this, R.raw.sast_wow);
		player.setVolume(0.3f, 0.3f);
		touchPlayer1 = MediaPlayer.create(this, R.raw.woosh);
		touchPlayer1.setVolume(1.0f, 1.0f);


		//the device is in landscape mode, so load the layout
		if(orientation == Surface.ROTATION_0){
			loadLandscape(savedInstanceState);
		}
		//the device is in portrait mode, so load the layout
		else{
			loadPortrait(savedInstanceState);
		}
	}


	/**
	 * explain5x5()
	 * 
	 * explains how to play the game with a 5x5 grid.
	 * 
	 * @return void
	 */
	public void explain5x5(){
		AlertDialog.Builder explainBuilder = new AlertDialog.Builder(this);
		explainBuilder.setTitle("5x5 Rules");
		explainBuilder.setMessage("1: The corner pieces remain the same as with the 3x3 grid.\n\n" +
				"2: All non-corner edge pieces now " + "change ALL horizontal and vertical neighbors, " +
				"in addition to itself.\n\n3: All inner (non-edge) pieces change ALL horizontal and vertical neighbors, " +
				"including itself.\n\nHave fun! And don't give up too quickly ;)");

		explainBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int num){

			}
		});		
		AlertDialog explain = explainBuilder.create();
		explain.show();
	}



	/**
	 * loadLandscape()
	 * 
	 * Loads the landscape layout and initializes all buttons and view appropriately.
	 * 
	 * @return void
	 */
	public void loadLandscape(Bundle savedInstanceState){
		//load the layout/initialize buttons and music and such
		setContentView(R.layout.activity_main);

		gridSurfaceView = (GridSurfaceView)findViewById(R.id.gridSurfaceView);

		//seekBar handlers:
		seekHandler1 = new SeekBarHandler(1,gridSurfaceView);
		seekHandler2 = new SeekBarHandler(2,gridSurfaceView);
		seekHandler3 = new SeekBarHandler(3,gridSurfaceView);
		//The "number of presses" view/its text:
		numPressesView = (TextView)findViewById(R.id.buttonPressView);
		displayText = numPressesView.getText() + " ";
		//buttons:
		resetButton = (Button)findViewById(R.id.resetButton);
		soundButton = (Button)findViewById(R.id.soundButton);
		grid3x3Button = (Button)findViewById(R.id.grid3x3Button);
		grid5x5Button = (Button)findViewById(R.id.grid5x5Button);
		//seekBars: 
		seekBarRed = (SeekBar)findViewById(R.id.redBar);
		seekBarGreen = (SeekBar)findViewById(R.id.greenBar);
		seekBarBlue = (SeekBar)findViewById(R.id.blueBar);
		//if the music has not been muted, start it at its last
		//position,
		if(savedInstanceState != null){
			wait = true;
			progressRed = savedInstanceState.getInt("prog1");
			progressGreen = savedInstanceState.getInt("prog2");
			progressBlue = savedInstanceState.getInt("prog3");
			GridSurfaceView.gridDimensions = savedInstanceState.getInt("dimensions");
			displayText = savedInstanceState.getString("display");
			numPressesView.setText(displayText);
			if(!isMuted){
				seekTime = savedInstanceState.getInt("seekTime");
			}
		}
		//if the game hasn't loaded before, set the text to what it currently is
		else{
			displayText = numPressesView.getText() + " ";
		}


		seekBarRed.setProgress(progressRed);
		seekBarGreen.setProgress(progressGreen);
		seekBarBlue.setProgress(progressBlue);


		//if the music isn't muted, start/reload it and make
		//sure that the "ON" sound icon is loaded
		if(!isMuted){
			soundButton.setBackgroundResource(R.drawable.sound);
			player.seekTo(seekTime);
			player.start();

		}
		//make sure that the mute icon is loaded
		else if(isMuted){
			soundButton.setBackgroundResource(R.drawable.mute);
		}


		//add our buttonseekBar listeners
		resetButton.setOnClickListener(this);
		soundButton.setOnClickListener(this);
		grid3x3Button.setOnClickListener(this);
		grid5x5Button.setOnClickListener(this);
		seekBarRed.setOnSeekBarChangeListener(seekHandler1);
		seekBarGreen.setOnSeekBarChangeListener(seekHandler2);
		seekBarBlue.setOnSeekBarChangeListener(seekHandler3);
	}


	/**
	 * loadPortrait
	 * 
	 * Loads the portrait layout and initializes all buttons/vies appropriately
	 * 
	 * @return void
	 */
	public void loadPortrait(Bundle savedInstanceState){
		//load the layout/initialize buttons and music and such
		setContentView(R.layout.portrait_layout);
		gridSurfaceView = (GridSurfaceView)findViewById(R.id.gridSurfaceViewPort);

		seekHandler1 = new SeekBarHandler(1,gridSurfaceView);
		seekHandler2 = new SeekBarHandler(2,gridSurfaceView);
		seekHandler3 = new SeekBarHandler(3,gridSurfaceView);
		//load the layout/initialize buttons and music and such
		numPressesView = (TextView)findViewById(R.id.buttonPressViewPort);
		resetButton = (Button)findViewById(R.id.resetButtonPort);
		soundButton = (Button)findViewById(R.id.soundButtonPort);
		grid3x3Button = (Button)findViewById(R.id.grid3x3ButtonPort);
		grid5x5Button = (Button)findViewById(R.id.grid5x5ButtonPort);
		seekBarRed = (SeekBar)findViewById(R.id.redBarPort);
		seekBarGreen = (SeekBar)findViewById(R.id.greenBarPort);
		seekBarBlue = (SeekBar)findViewById(R.id.blueBarPort);

		//if the device was rotated and the sound has not been
		//previously muted, get the track's last position
		if(savedInstanceState != null){
			if(!isMuted){
				seekTime = savedInstanceState.getInt("seekTime");
			}
			wait = true;
			//get the progress of the seekbars
			progressRed = savedInstanceState.getInt("prog1");
			progressGreen = savedInstanceState.getInt("prog2");
			progressBlue = savedInstanceState.getInt("prog3");
			GridSurfaceView.gridDimensions = savedInstanceState.getInt("dimensions");
			displayText = savedInstanceState.getString("display");
			numPressesView.setText(displayText);
		}
		//if this if the game's first time around, initialize the text
		//to simply what it is
		else{
			displayText = numPressesView.getText() + " ";
		}

		seekBarRed.setProgress(progressRed);
		seekBarGreen.setProgress(progressGreen);
		seekBarBlue.setProgress(progressBlue);



		//if the music isn't muted or playing, start/reload it
		//and make sure that the "ON" sound icon is loaded
		if(!isMuted){
			soundButton.setBackgroundResource(R.drawable.sound);
			player.seekTo(seekTime);
			player.start();
		}
		//make sure that the mute icon is loaded
		else if(isMuted){
			soundButton.setBackgroundResource(R.drawable.mute);
		}


		//add our button/seekBar listeners
		resetButton.setOnClickListener(this);
		soundButton.setOnClickListener(this);
		grid3x3Button.setOnClickListener(this);
		grid5x5Button.setOnClickListener(this);
		seekBarRed.setOnSeekBarChangeListener(seekHandler1);
		seekBarGreen.setOnSeekBarChangeListener(seekHandler2);
		seekBarBlue.setOnSeekBarChangeListener(seekHandler3);
	}


	/**
	 * alert5x5()
	 * 
	 * creates an alert dialog to confirm that the user wants to switch from 3x3 to 5x5.
	 * 
	 * @return boolean
	 */
	public boolean alert5x5(){
		//create an alert dialog builder
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Change Grid");
		alertBuilder.setMessage("Are you sure you want to change the grid?\nAll current progress will be deleted.");

		//if YES, set the grid dimensions to 5x5 and recreate() the app
		alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				GridSurfaceView.gridDimensions = 5;
				wait = false;
				confirmChange = true;
				recreate();
			}
		});
		//if NO, just go cancel
		alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				confirmChange = false;
				dialog.cancel();
			}
		});

		//create the dialog and show it
		AlertDialog alert = alertBuilder.create();
		alert.show();
		return confirmChange;
	}


	/**
	 * alert5x5()
	 * 
	 * creates an alert dialog to confirm that the user wants to switch from 3x3 to 5x5.
	 * 
	 * @return boolean
	 */
	public boolean alert3x3(){

		//create an alert dialog builder
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Change Grid");
		alertBuilder.setMessage("Are you sure you want to change the grid?\nAll current progress will be deleted.");

		//if YES, set the grid dimensions to 5x5 and recreate() the app
		alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				GridSurfaceView.gridDimensions = 3;
				wait = false;
				confirmChange = true;
				recreate();
			}
		});
		//if NO, just go cancel
		alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				confirmChange = false;
				dialog.cancel();
			}
		});

		//create the dialog and show it
		AlertDialog alert = alertBuilder.create();
		alert.show();
		return confirmChange;
	}


	/**
	 * alertReset()
	 * 
	 * Alerts and ask the user if they want to restart the game
	 */
	public boolean alertReset(){
		//create an alert dialog builder
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Reset Game");
		alertBuilder.setMessage("Are you sure you want to reset the game?\nAll current progress will be deleted.");

		//if YES, reset the game
		alertBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				wait = false;
				confirmChange = true;
				recreate();
			}
		});
		//if NO, just go cancel
		alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				confirmChange = false;
				dialog.cancel();
			}
		});

		//create the dialog and show it
		AlertDialog alert = alertBuilder.create();
		alert.show();
		return confirmChange;
	}


	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		player.pause();
		//if we have NOT just won, this method is being called as the result
		//of a device rotation
		if(!justWon){
			seekTime = player.getCurrentPosition();
			progressRed = seekBarRed.getProgress();
			progressGreen = seekBarGreen.getProgress();
			progressBlue = seekBarBlue.getProgress();
		}
		//if we just WON, this method is being called as the result of
		//the user wanting to restart the game
		else{
			seekTime = 0;
			//reset, so that this doesn't execute before we win again
			justWon = false;
		}
		savedInstanceState.putInt("seekTime", seekTime);
		savedInstanceState.putInt("prog1", progressRed);
		savedInstanceState.putInt("prog2", progressGreen);
		savedInstanceState.putInt("prog3", progressBlue);
		savedInstanceState.putString("display", displayText);
		savedInstanceState.putInt("dimensions", GridSurfaceView.gridDimensions);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	/**
	 * onClick
	 * 
	 * The overridden Android method which responds to "clicking" certain buttons.
	 */
	public void onClick(View v) {
		//sound button clicked
		if(v == soundButton){
			//if the sound is NOT muted, mute it and change to
			//the mute icon and save the music's current position
			if(player.isPlaying()){
				soundButton.setBackgroundResource(R.drawable.mute);
				seekTime = player.getCurrentPosition();
				player.pause();
				touchPlayer1.pause();
				isMuted = true;
			}
			//unmute the audio and change the icon back
			else{
				soundButton.setBackgroundResource(R.drawable.sound);
				player.seekTo(seekTime);
				player.start();
				touchPlayer1.seekTo(0);
				touchPlayer1.start();
				isMuted = false;
			}
		}
		else if(v == grid3x3Button){
			//if the dimensions are already 3x3, do nothing
			if(GridSurfaceView.gridDimensions == 3){
				return;
			}

			alert3x3();
		}
		else if(v == grid5x5Button){
			//if the dimensions are already 5x5, do nothing
			if(GridSurfaceView.gridDimensions == 5){
				return;
			}
			//if the current dimensions are 3x3 and the user hit the 5x5 button...
			alert5x5();
		}
		else if(v == resetButton){
			//make sure the user wants to reset the game
			if(alertReset()){
				recreate();
			}
		}
	}    


	/**
	 * changePressCountDisplay()
	 * 
	 * Displays to the user how many times they have pressed a square in the
	 * square grid (ie. how many presses they have currently taken to solve the puzzle)
	 * 
	 * @param count - The current total number of presses
	 */
	public void changePressCountDisplay(int count){
		numPressesView.setText(displayText + count);
	}

	/**
	 * winner()
	 * 
	 * called by GridSurfaceView when we have a winner! (Also ends the game)
	 * 
	 * @return void
	 */
	public void winner(){
		wait = false;
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Success!");
		alertBuilder.setMessage("WINNER!\n\nWould you like to play again? (Try 5x5 for added difficulty)");

		alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int num){
				//we just won and want to change the squares
				justWon = true;
				recreate();
			}
		});

		alertBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int num){
				//kill the process
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		//create the alert and show it
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}
}
