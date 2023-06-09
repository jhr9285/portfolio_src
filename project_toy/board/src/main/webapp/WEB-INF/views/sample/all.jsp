<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
    
<!DOCTYPE html PUBLIC
"-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd" >

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>All</title>
</head>
<body>
	<h1>All Page</h1>
	
	<sec:authorize access="isAnonymous()"> <!-- isAnonumous() : 로그인을 하지 않은 경우 true 반환 -->
		<a href="/customLogin">Login</a> <!-- 로그인 링크 안내 -->
	</sec:authorize>
	
	<sec:authorize access="isAuthenticated()"> <!-- isAuthenticated() : 로그인 한 경우 true 반환 -->
		<a href="/customLogout">Logout</a> <!-- 로그아웃 링크 안내 -->
	</sec:authorize>

</body>
</html>