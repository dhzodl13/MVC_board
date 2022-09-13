package action;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import svc.BoardModifyProService;
import vo.ActionForward;
import vo.BoardDTO;

public class BoardModifyProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("BoardModifyProAction");
		
		ActionForward forward = null;
		
		// 파라미터 가져와서 변수에 저장
		BoardDTO board = new BoardDTO();
//		board.setBoard_num(Integer.parseInt(request.getParameter("board_num")));
//		board.setBoard_name(request.getParameter("board_name"));
//		board.setBoard_pass(request.getParameter("board_pass"));
//		board.setBoard_subject(request.getParameter("board_subject"));
//		board.setBoard_content(request.getParameter("board_content"));
		// --------- 파일 수정 기능 추가로 인한 MultipartRequest 객체 사용 -----------
		String uploadPath = "upload"; // 가상의 폴더명
		int fileSize = 1024 * 1024 * 10; // byte(1) -> KB(1024Byte) -> MB(1024KB) -> 10MB 단위 변환
		String realPath = request.getServletContext().getRealPath(uploadPath); // 가상의 업로드 폴더명을 파라미터로 전달
		
		// MultipartRequest 객체 생성
		MultipartRequest multi = new MultipartRequest(
			request, // 1) 실제 요청 정보가 포함된 request 객체
			realPath, // 2) 실제 업로드 폴더 경로
			fileSize, // 3) 업로드 파일 크기
			"UTF-8", // 4) 파일명에 대한 인코딩 방식(한글 처리 등이 필요하므로 UTF-8 지정)
			new DefaultFileRenamePolicy() // 5) 중복 파일명에 대한 처리를 담당하는 객체(파일명 뒤에 숫자 1 부터 차례대로 부여)
		);
		
		board.setBoard_num(Integer.parseInt(multi.getParameter("board_num")));
		board.setBoard_name(multi.getParameter("board_name"));
		board.setBoard_pass(multi.getParameter("board_pass"));
		board.setBoard_subject(multi.getParameter("board_subject"));
		board.setBoard_content(multi.getParameter("board_content"));
		// 주의! 업로드 파일을 선택하지 않았을 경우 null 값이 전달됨
		// => 따라서, NOT NULL 제약조건 때문에 수정이 불가능하게 되므로 업데이트 대상에서 제외 
		board.setBoard_file(multi.getOriginalFileName("board_file")); // 원본 파일명
		board.setBoard_real_file(multi.getFilesystemName("board_file")); // 실제 업로드 파일명
		// ---------------------------------------------------------------------------
//		System.out.println(board);
		
		// 게시물 수정 권한 판별을 위해 전달받은 파라미터 중 패스워드 비교
		// => BoardModifyProService 의 isBoardWriter() 메서드를 호출
		//    파라미터 : 글번호, 패스워드    리턴타입 : boolean(isBoardWriter)
		// => 작업 내용은 BoardDeleteProService 의 isBoardWriter() 와 동일
		BoardModifyProService service = new BoardModifyProService();
		boolean isBoardWriter = service.isBoardWriter(board.getBoard_num(), board.getBoard_pass());
		
		// 수정 가능 여부 판별(isBoardWriter 변수값 판별)
		// => 패스워드가 일치하지 않았을 경우(= isBoardWriter 가 false)
		//    자바스크립트를 사용하여 "수정 권한 없음" 출력 후 이전페이지로 돌아가기
		// => 아니면, "수정 권한 있음" 출력
		if(!isBoardWriter) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('수정 권한 없음!')");
			out.println("history.back()");
			out.println("</script>");
		} else { // 패스워드가 일치할 경우
			// BoardModifyProService 의 modifyBoard() 메서드 호출하여 글수정 작업 요청
			// => 파라미터 : BoardDTO 객체    리턴타입 : boolean(isModifySuccess)
			boolean isModifySuccess = service.modifyBoard(board);
			
			// 글 수정 작업 결과 판별
			// 실패 시 자바스크립트를 사용하여 "글 수정 실패!" 출력 후 이전페이지로 돌아가기
			// 성공 시 ActionForward 객체 생성하여 BoardDetail.bo 페이지 요청
			// => 파라미터 : 글번호, 페이지번호
			if(!isModifySuccess) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('글 수정 실패!')");
				out.println("history.back()");
				out.println("</script>");
			} else {
				// ------------- 기존 업로드 파일 삭제 작업 추가 ------------
				File f = new File(realPath, multi.getParameter("board_real_file"));
				
				if(f.exists()) {
					f.delete();
				}
				// ----------------------------------------------------------
				
				forward = new ActionForward();
				forward.setPath("BoardDetail.bo?board_num=" + board.getBoard_num() + "&pageNum=" + multi.getParameter("pageNum"));
				forward.setRedirect(true);
			}
		}
		
		return forward;
	}

}













