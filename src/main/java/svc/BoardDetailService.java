package svc;

import vo.BoardDTO;

import static db.JdbcUtil.*;

import java.sql.Connection;

import dao.BoardDAO;

public class BoardDetailService {

	// 조회수 증가 작업을 요청하는 increaseReadcount() 메서드
	public void increaseReadcount(int board_num) {
		Connection con = getConnection();
		
		BoardDAO dao = BoardDAO.getInstance();
		
		dao.setConnection(con);
		
		// BoardDAO 객체의 updateReadcount() 메서드를 호출하여 조회수 증가 작업 수행
		// => 파라미터 : board_num
		dao.updateReadcount(board_num);
		
		// 조회수 증가 작업 commit
		commit(con);
		
		close(con);
	}

	// 1개 게시물 상세 정보 조회 작업을 요청하는 getBoard() 메서드
	public BoardDTO getBoard(int board_num) {
		BoardDTO board = null;
		
		Connection con = getConnection();
		
		BoardDAO dao = BoardDAO.getInstance();
		
		dao.setConnection(con);
		
		// BoardDAO 객체의 selectBoard() 메서드를 호출하여 1개 게시물의 상세 정보 조회 작업 수행
		// => 파라미터 : board_num   리턴타입 : BoardDTO(board)
		board = dao.selectBoard(board_num);
		
		close(con);
		
		return board;
	}

}










