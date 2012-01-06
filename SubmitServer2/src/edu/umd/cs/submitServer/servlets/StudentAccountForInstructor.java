/**
 * Marmoset: a student project snapshot, submission, testing and code review
 * system developed by the Univ. of Maryland, College Park
 * 
 * Developed as part of Jaime Spacco's Ph.D. thesis work, continuing effort led
 * by William Pugh. See http://marmoset.cs.umd.edu/
 * 
 * Copyright 2005 - 2011, Univ. of Maryland
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package edu.umd.cs.submitServer.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.umd.cs.marmoset.modelClasses.Course;
import edu.umd.cs.marmoset.modelClasses.Student;
import edu.umd.cs.marmoset.modelClasses.StudentRegistration;

public class StudentAccountForInstructor extends SubmitServerServlet {


	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to
	 * post.
	 *
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection conn = null;
		Course course = (Course) request.getAttribute(COURSE);
		Student user = (Student) request.getAttribute(USER);
		Student student = (Student) request.getAttribute(STUDENT);
		StudentRegistration instructor = (StudentRegistration) request.getAttribute(STUDENT_REGISTRATION);
		if (instructor.getStudentPK() != user.getStudentPK())
			throw new IllegalArgumentException();
		if (!instructor.isInstructorModifiy())
			throw new IllegalArgumentException();
		

		try {
			conn = getConnection();

			Student student2 = new Student();
			student2.setLastname(student.getLastname());
			student2.setFirstname(student.getFirstname());
			student2.setCampusUID(student.getCampusUID());
			student2.setLoginName(student.getLoginName()+ "-student");
			student2 = student2.insertOrUpdateCheckingLoginNameAndCampusUID(conn);

			StudentRegistration registration = StudentRegistration
					.lookupByStudentPKAndCoursePK(student2.getStudentPK(),
							course.getCoursePK(), conn);
			if (registration == null) {

				registration = new StudentRegistration();
				registration.setStudentPK(student2.getStudentPK());
				registration.setCoursePK(course.getCoursePK());
				registration.setClassAccount(instructor.getClassAccount()
						+ "-student");

				registration.setInstructorCapability(StudentRegistration.PSEUDO_STUDENT_CAPABILITY);
				registration.setFirstname(student2.getFirstname());
				registration.setLastname(student2.getLastname());
				registration.setCourse(course.getCourseName());
				registration.setSection(course.getSection());
				registration.setCourseID(-1);

				registration.insert(conn);	
			}
			HttpSession session = request.getSession(false);
			
			session.invalidate();
			session = request.getSession(true);
		
			PerformLogin.setUserSession(session, student2, conn);
			String redirectUrl = request.getContextPath()
					+ "/view/course.jsp?coursePK="
					+ course.getCoursePK();

			response.sendRedirect(redirectUrl);
		} catch (SQLException e) {
			throw new ServletException(e);
		} finally {
			releaseConnection(conn);
			
		}

	}


	private void importStudents(String term, Course course, int courseID,
			Connection gradesConn, Connection conn) throws SQLException {
		String query = "SELECT lastName, firstName, uid, directoryID, email, classAccount, role, course, section"
				+ " FROM submitexport "
				+ " WHERE term = ?"
				+ " AND courseID = ?";
		PreparedStatement stmt = gradesConn.prepareStatement(query);
		stmt.setString(1, term);
		stmt.setInt(2, courseID);
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			int col = 1;
			String lastname = rs.getString(col++);
			String firstname = rs.getString(col++);
			String campusUID = rs.getString(col++);
			String loginName = rs.getString(col++);
			String email = rs.getString(col++);
			String classAccount = rs.getString(col++);
			String role = rs.getString(col++);
			String courseName = rs.getString(col++);
			String sectionName = rs.getString(col++);

			Student s =  Student.insertOrUpdateByUID(
					campusUID,
					 firstname,
					 lastname,
					 loginName,
					null, conn);

			StudentRegistration registration =
				StudentRegistration.lookupByStudentPKAndCoursePK(s.getStudentPK(), course.getCoursePK(), conn);

			if (registration == null) {

					registration = new StudentRegistration();
					registration.setCoursePK(course.getCoursePK());
					if (classAccount != null)
						registration.setClassAccount(classAccount);
					else
						registration.setClassAccount(loginName);

					registration.setStudentPK(s.getStudentPK());
					if ("Instructor".equals(role)
							|| "TA".equals(role))
						registration
								.setInstructorCapability(StudentRegistration.MODIFY_CAPABILITY);
					else if ("Grader".equals(role))

						registration.setInstructorCapability(StudentRegistration.READ_ONLY_CAPABILITY);
					else registration.setInstructorCapability(null);
					registration.setFirstname(s.getFirstname());
					registration.setLastname(s.getLastname());
					registration.setCourse(courseName);
					registration.setSection(sectionName);
					registration.setCourseID(courseID);
					registration.insert(conn);

			}

		}
	}


	public static void foo(String term, String courseId, Connection conn) throws SQLException {
		String query = "SELECT DISTINCT lastName, firstName, uid, directoryID, role"
			+ " FROM submitexport "
			+ " WHERE term = ?"
			+ " AND courseId = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, term);
		stmt.setString(2, courseId);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			String lastname = rs.getString(1);
			String firstname = rs.getString(2);
			String campusUID = rs.getString(3);
			String loginName = rs.getString(4);
			String role = rs.getString(5);

			Student s = Student.insertOrUpdateByUID(campusUID, firstname, lastname, loginName, null, conn);

		}



	}

}
