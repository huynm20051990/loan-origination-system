package com.loan.origination.system.mcpserver.assessment.tools;

import com.loan.origination.system.util.encryption.EncryptionUtils;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class AssessmentTools {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentTools.class);

  @McpTool(
      description =
          "Step 1: Verify the identity of the applicant using their secure SSN. Returns 'VERIFIED' or 'UNABLE_TO_VERIFY'.")
  public String checkIdentity(
      @ToolParam(description = "The encrypted Social Security Number of the applicant")
          String secureSsn) {

    String ssn = EncryptionUtils.decryptQuietly(secureSsn);
    LOG.info(
        "AI Tool [checkIdentity] called for SSN: ***-**-{}",
        ssn.substring(Math.max(0, ssn.length() - 4)));

    // Logic: Integration with Identity Provider
    return "VERIFIED";
  }

  @McpTool(
      description =
          "Step 2: Retrieve the credit score for the applicant based on their secure SSN.")
  public int getCreditScore(
      @ToolParam(description = "The encrypted Social Security Number of the applicant")
          String secureSsn) {

    String ssn = EncryptionUtils.decryptQuietly(secureSsn);
    LOG.info(
        "AI Tool [getCreditScore] called for SSN: ***-**-{}",
        ssn.substring(Math.max(0, ssn.length() - 4)));

    // Logic: Integration with Credit Bureaus
    return 720;
  }

  @McpTool(description = "Step 3: Retrieve verified financial data based on the secure SSN.")
  public Map<String, Object> getFinancials(
      @ToolParam(description = "The encrypted Social Security Number of the applicant")
          String secureSsn) {

    String ssn = EncryptionUtils.decryptQuietly(secureSsn);
    LOG.info(
        "AI Tool [getFinancials] called for SSN: ***-**-{}",
        ssn.substring(Math.max(0, ssn.length() - 4)));

    return Map.of(
        "income", 8500.0,
        "employer", "TechCorp",
        "status", "Active");
  }

  @McpTool(
      description = "Step 4: Calculate the estimated market value for a given property address.")
  public double evaluateProperty(
      @ToolParam(description = "The full physical address of the property") String address) {
    LOG.info("AI Tool [evaluateProperty] called for address: {}", address);
    return 550000.0;
  }
}
