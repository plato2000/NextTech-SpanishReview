<%--
  Created by IntelliJ IDEA.
  User: plato2000
  Date: 5/15/16
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.nexttech.spanishreview.database.MCPSStudent" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.codehaus.plexus.util.StringUtils" %>
<%@ page import="com.nexttech.spanishreview.worksheet.WorksheetGrader" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Spanish Review</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <!-- Stylesheets -->
    <link rel="stylesheet" href="/css/bootstrap.min.css" media="screen">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

    <script src="/js/bootstrap.min.js"></script>
    <!-- Google sign in stuff -->
    <%--<script src="https://apis.google.com/js/client:platform.js?onload=start" async defer></script>--%>
    <%--<meta name="google-signin-client_id" content="453755821502-1k95kijujmdh4g16opd1qpaqn6miboro.apps.googleusercontent.com">--%>
</head>
<body>
    <div class="navbar navbar-default navbar-fixed-top top">
        <div class="container">
            <div class="navbar-header">
                <a href="/" class="navbar-brand">Spanish Review</a>
                <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            </div>
            <div class="navbar-collapse collapse" id="navbar-main">
                <ul class="nav navbar-nav">

                </ul>

                <ul class="nav navbar-nav navbar-right">
                    <%
                        // Gets the authentication handler from the Users API
                        UserService userService = UserServiceFactory.getUserService();
                        User user = userService.getCurrentUser();

                        // If user is admin (teacher), redirect to teacher page
                        if(userService.isUserAdmin() || !StringUtils.isNumeric(user.getEmail().substring(0, user.getEmail().indexOf("@")))
                                && user.getEmail().substring(user.getEmail().indexOf("@") + 1).equals("mcpsmd.net")){
                            // Encoded means the cookie is sent w/ it for authorization on HTTP requests
                            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/teacher"));
//                            response.sendRedirect(response.encodeRedirectURL(userService.createLogoutURL(request.getRequestURI())));
                            return;
                        }

                        // Load in student from DataStore
                        // ofy() is the objectify service, which asks for the type of object to pull (MCPSStudent.class)
                        // The id is the MCPS student ID, which can be gotten from the email by splitting at the @ symbol
                        // If this changes at some point, it will break the entire program
                        MCPSStudent student = ofy().load().type(MCPSStudent.class).id(Long.parseLong(user.getEmail().substring(0, user.getEmail().indexOf("@")))).now();
                        // If student was not in database, it will be null
                        if(student == null) {
                            // Create a new student given just the email
                            student = new MCPSStudent(user.getEmail());
                            System.out.println("Saving entity in index.jsp too");
                            // Save student into database so that it exists there now
                            ofy().save().entity(student).now();
                        }

                        // Allows functions using the ${} syntax to use variable
                        pageContext.setAttribute("user", user);
                    %>
                    <li><p class="nav navbar-text">Hi, <%=student.getName()%>!</p></li>
                    <li><a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign Out</a></li>
                    <%
                        // In case of no MCPS account, opens modal with signout as action for all buttons
                        if(!user.getEmail().substring(user.getEmail().indexOf("@")).equals("@mcpsmd.net")) { %>
                    <script>
                        $(function(){
                            // Opens the modal for signin failures
                            setTimeout(function() {$('#signin-failure').appendTo("body").modal({keyboard:false});}, 100);
                        });
                    </script>
                    <%
                        // In case of MCPS account, open modal with info and options to get a worksheet
                        } else {
                    %>
                    <script>
                        $(function(){
                            // Opens modal with options for new worksheet
                            setTimeout(function() {$('#normal-modal').appendTo("body").modal({keyboard:false})}, 100);
                        });
                    </script>
                </ul>

            </div>
        </div>
    </div>

    <br />
    <br />
    <br />

    <!-- The modal dialog that opens on a normal user (MCPS student) -->
    <div class="modal fade" id="normal-modal" role="dialog">
        <div class="modal-dialog">
            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">Hi, <%=student.getName()%>!</h4>
                </div>
                <div class="modal-body">
                    <%
                        String score = "";
                        try {
                            // Gets score on last worksheet from URL (if the user just came from a worksheet)
                            score = ((String[]) request.getParameterMap().get("score"))[0];
                        } catch(NullPointerException e) {

                        }
                        // If score exists or is not blank (if it was just gotten through the parameterMap, display it
                        if(!(score == null || score.equals(""))) {
                    %>
                    <h1>Score: <%=score%>/40</h1>
                    <%
                        }
                        // If a deadline has been set, get the information for it and display
                        if(student.getDeadline() > 0) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(student.getDeadline());

                            int mYear = calendar.get(Calendar.YEAR);
                            int mMonth = calendar.get(Calendar.MONTH);
                            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    %>
                    <h2>Deadline: <%=mMonth + "/" + mDay + "/" + mYear%></h2>
                    <%
                        }
                        // If a teacher has been set (teacher logs in, gets users through classroom, presses submit)
                        // display teacher name
                        if(!student.getTeacher().equals("")) {
                    %>
                    <h2>Teacher: <%=student.getTeacher()%></h2>
                    <%
                        }
                        // For worksheet counts: display the number of worksheets completed. If the teacher has
                        // logged in, the required totals have a value, so display them.
                        String overallWorksheets = "Total Worksheets: " + student.getOverallWorksheets() + " completed";
                        if(!student.getTeacher().equals("")) {
                            overallWorksheets += " out of " + student.getRequiredTotal() + " required";
                        }
                    %>
                    <p><%=overallWorksheets%></p>
                    <%
                        String blankWorksheets = "Blank Worksheets: " + student.getBlankWorksheets() + " completed";
                        if(!student.getTeacher().equals("")) {
                            blankWorksheets += " out of " + student.getRequiredBlank() + " required";
                        }
                    %>
                    <p><%=blankWorksheets%></p>
                    <%
                        String overScore = "Worksheets Over " + WorksheetGrader.getThresholdScore() + "/40: " + student.getOverScore() + " completed";
                        if(!student.getTeacher().equals("")) {
                            overScore += " out of " + student.getRequiredOverScore() + " required";
                        }
                    %>
                    <p><%=overScore%></p>

                </div>
                <!-- Buttons to get the two types of worksheets -->
                <div class="modal-footer">
                    <a class="btn btn-default" href="javascript:blankSheet()">Blank Worksheet</a>
                    <a class="btn btn-default" href="javascript:normalSheet()">Normal Worksheet</a>
                </div>
            </div>

        </div>
    </div>
    <%
        }
    %>

    <!-- Error modal dialog for signin failures -->
    <div class="modal fade" id="signin-failure" role="dialog">
        <div class="modal-dialog">

            <!-- Modal content-->
            <div class="modal-content">
                <div class="modal-header">
                    <a href="<%=userService.createLogoutURL(request.getRequestURI())%>" class="close">&times;</a>
                    <h4 class="modal-title">Could not sign in.</h4>
                </div>
                <div class="modal-body">
                    <p>Sign in failed. Make sure you're signing in with your mcpsmd.net Google account. Your data will not
                        be seen by your teacher unless you do this.</p>
                </div>
                <div class="modal-footer">
                    <a href="<%=userService.createLogoutURL(request.getRequestURI())%>" class="btn btn-default">Close</a>
                </div>
            </div>

        </div>
    </div>

    <script src="/js/index.min.js"></script>

</body>
</html>
