<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.syos.domain.model.User" %>
<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = (String) session.getAttribute("userRole");
    if ("ADMIN".equals(userRole) || "STAFF".equals(userRole)) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        return;
    }

    request.setAttribute("pageTitle", "My Orders");
%>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="row">
    <div class="col-12">
        <h2 class="mb-4">My Order History</h2>

        <%-- Error/Success messages --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= error %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <div class="text-center py-5">
                    <i class="fas fa-receipt fa-3x text-muted mb-3"></i>
                    <h4 class="text-muted">No orders yet</h4>
                    <p class="text-muted mb-4">You haven't placed any orders yet. Start shopping to see your order history here.</p>
                    <a href="<%= request.getContextPath() %>/products.jsp" class="btn btn-primary">Start Shopping</a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>