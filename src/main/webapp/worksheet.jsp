<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.nexttech.spanishreview.worksheet.WorksheetGenerator" %>
<%@ page import="com.nexttech.spanishreview.worksheet.Worksheet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.nexttech.spanishreview.utils.Utils" %>
<%@ page import="com.nexttech.spanishreview.database.MCPSStudent" %>
<%@ page import="org.codehaus.plexus.util.StringUtils" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page pageEncoding="UTF-8" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Spanish Review - Worksheet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>

    <!-- Stylesheets -->
    <link rel="stylesheet" href="/css/bootstrap.min.css" media="screen">
    <link rel="stylesheet" href="/css/custom.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/dragula/3.7.1/dragula.min.css">


    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

    <script src="/js/bootstrap.min.js"></script>

</head>
<body>
<!-- Top navbar -->
<div class="navbar navbar-default navbar-fixed-top top">
    <div class="container">
        <div class="navbar-header">
            <a href="../" class="navbar-brand">Spanish Review</a>
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
                <li>
                    <button onclick="submitWorksheet()" class="btn btn-success">Submit</button>
                </li>
                <%
                    // Gets the authentication handler from the Users API
                    UserService userService = UserServiceFactory.getUserService();
                    User user = userService.getCurrentUser();
                    if(userService.isUserAdmin() || !StringUtils.isNumeric(user.getEmail().substring(0, user.getEmail().indexOf("@")))
                            && user.getEmail().substring(user.getEmail().indexOf("@") + 1).equals("mcpsmd.net")) {
                        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/teacher"));
                        return;
                    }
                    if(user != null) { // Somebody is logged in
                        // Allows functions using the ${} syntax to use variable
                        pageContext.setAttribute("user", user);
                        MCPSStudent student = ofy().load().type(MCPSStudent.class).id(Long.parseLong(user.getEmail().substring(0, user.getEmail().indexOf("@")))).now();
                        pageContext.setAttribute("student", student);
                        if(student == null) {
                            System.out.println("Student was null");
                            student = new MCPSStudent(user.getEmail());
//                        ofy().save().entity(student).now();
                            System.out.println("Student was added to the database");
                        } else {
                            System.out.println("Student was not null, student is " + student.toString());
                        }
                %>
                <li><p class="nav navbar-text">Hi, <%=student.getName()%>!</p></li>
                <li><a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign Out</a></li>
                <script>
                    userName = "${user.email}";
                </script>
                <%
                    // In case of no MCPS account, opens modal with signout as action for all buttons
                    if(!user.getEmail().substring(user.getEmail().indexOf("@") + 1).equals("mcpsmd.net")) { %>
                <script>
                    $(function () {
                        setTimeout(function () {
                            $('#signin-failure').modal({keyboard: false})
                        }, 1000);
                    });
                </script>
                <% } else {
                %>
            </ul>

        </div>
    </div>
</div>

<br/>
<br/>
<br/>
<!-- The main page - worksheet and wordbank -->
<div class="row">
    <%
        WorksheetGenerator worksheetGenerator = new WorksheetGenerator();
        Worksheet ws;
        // Gets type of worksheet - blank or standard
        String typeOfWorksheet = "";
        try {
            typeOfWorksheet = ((String[]) request.getParameterMap().get("type"))[0];
        } catch(NullPointerException e) {
//            e.printStackTrace();
        }
        // Defaults to standard
        if(typeOfWorksheet == null || typeOfWorksheet.equals("blank")) {
            ws = worksheetGenerator.getBlankWorksheet();
        } else {
            ws = worksheetGenerator.getRegularWorksheet();
        }
        // Stores things in variables to not have to get a new worksheet every time
        String[][] wsArray = ws.getWorksheet();
//        System.out.println("Got worksheet");
        student.setLastWorksheet(wsArray);
//        System.out.println("Set last worksheet");
        ofy().save().entity(student).now();
        System.out.println("Saved student");
        List<String> wordBank = ws.getWordBank();
    %>
    <div class="col-sm-8" id="worksheet-container">
        <br/>
        <br/>
        <br/>
        <table class="table table-striped table-responsive table-bordered" id="worksheet-table">
            <tr>
                <%
                    // Header row
                    for(String item : wsArray[0]) { %>
                <th id="<%=Utils.getIDMap().get(item)%>"><%=item%>
                </th>
                <%}%>
            </tr>
            <%
                // Go through every other row in table
                for(int i = 1; i < wsArray.length; i++) {%>
            <tr class="worksheet-row">
                <% for(int j = 0; j < wsArray[i].length; j++) {
                    String id = "";
                    if(wsArray[i][j].equals("")) {
]                %>
                <td class="cell droppable"></td>
                <% } else {
                    try {
                        // Gets the numeric ID from the BiMap in Utils
                        id = Utils.getIDMap().get(wsArray[i][j]).toString();
                    } catch(Exception e) {
                        id = "";
                    }
                %>
                <!-- Makes colspan appropriate for rows that are smaller in number of columns -->
                <td class="cell" id="<%=id%>"
                    colspan="<%=wsArray[i].length == 3 && j == 2 ? 3 : (wsArray[i].length == 2 && j == 1 ? 4 : 1) %>">
                    <%=wsArray[i][j]%>
                </td>
                <%
                        }
                    }
                %>
            </tr>
            <%}%>
        </table>
    </div>
    <!-- Wordbank -->
    <div class="col-sm-4 right droppable" id="wordbank-container">
        <br/>
        <br/>
        <br/>
        <%
            // Goes through every word in wordBank, places them in a well
            for(String word : wordBank) {
                String id = Utils.getIDMap().get(word.replaceAll("\\p{C}", "").replaceAll("[^\\x00-\\x7F]", "")).toString();
        %>
        <div id="<%=id%>" class="well"><%=word%>
        </div>
        <%
                    }
                }
            }
        %>
    </div>
</div>

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
<%
//    System.out.println("Reached absolute end");
%>
<div class="modal fade" id="incomplete-worksheet" tabindex="-1" role="dialog"
     aria-labelledby="incomplete-worksheet-label">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="incomplete-worksheet-label">Incomplete worksheet!</h4>
            </div>
            <div class="modal-body">
                You tried to submit this worksheet, but it is incomplete. Please finish the worksheet.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="submission-failure" tabindex="-1" role="dialog"
     aria-labelledby="submission-failure-label">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="submission-failure-label">Submission failure!</h4>
            </div>
            <div class="modal-body">
                You tried to submit this worksheet, but it didn't go through on the server. If you did nothing wrong,
                check your internet connection and submit again. If this does not work, please contact your teacher.
                If you tried to mess with something that caused this issue, please stop.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


<script src='https://cdnjs.cloudflare.com/ajax/libs/dragula/3.7.1/dragula.min.js'></script>
<script type="text/javascript"
        src="https://cdnjs.cloudflare.com/ajax/libs/jquery-json/2.5.1/jquery.json.min.js"></script>
<script src="/js/worksheet.min.js"></script>

</body>
</html>
