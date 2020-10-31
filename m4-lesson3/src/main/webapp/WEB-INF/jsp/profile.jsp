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

        Authorities: <sec:authentication property="principal.authorities" />
        <br/>
        Authorities New: <sec:authentication property="authorities" />
        <br/>
        Name: <sec:authentication property="name" />
        <br/>
        <%-- Org: <sec:authentication property="principal.organization" />  --%>

        <sec:authentication property="principal.username" var="currentUserName" scope="page"/>
        <c:if test="${currentUserName.startsWith('u')}">
        <div>User name starts with 'u'</div>
        </c:if>
    </div>

</body>
</html>
