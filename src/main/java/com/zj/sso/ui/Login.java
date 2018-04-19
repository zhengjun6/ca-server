package com.zj.sso.ui;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zj.sso.entity.UserBean;
import com.zj.sso.expection.FailedPdException;
import com.zj.sso.expection.FailedStateException;
import com.zj.sso.util.OpUtil;


/**
 * 自定义登录验证，多属性返回（可以返回登录的相关信息）
 * @author zj
 *
 */
public class Login extends AbstractUsernamePasswordAuthenticationHandler {
	 private static final org.slf4j.Logger LOGGER =LoggerFactory.getLogger(Login.class);
	
	public Login(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
			Integer order) {
		super(name, servicesManager, principalFactory, order);
		// TODO Auto-generated constructor stub
	}

	private String sql="select * from tpl_user_t where acount=?";
	@Override
	protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential transformedCredential,
			String originalPassword) throws GeneralSecurityException, PreventedException {
		JdbcTemplate template=null;
		try {
			template = OpUtil.createTemplate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String username=transformedCredential.getUsername();
		String pd=transformedCredential.getPassword();
//		//查询数据库加密的的密码
		UserBean user=template.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper<UserBean>(UserBean.class));
		if(user==null){
			throw new AccountNotFoundException("没有该用户");
		}
		if(!"1".equals(user.getStatus())){
			throw new FailedStateException("无效用户");
		}
//		//返回多属性,需要返回什么属性可以将这些数据放在map里，然后在将map的值传到createPrincipal
//		Map<String, Object> map=new HashMap<>(2);
//		map.put("email", user.getEmail().toString());
//		map.put("status", user.getStatus().toString());
		
		if(DigestUtils.sha256Hex(pd).equals(user.getPassword())){
			return createHandlerResult(transformedCredential, principalFactory.createPrincipal(username, null), null);
		}
		
		throw new FailedPdException("密码错误");
	}
}
