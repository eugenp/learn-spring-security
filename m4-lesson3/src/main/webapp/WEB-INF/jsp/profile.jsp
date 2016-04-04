<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Profile</title>
</head>
<body>
    <jsp:include page="layout.jsp" />
    <div class="container">
        <h1>Profile</h1>
    </div>
    
    <div class="container">
        <!--  authentication tag-->
        Current user name: <sec:authentication property="principal.username" /><br/>
        Current user name: <sec:authentication property="name" /><br/>
        <br/>
        Current user roles: <sec:authentication property="principal.authorities" /><br/>
        Current user roles: <sec:authentication property="authorities" /><br/>
        <br/>
        Current user organization : <sec:authentication property="principal.organization" />
        <br/>
        <!--  -->
        <sec:authentication property="principal.username" var="currentUserName" scope="page"/>
        <c:if test="${currentUserName.startsWith('u')}">
        <div>User name starts with 'u'</div>
        </c:if>
        
        <hr/>
        <!-- authorize tag -->
        <sec:authorize access="hasRole('ROLE_ADMIN')">
		    Only admins can see this message
		</sec:authorize>
		 
		<sec:authorize access="hasRole('ROLE_USER')">
		    Only users can see this message
		</sec:authorize>
		
		<hr/>
		
		<sec:authorize url="/user/delete/1">
		    Only users allowed to call the "/user/delete/1" URL can see this message
		</sec:authorize>
		
		<sec:authorize url="/user/delete/1" method="POST">
            Only users allowed to call POST "/user/delete/1" URL can see this message
        </sec:authorize>
        
        <hr/>
        
        <sec:authorize access="hasRole('ROLE_ADMIN')" var="isAdmin"/>
        <c:if test="${isAdmin}">
        <div>Current user is admin</div>
        </c:if>
        
    </div>
</body>
</html>
