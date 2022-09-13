package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

// 데이터베이스 접속 관련 처리를 수행하는 JdbcUtil 클래스 정의
// => 데이터베이스 접속 및 자원 반환(해제) 등의 공통적인 작업 수행
// => 인스턴스 생성 없이도 모든 메서드를 클래스명만으로 접근 가능하도록 static 메서드로 정의
public class JdbcUtil {
	// 1. 데이터베이스 접속을 수행하는 getConnection() 메서드 정의
	// => 데이터베이스 작업 1단계와 2단계를 수행하는 메서드
	// => DB 접속 수행 성공 시 리턴되는 java.sql.Connection 객체를 리턴
	public static Connection getConnection() {
		// 데이터베이스 연결 객체를 저장할 Connection 타입 변수 선언
		Connection con = null;
		
		try {
			// context.xml 에 설정된 DBCP(Connection Pool)로부터 Connection 객체 가져와서 리턴하기
			// 1. 톰캣으로부터 컨텍스트 객체 가져오기(context.xml 파일의 Context 태그 부분에 해당)
			// => InitialContext 객체 생성하여 Context 타입으로 업캐스팅하여 저장
			Context initCtx = new InitialContext();
			
			// 2. 생성된 Context 객체(initCtx) 로부터 context.xml 에 정의된 Resource 태그 부분 가져오기
			// => Context 객체의 lookup() 메서드를 호출하여 "java:comp/env" 문자열을 파라미터로 전달
			// => 리턴타입 Object 인 객체를 Context 타입으로 다운캐스팅하여 저장
//			Context envCtx = (Context)initCtx.lookup("java:comp/env");
			
			// 3. Resource 태그 내의 name 속성(= 리소스 이름 jdbc/MySQL)을 가져오기 위해
			//    생성된 Context 객체(envCtx)의 lookup() 메서드를 호출하여 리소스 이름을 파라미터로 전달
			// => 리턴타입 Object 인 객체를 type 속성에 지정된 객체명(javax.sql.DataSource)으로 다운캐스팅
//			DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL");
			
			// ----------- 참고! 2번과 3번 과정을 하나의 문장으로 결합하는 경우 -------------
			// => InitialContext 객체의 lookup() 메서드를 호출하여 2번&3번 문자열을 결합하여 전달
			DataSource ds = (DataSource)initCtx.lookup("java:comp/env/jdbc/MySQL");
			
			// 4. DataSource 객체(커넥션풀)로부터 미리 생성된 Connection 객체 가져오기
			// => DataSource 객체의 getConnection() 메서드를 호출하여 Connection 객체 리턴받기
			con = ds.getConnection();
			// ----------------------------------------------------------------------------------
			// 5. 트랜잭션 처리를 위해 데이터베이스 Connection 객체를 통해 Auto Commit 기능 해제
			// => Connection 객체의 setAutoCommit() 메서드 호출하여 false 값 전달
			con.setAutoCommit(false); // Auto Commit 기능 해제(기능 설정 시 true 값 전달)
			// => 주의! 이 기능을 해제한 이후로 DML(INSERT, UPDATE, DELETE) 작업 수행 후
			//    반드시 commit 작업을 수동으로 실행해야한다!
			// => 또한, 이전 상태로 되돌리려면 rollback 작업을 수동으로 실행해야한다!
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return con;
	}
	
	// 2. 각종 DB 관련 자원을 반환하는 close() 메서드 정의 => 메서드 오버로딩 활용
	// => Connection, PreparedStatement, ResultSet
	public static void close(Connection con) { // Connection 반환
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(PreparedStatement pstmt) { // PreparedStatement 반환
		try {
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(ResultSet rs) { // ResultSet 반환
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 트랜잭션 처리에 필요한 commit, rollback 작업을 수행할 메서드 정의
	// => 단, Connection 객체를 사용하여 Auto Commit 기능 해제 필수!
	// => 각 메서드 파라미터로 Connection 객체를 전달받아 commit(), rollback() 메서드 호출
	public static void commit(Connection con) {
		try {
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollback(Connection con) {
		try {
			con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}















