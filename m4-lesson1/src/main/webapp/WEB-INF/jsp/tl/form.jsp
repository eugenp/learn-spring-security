<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<title>Users : Create</title>
</head>
<body>
    <jsp:include page="../layout.jsp" />
    <div class="container">
        <h1>Users : Create</h1>
    </div>
    
    <div class="container">
        <form:form modelAttribute="user" id="userForm" action="${pageContext.request.contextPath}/user?form" method="post">
            <form:errors path="*" class="alert alert-error" />
            
            <div class="pull-right">
                <a href="${pageContext.request.contextPath}/user"> Users </a>
            </div>
            <form:input path="id" value="" type="hidden" />
            
            <label for="email">Email</label>
            <form:input path="email" value="" />
            
            <label for="password">Password</label>
            <form:input path="password" value="" />
            
            <label for="passwordConfirmation">Password Confirmation</label>
            <form:input path="passwordConfirmation" value="" />
            
            <div class="form-actions">
                <input type="submit" value="Save" />
            </div>
        </form:form>
    </div>
</body>
</html>
