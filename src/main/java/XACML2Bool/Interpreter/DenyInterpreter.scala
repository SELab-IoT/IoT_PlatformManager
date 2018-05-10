package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree._

object DenyInterpreter extends Interpreter{

  override def True: Mode = Deny

  override def interpretPermitRule(target: Target, condition: CTree): String = ???
  override def interpretDenyRule(target: Target, condition: CTree): String = ???

  override def interpretPOPolicies(policies: PTree*): String = ???
  override def interpretDOPolicies(policies: PTree*): String = ???
  override def interpretDuPPolicies(policies: PTree*): String = ???
  override def interpretPuDPolicies(policies: PTree*): String = ???

  override def interpretPORules(rules: RTree*): String = ???
  override def interpretDORules(rules: RTree*): String = ???
  override def interpretDuPRules(rules: RTree*): String = ???
  override def interpretPuDRules(rules: RTree*): String = ???

}
