package com.loan.origination.system.mcpserver.assessment.tools;

import com.loan.origination.system.contracts.domain.common.DecisionResult;
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

  @McpTool(
      description =
          "Final Step: Evaluates all gathered data including requested loan amount against lending policies.")
  public DecisionResult runRuleEngine(
      @ToolParam(description = "The requested loan amount") double loanAmount,
      @ToolParam(description = "The identity verification status") String identityStatus,
      @ToolParam(description = "The applicant's credit score") int creditScore,
      @ToolParam(description = "The monthly gross income") double income,
      @ToolParam(description = "The appraised property value") double propertyValue) {

    LOG.info(
        "AI Tool [runRuleEngine] evaluating: Loan={}, Identity={}, Credit={}, Income={}, Property={}",
        loanAmount,
        identityStatus,
        creditScore,
        income,
        propertyValue);

    // 1. Identity Check
    if (!"VERIFIED".equalsIgnoreCase(identityStatus)) {
      LOG.warn("Decision: REJECTED - Identity verification failed");
      return new DecisionResult("REJECTED", "Identity could not be verified.");
    }

    // 2. Credit Score Check
    if (creditScore < 640) {
      LOG.warn("Decision: REJECTED - Credit score {} below 640", creditScore);
      return new DecisionResult("REJECTED", "Credit score is below the minimum requirement.");
    }

    // 3. LTV (Loan-to-Value) Check
    double ltv = (loanAmount / propertyValue) * 100;
    if (ltv > 90.0) {
      LOG.warn("Decision: REJECTED - LTV too high: {}%", ltv);
      return new DecisionResult(
          "REJECTED", "Loan-to-Value ratio exceeds 90%. Higher down payment required.");
    }

    // 4. DTI (Debt-to-Income) / Affordability Check
    double estimatedAnnualPayment = loanAmount * 0.10;
    double annualIncome = income * 12;

    if (annualIncome < (estimatedAnnualPayment * 3)) {
      LOG.warn("Decision: REJECTED - Insufficient income for loan amount {}", loanAmount);
      return new DecisionResult(
          "REJECTED", "Income is insufficient to safely cover the requested loan payments.");
    }

    LOG.info("Decision: APPROVED for loan amount {}", loanAmount);
    return new DecisionResult(
        "APPROVED", "Applicant meets all lending criteria for the requested amount.");
  }
}
