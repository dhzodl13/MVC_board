<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	function confirmLogout() {
		if(confirm("로그아웃 하시겠습니까?")) { // 확인 : true, 취소 : false 리턴됨
			// MemberLogout.me 포워딩
			location.href = "MemberLogout.me";
		}
	}
</script>
</head>
<body>
	<header>
		<c:choose>
			<c:when test="${empty sessionScope.sId}">
				<a href="MemberLoginForm.me">로그인</a> | <a href="MemberJoinForm.me">회원가입</a>
			</c:when>
			<c:otherwise>
				<%-- 하이퍼링크에 자바스크립트 함수 연결 시 href 속성에 아무 경로도 지정하지 않는 방법 --%>
				${sessionScope.sId } 님 | <a href="javascript:void(0)" onclick="confirmLogout()">로그아웃</a>
				<%-- 세션 아이디가 "admin" 일 때만 관리자페이지 링크("AdminMain.me") 표시 --%>
				<c:if test="${sessionScope.sId eq 'admin'}"> | <a href="AdminMain.me">관리자페이지</a></c:if>
			</c:otherwise>
		</c:choose>
	</header>
	<h1>MVC 게시판 프로젝트</h1>
	<h3><a href="BoardWriteForm.bo">글쓰기</a></h3>
	<h3><a href="BoardList.bo">글목록</a></h3>
</body>
</html>










