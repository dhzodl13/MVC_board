package svc;

import static db.JdbcUtil.*;

import java.sql.Connection;

import dao.BoardDAO;
import vo.BoardDTO;

public class BoardModifyProService {

	// 수정 권한 판별을 위한 isBoardWriter() 메서드 정의
	public boolean isBoardWriter(int board_num, String board_pass) {
		boolean isBoardWriter = false;
		
		Connection con = getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 객체의 isBoardWriter() 메서드를 호출하여 수정 권한 판별 수행
		// => 글 삭제에 사용된 BoardDAO 의 isBoardWriter() 메서드 재사용 가능
		isBoardWriter = dao.isBoardWriter(board_num, board_pass);
		
		close(con);
		
		return isBoardWriter;
	}

	// 글 수정 작업 요청을 위한 modifyBoard() 메서드
	public boolean modifyBoard(BoardDTO board) {
		boolean isModifySuccess = false;
		
		Connection con = getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 의 updateBoard() 메서드를 호출하여 글 수정 작업 수행
		// => 파라미터 : BoardDTO 객체   리턴타입 : int(updateCount)
		int updateCount = dao.updateBoard(board);
		
		// 글 수정 작업 실행 결과 판별하여 성공 시 commit, 실패 시 rollback
		if(updateCount > 0) {
			commit(con);
			isModifySuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isModifySuccess;
	}

}











