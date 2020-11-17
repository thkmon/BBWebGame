package com.thkmon.wgbase.room.data;

import javax.websocket.Session;

import com.thkmon.wgbase.common.error.MessageException;
import com.thkmon.wgbase.room.etc.RoomConst;
import com.thkmon.wgbase.room.util.RoomServiceUtil;
import com.thkmon.wgbase.socket.data.UserSession;
import com.thkmon.wgbase.socket.data.UserSessionList;

public class RoomData implements RoomConst {
	
	private String roomId = null;
	private String roomName = null;
	private UserSessionList userSessionList = null;
	private TurnDataList turnDataList = null;
	
	private boolean bClosed = false;
	
	// 게임시작되었는지 여부
	private boolean gameIsStarted = false;

	
	public String getRoomId() {
		return roomId;
	}


	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	public String getRoomName() {
		return roomName;
	}


	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


	public RoomData() {
		userSessionList = new UserSessionList();
	}
	

	public UserSessionList getUserSessionList() {
		return userSessionList;
	}
	

	private void setUserSessionList(UserSessionList userSessionList) {
		this.userSessionList = userSessionList;
	}
	
	
	public boolean addUserSession(UserSession userSession) {
		if (userSession == null) {
			return false;
		}
		
		this.userSessionList.add(userSession);
		return true;
	}

	
	public UserSession getUserSession(int index) {
		return this.userSessionList.get(index);
	}


	public boolean isGameIsStarted() {
		return gameIsStarted;
	}


	private void setGameIsStarted(boolean gameIsStarted) {
		this.gameIsStarted = gameIsStarted;
	}
	
	
	public boolean isbClosed() {
		return bClosed;
	}


	public void setbClosed(boolean bClosed) {
		this.bClosed = bClosed;
	}
	

	/**
	 * 게임시작
	 * 
	 * @param session
	 */
	public void startNewGame(Session session) throws MessageException, Exception {

		// 게이머 리스트 구한다.
		turnDataList = createGamerIdList(session);
		
		// 새 타일 데이터(맵)을 생성한다.
		int gamerCount = turnDataList.size();
		
		// 게임시작
		this.gameIsStarted = true;
	}
	
	
	/**
	 * 다음턴 지정
	 * 
	 * @return
	 */
	public void setNextTurn() throws MessageException, Exception {
		turnDataList.getNextTurnIndex();
	}
	
	
	private TurnDataList getTurnDataList() {
		return turnDataList;
	}


	public void setTurnDataList(TurnDataList turnDataList) {
		this.turnDataList = turnDataList;
	}


	/**
	 * 연결 끊기면 턴 조정
	 * 
	 * @param sessionIdToRemove
	 */
	public void removeSessionOfTurn(String sessionIdToRemove) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return;
		}
		
		turnDataList.setTurnIsOver(sessionIdToRemove, false);
	}
	
	
	/**
	 * 턴을 되살린다.
	 * 
	 * @param sessionIdToRevive
	 * @param newSessionId
	 */
	public void reviveSessionOfTurn(String sessionIdToRevive, String newSessionId) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return;
		}
		
		turnDataList.reviveOveredTurn(sessionIdToRevive, newSessionId);
	}
	
	
	/**
	 * 모든 게이머(방장 포함)의 아이디를 수집해서 StringList로 만든다.
	 * 
	 * @param session
	 * @return
	 */
	private TurnDataList createGamerIdList(Session session) throws MessageException, Exception {
		
		UserSessionList userSessionList = RoomServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return null;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return null;
		}

		TurnDataList resultList = new TurnDataList();
		
		UserSession singleUserSession = null;
		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}

			singleUserSession = userSessionList.get(i);
			if (singleUserSession == null) {
				continue;
			}

			if (singleUserSession.isRoomChief() == true || singleUserSession.getUserType() == USER_TYPE_GAMER) {
				String singleSessionId = singleSession.getId();
				if (singleSessionId != null && singleSessionId.length() > 0) {
					
					TurnData turnData = new TurnData();
					turnData.setSessionId(singleSessionId);
					turnData.setUserNickName(singleUserSession.getUserNickName());
					
					resultList.add(turnData);
				}
				continue;
			}
		}

		return resultList;
	}
	
	
	public String getUserListString(Session session) throws MessageException, Exception {
		
		UserSessionList userSessionList = RoomServiceUtil.getUserSessionListBySession(session);
		if (userSessionList == null) {
			return null;
		}

		int sessionCount = userSessionList.size();
		if (sessionCount < 1) {
			return null;
		}

		StringBuffer resultBuff = new StringBuffer();
		
		UserSession singleUserSession = null;
		Session singleSession = null;

		for (int i = 0; i < sessionCount; i++) {
			singleSession = userSessionList.getOriginSession(i);
			if (singleSession == null) {
				continue;
			}

			if (!singleSession.isOpen()) {
				continue;
			}
			
			singleUserSession = userSessionList.get(i);
			if (singleUserSession == null) {
				continue;
			}
			
			resultBuff.append(singleUserSession.getUserNickName());

			if (singleUserSession.isRoomChief()) {
				resultBuff.append("(방장)");
				
			} else if (singleUserSession.getUserType() == USER_TYPE_GAMER) {
				// resultBuff.append("(참가)");
				
			} else if (singleUserSession.getUserType() == USER_TYPE_OBSERVER) {
				resultBuff.append("(관전)");
			}
			
			resultBuff.append(";");
		}

		return resultBuff.toString();
	}
	
	
	
	public boolean checkSessionIsTurnNow(Session session) throws MessageException, Exception {
		if (turnDataList == null || turnDataList.size() == 0) {
			return false;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return false;
		}
		
		String gamerId = turnDataList.get(currentTurnIndex).getSessionId();
		
		if (gamerId.equals(session.getId())) {
			return true;
		}
		
		return false;
	}
	
	
	public int getCurrentTurnIndex() {
		if (turnDataList == null || turnDataList.size() == 0) {
			return -1;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return -1;
		}
		
		return currentTurnIndex;
	}
	
	
	public String getCurrentTurnUserName() {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return null;
		}
		
		return turnDataList.get(currentTurnIndex).getUserNickName();
	}
	
	
	public TurnData getCurrentTurn() {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		int currentTurnIndex = turnDataList.getCurrentTurnIndex();
		if (currentTurnIndex < 0) {
			return null;
		}
		
		return turnDataList.get(currentTurnIndex);
	}
	
	
	public TurnData getTurnData(String sessionId) {
		if (turnDataList == null || turnDataList.size() == 0) {
			return null;
		}
		
		return turnDataList.getTurnData(sessionId);
	}
}