package svc;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

public class BoardDeleteProService {

	// 삭제 권한 판별 요청을 수행하는 isBoardWriter()
	public boolean isBoardWriter(int board_num, String board_pass) {
		boolean isBoardWriter = false;
		
		Connection con = getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 객체의 isBoardWriter() 메서드를 호출하여 삭제 권한 판별 수행
		isBoardWriter = dao.isBoardWriter(board_num, board_pass);
		
		close(con);
		
		return isBoardWriter;
	}

	// 삭제 요청을 수행하는 removeBoard()
	public boolean removeBoard(int board_num) {
		boolean isDeleteSuccess = false;
		
		Connection con = getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 객체의 deleteBoard() 메서드를 호출하여 삭제 작업 수행
		// => 파라미터 : 글번호    리턴타입 : int(deleteCount)
		int deleteCount = dao.deleteBoard(board_num);
		
		// deleteCount 가 0 보다 크면 commit, 아니면 rollback 작업 수행
		if(deleteCount > 0) {
			commit(con);
			// isDeleteSuccess 값을 true 로 변경하여 성공 표시
			isDeleteSuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isDeleteSuccess;
	}

	// 파일명 조회하기
	// => 파라미터 : 글번호   리턴타입 : String(realFile)
	public String getRealFile(int board_num) {
		String realFile = "";
		
		Connection con = getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 객체의 selectRealFile() 메서드를 호출하여 실제 파일명 조회
		// => 파라미터 : 글번호   리턴타입 : String(realFile)
		realFile = dao.selectRealFile(board_num);
		
		close(con);
		
		return realFile;
	}

}










