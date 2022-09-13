package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vo.MemberDTO;

import static db.JdbcUtil.close;


public class MemberDAO {
	// 1. 멤버변수 선언 및 인스턴스 생성
	private static MemberDAO instance = new MemberDAO();
	// 2. 생성자 정의
	private MemberDAO() {}
	// 3. Getter 정의(자동 생성)
	public static MemberDAO getInstance() {
		return instance;
	}
	// ----------------------------------------------------------------------------------------
	// 외부(Service 클래스)로부터 Connection 객체를 전달받아 관리하기 위해
	// Connection 타입 멤버변수와 Setter 메서드 정의
	private Connection con;
	public void setConnection(Connection con) {
		this.con = con;
	}
	// ----------------------------------------------------------------------------------------
	// 아이디 중복 체크
	public boolean selectDuplicateId(String id) {
		boolean isDuplicate = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT id FROM member WHERE id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 아이디가 이미 존재할 경우
				isDuplicate = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("MemberDAO - selectDuplicateId() 메서드 오류 : " + e.getMessage());
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return isDuplicate;
	}
	
	// 회원 가입
	public int insertMember(MemberDTO member) {
		int insertCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			String sql = "INSERT INTO member VALUES (?,?,?,?,?,?,?,now())";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, member.getName());
			pstmt.setString(2, member.getId());
			pstmt.setString(3, member.getPasswd());
			pstmt.setString(4, member.getEmail());
			pstmt.setString(5, member.getPost_code());
			pstmt.setString(6, member.getAddress1());
			pstmt.setString(7, member.getAddress2());
			
			insertCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("MemberDAO - insertMember() 메서드 오류 : " + e.getMessage());
		} finally {
			close(pstmt);
		}
		
		return insertCount;
	}
	
	// 로그인
	public boolean selectMember(MemberDTO member) {
		boolean isLoginSuccess = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM member WHERE id=? AND passwd=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPasswd());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				isLoginSuccess =  true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("MemberDAO - selectMember() 메서드 오류 : " + e.getMessage());
		} finally {
			close(pstmt);
			close(rs);
		}
		
		return isLoginSuccess;
	}
		
}












