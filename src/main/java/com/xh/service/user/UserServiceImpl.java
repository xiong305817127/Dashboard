package com.xh.service.user;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.PermissionDao;
import com.xh.dao.jsondatasource.UserDao;
import com.xh.dto.UserDto;
import com.xh.dto.common.PaginationDto;
import com.xh.service.common.BaseService;
import com.xh.service.common.ServiceException;
import com.xh.util.Utils;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 网站配置
 * 
 * @author Zihan
 * 
 */
@Service
public class UserServiceImpl extends BaseService implements UserService {

	@Resource
	private UserDao userDao;
	@Resource
	private PermissionDao permissionDao;

	@Override
	public UserDto addUser(UserDto user) throws CommonException {
		if(user ==null || Utils.isEmpty(user.getUsername())){
			throw new ServiceException(" user name cannot be empty !");
		}
		UserDto oldUser = null;
		try{
			oldUser = getUserByUserName(user.getUsername());
		}catch(CommonException e){
		}
		if(oldUser != null){
			throw new ServiceException(" user "+user.getUsername()+" already exists !");
		}
		
		user.setId( ( userDao.getMaxId()+1 )+"");
		user.setAvatar(getAvatar(user.getUsername()));
		user.setCreateTime(Utils.dateToStr(null));
		userDao.addUser(user);
		return user;
	}

	/**
	 * "http://dummyimage.com/100x100/cd79f2/757575.png&text=A"
	 * 
	 * @param userName
	 * @return
	 */
	private String getAvatar(String userName) {

		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(userName.charAt(0));
		if(pinyinArray != null && pinyinArray.length >0 ){
			userName=pinyinArray[0];
		}
		String text = (userName.charAt(0)+"").toUpperCase();

		//亮色
		Random random = new Random();
		String r = Integer.toHexString(random.nextInt(128)+127).toUpperCase();
		String g = Integer.toHexString(random.nextInt(128)+127).toUpperCase();
		String b = Integer.toHexString(random.nextInt(128)+127).toUpperCase();
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;
		String backgroundColor = r + g + b;
		
		//暗色
		r = Integer.toHexString(random.nextInt(128)).toUpperCase();
		g = Integer.toHexString(random.nextInt(128)).toUpperCase();
		b = Integer.toHexString(random.nextInt(128)).toUpperCase();
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;
		String foregroundColor = r + g + b;
		
		return "http://dummyimage.com/100x100/"+backgroundColor+"/"+foregroundColor+".png&text="+text ;

	}
	
	@Override
	public UserDto deleteUserById(String id) throws CommonException {
		UserDto oldUser = getUserById(id);
		if ("admin".equals(oldUser.getUsername())) {
			throw new ServiceException(" user admin is cannot be deleted ");
		}
		return userDao.deleteUser(id);
	}

	@Override
	public void deleteUserByIds(List<String> ids) throws CommonException {
		if (ids == null || ids.size() == 0) {
			return;
		}
		if (getAdminIds().stream().filter(id -> {
			return ids.contains(id);
		}).findAny().isPresent()) {
			throw new ServiceException(" user admin is cannot be deleted  ");
		}

		for (String id : ids) {
			deleteUserById(id);
		}
	}

	private List<String> getAdminIds() throws CommonException {
		UserDto admin = getUserByUserName("admin");
		return Utils.newArrayList(admin.getId());

	}

	@Override
	public UserDto updateUserById(String id, UserDto user) throws CommonException {

		UserDto oldUser = getUserById(id);
		if ("admin".equals(oldUser.getUsername())) {
			user.setPermissionId("admin");
		}
		updateObjectToBean(user, oldUser);
		return userDao.updateUser(oldUser);
	}

	public UserDto getUserById(String id) throws CommonException {
		return userDao.getUserById(id);
	}

	@Override
	public UserDto getUserByUserName(String userName) throws CommonException {
		return userDao.getUserByUserName(userName);
	}

	@Override
	public List<UserDto> getUserList() throws CommonException {
		return userDao.getUserList();
	}
	

	public PaginationDto<UserDto> getUserList(Integer page, Integer pageSize, String search) throws CommonException {

		PaginationDto<UserDto> result = new PaginationDto<UserDto>(page, pageSize, search);
		List<UserDto> listAll = getUserList();
		result.processingDataPaging(listAll, new PaginationDto.DealRowsInterface<UserDto>() {

			@Override
			public UserDto dealRow(Object obj) throws CommonException {
				return (UserDto) obj;
			}

			@Override
			public boolean match(Object obj, String search) {
				UserDto u = (UserDto) obj;
				return defaultMatch(u.getUsername(), search);
			}
		});

		return result;

	}

}
