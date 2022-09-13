package dao;

// static import 기능을 활용하여 JdbcUtil 클래스의 static 메서드 import
import static db.JdbcUtil.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vo.BoardDTO;

// 실제 비즈니스 로직(DB 작업)을 처리할 BoardDAO 클래스 정의
// => BoardDAO 클래스 인스턴스를 여러개 생성하여 서로 다른 값을 저장할 필요가 없으므로
//    싱글톤 디자인 패턴(Singleton Design Pattern)을 통해
//    하나의 인스턴스를 생성한 후 모두가 공유하도록 정의할 수 있다!
public class BoardDAO {
	// ---------------- 싱글톤 디자인 패턴을 활용한 BoardDAO 인스턴스 생성 작업 ---------------
	// 1. 외부에서 인스턴스 생성이 불가능하도록 생성자 정의 시 private 접근제한자 적용
//	private BoardDAO() {}
	
	// 2. 자신의 클래스 내에서 직접 인스턴스 생성하여 변수(instance)에 저장
	// => 외부에서 멤버변수에 접근이 불가능하도록 private 접근제한자 적용
	// => 클래스 로딩 시 getInstance() 메서드와 함께 로딩되기 위해 static 멤버변수로 선언
//	private static BoardDAO instance = new BoardDAO();

	// 3. 생성된 인스턴스를 외부로 리턴하기 위한 Getter 정의
	// => 외부에서 인스턴스 생성 없이도 호출이 가능하도록 static 메서드로 정의
	// => 이 때, 2번에서 선언된 멤버변수(instance)도 static 변수로 선언되어야 함
	//    (static 메서드 내에서 접근하는 변수도 static 변수여야하기 때문에)
//	public static BoardDAO getInstance() {
//		return instance;
//	}
	// ----------------------------------------------------------------------------------------
	// 1. 멤버변수 선언 및 인스턴스 생성
	private static BoardDAO instance = new BoardDAO();
	// 2. 생성자 정의
	private BoardDAO() {}
	// 3. Getter 정의(자동 생성)
	public static BoardDAO getInstance() {
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
	// 글쓰기 작업을 수행할 insertBoard() 메서드 정의
	// => 파라미터 : BoardDTO 객체(board)   리턴타입 : int(insertCount)
	public int insertBoard(BoardDTO board) {
//		System.out.println("BoardDAO - insertBoard()");
		
		// INSERT 작업 결과를 리턴받아 저장할 변수 선언
		int insertCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int num = 1; // 새 글 번호를 저장할 변수
		
		try {
			// 새 글 번호로 사용될 번호를 생성하기 위해 기존 게시물의 가장 큰 번호 조회
			// => 조회 결과가 있을 경우 해당 번호 + 1 값을 새 글 번호로 저장
			String sql = "SELECT MAX(board_num) FROM board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				num = rs.getInt(1) + 1; // 조회된 가장 큰 번호 + 1 값을 새 글 번호로 저장
			}
			
			// 사용 완료된 PreparedStatement 객체를 먼저 반환
			close(pstmt);
			
			// 전달받은 데이터를 board 테이블에 INSERT
			sql = "INSERT INTO board VALUES (?,?,?,?,?,?,?,?,?,?,?,now())";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, board.getBoard_name());
			pstmt.setString(3, board.getBoard_pass());
			pstmt.setString(4, board.getBoard_subject());
			pstmt.setString(5, board.getBoard_content());
			pstmt.setString(6, board.getBoard_file());
			pstmt.setString(7, board.getBoard_real_file());
			// 답글에 사용될 참조글 번호(board_re_ref)는 새 글에 대한 번호인 새 글 번호와 동일하게 지정
			pstmt.setInt(8, num); // board_re_ref
			pstmt.setInt(9, 0); // board_re_lev
			pstmt.setInt(10, 0); // board_re_seq
			pstmt.setInt(11, 0); // board_readcount
			
			insertCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 발생! - insertBoard()");
		} finally {
			// DB 자원 반환(주의! Connection 객체는 반환 금지! => Service 클래스에서 반환)
			close(rs);
			close(pstmt);
		}
		
		return insertCount;
	}
	
	// 전체 게시물 수를 조회할 selectListCount() 메서드 정의
	// => 파라미터 : 없음   리턴타입 : int(listCount)
	public int selectListCount() {
		int listCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// 3단계
			String sql = "SELECT COUNT(*) FROM board";
			pstmt = con.prepareStatement(sql);
			
			// 4단계
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 발생! - " + e.getMessage());
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return listCount;
	}
	
	// 게시물 목록을 조회하는 selectBoardList() 메서드 정의
	// => 파라미터 : 현재 페이지 번호(pageNum), 페이지 당 게시물 수(listLimit)
	//    리턴타입 : ArrayList<BoardDTO> boardList
	public ArrayList<BoardDTO> selectBoardList(int pageNum, int listLimit) {
		ArrayList<BoardDTO> boardList = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		// 현재 페이지 번호를 활용하여 조회 시 시작행 번호 계산
		int startRow = (pageNum - 1) * listLimit;
		
		try {
//			String sql = "SELECT * FROM board "
//					+ "ORDER BY board_num DESC "
//					+ "LIMIT ?,?";
			
			// 답글에 대한 처리 과정 추가
			// => 참조글번호(board_re_ref) 기준 내림차순
			//    순서번호(board_re_seq) 기준 오름차순
			String sql = "SELECT * FROM board "
					+ "ORDER BY board_re_ref DESC, board_re_seq ASC "
					+ "LIMIT ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, listLimit);
			
			rs = pstmt.executeQuery();
			
			// 전체 게시물을 저장할 ArrayList<BoardDTO> 객체 생성
			boardList = new ArrayList<BoardDTO>();
			
			// while 문을 사용하여 조회 결과에 대한 반복 작업 수행
			while(rs.next()) {
				// 1개 게시물 정보를 저장할 BoardDTO 객체 생성
				BoardDTO board = new BoardDTO();
				// 게시물 정보 저장
				board.setBoard_num(rs.getInt("board_num"));
				board.setBoard_name(rs.getString("board_name"));
				board.setBoard_pass(rs.getString("board_pass"));
				board.setBoard_subject(rs.getString("board_subject"));
				board.setBoard_content(rs.getString("board_content"));
				board.setBoard_file(rs.getString("board_file"));
				board.setBoard_real_file(rs.getString("board_real_file"));
				board.setBoard_re_ref(rs.getInt("board_re_ref"));
				board.setBoard_re_lev(rs.getInt("board_re_lev"));
				board.setBoard_re_seq(rs.getInt("board_re_seq"));
				board.setBoard_readcount(rs.getInt("board_readcount"));
				board.setBoard_date(rs.getDate("board_date"));
//				System.out.println(board);
				
				// 전체 게시물 정보를 저장하는 ArrayList 객체에 1개 게시물 정보 BoardDTO 객체 추가
				boardList.add(board);
			}
			
//			System.out.println(boardList.toString());
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 발생! - selectBoardList()");
		} finally {
			close(rs);
			close(pstmt);
		}

		return boardList;
	}
	
	// 조회수 증가 작업을 처리하는 updateReadcount() 메서드 
	// => 파라미터 : board_num
	public void updateReadcount(int board_num) {
		PreparedStatement pstmt = null;
		
		try {
			String sql = "UPDATE board SET board_readcount=board_readcount+1 WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 - updateReadcount() : " + e.getMessage());
		} finally {
			close(pstmt);
		}
	}
	
	// 1개 게시물의 상세 정보 조회 작업 수행하는 selectBoard() 메서드 
	// => 파라미터 : board_num   리턴타입 : BoardDTO(board)
	public BoardDTO selectBoard(int board_num) {
		BoardDTO board = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM board WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				board = new BoardDTO();
				board.setBoard_num(rs.getInt("board_num"));
				board.setBoard_name(rs.getString("board_name"));
				board.setBoard_pass(rs.getString("board_pass"));
				board.setBoard_subject(rs.getString("board_subject"));
				board.setBoard_content(rs.getString("board_content"));
				board.setBoard_file(rs.getString("board_file"));
				board.setBoard_real_file(rs.getString("board_real_file"));
				board.setBoard_re_ref(rs.getInt("board_re_ref"));
				board.setBoard_re_lev(rs.getInt("board_re_lev"));
				board.setBoard_re_seq(rs.getInt("board_re_seq"));
				board.setBoard_readcount(rs.getInt("board_readcount"));
				board.setBoard_date(rs.getDate("board_date"));
				
				System.out.println(board);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 - selectBoard() : " + e.getMessage());
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return board;
	}
	
	// 글 삭제 권한 판별을 수행하는 isBoardWriter()
	public boolean isBoardWriter(int board_num, String board_pass) {
		boolean isBoardWriter = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// 글번호와 패스워드가 모두 일치하는 레코드 조회
			String sql = "SELECT * FROM board WHERE board_num=? AND board_pass=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			pstmt.setString(2, board_pass);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// 조회 결과 있음 = 번호에 해당하는 패스워드가 일치한다
				isBoardWriter = true; // 결과값을 true 로 변경
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 - isBoardWriter() : " + e.getMessage());
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return isBoardWriter;
	}
	
	// 글 삭제 작업 수행하는 deleteBoard()
	public int deleteBoard(int board_num) {
		int deleteCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			String sql = "DELETE FROM board WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			
			deleteCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 - isBoardWriter() : " + e.getMessage());
		} finally {
			close(pstmt);
		}
		
		return deleteCount;
	}
	
	// 글 수정 작업을 수행하는 updateBoard() 메서드
	public int updateBoard(BoardDTO board) {
		int updateCount = 0;
		
		PreparedStatement pstmt = null;
		
		try {
			// 수정 시 업로드 할 파일을 선택하지 않았을 경우 수정 대상에서 파일명 제외시켜야함
			if(board.getBoard_file() == null) { // 업로드 파일이 선택되지 않았을 경우
				String sql = "UPDATE board "
						+ "SET board_name=?,board_subject=?,board_content=? "
						+ "WHERE board_num=?";
				
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, board.getBoard_name());
				pstmt.setString(2, board.getBoard_subject());
				pstmt.setString(3, board.getBoard_content());
				pstmt.setInt(4, board.getBoard_num());
			} else {
				String sql = "UPDATE board "
						+ "SET board_name=?,board_subject=?,board_content=?,board_file=?,board_real_file=? "
						+ "WHERE board_num=?";
				
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, board.getBoard_name());
				pstmt.setString(2, board.getBoard_subject());
				pstmt.setString(3, board.getBoard_content());
				pstmt.setString(4, board.getBoard_file());
				pstmt.setString(5, board.getBoard_real_file());
				pstmt.setInt(6, board.getBoard_num());
			}
			
			updateCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 - updateBoard() : " + e.getMessage());
		} finally {
			close(pstmt);
		}
		
		return updateCount;
	}
	
	// 답글 작성을 수행하는 insertReplyBoard() 메서드
	public int insertReplyBoard(BoardDTO board) {
		int insertCount = 0;
		
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rs = null;
		
		int num = 1; // 새 글 번호를 저장할 변수
		
		try {
			// 새 글 번호로 사용될 번호를 생성하기 위해 기존 게시물의 가장 큰 번호 조회
			// => 조회 결과가 있을 경우 해당 번호 + 1 값을 새 글 번호로 저장
			String sql = "SELECT MAX(board_num) FROM board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				num = rs.getInt(1) + 1; // 조회된 가장 큰 번호 + 1 값을 새 글 번호로 저장
			}
			
			// 기존 답글들에 대한 순서번호(board_re_seq) 증가 작업 처리
			// => 원본글의 참조글번호(board_re_ref) 와 같고(같은 레코드들 중에서)
			//    원본글의 순서번호(board_re_seq)보다 큰 레코드들의 순서번호를 1씩 증가시키기
			sql = "UPDATE board SET board_re_seq=board_re_seq+1 "
					+ "WHERE board_re_ref=? AND board_re_seq>?";
			pstmt2 = con.prepareStatement(sql);
			pstmt2.setInt(1, board.getBoard_re_ref()); // 참조글번호
			pstmt2.setInt(2, board.getBoard_re_seq()); // 순서번호
			pstmt2.executeUpdate();
			
			// 답글을 board 테이블에 INSERT 작업
			sql = "INSERT INTO board VALUES (?,?,?,?,?,?,?,?,?,?,?,now())";
			pstmt2 = con.prepareStatement(sql);
			pstmt2.setInt(1, num);
			pstmt2.setString(2, board.getBoard_name());
			pstmt2.setString(3, board.getBoard_pass());
			pstmt2.setString(4, board.getBoard_subject());
			pstmt2.setString(5, board.getBoard_content());
			// 답글 기능에서 파일 업로드 기능은 제외
			pstmt2.setString(6, "");
			pstmt2.setString(7, "");
			// ------------ 주의! 답글 관련 번호 ---------------
			// 답글에 사용될 참조글 번호(board_re_ref)는 원본글의 참조글번호와 동일
			pstmt2.setInt(8, board.getBoard_re_ref()); // board_re_ref
			// 들여쓰기레벨(board_re_lev)과 순서번호(board_re_seq)는 원본글의 각 번호 + 1 
			pstmt2.setInt(9, board.getBoard_re_lev() + 1); // board_re_lev
			pstmt2.setInt(10, board.getBoard_re_seq() + 1); // board_re_seq
			// -------------------------------------------------
			pstmt2.setInt(11, 0); // board_readcount
			
			insertCount = pstmt2.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL 구문 오류 - insertReplyBoard() : " + e.getMessage());
		} finally {
			close(pstmt);
			close(rs);
		}
		
		return insertCount;
	}
	
	// 실제 업로드 된 파일명 조회
	public String selectRealFile(int board_num) {
		String realFile = "";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// 글번호가 일치하는 레코드의 실제 파일명(board_real_file) 조회
			String sql = "SELECT board_real_file FROM board WHERE board_num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, board_num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				realFile = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		
		return realFile;
	}
	
}











