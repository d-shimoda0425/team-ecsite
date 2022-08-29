package jp.co.internous.sunflower.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.sunflower.model.domain.MstUser;
import jp.co.internous.sunflower.model.form.UserForm;
import jp.co.internous.sunflower.model.mapper.MstUserMapper;
import jp.co.internous.sunflower.model.mapper.TblCartMapper;
import jp.co.internous.sunflower.model.session.LoginSession;

@Controller
@RequestMapping("/sunflower/auth")
public class AuthController {
	
	@Autowired
	private LoginSession loginSession;
	
	@Autowired
	private MstUserMapper userMapper;
	
	@Autowired
	private TblCartMapper cartMapper;
	
	private Gson gson = new Gson();
	
	@ResponseBody
	@RequestMapping("/getLoginSession")
	public  String getLoginSession() {
		return loginSession.getUserName();
	}
	
	@ResponseBody
	@RequestMapping("/login")
	public  String login(@RequestBody UserForm f) {
		List<MstUser> users = userMapper.findByUserNameAndPassword(f.getUserName(),f.getPassword());
			
		if(users.size()>0) {
			MstUser user = users.get(0);
			long tempId = loginSession.getTempId();
			if( tempId != 0){
				long count = cartMapper.findCountByUserId(tempId);
				if (count > 0) {
					cartMapper.updateUserId(user.getId(),tempId);
				}
			}
			
			if(user != null) {
				loginSession.setUserId(user.getId());
				loginSession.setTempId(0);
				loginSession.setLogined(true);
				loginSession.setUserName(user.getUserName());
				loginSession.setPassword(user.getPassword());
			} else {
				loginSession.setUserId(0);
				loginSession.setLogined(false);
				loginSession.setUserName(null);
				loginSession.setPassword(null);
			}
			
			return  gson.toJson(user);
		}
		
		return "";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		loginSession.setUserId(0);
		loginSession.setTempId(0);
		loginSession.setLogined(false);
		loginSession.setUserName(null);
		loginSession.setPassword(null);
		
		return "index";
	}
}
