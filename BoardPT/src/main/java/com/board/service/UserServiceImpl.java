package com.board.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.board.config.SecurityConfiguration;
import com.board.dto.User;
import com.board.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {
	
	private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private BCryptPasswordEncoder pwEncoder;
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public int insertUser(User userVO) {
		String id = userVO.getUser_id();
		int check = userMapper.countUserById(id);
		
		if (check > 0) { // 해당 아이디가 이미 존재
			log.info("아이디가 이미 존재합니다.");
			return 0;
		}
		
		return userMapper.insertUser(userVO);
	}

	@Override
	public int countUserById(String id) {

		return 0;
	}

	
	@Override
	public int loginCheck(String id, String pw) {
		int check 		= 0;
		String password	= userMapper.getPasswordById(id);
		
		if (pwEncoder.matches(pw, password)) {
			check = 1;
		} else if (pw.equals(password)) {
			check = 1;
		}
		
		return check;
	}

	@Override
	public int insertLoginReg(String id, String ip) {
		log.info("IP :: {}", ip);
		return userMapper.insertLoginReg(id, ip);
	}

	@Override
	public List<User> getAllUsers(String search, int startRow, int pageSize) {

		return null;
	}

	@Override
	public User getUserById(String id) {

		return null;
	}

	@Override
	public int updateUser(User userVO) {

		return 0;
	}

	@Override
	public int deleteUser(String id) {

		return 0;
	}

	
	
}
