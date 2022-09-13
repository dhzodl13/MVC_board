package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;

import dao.MemberDAO;
import vo.MemberDTO;

public class MemberLoginProService {

	public boolean loginMember(MemberDTO member) {
		boolean isLoginSuccess = false;
		
		Connection con = getConnection();
		MemberDAO dao = MemberDAO.getInstance();
		dao.setConnection(con);
		
		isLoginSuccess = dao.selectMember(member);
		
		close(con);
		
		return isLoginSuccess;
	}

}









