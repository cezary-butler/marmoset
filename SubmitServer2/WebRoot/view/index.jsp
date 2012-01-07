<%--

 Marmoset: a student project snapshot, submission, testing and code review
 system developed by the Univ. of Maryland, College Park
 
 Developed as part of Jaime Spacco's Ph.D. thesis work, continuing effort led
 by William Pugh. See http://marmoset.cs.umd.edu/
 
 Copyright 2005 - 2011, Univ. of Maryland
 
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy of
 the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.

--%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ss" uri="http://www.cs.umd.edu/marmoset/ss"%>
<c:if test="${true}">
<c:if test="${singleCourse && instructorCapability && !initParam['demo.server']=='true'}">
	<c:redirect url="/view/instructor/course.jsp">
		<c:param name="coursePK" value="${courseList[0].coursePK}" />
	</c:redirect>
</c:if>
<c:if test="${singleCourse}">
	<c:redirect url="/view/course.jsp">
		<c:param name="coursePK" value="${courseList[0].coursePK}" />
	</c:redirect>
</c:if>
</c:if>
<!DOCTYPE HTML>
<html>

<ss:head title="Submit Server Home Page" />

<body>
<ss:header />
<ss:breadCrumb />

<div class="sectionTitle">
	<h1>Home</h1>
	<p class="sectionDescription">Welcome ${user.firstname}</p>
</div>

<p>
<h2>Courses</h2>
<ul>
<c:set var="statusMap" value="${userSession.instructorStatus}"/>
	<c:forEach var="course" items="${courseList}">
		<c:choose>
			<c:when
				test="${user.superUser || statusMap[course.coursePK]}">
				<c:set var="courseURL" value="/view/instructor/course.jsp" />
				<li style="list-style-type:circle">
			</c:when>
			<c:otherwise>
			    <li>
				<c:set var="courseURL" value="/view/course.jsp" />
			</c:otherwise>
		</c:choose>
		<c:url var="courseLink" value="${courseURL}">
			<c:param name="coursePK" value="${course.coursePK}" />
		</c:url> <a href="${courseLink}">
		<c:out value="${course.courseName}"/><c:if test="${not empty course.section}"><c:out value="${course.section}"/></c:if>:
		<c:out value="${course.description}"/> </a>
	</c:forEach>
    
	<c:if test="${student.canImportCourses}">
    <c:choose>
    <c:when test="${grades.server}">
	<c:url var="importCourseLink" value="/view/import/importCourse.jsp"/>
	<li><a href="${importCourseLink}">Import course from grade server</a>
    </li>
    </c:when>
    <c:otherwise>
    <c:url var="createCourseLink" value="/view/instructor/createCourse.jsp"/>
    <li><a href="${createCourseLink}">Create course</a>
    </li>
    </c:otherwise>
    </c:choose>
    <c:url var="buildServerConfigLink" value="/view/instructor/createBuildserverConfig.jsp"/>
     <li><a href="${buildServerConfigLink}">Generate buildserver config file</a>
	</c:if>
</ul>


<ss:footer />
</body>
</html>
