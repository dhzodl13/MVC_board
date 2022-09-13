package svc;

import java.sql.Connection;

import dao.BoardDAO;
import vo.BoardDTO;

import static db.JdbcUtil.*;

public class BoardReplyProService {
	// 답글 작성 요청 작업을 수행하는 replyBoard() 메서드 정의
	public boolean replyBoard(BoardDTO board) {
		boolean isReplySuccess = false;
		
		Connection con = getConnection();
		BoardDAO dao = BoardDAO.getInstance();
		dao.setConnection(con);
		
		// BoardDAO 의 insertReplyBoard() 메서드 호출하여 답글 등록 작업 수행
		// => 파라미터 : BoardDTO 객체   리턴타입 : int(insertCount)
		int insertCount = dao.insertReplyBoard(board);
		
		if(insertCount > 0) {
			commit(con);
			isReplySuccess = true;
		} else {
			rollback(con);
		}
		
		close(con);
		
		return isReplySuccess;
	}

}
