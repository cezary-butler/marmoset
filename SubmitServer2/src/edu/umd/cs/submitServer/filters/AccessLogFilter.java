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

/*
 * Created on Mar 18, 2005
 */
package edu.umd.cs.submitServer.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.umd.cs.marmoset.modelClasses.Student;
import edu.umd.cs.submitServer.SubmitServerUtilities;
import edu.umd.cs.submitServer.UserSession;

/**
 * @author jspacco
 *
 */
public class AccessLogFilter extends SubmitServerFilter {
	/**
	 * A separate commons-logging Log (backed by a log4j Logger) for tracking
	 * accesses to each page.
	 */
	private Logger accessLog;

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpSession session = request.getSession();
		// Try to get the studentPK from the user's session
		UserSession userSession = (UserSession) session
				.getAttribute(USER_SESSION);
		@Student.PK Integer studentPK = null;
		if (userSession != null) {
			studentPK = userSession.getStudentPK();
		}
		// If there's no session, see if a previous filter set the Student
		// object
		// as a request attribute. Things mapped to the /eclipse prefix should
		// do this
		// except for NegotiateOneTimePassword, which handles its own logging.
		if (studentPK == null) {
			Student student = (Student) request.getAttribute("user");
			if (student != null)
				studentPK = student.getStudentPK();
		}

		accessLog.info("studentPK "
				+ ((studentPK != null) ? studentPK : " unknown ")
				+ " requesting " + SubmitServerUtilities.extractURL(request));

		chain.doFilter(request, response);
	}

	/**
	 * String constant that will be used for dropping log messages at the Root
	 * that were generated by calls to {@link #logAccess(request) logAccess}.
	 * <p>
	 * This is necessary because there seems to be no way to tell a log message
	 * NOT to propagate up.
	 */
	private static final String ACCESS_LOG = "ACCESS_LOG";

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		accessLog = Logger.getLogger(getClass());
	}
}
