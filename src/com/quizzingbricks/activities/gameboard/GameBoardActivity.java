package com.quizzingbricks.activities.gameboard;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.quizzingbricks.R;
import com.quizzingbricks.R.drawable;
import com.quizzingbricks.activities.answerQuestion.QuestionPromptActivity;
import com.quizzingbricks.authentication.AuthenticationManager;

import com.quizzingbricks.communication.apiObjects.GamesThreadedAPI;
import com.quizzingbricks.communication.apiObjects.OnTaskCompleteAsync;
import com.quizzingbricks.tools.AsyncTaskResult;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path.FillType;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class GameBoardActivity extends Activity implements OnTaskCompleteAsync{
	private int gameID;
	private ArrayList<Integer> gameboard;
	private OnTaskCompleteAsync ontaskcomplete;
	private int BOARD_SIZE = 8;
	private GamesThreadedAPI gameThreadedAPI;
	private int y_coord, x_coord;
	private int myID;
	private int NULL_VALUE = 1000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthenticationManager AM = new AuthenticationManager(this);
        try {
        	 myID = AM.getUserId();
		} catch (Exception e) {
			e.printStackTrace();
			myID =-1;
		}
        Intent intentBuffer = getIntent();
        gameID = intentBuffer.getExtras().getInt("id");
        ActionBar ab = getActionBar();
        ab.setTitle("Game " + gameID);
        ab.setDisplayHomeAsUpEnabled(true);
        ontaskcomplete = this;
        gameboard = new ArrayList<Integer>();
        gameThreadedAPI = new GamesThreadedAPI(this);
        gameThreadedAPI.getGameInfo(gameID, this);
	}   
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
    }
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu items for use in the action bar
	        getMenuInflater().inflate(R.menu.game_board, menu);
	        return super.onCreateOptionsMenu(menu);
	    }
	
	@Override
	public void onComplete(AsyncTaskResult<JSONObject> result) {
    	System.out.println("GOT ON COMPLETE MESSAGE");
    	ArrayList<Integer> gameborder = new ArrayList<Integer>();
    	ArrayList<Integer> playerlist = new ArrayList<Integer>();
    	ArrayList<String> playerEmailList = new ArrayList<String>();
    	try {
    		if(result.hasException())	{
				System.out.println("Oh noes...");
				result.getException().printStackTrace();
    		
			//THIS IS PLACE BRICK
    		} else if (result.getResult().has("result")) {
    			System.out.println("IN PLACE BRICK");
    			Intent i = new Intent(getApplicationContext(), QuestionPromptActivity.class);
		    	i.putExtra("gameID", gameID);
		    	startActivityForResult(i, 1);
		    	
		    //THIS IS GET GAMEBOARD
			} else if (result.getResult().has("players")){
				System.out.println("THIS IS GAMEBOARD");
    			JSONObject bigJson = result.getResult();
    			JSONArray playerArray = bigJson.getJSONArray("players");
    			System.out.println(playerArray);
    			
    			boolean quizzfirst = false;
    			for (int i = 0; i < playerArray.length(); i++) {
    				int playerid = playerArray.getJSONObject(i).getInt("userId");
    				int playerstate = playerArray.getJSONObject(i).getInt("state");
    				System.out.println(playerstate);
    				boolean quizzstate = playerid == myID && playerstate == 1;
    				if (quizzstate) {
						System.out.println("quizzfirst "+quizzfirst);
    					quizzfirst=true;
					} 
    			}
//    			Object gameid = bigJson.get("gameid");
    			
				JSONArray board = bigJson.getJSONArray("board");
//    			System.out.println(gameid);
    			System.out.println(board);
    			for (int i = 0; i < board.length(); i++) {
					gameborder.add(Integer.parseInt(board.get(i).toString()));
//					System.out.println(board.get(i).toString());
				}
    			int clearBrickXPos = NULL_VALUE;
    			int clearBrickYPos = NULL_VALUE;
//    			gameboards = (int[]) board.get(index);
    			for (int i = 0; i < playerArray.length(); i++) {
    				int playerid = playerArray.getJSONObject(i).getInt("userId");
    				int playerstate = playerArray.getJSONObject(i).getInt("state");
    				
    				if(playerid == myID && (playerstate == 1 || playerstate == 2 || playerstate == 3))	{
    					clearBrickXPos = playerArray.getJSONObject(i).getInt("x");
    					clearBrickYPos = playerArray.getJSONObject(i).getInt("y");
    				}
    				
					Object playeranswerbool = playerArray.getJSONObject(i).get("answeredCorrectly");
					playerlist.add(playerid);
					playerEmailList.add(playerArray.getJSONObject(i).getString("email"));
					System.out.println("player "+playerid+" answered"+playeranswerbool);
				}
    			if(clearBrickXPos == NULL_VALUE && clearBrickYPos == NULL_VALUE)	{
    				makeNewAndImprovedGameBoard(gameborder, playerlist, playerEmailList);
    			}
    			else	{
    				makeNewAndImprovedGameBoard(gameborder, playerlist, playerEmailList, clearBrickXPos, clearBrickYPos);
    			}
    			
    		//Quizz fight, start quizz first, (after printing gameboard) 
    			if (quizzfirst) {
    				Intent i = new Intent(getApplicationContext(), QuestionPromptActivity.class);
    		    	i.putExtra("gameID", gameID);
    		    	startActivityForResult(i, 1);
    			}
    		} else if (result.getResult().has("errors")) {
				System.out.println(result.getResult().getJSONArray("errors").get(0).toString());
				Toast.makeText(this, "Move not allowed", 2).show();
			}
    		
		} catch (Exception e) {
			e.printStackTrace();
			
			//IF ELSE FAIL JUST MAKE EMPTY GAMEBOARD
			LinearLayout asd = new LinearLayout(this);
			Button panicbutton = new Button(this);
			panicbutton.setText("PANIC BUTTON");
			panicbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {System.out.println("PANIC");}});
			asd.addView(panicbutton);
			setContentView(asd);
			System.out.println("Exception in onComplete, PANIC: make empty gameboard");
//			makeGameBoard();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		 if (requestCode == 1) {
		System.out.println(requestCode);
		System.out.println(resultCode);
		     if(resultCode == RESULT_OK){
//		    	 GamesThreadedAPI gamesThreadedAPI = new GamesThreadedAPI(this);
//		    	 gameThreadedAPI.getGameInfo(gameID, ontaskcomplete);
		     }
		     else if (resultCode == RESULT_CANCELED) {    
		         //Write your code if there's no result
		     }
//		 }
	}
	
	public void updateGameBoard(MenuItem menuItem)	{
		new GamesThreadedAPI(this).getGameInfo(gameID, this);
	}
	
	private void makeNewAndImprovedGameBoard(ArrayList<Integer> gameboards, ArrayList<Integer> playerlist, 
		ArrayList<String> playerEmailList, int... clearBrickPos){
		ScrollView a = new ScrollView(this);
        a.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        HorizontalScrollView b = new HorizontalScrollView(this);
        b.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        LinearLayout c = new LinearLayout(this);
        c.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        c.setOrientation(LinearLayout.VERTICAL);
        
        
        //Extra text for player
        LinearLayout gameInfo = new LinearLayout(this);
        gameInfo.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        gameInfo.setOrientation(LinearLayout.HORIZONTAL);
        TextView playerinfo = new TextView(this);
        if (playerlist.size() == 2) {
        	System.out.println("list == 2");
	        if (myID == playerlist.get(0)) {
	        	playerinfo.append("You are blue, ");
			} else {
				playerinfo.append(playerEmailList.get(0).toString()+" is blue, ");
			} 
	        if (myID == playerlist.get(1)) {
	        	playerinfo.append("You are green");
			} else {
				playerinfo.append(playerEmailList.get(1).toString()+" is green");
			} 
        } else {
        	System.out.println("list == 4");
        	if (myID == playerlist.get(0)) {
	        	playerinfo.append("You are blue, ");
			} else {
				playerinfo.append(playerEmailList.get(0).toString()+" is blue, ");
			} 
	        if (myID == playerlist.get(1)) {
	        	playerinfo.append("You are green, ");
			} else {
				playerinfo.append(playerEmailList.get(1).toString()+" is green, ");
			}
			if (myID == playerlist.get(2)) {
	        	playerinfo.append("You are yellow, ");
			} else {
				playerinfo.append(playerEmailList.get(2).toString()+" is yellow, ");
			} 
	        if (myID == playerlist.get(3)) {
	        	playerinfo.append("You are red, ");
			} else {
				playerinfo.append(playerEmailList.get(3).toString()+" is red");
			}
		}
        
        
        gameInfo.addView(playerinfo);
		c.addView(gameInfo);
        
        for (int i = 0; i < BOARD_SIZE; i++) {
        	LinearLayout d = new LinearLayout(this);
            d.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            d.setPadding(-5, 0, -5, 0);
            d.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < BOARD_SIZE; j++) {
            	ImageButton ib = new ImageButton(this);
//            	
            	
            	ib.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            	ib.setId((i*BOARD_SIZE)+j);
            	ib.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						GamesThreadedAPI GTAPI = new GamesThreadedAPI(getApplicationContext());
						
						int id = v.getId();
//						x_coord = id / BOARD_SIZE;
//						y_coord = id % BOARD_SIZE;
						//FIXME: The coordinates are now swapped  
						y_coord = id / BOARD_SIZE;
						x_coord = id % BOARD_SIZE;
						System.out.println(gameID);
						System.out.println(x_coord);
						System.out.println(y_coord);
						
						GTAPI.placeBricks(gameID, x_coord, y_coord, ontaskcomplete);
//						Intent i = new Intent(getApplicationContext(), QuestionPromptActivity.class);
////						System.out.println("loltest"+id);
//				    	i.putExtra("gameID", gameID);
////						startActivity(i);
//				    	startActivityForResult(i, 1);
				    	
//				    	 ImageButton b = (ImageButton) findViewById(id);
//							b.setImageResource(R.drawable.boardcellblue);

					}
				});
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            	System.out.println((i*BOARD_SIZE)+j);
            	
//            	int tileistakenby = gameboards;
            	int is_owned_by = gameboards.get((i*BOARD_SIZE)+j);
            	
            	if(clearBrickPos.length > 0 && j == clearBrickPos[0] && i == clearBrickPos[1])	{
        			if (myID == playerlist.get(0)) {
        				ib.setImageResource(R.drawable.boardcellbluepending);
        			}else if (myID==playerlist.get(1)) {
        				ib.setImageResource(R.drawable.boardcellgreenpending);
        			}else if (myID==playerlist.get(2)) {
        				ib.setImageResource(R.drawable.boardcellyellowpending);
        			}else if (myID==playerlist.get(3)) {
        				ib.setImageResource(R.drawable.boardcellredpending);
        			}
            	}
            	else if (playerlist.size()==2) {
            		if (is_owned_by == playerlist.get(0)) {
                		ib.setImageResource(R.drawable.boardcellblue);
    				}else if (is_owned_by==playerlist.get(1)) {
    					ib.setImageResource(R.drawable.boardcellgreen);
    				}else {
    					ib.setImageResource(R.drawable.boardcellempty);
    				}
				}  
    			else if (playerlist.size()==4) {
					if (is_owned_by == playerlist.get(0)) {
	            		ib.setImageResource(R.drawable.boardcellblue);
					}else if (is_owned_by==playerlist.get(1)) {
						ib.setImageResource(R.drawable.boardcellgreen);
					}else if (is_owned_by==playerlist.get(2)) {
						ib.setImageResource(R.drawable.boardcellyellow);
					}else if (is_owned_by==playerlist.get(3)) {
						ib.setImageResource(R.drawable.boardcellred);
					}else {
						ib.setImageResource(R.drawable.boardcellempty);
					}
				} 
            	d.addView(ib);//new Button(this));
    		}
            c.addView(d);
		}
        b.addView(c);
        a.addView(b);
        setContentView(a);
	}
}