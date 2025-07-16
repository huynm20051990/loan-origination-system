package com.loan.origination.system.api.composite.product;

public class ServiceAddresses {
  private final String cmp;
  private final String pro;
  private final String rat;
  private final String rev;

  public ServiceAddresses(String cmp, String pro, String rat, String rev) {
    this.cmp = cmp;
    this.pro = pro;
    this.rat = rat;
    this.rev = rev;
  }

  public String getCmp() {
    return cmp;
  }

  public String getPro() {
    return pro;
  }

  public String getRat() {
    return rat;
  }

  public String getRev() {
    return rev;
  }
}
