/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.xh.web.ext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.dto.UserDto;
import com.xh.dto.common.ReturnCodeDto;
import com.xh.util.Base64Util;
import com.xh.util.UserHolder;
import com.xh.util.Utils;

import net.sf.json.JSONObject;

/**
 * Global filter to ensure correct JSON data wrapper during every request and
 * response.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class SsoFilter implements Filter {
	
	protected final Log logger = LogFactory.getLog(getClass());

	private List<String> ExcludeUriPattern ;
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(true);

		//查找cookie中的认证信息
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			Optional<Cookie> opt = Arrays.asList(cookies).stream().filter(cookis -> {
				return cookis != null && Utils.SSO_COOKIE_NAME.equals(cookis.getName())
						&& Base64Util.decodeUser(cookis.getValue()) != null;
			}).findAny();

			if (opt.isPresent()) {
				//找到用户信息,更新时间
				Cookie cookie = opt.get();
				UserDto user = Base64Util.decodeUser(cookie.getValue());
				request.setAttribute("user", user);
				session.setAttribute("user", user);
				UserHolder.setUser(user);
				
				cookie.setValue(Base64Util.encodeUser(user));
				cookie.setHttpOnly(true);
				cookie.setPath("/");
				response.addCookie(cookie);
				fc.doFilter(req, res);
				return;
			}
		}
		//没有认证信息,判断是否一定需要认证
		String contextPath = request.getServletPath();
		Optional<String> optUri = ExcludeUriPattern.stream().filter( uriPattern -> { return Utils.stringMatcher(contextPath, uriPattern) ; }).findAny() ;
		if(optUri.isPresent()){
			fc.doFilter(req, res);
			return ;
		}
		
		// 未登录
		response.getOutputStream().write(JSONObject.fromObject(new ReturnCodeDto(false ,"not login!",null,401)).toString().getBytes());
		return;
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
//		ExcludeUri
		String excludeUri = fc.getInitParameter("ExcludeUris");
		if(Utils.isEmpty(excludeUri)){
			ExcludeUriPattern = Utils.newArrayList();
		}else{
			String[] excludeUris = Utils.splitPath(excludeUri, ",");
			ExcludeUriPattern = Arrays.asList(excludeUris);
		}
		
		//默认排除 登录的api
		if(!ExcludeUriPattern.contains("/api/v1/login")){
			ExcludeUriPattern.add("/api/v1/login");
		}

		//默认排除 非/api/vi开头的api(静态资源,swagger信息)
		if(!ExcludeUriPattern.contains("^(?!/api/v1).*$")){
			ExcludeUriPattern.add("^(?!/api/v1).*$");
		}
		
	}

}
