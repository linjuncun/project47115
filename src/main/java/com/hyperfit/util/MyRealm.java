package com.hyperfit.util;

import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class MyRealm extends AuthorizingRealm {
	
	@Autowired
	private HttpSession httpSession;
	
	public final static String REALM_NAME = "MyRealm";

	public MyRealm() {
		setName(REALM_NAME);
	}

	/**
	 * 认证。
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String username = token.getUsername();
		String mixedPWD = "ok";
		return new SimpleAuthenticationInfo(username, mixedPWD, getName());
	}

	/**
	 * 授权。
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//
		// 暂时不对用户进行分组管理，如需要分组设置不同的权限可以通过以下方式进行处理：
		// 1. 获取用户名
		// String username = (String)getAvailablePrincipal(principals);
		// 2. 调用ESB接口获取用户属组和对应的权限
		// 3. 设置用户属组与权限
		//
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRole("ROLE_USER");
		info.addStringPermission("user:all");
		return info;
	}

}
