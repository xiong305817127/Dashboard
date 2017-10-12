package com.xh.service.user;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xh.common.dto.PaginationDto;
import com.xh.common.exception.WebException;
import com.xh.common.service.BaseService;
import com.xh.dao.jsondatasource.PermissionDao;
import com.xh.dao.jsondatasource.UserDao;
import com.xh.entry.User;
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
	public User addUser(User user) throws Exception {
		if(user ==null || Utils.isEmpty(user.getUsername())){
			throw new WebException(" user name cannot be empty !");
		}
		User oldUser = null;
		try{
			oldUser = getUserByUserName(user.getUsername());
		}catch(Exception e){
		}
		if(oldUser != null){
			throw new WebException(" user "+user.getUsername()+" already exists !");
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
	public User deleteUserById(String id) throws Exception {
		User oldUser = getUserById(id);
		if ("admin".equals(oldUser.getUsername())) {
			throw new WebException(" user admin is cannot be deleted ");
		}
		return userDao.deleteUser(id);
	}

	@Override
	public void deleteUserByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return;
		}
		if (getAdminIds().stream().filter(id -> {
			return ids.contains(id);
		}).findAny().isPresent()) {
			throw new WebException(" user admin is cannot be deleted  ");
		}

		for (String id : ids) {
			deleteUserById(id);
		}
	}

	private List<String> getAdminIds() throws Exception {
		User admin = getUserByUserName("admin");
		return Utils.newArrayList(admin.getId());

	}

	@Override
	public User updateUserById(String id, User user) throws Exception {

		User oldUser = getUserById(id);
		if ("admin".equals(oldUser.getUsername())) {
			user.setPermissionId("admin");
		}
		updateObjectToBean(user, oldUser);
		return userDao.updateUser(oldUser);
	}

	public User getUserById(String id) throws Exception {
		return userDao.getUserById(id);
	}

	@Override
	public User getUserByUserName(String userName) throws Exception {
		return userDao.getUserByUserName(userName);
	}

	@Override
	public List<User> getUserList() throws Exception {
		return userDao.getUserList();
	}
	

	public PaginationDto<User> getUserList(Integer page, Integer pageSize, String search) throws Exception {

		PaginationDto<User> result = new PaginationDto<User>(page, pageSize, search);
		List<User> listAll = getUserList();
		result.processingDataPaging(listAll, new PaginationDto.DealRowsInterface<User>() {

			@Override
			public User dealRow(Object obj) throws Exception {
				return (User) obj;
			}

			@Override
			public boolean match(Object obj, String search) {
				User u = (User) obj;
				return defaultMatch(u.getUsername(), search);
			}
		});

		return result;

	}

}
