<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
<title>Users : View</title>
</head>
<body>
    <jsp:include page="../layout.jsp" />

    <div class="container">
        <h1>Users : Create</h1>
    </div>
    
    <div class="container">
        <c:if test="${globalMessage}">
        <div class="alert alert-success"><c:out value="globalMessage"/></div>
        </c:if>
        
        <div class="pull-right">
            <a href="${pageContext.request.contextPath}/user"> Users </a>
        </div>
        <dl>
            <dt>ID</dt>
            <dd id="id">${user.id}</dd>
            <dt>Date</dt>
            <dd id="created"><fmt:formatDate type="both" dateStyle="long" timeStyle="long" value="${user.created.time}" /></dd>
            <dt>Email</dt>
            <dd id="email">${user.email}</dd>
        </dl>
        <div class="pull-left">
            <a href="${pageContext.request.contextPath}/user/delete/${user.id}"> delete </a> | <a href="${pageContext.request.contextPath}/user/modify/${user.id}"> modify </a>
        </div>
    </div>
</body>
</html>
