package com.thkmon.wgbase.room.controller;

import java.net.URLDecoder;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.thkmon.wgbase.room.data.RoomData;
import com.thkmon.wgbase.room.util.RoomServiceUtil;

@Controller
public class RoomController {
	
	
	@RequestMapping(value = "/room/list", method = {RequestMethod.GET, RequestMethod.POST})
	public String roomList(Locale locale, Model model) {
		
		return "room/list";
	}
	
	
	@RequestMapping(value = "/room/room", method = RequestMethod.GET)
	public String roomFromGet(Locale locale, Model model) {
		return "room/list";
	}
	
	
	@RequestMapping(value = "/room/room", method = RequestMethod.POST)
	public String roomFromPost(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {

		try {
			String userNickName = request.getParameter("userNickName");
			String userType = request.getParameter("userType");
			
			if (userNickName == null || userNickName.length() == 0) {
				return "room/wrong_access";
			} else {
				userNickName = URLDecoder.decode(userNickName, "UTF-8");
			}
			
			if (userType == null || userType.length() == 0) {
				return "room/wrong_access";
			}
			
			String roomId = request.getParameter("roomId");
			String roomName = null;
			
			if (roomId != null && roomId.length() > 0) {
				// 1. 기존 방에 접속
				RoomData roomData = RoomServiceUtil.getRoomData(roomId);
				if (roomData == null) {
					// throw new MessageException("존재하지 않는 방입니다.");
					return "room/wrong_access";
				}
				
				if (roomData.isbClosed()) {
					// throw new MessageException("종료된 방입니다.");
					return "room/wrong_access";
				}
				
				roomName = roomData.getRoomName();
				
			} else {
				// 2. 방 생성
				roomName = request.getParameter("roomName");
				
				if (roomName == null || roomName.length() == 0) {
					return "room/wrong_access";
				} else {
					roomName = URLDecoder.decode(roomName, "UTF-8");
				}
				
				RoomData roomData = RoomServiceUtil.makeNewRoom(roomName);
				roomId = roomData.getRoomId();
			}
			
			model.addAttribute("roomId", roomId);
			model.addAttribute("roomName", roomName);
			model.addAttribute("userNickName", userNickName);
			model.addAttribute("userType", userType);
			
			return "room/room";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "room/wrong_access";
		}
	}
}