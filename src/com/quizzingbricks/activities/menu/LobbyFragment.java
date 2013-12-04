package com.quizzingbricks.activities.menu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.quizzingbricks.communication.apiObjects.LobbyThreadedAPI;
import com.quizzingbricks.communication.apiObjects.OnTaskCompleteAsync;
import com.quizzingbricks.tools.AsyncTaskResult;

public class LobbyFragment extends ListFragment implements OnTaskCompleteAsync {
	ArrayList<String> list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LobbyThreadedAPI lobbyThreadedAPI = new LobbyThreadedAPI(getActivity());
		lobbyThreadedAPI.getGameLobbies(this);
		
	}
	
	@Override
	public void onComplete(AsyncTaskResult<JSONObject> result) {
		// TODO Auto-generated method stub
		if (list == null) {
			list = new ArrayList<String>();
		}
		list.clear();
		list.add("+Create New Game");
		try {
			if(result.hasException())	{
				System.out.println("Oh noes...");
				result.getException().printStackTrace();
			}
			else	{
				JSONArray lobbyArray = result.getResult().getJSONArray("lobbies");
				for (int i = 0; i < lobbyArray.length(); i++) {
					JSONObject lobbyObject = lobbyArray.getJSONObject(i);
					Object lobbyid = lobbyObject.get("l_id");
					boolean lobbyowner = lobbyObject.getBoolean("owner");
					String lobbyname;
					if (lobbyowner == true) {
						lobbyname = lobbyid.toString()+", your lobby";
					}else {
						lobbyname = lobbyid.toString();
					}
//					String lobbyName = lobbyOwner.toString();
					list.add(lobbyname);
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			list.add("No Lobbys");
		}
		Adapter md = new Adapter(getActivity(), list);
		setListAdapter(md);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (position == 0) {
			
			 Intent i = new Intent(getActivity(), CreateLobbyActivity.class);
			 startActivityForResult(i, 1);
		} else {
			
		}
		
		
	}
}