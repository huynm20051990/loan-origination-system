package com.loan.origination.system.mcpserver.assessment.tools;

import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class AssessmentTools {

  @Tool(
      description =
          "Step 1: Verify the identity of the applicant. Returns 'VERIFIED' or 'REJECTED'.")
  public String checkIdentity(
      @ToolParam(description = "The unique identifier for the applicant") String appNum) {
    // Logic: Integration with Identity Provider (e.g., Onfido)
    return "VERIFIED";
  }

  @Tool(
      description =
          "Step 2: Retrieve verified financial data including monthly income and employment status.")
  public Map<String, Object> getFinancials(
      @ToolParam(description = "The unique identifier for the applicant") String appNum) {
    // Logic: Integration with Payroll/Banking API
    return Map.of(
        "income", 8500.0,
        "employer", "TechCorp",
        "status", "Active");
  }

  @Tool(description = "Step 3: Calculate the estimated market value for a given property address.")
  public double evaluateProperty(
      @ToolParam(description = "The full physical address of the property") String address) {
    // Logic: Integration with Real Estate Appraisal Service
    return 550000.0;
  }

  @Tool(
      description =
          "Final Step: Evaluates income and property value against lending policies. Returns 'POLICY_PASS' or failure reason.")
  public String runRuleEngine(
      @ToolParam(description = "The monthly gross income of the applicant") double income,
      @ToolParam(description = "The appraised value of the property") double propertyValue) {
    // Logic: Business Rule Engine (BRE)
    return (income * 50 > propertyValue) ? "POLICY_PASS" : "POLICY_FAIL_INSUFFICIENT_INCOME";
  }
}
