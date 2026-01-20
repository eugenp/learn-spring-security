<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
<title>Layout</title>

<link type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet" />

</head>
<body>
    <div class="container">
        <div class="navbar">
            <div class="navbar-inner">
                <sec:authorize access="hasRole('ROLE_ADMIN')">
		           <a class="brand" href="/"> Admins Home </a>
		        </sec:authorize>
		         
		        <sec:authorize access="hasRole('ROLE_USER')">
		            <a class="brand" href="/"> Users Home </a>
		        </sec:authorize>
		        <!--  -->
		        
                <ul class="nav">
                    <li><a href="${pageContext.request.contextPath}/user"> Users </a></li>
                </ul>
                <ul class="nav pull-right">
                    <li><span class="navbar-text">Hi, <sec:authentication property="principal.username" /></span></li>
                    <li><a href="${pageContext.request.contextPath}/logout" class="menu-right" > Logout </a></li>
                </ul>
            </div>
        </div>
        <!-- Content goes here -->
    </div>
</body>
</html>
