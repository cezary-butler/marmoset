package edu.umd.review.gwt.rpc.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.umd.cs.marmoset.modelClasses.CodeReviewer;
import edu.umd.cs.marmoset.modelClasses.Submission;

/**
 * Data object for transferring information about the logger-in user.
 *
 * @author rwsims@umd.edu (Ryan W Sims)
 */
public class ReviewerDto implements IsSerializable {
  private String username;
  private String key;
  private String headerTitle;
  private String headerBackUrl;
  private String headerBackLinkText;
  private @CodeReviewer.PK int codeReviewerPK;
  private @Submission.PK int submissionPK;
  private boolean isAuthor = false;

  /** @deprecated GWT use only. */
  @Deprecated
  @SuppressWarnings("unused")
  private ReviewerDto() {
    this.username = "0xdeadbeef";
    this.key = "0xdeadbeef";
  }

  /**
   * @deprecated Use {@link #ReviewerDto(String,int,int,String)} instead
   */
  public ReviewerDto(String username, String key) {
    this(username, 0xdeadbeef, 0xdeadbeef, key);
  }

  public ReviewerDto(String username, @CodeReviewer.PK int codeReviewerPK, @Submission.PK int submissionPK, String key) {
    this.username = username;
    this.key = key;
    this.codeReviewerPK = codeReviewerPK;
    this.submissionPK = submissionPK;
  }

  public void setHeaderInfo(String title, String backUrl, String backText) {
    this.headerTitle = title;
    this.headerBackUrl = backUrl;
    this.headerBackLinkText = backText;
  }

  public String getHeaderBackLinkText() {
    return headerBackLinkText;
  }

  public String getHeaderBackUrl() {
    return headerBackUrl;
  }

  public String getHeaderTitle() {
    return headerTitle;
  }

  public boolean isAuthor() {
    return isAuthor;
  }

  public void setAuthor(boolean isAuthor) {
    this.isAuthor = isAuthor;
  }

  public String getUsername() {
    return username;
  }

  /**
   * Return the auth token that identifies this reviewer object. Should be the same as the URL
   * parameter passed to the module.
   */
  public String getKey() {
    return this.key;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ReviewerDto)) {
      return false;
    }
    return username.equals(((ReviewerDto) obj).username);
  }

  @Override
  public int hashCode() {
    return username.hashCode();
  }

  @Override
  public String toString() {
    return "REVIEWER " + username;
  }

  public @CodeReviewer.PK int getCodeReviewerPK() {
    return codeReviewerPK;
  }

  public @Submission.PK int getSubmissionPK() {
    return submissionPK;
  }
}
